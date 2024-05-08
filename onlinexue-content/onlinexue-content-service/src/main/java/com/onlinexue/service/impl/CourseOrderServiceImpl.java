package com.onlinexue.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseOrderMapper;
import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CourseOrder;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.service.CourseOrderService;
import com.onlinexue.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.onlinexue.util.RedisConstants.LOGIN_USER_KEY;

/**
 * @author 赵承康
 * @date 2024/5/6
 */
@Service
public class CourseOrderServiceImpl extends ServiceImpl<CourseOrderMapper, CourseOrder> implements CourseOrderService {
    @Autowired
    private CoursePublishService coursePublishService;
    @Autowired
    private CourseBaseServiceImpl courseBaseService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public Result createOrder(String courseId, HttpServletRequest request) {
        if (StrUtil.isEmpty(courseId)) {
            return Result.fail("服务器在维护!");
        }
        String token = request.getHeader("Token");
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Result.fail("登录时间过期,请重新登录!");
        }
        String userId = (String) entries.get("id");
        //获取是否存在订单
        CourseOrder order = getOne(new LambdaQueryWrapper<CourseOrder>().eq(CourseOrder::getCourseId, courseId).eq(CourseOrder::getUserId, userId));
        if (order != null) {
            return Result.fail("您已经购买了,请不要刷单!");
        }
        //订单不存在
        CoursePublish coursePublish = coursePublishService.getById(courseId);
        int purchases = coursePublish.getNumberofpurchases() + 1;
        CourseOrder courseOrder = new CourseOrder();//订单信息
        courseOrder.setCourseId(courseId);
        courseOrder.setCourseName(coursePublish.getName());
        courseOrder.setOriginalPrice(BigDecimal.valueOf(coursePublish.getOriginalPrice()));//原价格
        courseOrder.setCoursePrice(BigDecimal.valueOf(coursePublish.getPrice()));//课程现在价格
        courseOrder.setCourseTeacher(coursePublish.getTeacherName());
        courseOrder.setCoursePic(coursePublish.getPic());
        courseOrder.setOrderNo("course_" + UUID.randomUUID() + DateUnit.DAY);
        courseOrder.setUserId(userId);
        saveOrUpdate(courseOrder);
        coursePublishService.update(new LambdaUpdateWrapper<CoursePublish>().eq(CoursePublish::getId, courseId).set(CoursePublish::getNumberofpurchases, purchases));
        courseBaseService.update(new LambdaUpdateWrapper<CourseBase>().eq(CourseBase::getId, courseId).set(CourseBase::getNumberofpurchases, purchases));
        return Result.ok(courseOrder.getId());
    }

    @Override
    public Result getCourseOrder(String orderId) {
        if (StrUtil.isEmpty(orderId)) {
            return Result.fail("服务器在维护!");
        }
        CourseOrder courseOrder = getById(orderId);
        return Result.ok(courseOrder);
    }

    @Override
    public Result getOrderListByUserId(Long page, Long limit, HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StrUtil.isEmpty(token)) {
            return Result.fail("请先登录!");
        }
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        String userId = (String) entries.get("id");
        IPage<CourseOrder> iPage = new Page<>(page, limit);
        LambdaQueryWrapper<CourseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseOrder::getUserId, userId);
        IPage<CourseOrder> courseOrderIPage = page(iPage, wrapper);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("items", courseOrderIPage.getRecords());
        returnMap.put("total", courseOrderIPage.getTotal());
        return Result.ok(returnMap);
    }

    @Override
    @Transactional
    public Result removeByIdOrder(String id) {
        removeById(id);
        return Result.ok();
    }
}
