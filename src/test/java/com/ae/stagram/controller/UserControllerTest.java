package com.ae.stagram.controller;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.User;
import com.ae.stagram.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(value = {SpringExtension.class, MockitoExtension.class})
@SpringBootTest
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void joinUser_정상적인_로그인_요청() throws Exception {

        UserDto userDto = UserDto.builder()
            .uuid("12345")
            .displayName("test")
            .email("test@naver.com")
            .build();

        willDoNothing().given(userService).addUser(
            User.builder()
                .id(1L)
                .uuid("12345")
                .displayName("test")
                .email("test@naver.com")
                .build());

        //when
        ResultActions resultActions = this.mockMvc.perform(post("/user/login")
            .accept(MediaType.APPLICATION_JSON)
            .requestAttr("firebaseUser", userDto));

        //then
        resultActions.andExpect(status().isOk());
    }
}