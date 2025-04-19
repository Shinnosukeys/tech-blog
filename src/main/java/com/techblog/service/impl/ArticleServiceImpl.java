package com.techblog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;
import com.techblog.mapper.ArticleMapper;
import com.techblog.service.IArticleService;
import com.techblog.service.ITagService;
import com.techblog.service.IArticleTagService;
import com.techblog.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ITagService tagService;
    
    @Autowired
    private IArticleTagService articleTagService;
    
    private static final String CACHE_ARTICLE_KEY = "cache:article:";
    private static final long CACHE_ARTICLE_TTL = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addArticleWithTag(Article article, List<Tag> tagList) {
        if (article == null || StringUtils.isBlank(article.getTitle())) {
            return Result.fail("文章标题不能为空");
        }
        if (tagList.isEmpty()) {
            return Result.fail("至少需要1个标签");
        }

        Integer userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        article.setUserId(userId).setCreatedAt(now).setUpdatedAt(now);

        save(article);
        return Result.ok();
    }

    @Override
    public Result deleteArticle(Integer articleId) {
        Integer userId = UserHolder.getUser().getId();

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
    public Result queryArticleById(Integer articleId) {
        return Result.ok(getById(articleId));
    }
    
    @Override
    public Result updateArticle(Article article) {
        if (article == null || article.getId() == null) {
            return Result.fail("文章ID不能為空");
        }

        Article queryArticle = getById(article.getId());
        if (queryArticle == null) {
            return Result.fail("文章不存在");
        }

        Integer userId = UserHolder.getUser().getId();
        if (!queryArticle.getUserId().equals(userId)) {
            return Result.fail("您沒有權限刪除此文章");
        }

        boolean success = updateById(article);
        if (success) {
            return Result.ok("文章更新成功");
        } else {
            return Result.fail("文章更新失敗，可能文章不存在");
        }
    }
}
