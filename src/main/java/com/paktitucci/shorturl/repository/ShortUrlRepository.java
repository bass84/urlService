package com.paktitucci.shorturl.repository;

import com.paktitucci.shorturl.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    /**
     * Original URL의 hash 값으로 Shortening URL을 조회한다.
     */
    ShortUrlEntity getShortUrlEntityByOriginalUrlHash(String originalUrlHash);

    /**
     * sequence로 Shortening URL을 조회한다.
     */
    ShortUrlEntity getShortUrlEntityBySequence(long sequence);
}
