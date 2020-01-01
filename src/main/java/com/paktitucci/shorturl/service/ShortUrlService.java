package com.paktitucci.shorturl.service;

import com.paktitucci.shorturl.entity.ShortUrlEntity;

public interface ShortUrlService {

    /**
     * 새 Shortening URL을 저장한다.
     */
    ShortUrlEntity saveShortUrl(ShortUrlEntity shortUrlEntity);

    /**
     * sequence를 이용하여 저장되어있는 Shortening URL을 조회한다.
     */
    ShortUrlEntity getShortUrlEntityBySequence(long sequence);
}
