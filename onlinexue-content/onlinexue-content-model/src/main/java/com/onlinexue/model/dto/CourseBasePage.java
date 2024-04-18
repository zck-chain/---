package com.onlinexue.model.dto;

import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CoursePublish;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseBasePage {
    public Integer current;//当前页
    public List<CourseBase> records;//分页数据
    public long total;//分页总数
    public List<Integer> pages;
    public List<CoursePublish> recordsPush;


}
