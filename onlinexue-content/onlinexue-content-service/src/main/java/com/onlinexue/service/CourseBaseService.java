package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dto.FormInline;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CourseBaseService extends IService<CourseBase> {

    /**
     * 查询基本课程信息
     *
     * @param formInline 查询实体类
     * @return
     */
    Result getCourseBaseList(FormInline formInline);

    /**
     * @param id 按课程id查询
     * @return
     */
    public Result getCourseById(@PathVariable String id);

    /**
     * 更新课程信息
     *
     * @param coursePublish
     * @return
     */
    Result couresUpdata(CoursePublish coursePublish);

    /**
     * 删除课程
     *
     * @param id
     * @return
     */
    Result couresDelete(List<String> id);

    /**
     * 课程添加
     *
     * @param coursePublish
     * @return
     */
    Result courseAdd(CoursePublish coursePublish, HttpServletRequest request);


    void deleteKeysWithPatternUsingScan(String courseBasePage);

    Result courseSubmit(CourseBase id);
}
