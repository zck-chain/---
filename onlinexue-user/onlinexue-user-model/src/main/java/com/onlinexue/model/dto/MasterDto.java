package com.onlinexue.model.dto;

import com.onlinexue.model.dao.Master;
import lombok.Data;

@Data
public class MasterDto extends Master {

    /**
     * 验证码
     */
    private String code;
    /**
     * 图片验证码
     */
    private String codeimg;
}
