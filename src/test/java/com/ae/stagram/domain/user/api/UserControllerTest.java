package com.ae.stagram.domain.user.api;

import static com.ae.stagram.global.util.RestDocsUtils.getDocumentRequest;
import static com.ae.stagram.global.util.RestDocsUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.domain.user.service.UserService;
import com.ae.stagram.global.config.interceptor.AuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(value = RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthInterceptor authInterceptor;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUpMockMvc(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders
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
        given(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
            .willReturn(true);

        //when
        ResultActions result = this.mockMvc.perform(post("/users/login")
            .requestAttr("firebaseUser", userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("user",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("FireBase Access 토큰")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN).description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("응답 내용")
                    )));
    }
}