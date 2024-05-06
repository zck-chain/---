package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dao.CourseReviews;
import com.onlinexue.model.dto.FormInline;

/**
 * @author 赵承康
 * @date 2024/3/13
 */
public interface CoursePublishService extends IService<CoursePublish> {


    Result coursePublishList(FormInline formInline);

    Result getCoursePublish(String id);

    Result getPopularCoursesList();

    Result addComment(CourseReviews data);

    Result getReviewsList(Long page, Long limit, String courseId);

    Result selectCouse(String courseName);
}
