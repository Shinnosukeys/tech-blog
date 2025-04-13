package com.techblog.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_comments")
public class Comment implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String content;

    private Integer articleId;

    private Integer userId;

    private Integer parentId;

    private LocalDateTime createdAt;
}