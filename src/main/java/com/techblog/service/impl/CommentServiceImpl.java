package com.techblog.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.entity.article.Comment;
import com.techblog.mapper.CommentMapper;
import com.techblog.service.ICommentService;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
