package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dto.CourseChapterDto;
import com.onlinexue.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
@RestController
public class CourseChapterController {
    @Autowired
    private CourseChapterService courseChapterService;//课程章节

    /**
     * 构建视频目录
     *
     * @param courseChapterDto
     * @param courseId
     * @return
     */
    @PostMapping("/course/setchapters/{courseId}")
    public Result setCourseChapters(@RequestBody List<CourseChapterDto> courseChapterDto, @PathVariable String courseId) {
        return courseChapterService.setCourseChapters(courseChapterDto, courseId);
    }

    /**
     * 查询课程
     *
     * @param courseId
     * @return
     */
    @GetMapping("/course/getchapters/{courseId}")
    public Result getCourseChapters(@PathVariable String courseId) {
        return courseChapterService.getCourseChapters(courseId);
    }
}
