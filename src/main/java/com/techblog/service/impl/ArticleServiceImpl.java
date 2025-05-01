package com.techblog.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;
import com.techblog.mapper.ArticleMapper;
import com.techblog.service.IArticleService;
import com.techblog.service.ITagService;
import com.techblog.service.IArticleTagService;
import com.techblog.utils.CacheClient;
import com.techblog.utils.RedisData;
import com.techblog.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.techblog.utils.constants.ArticleConstants.*;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ITagService tagService;

    @Resource
    private IArticleTagService articleTagService;

    @Resource
    private CacheClient cacheClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addArticleWithTag(Article article, List<Tag> tagList) {
        if (article == null || StringUtils.isBlank(article.getTitle())) {
            return Result.fail("文章标题不能为空");
        }
        if (tagList.isEmpty()) {
            return Result.fail("至少需要1个标签");
        }

        Long userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        article.setUserId(userId).setCreatedAt(now).setUpdatedAt(now);
        save(article);

        for (Tag tag : tagList) {
            Tag existingTag = tagService.lambdaQuery()
                    .eq(Tag::getName, tag.getName())
                    .one();

            Long tagId;
            if (existingTag != null) {
                tagId = existingTag.getId();
            } else {
                tag.setCreatedAt(now);
                tagService.save(tag);
                tagId = tag.getId();
            }

            articleTagService.saveArticleTag(article.getId(), tagId);
        }

        return Result.ok();
    }

    @Override
    public Result deleteArticle(Long articleId) {
        Long userId = UserHolder.getUser().getId();

        Article article = getById(articleId);
        if (article == null) {
            return Result.fail("文章不存在");
        }

        if (!article.getUserId().equals(userId)) {
            return Result.fail("您沒有權限刪除此文章");
        }
        removeById(articleId);
        return Result.ok();
    }

    @Override
    public Result queryArticleById(Long articleId) {
        // redis逻辑过期策略
        Article article = cacheClient.queryWithLogicalExpire(
                CACHE_ARTICLE_KEY,
                LOCK_ARTICLE_KEY,
                articleId,
                Article.class,
                this::getById,
                CACHE_ARTICLE_TTL,
                TimeUnit.SECONDS
        );

        if (article == null) {
            return Result.fail("文章不存在");
        }

        return Result.ok(article);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateArticle(Article article) {
        if (article == null || article.getId() == null) {
            return Result.fail("文章ID不能為空");
        }

        Article queryArticle = getById(article.getId());
        if (queryArticle == null) {
            return Result.fail("文章不存在");
        }

        Long userId = UserHolder.getUser().getId();
        if (!queryArticle.getUserId().equals(userId)) {
            return Result.fail("您沒有權限刪除此文章");
        }

        boolean success = updateById(article);
        if (success) {
            stringRedisTemplate.delete(CACHE_ARTICLE_KEY + article.getId());
            return Result.ok("文章更新成功");
        } else {
            return Result.fail("文章更新失敗，可能文章不存在");
        }
    }
}
