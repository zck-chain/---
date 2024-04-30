package com.onlinexue.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseArticlesMapper;
import com.onlinexue.model.dao.CourseArticles;
import com.onlinexue.model.dao.User;
import com.onlinexue.service.CourseArticlesService;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 赵承康
 * @date 2024/4/28
 */
@Service
public class CourseArticlesServiceImpl extends ServiceImpl<CourseArticlesMapper, CourseArticles> implements CourseArticlesService {
    @Override
    public Result setArticles(Map<String, String> articles, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Result.fail("请先登录!");
        }
        User user = null;
        for (Cookie item : cookies) {
            if ("guli_ucenter".equals(item.getName())) {
                user = (User) JSON.parse(item.getName());
            }
        }
        return null;
    }
}
