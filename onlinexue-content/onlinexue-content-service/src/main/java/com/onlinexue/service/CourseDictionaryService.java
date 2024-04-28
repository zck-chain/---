package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseDictionary;

public interface CourseDictionaryService extends IService<CourseDictionary> {
    /**
     * 获取课程等级
     *
     * @return
     */
    Result getcourseGrade();


    /**
     * 获取课程播放
     *
     * @return
     */
    Result getcourseTeachmode();

    Result getTeacherGrade();
}
