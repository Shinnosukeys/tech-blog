package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;

import java.util.List;

public interface IArticleService extends IService<Article>  {

    Result addArticleWithTag(Article article, List<Tag> tagList);

    Result queryArticleById(Long articleId);
    
    Result deleteArticle(Long articleId);
    
    Result updateArticle(Article article);
}
