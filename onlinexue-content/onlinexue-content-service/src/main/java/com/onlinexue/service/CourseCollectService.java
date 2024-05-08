package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseCollect;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 赵承康
 * @date 2024/5/7
 */
public interface CourseCollectService extends IService<CourseCollect> {
    Result setCollectCourse(String courseId, HttpServletRequest request);

    Result getCollectList(HttpServletRequest request);
}
