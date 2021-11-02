package com.ae.stagram.global.util.s3;

import com.ae.stagram.web.dto.feed.FileUploadDto;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class S3UploaderUtils {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public FileUploadDto upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
            .orElseThrow(
                () -> new IllegalArgumentException("error: MultipartFile ->File convert fail"));

        return upload(uploadFile, dirName);
    }

    public void delete(String filePath) {
        amazonS3Client.deleteObject(bucket, filePath);
    }

    //S3로 파일 업로드 하기
    private FileUploadDto upload(File upLoadFile, String dirName) {
        // S3에 저장된 파일 이름
        String filePath = dirName + "/" + UUID.randomUUID() + upLoadFile.getName();
        // S3로 업로드
        String uploadImageUrl = putS3(upLoadFile, filePath);
        removeNewFile(upLoadFile);

        return FileUploadDto.builder()
            .fileFullPath(filePath)
            .fileUrl(uploadImageUrl)
            .build();
    }

    private String putS3(File upLoadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, upLoadFile).withCannedAcl(
            CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("file delete success");
            return;
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(
            System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            //FileOutputStream 데이터를 파일에 바이트 스트림으로 저장한다.
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
