package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程订单
 *
 * @author 赵承康
 * @date 2024/5/6
 */
@Data
@TableName("course_order")
public class CourseOrder {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String courseId;

    private String courseName;

    private BigDecimal coursePrice;

    private String courseTeacher;

    private BigDecimal originalPrice;

    private String coursePic;

    private String orderNo;

    private String userId;//用户id;

    private Integer tradeState;//订单状态

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;
}
