package com.ae.stagram.dto;

import com.ae.stagram.entity.Image;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MainFeedDto {

    private String uuid;

    private String display_name;

    private String content;

    private List<Image> images;
}
