package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 字典
 */
@Data
@TableName("course_dictionary")
public class CourseDictionary {
    /**
     * 课程等级状态码
     */
    @TableId
    private String code;
    /**
     * 课程等级名称
     */
    private String name;
}
