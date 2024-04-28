package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CourseBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CourseBaseController {
    @Autowired
    CourseBaseService courseBaseService;

    /**
     * 查询基本课程信息
     *
     * @param formInline 查询实体类
     * @return
     */
    @PostMapping("/list")
    public Result getCourseBaseList(@RequestBody FormInline formInline) {
        return courseBaseService.getCourseBaseList(formInline);
    }

    /**
     * @param id 按课程id查询
     * @return
     */
    @GetMapping("/course/{id}")
    public Result getCourseById(@PathVariable String id) {
        return courseBaseService.getCourseById(id);
    }

    /**
     * 更新课程信息
     *
     * @param coursePublish
     * @return
     */
    @PostMapping("/course-updata")
    public Result couresUpdata(@RequestBody CoursePublish coursePublish) {
        return courseBaseService.couresUpdata(coursePublish);
    }

    /**
     * 按id删除课程
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    public Result couresDelete(@RequestBody List<String> ids) {
        return courseBaseService.couresDelete(ids);
    }

    /**
     * 新增课程
     *
     * @param coursePublish
     * @return
     */
    @PostMapping("/courseadd")
    public Result courseAdd(@RequestBody CoursePublish coursePublish, HttpServletRequest request) {
        return courseBaseService.courseAdd(coursePublish, request);
    }


    /**
     * 提交审核
     *
     * @param courseBase
     * @return
     */
    @PutMapping("/course/submit")
    public Result courseSubmit(@RequestBody CourseBase courseBase) {
        return courseBaseService.courseSubmit(courseBase);
    }

    /**
     * 发布课程
     *
     * @param
     * @return
     */
    @PostMapping("/course/publish")
    public Result coursePublish(@RequestBody List<String> ids) {
        return courseBaseService.coursePublish(ids);
    }


    /**
     * 下架
     *
     * @param ids
     * @return
     */
    @Transactional
    @DeleteMapping("/course/publish/Offline")
    public Result courseOffline(@RequestBody List<String> ids) {
        return courseBaseService.courseOffline(ids);

    }


}
