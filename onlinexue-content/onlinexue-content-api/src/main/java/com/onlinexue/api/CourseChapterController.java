package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dto.CourseChapterDto;
import com.onlinexue.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
@RestController
public class CourseChapterController {
    @Autowired
    private CourseChapterService courseChapterService;//课程章节

    @PostMapping("/course/chapters")
    public Result setCourseChapters(@RequestBody List<CourseChapterDto> courseChapterDto) {
        return null;
    }
}
