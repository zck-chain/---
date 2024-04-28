package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseReviews;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * 按id查询课程信息
     * @param id
     * @return
     */
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

    /**
     * 获取发布课程的信息
     *
     * @param formInline
     * @return
     */
    @PostMapping("/course/publish/list")
    public Result getPublishList(@RequestBody FormInline formInline) {
        return coursePublishService.coursePublishList(formInline);
    }

    /**
     * 添加评论
     *
     * @return
     */
    @PostMapping("/publish/add/comment")
    public Result addComment(@RequestBody CourseReviews courseReviews) {
        return coursePublishService.addComment(courseReviews);
    }

    /**
     * 获取课程评论的信息
     *
     * @param page
     * @param limit
     * @param courseId
     * @return
     */
    @GetMapping("/reviews/{page}/{limit}/{courseId}")
    public Result getReviewsList(@PathVariable Long page, @PathVariable Long limit, @PathVariable String courseId) {
        return coursePublishService.getReviewsList(page, limit,courseId);
    }

}
