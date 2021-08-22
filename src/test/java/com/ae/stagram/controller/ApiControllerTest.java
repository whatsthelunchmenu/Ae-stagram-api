package com.ae.stagram.controller;

import static com.ae.stagram.ApidocumentUtils.getDocumentRequest;
import static com.ae.stagram.ApidocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ae.stagram.model.Gender;
import com.ae.stagram.model.Person;
import com.ae.stagram.service.ApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest({ApiController.class, ApiService.class})
public class ApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ApiService apiService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    void contextLoads() throws Exception {
        this.mockMvc.perform(get("/api/sample")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("api"));
    }

    @Test
    public void update() throws Exception {
        //given
        Person response = Person.builder()
            .id(1L)
            .firstName("준희")
            .lastName("이")
            .birthDate(LocalDate.of(1985, 2, 1))
            .gender(Gender.MALE)
            .hobby("신나게놀기")
            .build();

        given(apiService.insert(eq(1L),any(Person.class)))
            .willReturn(response);

        //when

        ResultActions result = this.mockMvc.perform(
            put("/api/{id}",1L)
                .content(objectMapper.writeValueAsString(response))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
//
//        System.out.println(result.getResponse().getContentAsString());
        //then
        result.andExpect(status().isOk())
            .andDo(document("persons-update", // (4)
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("id").description("아이디")
                ),
                requestFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("firstName").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("lastName").type(JsonFieldType.STRING).description("성"),
                    fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생년월일"),
                    fieldWithPath("gender").type(JsonFieldType.STRING).description("성별, `MALE`, `FEMALE`"),
                    fieldWithPath("hobby").type(JsonFieldType.STRING).description("취미").optional()
                ),
                responseFields(
//                    fieldWithPath("code").type(JsonFieldType.STRING).description("결과코드"),
//                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("firstName").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("lastName").type(JsonFieldType.STRING).description("성"),
//                    fieldWithPath("data.person.age").type(JsonFieldType.NUMBER).description("나이"),
                    fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생년월일"),
                    fieldWithPath("gender").type(JsonFieldType.STRING).description("성별, `MALE`, `FEMALE`"),
                    fieldWithPath("hobby").type(JsonFieldType.STRING).description("취미").optional()
                )
            ));
    }
}