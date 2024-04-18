package com.onlinexue.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_teacher")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 课程标识
     */
    //@NotEmpty(message = "课程id不能为空")
    private String courseId;

    /**
     * 教师标识
     */
    @NotEmpty(message = "教师名字不能为空")
    private String teacherName;

    /**
     * 教师职位
     */
    @NotEmpty(message = "教师职位不能为空")
    private String position;

    /**
     * 教师简介
     */
    @NotEmpty(message = "教师简介不能为空")
    @Size(message = "教师简介内容过少,最少6位", min = 6)
    private String introduction;

    /**
     * 照片
     */
    private String photograph;

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


    // 重写 equals 和 hashCode 方法，以便在比较对象时使用
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseTeacher myClass = (CourseTeacher) o;
        return Objects.equals(teacherName, myClass.teacherName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherName);
    }


}
