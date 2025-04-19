package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;

public interface IArticleService extends IService<Article>  {

    Result add(Article article);

    Result queryById(Integer articleId);
}
