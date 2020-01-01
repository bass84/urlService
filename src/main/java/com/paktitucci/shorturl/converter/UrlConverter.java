package com.paktitucci.shorturl.converter;

import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.entity.ShortUrlEntity;

public interface UrlConverter {

    /**
     * URL을 변환하는 기능.
     */
    ShortUrlEntity convert(ShortUrlParam entity);
}
