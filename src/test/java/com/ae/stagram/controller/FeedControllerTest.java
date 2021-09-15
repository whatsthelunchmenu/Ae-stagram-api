package com.ae.stagram.controller;

import static com.ae.stagram.util.RestDocsUtils.getDocumentRequest;
import static com.ae.stagram.util.RestDocsUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.dto.ImageDto;
import com.ae.stagram.dto.MainFeedDto;
import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.Image;
import com.ae.stagram.interceptor.AuthInterceptor;
import com.ae.stagram.service.FeedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(value = {RestDocumentationExtension.class, SpringExtension.class,
    MockitoExtension.class})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class FeedControllerTest {

    @MockBean
    private FeedService feedService;

    @MockBean
    private AuthInterceptor authInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    public void setUpMockMvc(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .build();

        userDto = UserDto.builder()
            .uuid("12345")
            .displayName("test")
            .email("test@naver.com")
            .build();
    }

    @Test
    public void createFeed_피드_추가() throws Exception {

        FeedDto feedDto = FeedDto.builder()
            .content("컨텐츠 내용")
            .images(Lists.newArrayList(
                ImageDto.builder().path("http://localhost/images/test.jpg").build()))
            .build();

        willDoNothing().given(feedService).insertFeed(feedDto, this.userDto);
        given(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
            .willReturn(true);

        //when
        ResultActions result = this.mockMvc.perform(post("/feeds")
            .requestAttr("firebaseUser", this.userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(objectMapper.writeValueAsString(feedDto)));

        //then
        result.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document("feed-create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("FireBase Access 토큰")
                    ),
                    requestFields(
                        fieldWithPath("id").type(JsonFieldType.NULL).description("피드 아이디"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 본문 내용"),
                        fieldWithPath("images.[].id").type(JsonFieldType.NULL)
                            .description("이미지 아이디"),
                        fieldWithPath("images.[].path").type(JsonFieldType.STRING)
                            .description("이미지 url")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http 상태 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("응답 내용")
                    )));
    }

    @Test
    public void putFeed_피드_업데이트() throws Exception {
        FeedDto feedDto = FeedDto.builder()
            .id(1L)
            .content("컨텐츠 내용")
            .images(Lists.newArrayList(
                ImageDto.builder()
                    .id(1L)
                    .path("http://localhost/images/test.jpg").build()))
            .build();

        given(feedService.updateFeed(anyLong(), any(FeedDto.class)))
            .willReturn(MainFeedDto.builder()
                .id(1L)
                .content("컨텐츠 내용")
                .display_name("홍길동")
                .images(Lists.newArrayList(Image.builder()
                    .id(1L)
                    .imagePath("http://localhost/images/test.jpg")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()))
                .build());
        given(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
            .willReturn(true);

        Long id = 1L;
        //when
        ResultActions result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.put("/feeds/{id}", id)
                .requestAttr("firebaseUser", this.userDto)
                .header("Authorization",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedDto)));

        //then
        result.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document("feed-update",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION).description(
                            "FireBase Access 토큰")
                    ),
                    pathParameters(
                        parameterWithName("id").description("업데이트 할 피드 아이디")
                    ),
                    requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 아이디"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 본문 내용"),
                        fieldWithPath("images.[].id").type(JsonFieldType.NUMBER)
                            .description("이미지 아이디"),
                        fieldWithPath("images.[].path").type(JsonFieldType.STRING)
                            .description("이미지 url")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http 상태 코드"),
                        fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("피드 아이디"),
                        fieldWithPath("body.display_name").type(JsonFieldType.STRING)
                            .description("유저 이름"),
                        fieldWithPath("body.content").type(JsonFieldType.STRING)
                            .description("피드 본문 내용"),
                        fieldWithPath("body.images.[].id").type(JsonFieldType.NUMBER)
                            .description("해당 피드의 이미지 아이디"),
                        fieldWithPath("body.images.[].imagePath").type(JsonFieldType.STRING)
                            .description("해당 피드의 이미지 경로"),
                        fieldWithPath("body.images.[].createdAt").type(JsonFieldType.STRING)
                            .description("이미지 생성 날짜 및 시간"),
                        fieldWithPath("body.images.[].updatedAt").type(JsonFieldType.STRING)
                            .description("최근 이미지 업데이트 된 날짜 및 시간")
                    )
                )
            );
    }

    @Test
    public void getFeeds_피드_검색() throws Exception {
        given(feedService.getMainFeeds()).willReturn(Lists.newArrayList(MainFeedDto.builder()
            .id(1L)
            .display_name("호돌맨")
            .content("본문 내용")
            .images(Lists.newArrayList(Image.builder()
                .id(1L)
                .imagePath("http://image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()))
            .build()));

        given(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
            .willReturn(true);

        //when
        ResultActions result = this.mockMvc.perform(get("/feeds")
            .requestAttr("firebaseUser", this.userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document("feed-read",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION).description(
                            "FireBase Access 토큰")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http 상태 코드"),
                        fieldWithPath("body.[].id").type(JsonFieldType.NUMBER)
                            .description("피드 아이디"),
                        fieldWithPath("body.[].display_name").type(JsonFieldType.STRING)
                            .description("유저 이름"),
                        fieldWithPath("body.[].content").type(JsonFieldType.STRING)
                            .description("피드 본문 내용"),
                        fieldWithPath("body.[].images.[].id").type(JsonFieldType.NUMBER)
                            .description("해당 피드의 이미지 아이디"),
                        fieldWithPath("body.[].images.[].imagePath").type(JsonFieldType.STRING)
                            .description("해당 피드의 이미지 경로"),
                        fieldWithPath("body.[].images.[].createdAt").type(JsonFieldType.STRING)
                            .description("이미지 생성 날짜 및 시간"),
                        fieldWithPath("body.[].images.[].updatedAt").type(JsonFieldType.STRING)
                            .description("최근 이미지 업데이트 된 날짜 및 시간")
                    )
                )
            );
    }

    @Test
    public void deleteFeed_피드_삭제() throws Exception {

        willDoNothing().given(feedService).removeFeed(anyLong());
        given(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
            .willReturn(true);
        Long id = 1L;
        //when
        ResultActions result = this.mockMvc
            .perform(RestDocumentationRequestBuilders.delete("/feeds/{id}", id)
                .requestAttr("firebaseUser", this.userDto)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document("feed-delete",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION).description(
                            "FireBase Access 토큰")
                    ),
                    pathParameters(
                        parameterWithName("id").description("삭제 할 피드 아이디")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http 상태 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("응답 내용")
                    )
                )
            );
    }

}