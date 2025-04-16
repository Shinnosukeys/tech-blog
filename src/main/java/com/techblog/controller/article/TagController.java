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
    public Result addArticle(@RequestBody Request request) {
        return Result.ok(tagService.save(request.getTag()));
    }

    @DeleteMapping("/delete/{tagId}")
    public Result deleteArticle(@PathVariable Long tagId) {
        return Result.ok(tagService.removeById(tagId));
    }

    @PutMapping("/update")
    public Result updateArticle(@RequestBody Request request) {
        return Result.ok(tagService.updateById(request.getTag()));
    }

    @GetMapping("/get/{tagId}")
    public Tag getArticle(@PathVariable Long tagId) {
        return tagService.getById(tagId);
    }

    @GetMapping("/list")
    public List<Tag> listArticle() {
        return tagService.list();
    }
}

