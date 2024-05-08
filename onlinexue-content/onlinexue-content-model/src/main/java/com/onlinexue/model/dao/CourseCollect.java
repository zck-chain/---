package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 赵承康
 * @date 2024/5/7
 */
@Data
@TableName("course_collect")
public class CourseCollect {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String courseId;

    private String userId;

    private String courseName;

    private String courseTeacher;

    private BigDecimal coursePrice;

    private String coursePic;

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
