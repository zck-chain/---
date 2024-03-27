package com.onlinexue.model.dto;

import com.onlinexue.model.dao.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
@Data
public class CourseChapterDto {
    private String title;
    private List<CourseCategory> sections;
}
