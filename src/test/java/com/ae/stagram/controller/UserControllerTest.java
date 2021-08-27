package com.ae.stagram.controller;

import static com.ae.stagram.util.RestDocsUtils.getDocumentRequest;
import static com.ae.stagram.util.RestDocsUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.dto.response.ResponseMessage;
import com.ae.stagram.dto.response.ResponseMessageHeader;
import com.ae.stagram.entity.User;
import com.ae.stagram.interceptor.AuthInterceptor;
import com.ae.stagram.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@ExtendWith(value = {RestDocumentationExtension.class, SpringExtension.class,
    MockitoExtension.class})
//@WebMvcTest(value = {UserController.class, UserService.class})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @MockBean
    private WebContentInterceptor authInterceptor;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUpMockMvc(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentationContextProvider) {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        this.mockMvc = MockMvcBuilders
//            .standaloneSetup(userController)
            .webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .build();
    }

    @Test
    public void joinUser_정상적인_로그인_요청() throws Exception {

        UserDto userDto = UserDto.builder()
            .uuid("12345")
            .displayName("test")
            .email("test@naver.com")
            .build();

        willDoNothing().given(userService).addUser(userDto);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setParameter("/user/login");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        given(authInterceptor.preHandle(request,response)).willReturn(true);
//        given(userController.login(any(UserDto.class)))
//            .willReturn(ResponseEntity.ok().body(responseMessage));

        //when
        ResultActions result = this.mockMvc.perform(post("/user/login")
            .requestAttr("firebaseUser", userDto)
//            .content(objectMapper.writeValueAsString(userDto))
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        //then
//        result.andExpect(status().isOk())
//            .andDo(print());
        result.andExpect(status().isOk())
            .andDo(
                document("user",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                      headerWithName(HttpHeaders.AUTHORIZATION).description("FireBase 토큰")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN).description("result"),
                        fieldWithPath("header.resultCode").type(JsonFieldType.STRING).description("resultCode"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING).description("message"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER).description("status"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("body")
                    )));
    }
}