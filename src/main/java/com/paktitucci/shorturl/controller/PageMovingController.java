package com.paktitucci.shorturl.controller;


import com.paktitucci.shorturl.dto.JsonResult;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.service.UrlService;
import com.paktitucci.shorturl.util.UrlEncodingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Slf4j
public class PageMovingController {

    private final UrlService urlService;
    private final UrlEncodingUtil urlEncodingUtil;

    @Autowired
    public PageMovingController(UrlService urlService, UrlEncodingUtil urlEncodingUtil) {
        this.urlService = urlService;
        this.urlEncodingUtil = urlEncodingUtil;
    }

    /**
     * Shortening URL을 Original URL로 변환해서 해당 페이지로 리다이렉트 한다.(브라우저 URL 입력창에 입력했을 경우)
     */
    @GetMapping("/{shortUrl}")
    public RedirectView goOriginalUrl(@PathVariable("shortUrl") String shortUrl) {
        String originalUrl = urlService.getOriginalUrl(shortUrl);
        originalUrl = urlEncodingUtil.encodeRedirectUrl(originalUrl);
        log.info("encoded redirect url = [{}]", originalUrl);

        return new RedirectView(originalUrl);
    }

    /**
     * Shortening URL을 Original URL로 변환해서 해당 페이지로 리다이렉트 한다.(input 입력창에 입력했을 경우)
     */
    @GetMapping("/redirect-original-url")
    public RedirectView redirectOriginalUrl(@RequestParam String redirectUrl) {
        redirectUrl = urlEncodingUtil.encodeRedirectUrl(redirectUrl);
        log.info("encoded redirect url = [{}]", redirectUrl);

        return new RedirectView(redirectUrl);

    }

    @ExceptionHandler(UrlShorteningException.class)
    public JsonResult<Boolean> handleUrlShorteningException(UrlShorteningException e) {
        JsonResult<Boolean> failResult = new JsonResult<>(Boolean.FALSE, e.getCode(), e.getMessage());

        return failResult;
    }
}
