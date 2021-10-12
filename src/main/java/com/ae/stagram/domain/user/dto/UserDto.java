package com.ae.stagram.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NonNull
    private String uuid;

    @NonNull
    private String email;

    @NonNull
    private String displayName;
}
