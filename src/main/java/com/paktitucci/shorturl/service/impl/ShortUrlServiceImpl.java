package com.paktitucci.shorturl.service.impl;

import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.repository.ShortUrlRepository;
import com.paktitucci.shorturl.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    @Autowired
    public ShortUrlServiceImpl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }


    /**
     * 새 Shortening URL을 저장한다.
     */
    @Transactional
    @Override
    public ShortUrlEntity saveShortUrl(ShortUrlEntity entity) {
        return shortUrlRepository.saveAndFlush(entity);
    }


    /**
     * sequence를 이용하여 저장되어있는 Shortening URL을 조회한다.
     */
    @Override
    public ShortUrlEntity getShortUrlEntityBySequence(long sequence) {
        ShortUrlEntity shortUrlEntity = shortUrlRepository.getShortUrlEntityBySequence(sequence);

        return shortUrlEntity;
    }
}
