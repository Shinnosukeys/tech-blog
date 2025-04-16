package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Comment;
import com.techblog.mapper.ArticleMapper;
import com.techblog.mapper.CommentMapper;
import com.techblog.service.IArticleService;
import com.techblog.service.ICommentService;

public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
