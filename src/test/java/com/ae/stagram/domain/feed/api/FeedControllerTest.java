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
 * ???????????? @SpringBootTest ?????????????????? ?????? ?????? ??????????????? ???????????? ????????? ?????? ????????? MVC ???????????? ???????????? @WebMvcTest??? ??????.
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
    public void createFeed_??????_??????() throws Exception {

        String content = "?????? ?????? ??????";
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
                        headerWithName(HttpHeaders.AUTHORIZATION).description("FireBase Access ??????")
                    ),
                    requestParameters(
                        parameterWithName("content").description(content)
                    ),
                    requestParts(
                        partWithName("images").description("????????? ?????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : ????????????, `false` : ????????????"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("?????????"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http ?????? ??????"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("?????? ??????")
                    )));
    }

    @Test
    public void putFeed_??????_????????????() throws Exception {
        String content = "????????? ???????????? ??????";
        Long updateFeedId = 1L;

        given(feedService.updateFeed(anyLong(), any(FeedRequestDto.class)))
            .willReturn(FeedInfoDto.builder()
                .id(1L)
                .content(content)
                .display_name("?????????")
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
                            "FireBase Access ??????")
                    ),
                    pathParameters(
                        parameterWithName("id").description("???????????? ??? ?????? ?????????")
                    ),
                    requestParameters(
                        parameterWithName("content").description(content)
                    ),
                    requestParts(
                        partWithName("images").description("????????? ?????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : ????????????, `false` : ????????????"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("?????????"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http ?????? ??????"),
                        fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                        fieldWithPath("body.display_name").type(JsonFieldType.STRING)
                            .description("?????? ??????"),
                        fieldWithPath("body.content").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("body.images").type(JsonFieldType.ARRAY)
                            .description("?????? ????????? ????????? ??????"),
                        fieldWithPath("body.createdAt").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("body.updatedAt").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????")
                    )
                )
            );
    }

    @Test
    public void getFeeds_??????_??????() throws Exception {
        Long cursorIndex = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();
        String nextToken = String.format("%d%s%s", cursorIndex, PageNationUtils.splitPageInfo,
            updatedAt);
        System.out.println(nextToken);

        given(feedService.getMainFeeds(nextToken)).willReturn(
            FeedResponseDto.builder()
                .feedInfos(Lists.newArrayList(FeedInfoDto.builder()
                    .id(1L)
                    .display_name("?????????")
                    .content("?????? ??????")
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
                            "FireBase Access ??????")
                    ),
                    requestParameters(
                        parameterWithName("nextToken").description("?????? ?????? ?????? ??????").optional()
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : ????????????, `false` : ????????????"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("?????????"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http ?????? ??????"),
                        fieldWithPath("body.hasNextToken").type(JsonFieldType.STRING)
                            .description("?????? ????????? ??????"),
                        fieldWithPath("body.maxResults").type(JsonFieldType.NUMBER)
                            .description("?????? ????????? ??????"),
                        fieldWithPath("body.feedInfos.[].id").type(JsonFieldType.NUMBER)
                            .description("?????? ?????????"),
                        fieldWithPath("body.feedInfos.[].display_name").type(JsonFieldType.STRING)
                            .description("?????? ??????"),
                        fieldWithPath("body.feedInfos.[].content").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("body.feedInfos.[].images").type(JsonFieldType.ARRAY)
                            .description("?????? ????????? ????????? ??????"),
                        fieldWithPath("body.feedInfos.[].createdAt").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("body.feedInfos.[].updatedAt").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????")
                    )
                )
            );
    }

    @Test
    public void deleteFeed_??????_??????() throws Exception {

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
                            "FireBase Access ??????")
                    ),
                    pathParameters(
                        parameterWithName("id").description("?????? ??? ?????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("header.result").type(JsonFieldType.BOOLEAN)
                            .description("`true` : ????????????, `false` : ????????????"),
                        fieldWithPath("header.message").type(JsonFieldType.STRING)
                            .description("?????????"),
                        fieldWithPath("header.status").type(JsonFieldType.NUMBER)
                            .description("Http ?????? ??????"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("?????? ??????")
                    )
                )
            );
    }
}