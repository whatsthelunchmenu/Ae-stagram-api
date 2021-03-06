package com.ae.stagram.domain.feed.api;

import static com.ae.stagram.global.util.RestDocsUtils.getDocumentRequest;
import static com.ae.stagram.global.util.RestDocsUtils.getDocumentResponse;
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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.domain.feed.dto.FeedInfoDto;
import com.ae.stagram.domain.feed.dto.FeedRequestDto;
import com.ae.stagram.domain.feed.dto.FeedResponseDto;
import com.ae.stagram.domain.feed.service.FeedService;
import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.global.config.interceptor.AuthInterceptor;
import com.ae.stagram.global.util.pageable.PageNationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 기존에는 @SpringBootTest 사용하였지만 굳이 모든 리소스들을 초기화할 필요가 없기 때문에 MVC 테스트를 하기위해 @WebMvcTest을 사용.
 */
@ExtendWith(value = RestDocumentationExtension.class)
@WebMvcTest(FeedController.class)
class FeedControllerTest {

    @MockBean
    private FeedService feedService;

    @MockBean
    private AuthInterceptor authInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private MockMultipartFile mockMultipartFile;

    private UserDto userDto;

    @Value("${app.page-size}")
    private int pageSize;

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

        mockMultipartFile = new MockMultipartFile("images", "imagefile.png",
            "image/png", "<<png data>>".getBytes());
    }

    @Test
    public void createFeed_피드_추가() throws Exception {

        String content = "피드 본문 내용";
        FeedRequestDto feedRequestDto = FeedRequestDto.builder()
            .content(content)
            .images(Lists.newArrayList(mockMultipartFile))
            .build();

        willDoNothing().given(feedService).insertFeed(feedRequestDto, this.userDto);
        given(authInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);

        //when
        ResultActions result = this.mockMvc.perform(multipart("/feeds")
            .file(mockMultipartFile)
            .param("content", content)
            .requestAttr("firebaseUser", this.userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8"));

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
                    requestParameters(
                        parameterWithName("content").description(content)
                    ),
                    requestParts(
                        partWithName("images").description("이미지 파일 리스트")
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
        String content = "컨텐츠 업데이트 내용";
        Long updateFeedId = 1L;

        given(feedService.updateFeed(anyLong(), any(FeedRequestDto.class)))
            .willReturn(FeedInfoDto.builder()
                .id(1L)
                .content(content)
                .display_name("홍길동")
                .images(Lists.newArrayList("http://localhost/images/test.jpg",
                    "http://localhost/images/test.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        given(authInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);

        MockMultipartHttpServletRequestBuilder fileUpload = (MockMultipartHttpServletRequestBuilder) RestDocumentationRequestBuilders.fileUpload(
            "/feeds/{id}", updateFeedId).with(request -> {
            request.setMethod(HttpMethod.PATCH.name());
            return request;
        });

        //when
        ResultActions result = this.mockMvc.perform(fileUpload
            .file(mockMultipartFile)
            .param("content", content)
            .requestAttr("firebaseUser", this.userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8"));

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
                    requestParameters(
                        parameterWithName("content").description(content)
                    ),
                    requestParts(
                        partWithName("images").description("이미지 파일 리스트")
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
                        fieldWithPath("body.images").type(JsonFieldType.ARRAY)
                            .description("해당 피드의 이미지 목록"),
                        fieldWithPath("body.createdAt").type(JsonFieldType.STRING)
                            .description("피드 생성 시간"),
                        fieldWithPath("body.updatedAt").type(JsonFieldType.STRING)
                            .description("피드 수정 시간")
                    )
                )
            );
    }

    @Test
    public void getFeeds_피드_검색() throws Exception {
        Long cursorIndex = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();
        String nextToken = String.format("%d%s%s", cursorIndex, PageNationUtils.splitPageInfo,
            updatedAt);
        System.out.println(nextToken);

        given(feedService.getMainFeeds(nextToken)).willReturn(
            FeedResponseDto.builder()
                .feedInfos(Lists.newArrayList(FeedInfoDto.builder()
                    .id(1L)
                    .display_name("호돌맨")
                    .content("본문 내용")
                    .images(Lists.newArrayList("http://localhost/images/test.jpg",
                        "http://localhost/images/test.jpg"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()))
                .hasNextToken(
                    String.format("%d%s%s", 1L, PageNationUtils.splitPageInfo, LocalDateTime.now()))
                .maxResults(pageSize)
                .build()
        );

        given(authInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);

        //when
        ResultActions result = this.mockMvc.perform(get("/feeds")
            .requestAttr("firebaseUser", this.userDto)
            .header("Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
            .param("nextToken", nextToken));

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
                    requestParameters(
                        parameterWithName("nextToken").description("다음 피드 요청 토큰").optional()
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : 요청성공, `false` : 요청실패"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http 상태 코드"),
                        fieldWithPath("body.hasNextToken").type(JsonFieldType.STRING)
                            .description("다음 페이지 토큰"),
                        fieldWithPath("body.maxResults").type(JsonFieldType.NUMBER)
                            .description("최대 페이지 갯수"),
                        fieldWithPath("body.feedInfos.[].id").type(JsonFieldType.NUMBER)
                            .description("피드 아이디"),
                        fieldWithPath("body.feedInfos.[].display_name").type(JsonFieldType.STRING)
                            .description("유저 이름"),
                        fieldWithPath("body.feedInfos.[].content").type(JsonFieldType.STRING)
                            .description("피드 본문 내용"),
                        fieldWithPath("body.feedInfos.[].images").type(JsonFieldType.ARRAY)
                            .description("해당 피드의 이미지 목록"),
                        fieldWithPath("body.feedInfos.[].createdAt").type(JsonFieldType.STRING)
                            .description("피드 생성 시간"),
                        fieldWithPath("body.feedInfos.[].updatedAt").type(JsonFieldType.STRING)
                            .description("피드 수정 시간")
                    )
                )
            );
    }

    @Test
    public void deleteFeed_피드_삭제() throws Exception {

        willDoNothing().given(feedService).removeFeed(anyLong());
        given(authInterceptor.preHandle(any(), any(), any()))
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