package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseCategory;

public interface CourseCategoryService extends IService<CourseCategory> {
    /**
     * @param id 根节点
     * @return 课程分类按Tree结构
     */
    Result queryTreaNode(String id);

    Result getOneTree();
}
