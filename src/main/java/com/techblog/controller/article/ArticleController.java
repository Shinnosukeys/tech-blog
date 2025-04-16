package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.article.Article;
import com.techblog.service.IArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    @PostMapping("/add")
    public Result addArticle(@RequestBody Request request) {
        return Result.ok(articleService.save(request.getArticle()));
    }

    @DeleteMapping("/delete/{articleId}")
    public Result deleteArticle(@PathVariable Long articleId) {
        return Result.ok(articleService.removeById(articleId));
    }

    @PutMapping("/update")
    public Result updateArticle(@RequestBody Request request) {
        return Result.ok(articleService.updateById(request.getArticle()));
    }

    @GetMapping("/get/{articleId}")
    public Article getArticle(@PathVariable Long articleId) {
        return articleService.getById(articleId);
    }

    @GetMapping("/list")
    public List<Article> listArticle() {
        return articleService.list();
    }

}
