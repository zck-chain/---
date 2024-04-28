package com.onlinexue.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 赵承康
 * @date 2024/4/2
 */
@Data
@AllArgsConstructor
public class VideoVo {
    private String videoUrl;//视频路径
    private String videoName;//视频名称
}
