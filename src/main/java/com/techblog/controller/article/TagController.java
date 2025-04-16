package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.article.Tag;
import com.techblog.service.ITagService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    private ITagService tagService;

    @PostMapping("/add")
    public Result addTag(@RequestBody Request request) {
        // 保存标签并关联文章和标签
        List<Tag> tagList = request.getTagList();
        for (Tag tag : tagList) {
            // 保存标签
            tagService.save(tag);
            Integer tagId = tag.getId();
        }

        return Result.ok();
    }

    @DeleteMapping("/delete/{tagId}")
    public Result deleteTag(@PathVariable Long tagId) {
        return Result.ok(tagService.removeById(tagId));
    }

    @PutMapping("/update")
    public Result updateTag(@RequestBody Request request) {
        // 保存标签并关联文章和标签
        List<Tag> tagList = request.getTagList();
        for (Tag tag : tagList) {
            // 保存标签
            tagService.save(tag);
            Integer tagId = tag.getId();
        }

        return Result.ok();
    }

    @GetMapping("/get/{tagId}")
    public Tag getTag(@PathVariable Long tagId) {
        return tagService.getById(tagId);
    }

    @GetMapping("/list")
    public List<Tag> listTag() {
        return tagService.list();
    }
}

