package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_publish")
public class CoursePublish implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 适用人群
     */
    private String users;

    /**
     * 课程标签
     */
    private String tags;

    /**
     * 大分类
     */
    private String mt;


    /**
     * 小分类
     */
    private String st;


    /**
     * 课程等级
     */
    private String grade;


    /**
     * 课程评分
     */
    private Float start;

    /**
     * 收费情况(0免费,1收费)
     */
    private String charge;

    /**
     * 教育模式(101 录播，102直播等）
     */
    private String teachmode;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程图片
     */
    private String pic;

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

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 更新人
     */
    private String changePeople;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 课程发布状态 1未发布  2已发布 3下线
     */
    private String status;

    /**
     * 现价
     */
    private Float price;

    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 教师标识
     */
    private String teacherName;

    /**
     * 教师职位
     */
    private String position;

    /**
     * 照片
     */
    private String photograph;

    /**
     * 购买数
     */
    private Integer numberofpurchases;


    /**
     * 课时数
     */
    private Integer numberoflessons;

    /**
     * 浏览数
     */
    private Integer numberofviews;


}
