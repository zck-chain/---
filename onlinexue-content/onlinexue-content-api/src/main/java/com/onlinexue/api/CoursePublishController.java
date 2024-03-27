package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 课程发布成功
 *
 * @author 赵承康
 * @date 2024/3/13
 */
@RestController
public class CoursePublishController {
    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 发布课程
     *
     * @param courseBase
     * @return
     */
    @PostMapping("/course/publish")
    public Result coursePublish(@RequestBody CourseBase courseBase) {
        return coursePublishService.coursePublish(courseBase);
    }

    @PostMapping("/course/publish/list")
    public Result coursePublishList(@RequestBody FormInline formInline) {
        return coursePublishService.coursePublishList(formInline);

    }

    @GetMapping("/course/publish/{id}")
    public Result getCoursePublish(@PathVariable String id) {
        return coursePublishService.getCoursePublish(id);
    }

    /**
     * 获取热门课程
     *
     * @return
     */
    @GetMapping("/index/getPopularCoursesList")
    public Result getPopularCoursesList() {
        return coursePublishService.getPopularCoursesList();
    }

    @Transactional
    @DeleteMapping("/course/publish/Offline")
    public Result courseOffline(@RequestBody CoursePublish coursePublish) {
        return coursePublishService.courseOffline(coursePublish);

    }
}
