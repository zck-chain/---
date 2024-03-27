package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.service.CourseCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "课程信息管理系统", tags = "课程分类查询接口")
@RestController
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public Result queryTreaNode() {
        return courseCategoryService.queryTreaNode("1");
    }

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @GetMapping("/get/tree")
    public Result getOneTree() {
        return courseCategoryService.getOneTree();
    }
}
