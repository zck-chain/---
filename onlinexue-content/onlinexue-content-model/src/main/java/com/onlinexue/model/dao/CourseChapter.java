package com.onlinexue.model.dao;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_chapter")
public class CourseChapter {
    /**
     * 主键，课程id
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 标题
     */
    private String title;
    /**
     * 父级id
     */
    private String parentId;
    /**
     * 课程id
     */
    private String courseId;
    /**
     * 视频链接
     */
    private String videoUrl;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 排序
     */
    private Integer sort;


}
