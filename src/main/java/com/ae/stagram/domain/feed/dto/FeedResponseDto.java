package com.ae.stagram.domain.feed.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedResponseDto {

    private String hasNextToken;

    private int maxResults;

    private List<FeedInfoDto> feedInfoDtos;

}
