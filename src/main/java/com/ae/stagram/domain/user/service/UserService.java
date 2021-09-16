package com.ae.stagram.domain.user.service;

import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.domain.user.domain.User;
import com.ae.stagram.domain.user.dao.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void addUser(UserDto userDto) {

        userRepository.findByUuid(userDto.getUuid())
            .orElseGet(() -> userRepository.save(User.builder()
                .uuid(userDto.getUuid())
                .displayName(userDto.getDisplayName())
                .email(userDto.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));
    }
}
