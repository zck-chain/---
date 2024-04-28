package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.service.CourseDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseDictionaryController {
    @Autowired
    CourseDictionaryService courseDictionaryService;

    /**
     * 获取课程等级分类字典
     *
     * @return
     */
    @GetMapping("/course-grade")
    public Result getcourseGrade() {
        return courseDictionaryService.getcourseGrade();
    }

    /**
     * 获取课程播放字典
     *
     * @return
     */
    @GetMapping("/course-teachmode")
    public Result getcourseTeachmode() {
        return courseDictionaryService.getcourseTeachmode();
    }

    @GetMapping("/teacher-grade")
    public Result getTeacherGrade() {
        return courseDictionaryService.getTeacherGrade();
    }
}
