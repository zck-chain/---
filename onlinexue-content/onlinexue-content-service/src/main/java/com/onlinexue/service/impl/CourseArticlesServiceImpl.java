package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseArticlesMapper;
import com.onlinexue.model.dao.CourseArticles;
import com.onlinexue.model.dao.CourseReviews;
import com.onlinexue.model.dto.CourseArticlesDto;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CourseArticlesService;
import com.onlinexue.service.CourseReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlinexue.util.RedisConstants.LOGIN_USER_KEY;

/**
 * @author 赵承康
 * @date 2024/4/28
 */
@Service
@Transactional
public class CourseArticlesServiceImpl extends ServiceImpl<CourseArticlesMapper, CourseArticles> implements CourseArticlesService {
    @Autowired
    private CourseReviewsService courseReviewsService;//评论信息
    @Autowired
    private StringRedisTemplate stringRedisTemplate;//redis

    @Override
    public Result setArticles(Map<String, Object> articles) {
        if (articles == null) {
            return Result.fail("服务器维护中!");
        }
        String title = (String) articles.get("title");
        String content = (String) articles.get("content");
        Map<String, String> loginInfo = (Map<String, String>) articles.get("loginInfo");
        String userId = loginInfo.get("id");
        String userName = loginInfo.get("nickName");
        String icon = loginInfo.get("icon");
        CourseArticles courseArticles = new CourseArticles();
        courseArticles.setTitle(title);
        courseArticles.setContent(content);
        courseArticles.setUserId(userId);
        courseArticles.setUserName(userName);
        courseArticles.setIcon(icon);
        save(courseArticles);
        CourseArticlesDto courseArticlesDto = new CourseArticlesDto();
        BeanUtil.copyProperties(courseArticles, courseArticlesDto);
        return Result.ok(courseArticlesDto);
    }

    /**
     * 获取文章和评论的信息
     *
     * @param formData
     * @return
     */
    @Override
    public Result getArticles(FormInline formData) {
        if (formData == null) {
            return Result.fail("服务器在维护");
        }
        int page = formData.getPage();
        int limit = formData.getLimit();
        //获取文章信息
        Page<CourseArticles> iPage = new Page<>(page, limit);
        LambdaQueryWrapper<CourseArticles> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CourseArticles::getCreateDate);
        Page<CourseArticles> articlesPage = page(iPage, wrapper);
        //获取评论的信息
        List<CourseArticles> articlesList = articlesPage.getRecords();
        List<CourseArticlesDto> courseArticlesDtoList = new ArrayList<>();
        articlesList.stream().forEach(item -> {
            String id = item.getId();
            List<CourseReviews> courseReviewsList = courseReviewsService.list(new LambdaQueryWrapper<CourseReviews>()
                    .eq(CourseReviews::getArticlesId, id).orderByDesc(CourseReviews::getCreateDate).last("limit 8"));
            CourseArticlesDto courseArticlesDto = new CourseArticlesDto();
            BeanUtil.copyProperties(item, courseArticlesDto);
            courseArticlesDto.setComments(courseReviewsList);
            courseArticlesDtoList.add(courseArticlesDto);
        });
        // 计算总页数
        int totalPages = (int) Math.ceil((double) articlesPage.getTotal() / limit);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pages.add(i);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("pages", pages);
        result.put("data", courseArticlesDtoList);
        return Result.ok(result);
    }

    /**
     * 添加评论
     *
     * @param articleId
     * @param comment
     * @return
     */
    @Override
    @Transactional
    public Result setComments(String articleId, Map<String, String> comment, HttpServletRequest request) {
        if (comment == null) {
            return Result.fail("服务器在维护!");
        }
        Cookie[] cookies = request.getCookies();
        String token = "";
        for (Cookie cookie : cookies) {
            if ("guli_token".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        String redisKey = LOGIN_USER_KEY + token;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(redisKey);
        String nickName = (String) entries.get("nickName");
        String icon = (String) entries.get("icon");
        CourseReviews courseReviews = new CourseReviews();
        courseReviews.setReviews(comment.get("reviews"));
        courseReviews.setArticlesId(articleId);
        courseReviews.setUserName(nickName);
        courseReviews.setUserIcon(icon);
        courseReviewsService.save(courseReviews);
        return Result.ok(courseReviews);
    }
}
