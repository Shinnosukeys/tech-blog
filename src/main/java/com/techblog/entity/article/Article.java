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
@TableName("tb_articles")
public class Article implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * SEO友好URL
     */
    private String slug;

    /**
     * 正文（支持Markdown）
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 封面图路径
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 投币数
     */
    private Integer coinCount;

    /**
     * 草稿状态
     */
    private Boolean isDraft;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
