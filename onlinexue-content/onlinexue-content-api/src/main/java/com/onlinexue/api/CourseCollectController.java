package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.service.CourseCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 赵承康
 * @date 2024/5/7
 */
@RestController
public class CourseCollectController {
    @Autowired
    private CourseCollectService collectService;//收藏表

    @PostMapping("/collent/{courseId}")
    public Result setCollectCourse(@PathVariable String courseId, HttpServletRequest request) {
        return collectService.setCollectCourse(courseId, request);
    }

    @PostMapping("/collect/list")
    public Result getCollectList(HttpServletRequest request) {
        return collectService.getCollectList(request);
    }

    @PostMapping("/collect/remove/{id}")
    public Result removeById(@PathVariable String id) {
        boolean removeById = collectService.removeById(id);
        return removeById == true ? Result.ok() : Result.fail("删除失败!");
    }
}
