package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.user.User;
import com.techblog.mapper.ArticleMapper;
import com.techblog.mapper.UserMapper;
import com.techblog.service.IArticleService;
import com.techblog.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private ArticleMapper articleMapper;

    @Override
    public Result add(Article article) {
        save(article);
        articleMapper.insert(article);
        return Result.ok();
    }
}
