package com.techblog.mapper;

import org.apache.ibatis.annotations.Insert;

public interface ArticleTagMapper {
    @Insert("INSERT INTO tb_article_tags (article_id, tag_id) VALUES (#{articleId}, #{tagId})")
    void insertArticleTag(Integer articleId, Integer tagId);
}