package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.entity.article.Article;
import com.techblog.entity.user.UserInfo;
import com.techblog.service.IArticleService;
import com.techblog.service.IUserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    @PostMapping("/add")
    public Result addArticle(@RequestBody Article article) {
        return Result.ok(articleService.save(article));
    }

    @DeleteMapping("/delete/{articleId}")
    public Result deleteArticle(@PathVariable Long articleId) {
        return Result.ok(articleService.removeById(articleId));
    }

    @PutMapping("/update")
    public Result updateArticle(@RequestBody Article article) {
        return Result.ok(articleService.updateById(article));
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
