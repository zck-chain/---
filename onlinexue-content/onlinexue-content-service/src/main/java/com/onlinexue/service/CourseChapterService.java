package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseChapter;
import com.onlinexue.model.dto.CourseChapterDto;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
public interface CourseChapterService extends IService<CourseChapter> {
    Result setCourseChapters(List<CourseChapterDto> courseChapterDto, String courseId);

    Result getCourseChapters(String courseId);
}
