package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseChapterMaper;
import com.onlinexue.model.dao.CourseChapter;
import com.onlinexue.model.dto.CourseChapterDto;
import com.onlinexue.service.CourseChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 赵承康
 * @date 2024/3/21
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMaper, CourseChapter> implements CourseChapterService {

    public static List<CourseChapterDto> getCourseChapterDtoList(List<CourseChapter> courseChapterList) {
        List<CourseChapterDto> courseChapterDtoList = new ArrayList<>();
        courseChapterList.forEach(item -> {
            CourseChapterDto chapterDto = new CourseChapterDto();
            BeanUtil.copyProperties(item, chapterDto);
            courseChapterDtoList.add(chapterDto);
        });

// 构建章节映射表并按sort属性排序
        Map<String, CourseChapterDto> courseChapterMap = courseChapterDtoList.stream()
                .filter(item -> "0".equals(item.getParentId()))
                .sorted(Comparator.comparing(CourseChapterDto::getSort))
                .collect(Collectors.toMap(CourseChapterDto::getId, Function.identity(), (key1, key2) -> key2, LinkedHashMap::new));

// 将子章节添加到父章节的子章节列表中
        courseChapterList.forEach(item -> {
            String parentId = item.getParentId();
            if (courseChapterMap.containsKey(parentId)) {
                CourseChapterDto courseChapterDto = courseChapterMap.get(parentId);
                if (courseChapterDto.getSections() == null) {
                    courseChapterDto.setSections(new ArrayList<>());
                }
                courseChapterDto.getSections().add(item);
            }
        });

// 对小节列表按sort属性排序
        courseChapterMap.values().forEach(chapterDto -> {
            if (chapterDto.getSections() != null) {
                chapterDto.getSections().sort(Comparator.comparing(CourseChapter::getSort));
            }
        });
        return new ArrayList<>(courseChapterMap.values());
    }

    /**
     * 构建视频目录
     *
     * @param courseChapterDtoList
     * @param courseId
     * @return
     */
    @Transactional
    @Override
    public Result setCourseChapters(List<CourseChapterDto> courseChapterDtoList, String courseId) {
        List<CourseChapter> courseChapterList = list(new LambdaQueryWrapper<CourseChapter>().eq(CourseChapter::getCourseId, courseId));
        if (!courseChapterList.isEmpty()) {
            //表示修改
            removeByIds(courseChapterList);//删除之前的
        }
        if (courseChapterDtoList.isEmpty()) {
            return Result.ok();
        }
        // 创建一个存储生成的章节列表的结果集
        List<CourseChapter> resultList = new ArrayList<>();
        Integer parentCount = 0; // 每个小节可以放入1000个
        // 遍历课程章节DTO列表
        for (CourseChapterDto chapterDto : courseChapterDtoList) {
            // 生成父章节ID
            String parentId = UUID.randomUUID().toString();
            // 创建父章节对象并设置属性
            CourseChapter parentChapter = new CourseChapter();
            parentChapter.setId(parentId);
            parentChapter.setCourseId(courseId);
            parentChapter.setTitle(chapterDto.getTitle());
            parentChapter.setParentId("0");
            parentChapter.setSort(parentCount += 1000);
            // 添加父章节到结果集
            resultList.add(parentChapter);
            // 初始化小节排序数
            int childCount = 0;
            // 遍历小节列表
            for (CourseChapter section : chapterDto.getSections()) {
                // 创建小节对象并设置属性
                CourseChapter childChapter = new CourseChapter();
                childChapter.setId(UUID.randomUUID().toString());
                childChapter.setCourseId(courseId);
                childChapter.setParentId(parentId);
                childChapter.setTitle(section.getTitle());
                childChapter.setVideoUrl(section.getVideoUrl());
                childChapter.setVideoName(section.getVideoName());
                // 将小节添加到结果集并递增排序数
                childChapter.setSort(childCount++);
                resultList.add(childChapter);
            }
        }
        saveBatch(resultList);
        return Result.ok();
    }

    @Override
    public Result getCourseChapters(String courseId) {
        // 按课程id查询章节消息
        List<CourseChapter> courseChapterList = list(new LambdaQueryWrapper<CourseChapter>().eq(CourseChapter::getCourseId, courseId));
        // 如果章节列表为空，返回失败结果
        if (courseChapterList == null || courseChapterList.isEmpty()) {
            return Result.fail("暂时还没有视频信息!");
        }
        List<CourseChapterDto> returnList = getCourseChapterDtoList(courseChapterList);
        return Result.ok(returnList);
    }

}
