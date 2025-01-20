package org.styd.intproj.savorly.service;

import org.styd.intproj.savorly.config.AmazonS3Config;
import com.amazonaws.HttpMethod;
import com.amazonaws.Request;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.net.URL;

@Service
public class S3Service {


    @Autowired
    private AmazonS3 amazonS3;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    public String generateUrl(String filename, HttpMethod httpMethod) {
        Date expiration = new Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 10; // 10 分钟
        expiration.setTime(msec);

        // build request
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, filename)
                        .withMethod(httpMethod)
                        .withExpiration(expiration);

        // build URL
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

}
