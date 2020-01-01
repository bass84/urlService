package com.paktitucci.shorturl.service;

import com.paktitucci.shorturl.entity.ShortUrlEntity;

public interface UrlCheckService {

    /**
     * Original URL Hash 값으로 Shortening URL을 조회한다.
     */
    ShortUrlEntity getShortUrlIfExists(String originalUrlHash);

}
