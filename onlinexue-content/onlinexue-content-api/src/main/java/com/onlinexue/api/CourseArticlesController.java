package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CourseArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 赵承康
 * @date 2024/4/28
 * 文章表
 */
@RestController
public class CourseArticlesController {
    @Autowired
    private CourseArticlesService courseArticlesService;

    /**
     * 发布文章
     *
     * @return
     */
    @PostMapping("/setArticles")
    public Result setArticles(@RequestBody Map<String, Object> articles) {
        return courseArticlesService.setArticles(articles);
    }

    @PostMapping("/getArticles")
    public Result getArticles(@RequestBody FormInline formInline) {
        return courseArticlesService.getArticles(formInline);
    }

    @PostMapping("/articles/{articleId}/comments")
    public Result setComments(@PathVariable String articleId, @RequestBody Map<String, String> comment, HttpServletRequest request) {
        return courseArticlesService.setComments(articleId, comment, request);
    }

}

