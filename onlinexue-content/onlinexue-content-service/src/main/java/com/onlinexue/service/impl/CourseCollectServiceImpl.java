package com.onlinexue.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseCollectMapper;
import com.onlinexue.model.dao.CourseCollect;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.service.CourseCollectService;
import com.onlinexue.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.onlinexue.util.RedisConstants.LOGIN_USER_KEY;

/**
 * @author 赵承康
 * @date 2024/5/7
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CoursePublishService coursePublishService;

    @Override
    public Result setCollectCourse(String courseId, HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StrUtil.isEmpty(token)) {
            return Result.fail("请先登录!");
        }
        CourseCollect collect = getOne(new LambdaQueryWrapper<CourseCollect>().eq(CourseCollect::getCourseId, courseId));
        if (collect != null) {
            return Result.fail("已经收藏过了!");
        }
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Result.fail("登录时间过期,请重新登录!");
        }
        String userId = (String) entries.get("id");
        CoursePublish coursePublish = coursePublishService.getById(courseId);
        CourseCollect courseCollect = new CourseCollect();
        courseCollect.setCourseId(courseId);
        courseCollect.setUserId(userId);
        courseCollect.setCourseName(coursePublish.getName());
        courseCollect.setCoursePrice(BigDecimal.valueOf(coursePublish.getPrice()));
        courseCollect.setCourseTeacher(coursePublish.getTeacherName());
        courseCollect.setCoursePic(coursePublish.getPic());
        save(courseCollect);
        return Result.ok("收藏成功!");
    }

    @Override
    public Result getCollectList(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StrUtil.isEmpty(token)) {
            return Result.fail("请先登录!");
        }
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        String userId = (String) entries.get("id");
        List<CourseCollect> courseCollects = list(new LambdaQueryWrapper<CourseCollect>().eq(CourseCollect::getUserId, userId));
        return Result.ok(courseCollects);
    }
}
