package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseOrder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 赵承康
 * @date 2024/5/6
 */
public interface CourseOrderService extends IService<CourseOrder> {
    Result createOrder(String courseId, HttpServletRequest request);

    Result getCourseOrder(String orderId);

    Result getOrderListByUserId(Long page, Long limit, HttpServletRequest request);

    Result removeByIdOrder(String id);
}
