package com.ae.stagram.service;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.User;
import com.ae.stagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void addUser(UserDto userDto) {
        userRepository.save(User.builder()
            .uuid(userDto.getUuid())
            .displayName(userDto.getDisplayName())
            .email(userDto.getEmail())
            .build());
    }
}
