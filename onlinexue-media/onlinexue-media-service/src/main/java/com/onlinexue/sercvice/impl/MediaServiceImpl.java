package com.onlinexue.sercvice.impl;

import cn.hutool.core.util.StrUtil;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dto.VideoVo;
import com.onlinexue.sercvice.MediaService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.onlinexue.util.MinioUtils.*;

@Slf4j
@Component
public class MediaServiceImpl implements MediaService {
    @Autowired
    MinioClient minioClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    //删除minio中的文件
    public boolean deleteMediaFiles(String iconUrl) {
        int indexOf = iconUrl.indexOf("mr");
        iconUrl = iconUrl.substring(indexOf);
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().
                bucket(BUCKET_MEDIAFILES).object(iconUrl).
                build();
        try {
            minioClient.removeObject(removeObjectArgs);
            log.debug("删除成功");
            return true;
        } catch (Exception e) {
            log.error("上传删除出错,错误信息:{}", e.getMessage());
            return false;
        }
    }

    //将文件上传到minio
    public Result upload(MultipartFile file, String id, String status, HttpServletRequest request) {
        String filename = file.getOriginalFilename();
        //得到扩张名
        String extension = filename.substring(filename.lastIndexOf("."));//.jpg
        //得到mimeType
        String mimeType = getMimeType(extension);
        String localFilePath = getloaclFile(file);
        String defaultFolderPath = getDefaultFolderPath();
        String fileMd5 = getFileMd5(new File(localFilePath));//获取文件的md5
        String objectName = getIconPath(status, id, defaultFolderPath, fileMd5, extension);
        if (StrUtil.isEmpty(objectName)) {
            return Result.fail("上传失败");
        }
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(BUCKET_MEDIAFILES)//桶
                    .filename(localFilePath)//本地文件
                    .object(objectName)//对象名
                    .contentType(mimeType)//文件类型
                    .build();
            minioClient.uploadObject(uploadObjectArgs);//上传到mimio
            log.debug("上传文件到minio成功,bucket:{},objectName:{},错误信息:{}", BUCKET_MEDIAFILES, objectName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错,bucket:{},objectName:{},错误信息:{}", BUCKET_MEDIAFILES, objectName, e.getMessage());
        }
        return Result.ok(MINIO_PATH + BUCKET_MEDIAFILES + "/" + objectName);
    }

    public String getloaclFile(MultipartFile file) {
        //接收到文件了
        //创建一个临时文件
        File tempFile = null;
        try {
            tempFile = File.createTempFile("minio", ".temp");
            file.transferTo(tempFile);
        } catch (Exception e) {
            log.error("创建临时文件失败,e{}", e);
        }
        //文件路径
        return tempFile.getAbsolutePath();//获取本地文件目录
    }

    /**
     * 上传视频
     *
     * @param courseId     课程id
     * @param chapterIndex 章
     * @param sectionIndex 节
     * @param videoFile    视频
     * @return
     */
    @Override
    public Result uploadVideo(String courseId, int chapterIndex, int sectionIndex, MultipartFile videoFile) {
        String originalFilename = videoFile.getOriginalFilename();//视频名字
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        //得到mimeType
        String mimeType = getMimeType(extension);
        String localFilePath = getloaclFile(videoFile);
        String defaultFolderPath = getDefaultFolderPath();
        String fileMd5 = getFileMd5(new File(localFilePath));//获取文件的md5
        //视频文件上传的位置
        String objectName = courseId + "/" + chapterIndex + "/" + sectionIndex + "/" + defaultFolderPath + fileMd5 + extension;
        if (StrUtil.isEmpty(objectName)) {
            return Result.fail("上传失败");
        }
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(BUCKET_VIDEOFILES)//桶
                    .filename(localFilePath)//本地文件
                    .object(objectName)//对象名
                    .contentType(mimeType)//文件类型
                    .build();
            minioClient.uploadObject(uploadObjectArgs);//上传到mimio
            log.debug("上传文件到minio成功,bucket:{},objectName:{},错误信息:{}", BUCKET_VIDEOFILES, objectName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错,bucket:{},objectName:{},错误信息:{}", BUCKET_VIDEOFILES, objectName, e.getMessage());
        }
        return Result.ok(new VideoVo(MINIO_PATH + BUCKET_VIDEOFILES + "/" + objectName, originalFilename));
    }

    //根据扩展名获取mimeType
    public String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType,字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //获取文件的md5值
    private String getFileMd5(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            String fileMd5 = DigestUtils.md2Hex(inputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取文件默认存储目录路径 年/month/day
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/") + "/";
    }

    /**
     * 头像上传的文件目录
     *
     * @param id                用户id
     * @param defaultFolderPath 时间
     * @param fileMd5           文件md5
     * @param extension         后缀名
     * @return
     */
    private String getIconPath(String start, String id, String defaultFolderPath, String fileMd5, String extension) {
        if ("userIcon".equals(start)) {
            //上传头像文件
            return "icon/" + id + "/" + defaultFolderPath + fileMd5 + extension;
        } else if ("teachIcon".equals(start)) {
            //上传教师头像
            return "teach/" + id + "/" + defaultFolderPath + fileMd5 + extension;
        } else if ("courseIcon".equals(start)) {
            return "course/" + id + "/" + defaultFolderPath + fileMd5 + extension;
        }
        return "";
    }
}
