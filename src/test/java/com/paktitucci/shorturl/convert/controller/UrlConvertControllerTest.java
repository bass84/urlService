package com.paktitucci.shorturl.convert.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.paktitucci.shorturl.ShorteningUrlApplication;
import com.paktitucci.shorturl.dto.ShortUrlParam;

import com.paktitucci.shorturl.dto.JsonResult;
import com.paktitucci.shorturl.util.UrlType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShorteningUrlApplication.class)
@WebAppConfiguration
public class UrlConvertControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;
    private ObjectMapper objectMapper;


    @Before
    public void init() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 단축 URL이 이미 존재하는 URL에 대한 테스트
     */
    @Test
    public void existsUrlTest() throws Exception{

        ShortUrlParam param = ShortUrlParam.of("https://www.naver.com");

        String inputJson = objectMapper.writeValueAsString(param);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                                 .post("/convert-url")
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson))
                                 .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonResult<Map<String, String>> result = objectMapper.readValue(content, JsonResult.class);
        assertThat(result.getCode(), is(0));
        assertThat(result.getMessage(), is("success"));

        Map<String, String> body = result.getBody();
        assertThat(body.get("url"), is("http://localhost/emkB"));
        assertThat(body.get("urlType"), is(UrlType.ORIGINAL_URL.name()));
        assertThat(body.get("newUrl"), is(Boolean.FALSE));
    }


    /**
     * 새 단축 URL 생성 테스트
     */
    @Test
    @Transactional
    public void newShortUrlGenerateTest() throws Exception{

        ShortUrlParam param = ShortUrlParam.of("https://dict.naver.com//");

        String inputJson = objectMapper.writeValueAsString(param);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                                 .post("/convert-url")
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson))
                                 .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonResult<Map<String, String>> result = objectMapper.readValue(content, JsonResult.class);
        assertThat(result.getCode(), is(0));
        assertThat(result.getMessage(), is("success"));

        Map<String, String> body = result.getBody();
        assertThat(body.get("urlType"), is(UrlType.ORIGINAL_URL.name()));
        assertThat(body.get("newUrl"), is(Boolean.TRUE));
    }



    /**
     * 단축 URL을 원래 URL로 가져오는 테스트
     */
    @Test
    public void shortUrlToOriginalUrlTest() throws Exception{

        ShortUrlParam param = ShortUrlParam.of("http://localhost/emkB");

        String inputJson = objectMapper.writeValueAsString(param);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                                 .post("/convert-url")
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson))
                                 .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonResult<Map<String, String>> result = objectMapper.readValue(content, JsonResult.class);
        assertThat(result.getCode(), is(0));
        assertThat(result.getMessage(), is("success"));

        Map<String, String> body = result.getBody();
        assertThat(body.get("url"), is("https://www.naver.com"));
        assertThat(body.get("urlType"), is(UrlType.SHORT_URL.name()));
        assertThat(body.get("newUrl"), is(Boolean.FALSE));
    }


    /**
     * input 창에 단축URL 넣었을 경우 리다이렉트 테스트
     */
    @Test
    public void redirectUrlTest() throws Exception{
        mvc.perform(get("/redirect-original-url")
           .contentType(MediaType.APPLICATION_JSON_UTF8)
           .param("redirectUrl", "https://www.naver.com"))
           .andExpect(status().is3xxRedirection())
           .andExpect(header().string("Location", "https://www.naver.com"))
           .andDo(MockMvcResultHandlers.print());
    }

    /**
     * input 창에 잘못된 유형의 단축URL 넣어서 실패하는 테스트
     */
    @Test(expected = AssertionError.class)
    public void redirectUrlFailTest() throws Exception{
        mvc.perform(get("/redirect-original-url")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("redirectUrl", "//www.johnpatitucci.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://www.johnpatitucci.com"))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 브라우저에 단축URL 넣어 요청했을 경우 리다이렉트 테스트
     */
    @Test
    public void pathVariableRedirectUrlTest() throws Exception{
        mvc.perform(get("/emkB")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://www.naver.com"))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 브라우저에 없는 단축URL 넣어서 실패하는 테스트
     */
    @Test(expected = AssertionError.class)
    public void pathVariableRedirectUrlFailTest() throws Exception{
        mvc.perform(get("/aa")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://www.naver.com"))
                .andDo(MockMvcResultHandlers.print());
    }




}
