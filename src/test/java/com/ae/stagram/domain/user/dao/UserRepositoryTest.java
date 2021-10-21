package com.ae.stagram.domain.user.dao;

import com.ae.stagram.domain.user.domain.User;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void 사용자_추가_테스트() {
        User user = User.builder()
            .uuid("123456")
            .email("test@naver.com")
            .displayName("test")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        User savedUser = userRepository.save(user);
        Assertions.assertThat(user.getUuid()).isEqualTo(savedUser.getUuid());
    }
}