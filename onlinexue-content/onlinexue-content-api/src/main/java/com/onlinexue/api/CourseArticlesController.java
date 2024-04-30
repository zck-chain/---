package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.service.CourseArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result setArticles(@RequestBody Map<String, String> articles, HttpServletRequest request) {
        return courseArticlesService.setArticles(articles, request);

    }
}

