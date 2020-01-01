package com.paktitucci.shorturl.service;

import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.dto.ConvertResult;

public interface UrlService {

    /**
     * URL을 변환한다.
     */
    ConvertResult convertUrl(ShortUrlParam originalUrl);

    /**
     * Original URL을 조회한다.
     */
    String getOriginalUrl(String shortUrl);
}
