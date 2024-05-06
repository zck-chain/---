package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 赵承康
 * @date 2024/4/20
 */
@Data
@TableName("course_reviews")
public class CourseReviews {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 评论人
     */
    private String userName;

    /**
     * 评论人头像
     */
    private String userIcon;

    /**
     * 课程id
     */
    private String courseId;

    /**
     * 评论
     */
    private String reviews;


    /**
     * 回复人Id
     */
    private String respondentsId;


    /**
     * 文章id
     */
    private String articlesId;

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
