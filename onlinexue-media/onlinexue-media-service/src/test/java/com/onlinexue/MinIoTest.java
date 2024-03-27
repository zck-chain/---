package com.onlinexue;

import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class MinIoTest {
    private static MinioClient minioClient;

    static {
        minioClient = MinioClient.builder()
                .endpoint("http://192.168.239.128:9000/")
                .credentials("WAI75M3XNAEOS7MHMBJU", "rUcbZvVToi8ATaKVyFGyTohnqIlwMebDWHsy15hn")
                .build();
        System.out.println("客户端连接成功minio:" + minioClient);
    }

    @Test
    void testInsert() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //根据扩展名得到媒体资源类型mimeType
        Bucket bucket = minioClient.listBuckets().get(0);
        System.out.println(bucket.name());
//        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".png");
//        String mineType="";
//        if (extensionMatch!=null) {
//            mineType = extensionMatch.getMimeType();
//        }
//        UploadObjectArgs uploadObjectArgs= UploadObjectArgs.builder()
//                .bucket("test")
//                .filename("C:\\Users\\Administrator\\Desktop\\mylogo.png")//指定本地文件地址
//                .object("test/icon/mr2.jpg")
//                .contentType(mineType)
//                .build();
//        minioClient.uploadObject(uploadObjectArgs);
    }
}
