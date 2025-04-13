package com.techblog.entity.article;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_article_tags")
public class ArticleTag implements Serializable {
    private Integer articleId;

    private Integer tagId;
}
