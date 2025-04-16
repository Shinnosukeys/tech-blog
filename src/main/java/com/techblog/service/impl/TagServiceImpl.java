package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;
import com.techblog.mapper.ArticleMapper;
import com.techblog.mapper.TagMapper;
import com.techblog.service.IArticleService;
import com.techblog.service.ITagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
}
