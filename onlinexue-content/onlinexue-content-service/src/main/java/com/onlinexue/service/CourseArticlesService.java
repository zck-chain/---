package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseArticles;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 赵承康
 * @date 2024/4/28
 */
public interface CourseArticlesService extends IService<CourseArticles> {
    Result setArticles(Map<String, String> articles, HttpServletRequest request);
}
