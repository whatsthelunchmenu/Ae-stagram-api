package com.ae.stagram.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.ae.stagram.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void 사용자_추가_테스트(){
        User user = User.builder()
            .uuid("123456")
            .email("test@naver.com")
            .displayName("test")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        userRepository.save(user);
    }
}