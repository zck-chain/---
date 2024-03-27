package com.onlinexue.sercvice;

import com.onlinexue.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 111
 */
public interface MediaService {


    Result upload(MultipartFile file, String id, String status, HttpServletRequest request);

    Result uploadVideo(String courseId, int chapterIndex, int sectionIndex, MultipartFile videoFile);
}
