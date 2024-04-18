package com.onlinexue.model.dto;

import com.onlinexue.model.dao.CourseChapter;
import lombok.Data;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
@Data
public class CourseChapterDto extends CourseChapter {
    private List<CourseChapter> sections;
}
