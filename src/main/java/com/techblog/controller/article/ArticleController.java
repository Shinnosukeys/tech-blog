package com.techblog.controller.article;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.article.Article;
import com.techblog.entity.article.Tag;
import com.techblog.service.IArticleService;
import com.techblog.service.IArticleTagService;
import com.techblog.service.ITagService;
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
    public Result addArticle(@RequestBody Request request) {
        // 保存文章
        Article article = request.getArticle();
        articleService.save(article);
        Integer articleId = article.getId();

        // 保存标签并关联文章和标签
        List<Tag> tagList = request.getTagList();
        for (Tag tag : tagList) {
            // 保存标签
            tagService.save(tag);
            Integer tagId = tag.getId();

            // 插入关联记录到 tb_article_tags 表
            articleTagService.saveArticleTag(articleId, tagId);
        }
        return Result.ok();
    }

    @DeleteMapping("/delete/{articleId}")
    public Result deleteArticle(@PathVariable Long articleId) {
        return Result.ok(articleService.removeById(articleId));
    }

    @PutMapping("/update")
    public Result updateArticle(@RequestBody Request request) {
        Article article = request.getArticle();
        if (article == null || article.getId() == null) {
            return Result.fail("文章ID不能為空");
        }
        
        boolean success = articleService.updateById(article);
        if (success) {
            return Result.ok("文章更新成功");
        } else {
            return Result.fail("文章更新失敗，可能文章不存在");
        }
    }

    @GetMapping("/get/{articleId}")
    public Result getArticle(@PathVariable Integer articleId) {
        return articleService.queryById(articleId);
    }

    @GetMapping("/list")
    public List<Article> listArticle() {
        return articleService.list();
    }

}
