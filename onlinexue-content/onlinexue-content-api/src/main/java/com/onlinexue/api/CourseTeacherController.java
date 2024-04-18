package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseTeacher;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CourseTeacherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * //课程教师
 */
@RestController
public class CourseTeacherController {
    @Resource
    CourseTeacherService courseTeacherService;

    /**
     * 按课程id查询讲师
     *
     * @param courseId
     * @return
     */
    @GetMapping("/courseTeacher/{courseId}")
    public Result courseTeacherByid(@PathVariable String courseId) {
        return courseTeacherService.courseTeacherByid(courseId);
    }

    /**
     * 更新课程教师信息
     *
     * @param courseTeacher
     * @return
     */
    @PostMapping("/courseTeacher/updataorAdd")
    public Result courseTeacherUpdata(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.courseTeacherUpdataorAdd(courseTeacher);
    }

    @GetMapping("/index/getPopularTeacherList")
    public Result getPopularTeacherList() {
        return courseTeacherService.getPopularTeacherList();
    }


    @PostMapping("/front/get/teacher/list")
    public Result getTeacherList(@RequestBody FormInline formInline) {
        return courseTeacherService.getTeacherList(formInline);
    }

    @GetMapping("/front/get/teacher/info/{id}")
    public Result getTeacherById(@PathVariable("id") String teacherId) {
        return courseTeacherService.getTeacherById(teacherId);
    }
}
