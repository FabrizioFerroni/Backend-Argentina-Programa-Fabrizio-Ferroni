package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.vm.Asset;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
@Service
public class S3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String putObject(MultipartFile file, String name) {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = String.format("%s.%s", name, ext);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);

            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Asset getImage(String controller, String key) {
        String url = controller + "/" + key;
        S3Object s3Object = amazonS3.getObject(bucketName, url);
        ObjectMetadata metadata = s3Object.getObjectMetadata();
        try {
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            return new Asset(bytes, metadata.getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImage(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    public String getUrlImg(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

    public InputStream downloadFile(String controller, String key) {
        String url = controller + "/" + key;
        S3Object object = amazonS3.getObject(bucketName, url);
        return object.getObjectContent();
    }
}
