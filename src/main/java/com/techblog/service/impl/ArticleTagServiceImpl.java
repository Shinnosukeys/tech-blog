package com.techblog.service.impl;

import com.techblog.service.IArticleTagService;
import com.techblog.mapper.ArticleTagMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class ArticleTagServiceImpl implements IArticleTagService {

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    public void saveArticleTag(Integer articleId, Integer tagId) {
        articleTagMapper.insertArticleTag(articleId, tagId);
    }
}
