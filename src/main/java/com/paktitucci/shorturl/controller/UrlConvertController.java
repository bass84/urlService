package com.paktitucci.shorturl.controller;

import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.service.UrlService;
import com.paktitucci.shorturl.dto.JsonResult;
import com.paktitucci.shorturl.dto.ConvertResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@Slf4j
public class UrlConvertController {

    private final UrlService urlService;

    @Autowired
    public UrlConvertController(UrlService urlService) {
        this.urlService = urlService;
    }


    /**
     * 사용자가 URL 변환 요청하는 것을 처리하는 Handler.
     * 1. Original URL이 들어오면 Shortening URL을 반환한다.
     * 2. 'http://localhost'로 시작하는 Shortening URL이 들어오면 Original URL을 반환한다.
     */
    @PostMapping("/convert-url")
    public JsonResult<ConvertResult> convertUrl(@RequestBody ShortUrlParam param) {
        log.info("url = [{}]", param.getUrl());

        ConvertResult convertResult = urlService.convertUrl(param);
        log.info("convertResult = [{}]", convertResult);

        return JsonResult.success(convertResult);
    }

    @ExceptionHandler(UrlShorteningException.class)
    public JsonResult<Boolean> handleUrlShorteningException(UrlShorteningException e) {
        JsonResult<Boolean> failResult = new JsonResult<>(Boolean.FALSE, e.getCode(), e.getMessage());

        return failResult;
    }
}
