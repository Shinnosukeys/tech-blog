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
@TableName("tb_categories")
public class Categorie implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private LocalDateTime createdAt;
}
