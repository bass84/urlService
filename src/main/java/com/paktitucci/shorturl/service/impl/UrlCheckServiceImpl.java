package com.paktitucci.shorturl.service.impl;

import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.repository.ShortUrlRepository;
import com.paktitucci.shorturl.service.UrlCheckService;
import org.springframework.stereotype.Service;

@Service
public class UrlCheckServiceImpl implements UrlCheckService {

    private final ShortUrlRepository shortUrlRepository;

    public UrlCheckServiceImpl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    /**
     * Original URL Hash 값으로 Shortening URL을 조회한다.
     */
    @Override
    public ShortUrlEntity getShortUrlIfExists(String originalUrlHash) {
        ShortUrlEntity shortUrlEntity = shortUrlRepository.getShortUrlEntityByOriginalUrlHash(originalUrlHash);

        return shortUrlEntity;
    }
}
