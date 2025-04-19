package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;

import java.util.List;

public interface IArticleService extends IService<Article>  {

    Result addArticleWithTag(Article article, List<Tag> tagList);


    Result queryArticleById(Integer articleId);
    
    Result deleteArticle(Integer articleId);
    
    Result updateArticle(Article article);
}
