package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.user.UserInfo;

public interface IArticleService extends IService<Article>  {

    Result add(Article article);
}
