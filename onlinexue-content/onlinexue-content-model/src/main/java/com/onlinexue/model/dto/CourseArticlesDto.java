package com.onlinexue.model.dto;

import com.onlinexue.model.dao.CourseArticles;
import com.onlinexue.model.dao.CourseReviews;
import lombok.Data;

import java.util.List;

/**
 * @author 赵承康
 * @date 2024/5/3
 */
@Data
public class CourseArticlesDto extends CourseArticles {
    private List<CourseReviews> comments;
}
