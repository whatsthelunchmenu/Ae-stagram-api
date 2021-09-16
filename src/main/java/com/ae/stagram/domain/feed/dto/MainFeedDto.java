package com.ae.stagram.domain.feed.dto;

import com.ae.stagram.domain.feed.domain.Image;
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

    private Long id;

    private String display_name;

    private String content;

    private List<Image> images;
}
