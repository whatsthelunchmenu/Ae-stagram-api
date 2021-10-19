package com.ae.stagram.global.util.s3.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class FileUploadDto {

    private String fileFullPath;
    private String fileUrl;

    @Builder
    public FileUploadDto(String fileFullPath, String fileUrl) {
        this.fileFullPath = fileFullPath;
        this.fileUrl = fileUrl;
    }
}
