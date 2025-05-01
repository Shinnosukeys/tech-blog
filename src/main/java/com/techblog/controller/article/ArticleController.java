package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;
import com.techblog.service.IArticleService;
import com.techblog.service.IArticleTagService;
import com.techblog.service.ITagService;
import com.techblog.utils.UserHolder;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    @Resource
    private ITagService tagService;

    @Resource
    private IArticleTagService articleTagService;

    @PostMapping("/add")
    public Result addArticleWithTag(@RequestBody Request request) {
        return articleService.addArticleWithTag(request.getArticle(), request.getTagList());
    }

    @DeleteMapping("/delete/{articleId}")
    public Result deleteArticle(@PathVariable Long articleId) {
        return articleService.deleteArticle(articleId);
    }

    @PutMapping("/update")
    public Result updateArticle(@RequestBody Request request) {
        return articleService.updateArticle(request.getArticle());

    }

    @GetMapping("/get/{articleId}")
    public Result getArticle(@PathVariable Long articleId) {
        return articleService.queryArticleById(articleId);
    }

    @GetMapping("/list")
    public List<Article> listArticle() {
        return articleService.list();
    }

}
