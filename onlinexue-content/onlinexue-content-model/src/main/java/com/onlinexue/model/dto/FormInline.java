package com.onlinexue.model.dto;

import lombok.Data;

@Data
public class FormInline {
    private int page;//查询页数

    private int limit;//分页数

    private String selectname;//查询课程名称

    private String selectuser;//查询课程用户

    private String id;//课程类别id

    private String buyCountSort;//销量
    private String gmtCreateSort;//最新
    private String priceSort;//价格
    private String courseId;
}
