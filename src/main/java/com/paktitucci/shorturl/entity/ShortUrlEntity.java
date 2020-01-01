package com.paktitucci.shorturl.entity;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="short_url")
@SequenceGenerator(
        name="SHORT_URL_SEQ_GEN",
        sequenceName="SHORT_URL_SEQ",
        initialValue=1,
        allocationSize=1
)
public class ShortUrlEntity {

    @Id
    @Column(name="sequence")
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE,
            generator="SHORT_URL_SEQ_GEN"
    )
    private Long sequence;

    @Column(name="original_url")
    private String originalUrl;

    @Column(name="original_url_hash")
    private String originalUrlHash;

    @Column(name="registration_date_time")
    @CreationTimestamp
    private LocalDateTime registrationDateTime;

    @Transient
    private String shortUrl;
    @Transient
    private boolean isGeneratedNewShortUrl;


    @Builder
    public ShortUrlEntity(Long sequence, String originalUrl, String originalUrlHash, String shortUrl,
                          boolean isGeneratedNewShortUrl) {
        this.sequence = sequence;
        this.originalUrl = originalUrl;
        this.originalUrlHash = originalUrlHash;
        this.shortUrl = shortUrl;
        this.isGeneratedNewShortUrl = isGeneratedNewShortUrl;
    }

}
