package com.onlinexue.api;

import com.onlinexue.dto.Result;
import com.onlinexue.service.CourseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 课程支付表
 *
 * @author 赵承康
 * @date 2024/5/6
 */
@RestController
public class CourseOrderController {
    @Autowired
    private CourseOrderService courseOrderService;//课程订单

    @PostMapping("/order/create/{courseId}")
    public Result createOrder(@PathVariable String courseId, HttpServletRequest request) {
        return courseOrderService.createOrder(courseId, request);
    }

    @GetMapping("/order/get/order/info/{orderId}")
    public Result getCourseOrder(@PathVariable String orderId) {
        return courseOrderService.getCourseOrder(orderId);
    }

    @GetMapping("/order/{page}/{limit}")
    public Result getOrderListByUserId(@PathVariable Long page, @PathVariable Long limit, HttpServletRequest request) {
        return courseOrderService.getOrderListByUserId(page, limit, request);
    }

    @PostMapping("/order/remove/{id}")
    public Result removeByIdOrder(@PathVariable String id) {
        return courseOrderService.removeByIdOrder(id);
    }
}
