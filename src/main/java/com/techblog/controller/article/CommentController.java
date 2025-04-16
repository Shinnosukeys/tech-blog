package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.article.Comment;
import com.techblog.service.ICommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private ICommentService commentService;

    @PostMapping("/add")
    public Result addComment(@RequestBody Request request) {
        return Result.ok(commentService.save(request.getComment()));
    }

    @DeleteMapping("/delete/{commentId}")
    public Result deleteComment(@PathVariable Long commentId) {
        return Result.ok(commentService.removeById(commentId));
    }

    @PutMapping("/update")
    public Result updateComment(@RequestBody Request request) {
        return Result.ok(commentService.updateById(request.getComment()));
    }

    @GetMapping("/get/{commentId}")
    public Comment getComment(@PathVariable Long commentId) {
        return commentService.getById(commentId);
    }

    @GetMapping("/list")
    public List<Comment> listComment() {
        return commentService.list();
    }
}
