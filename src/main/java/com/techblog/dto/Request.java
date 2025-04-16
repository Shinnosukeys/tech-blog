package com.techblog.dto;

import com.techblog.dto.LoginFormDTO;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Comment;
import com.techblog.entity.article.Tag;
import com.techblog.entity.user.User;
import com.techblog.entity.user.UserInfo;
import lombok.Data;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Data
public class Request {
    private LoginFormDTO loginFormDTO;
    private User user;
    private UserInfo userInfo;
    private Article article;
    private List<Tag> tagList;
    private Comment comment;
}
