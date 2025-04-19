package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.mapper.ArticleMapper;
import com.techblog.service.IArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String CACHE_ARTICLE_KEY = "cache:article:";
    private static final long CACHE_ARTICLE_TTL = 30;

    @Override
    public Result add(Article article) {
        save(article);
        articleMapper.insert(article);
        return Result.ok();
    }

    @Override
    public Result queryById(Integer articleId) {
        // 1.從redis查詢文章緩存
        String key = CACHE_ARTICLE_KEY + articleId;
        String cacheArticle = stringRedisTemplate.opsForValue().get(key);
        
        // 2.判斷是否存在
        if (cacheArticle != null) {
            try {
                // 3.存在，直接返回
                Article article = objectMapper.readValue(cacheArticle, Article.class);
                return Result.ok(article);
            } catch (Exception e) {
                log.error("解析緩存文章失敗", e);
            }
        }
        
        // 4.不存在，根據id查詢數據庫
        Article article = articleMapper.selectById(articleId);
        
        // 5.不存在，返回錯誤
        if (article == null) {
            return Result.fail("文章不存在");
        }
        
        // 6.存在，寫入redis
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(article), CACHE_ARTICLE_TTL, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("寫入緩存文章失敗", e);
        }
        
        // 7.返回
        return Result.ok(article);
    }
}
