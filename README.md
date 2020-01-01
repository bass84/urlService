# 단축 URL 생성 프로젝트 
URL을 입력받아 줄여주고, 단축URL을 입력하면 원래 URL 로 리다이렉트하는 URL Shortening Service  
예) https://recruit.navercorp.com/naver/job/detail/developer/?annoId=20002705&classId&jobId&fbclid=IwAR3sVYHouw4HfYVW2161BdBwtW4r5Cyd7u_Ho_DiOy9YtUVG_hRoy7k88uI => https://naver.com/AZa0QRI1

## 1. 개발 내용
* 단축URL Key 는 8 Character 이내로 생성되도록 하였다.
* 동일한 URL 에 대한 요청은 동일한 단축URL Key 로 응답한다.
* 단축URL 을 요청받으면 원래 URL 로 리다이렉트한다.
* 단축URL Key 생성 알고리즘은 직접 구현하였다.


## 2. 개발 환경

* FRONT-END
    * HTML
    * JQuery
    * Javascript
    * BootStrap

* BACK-END
    * Java 8
    * Spring Boot 2.1.9
    * Gradle
    * H2 DB

## 3. Build
` gradle build`

## 4. Execute
* Build 된 jar 파일 실행
    * `java -jar build/libs/shorturl-1.0.0.jar` 

* 직접 실행
    * `./gradlew bootRun`

* Page URL
    * `http://localhost`

---

## 5. 개발에 대한 고민과 해결 방안

#### 5.1. Shortening URL 생성 알고리즘은 어떻게 만들 것인가?
62진법을 이용한 base62 방식으로 처리하였다.  
사용자가 Shortening URL 생성을 요청하면, DB로부터 자동 증가한 값(sequence를 이용함)을 받아서 62진법으로 계산하여 변환한다.  
```
예를 들어 10000 이라는 값을 변환한다면,

1. 100000 divide 62 -> 몫 : 161, 나머지 : 17
2. 161 divide 62 -> 몫 : 2, 나머지 : 37
3. 2 divide 62 -> 몫 : 0, 나머지 : 2

위의 계산을 이용해 다시 계산한다면,
(2 * 62^2) + (37 * 62^1) + (17 * 62 ^ 0) = 10000
이 된다.

여기에서 나머지 값을 3번 부터 역순으로 [2, 37, 17] 배열에 넣는다. 
```
`String base62 = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"`  
배열 [2, 37, 17] 값의 순서가 위에 정의한 base62의 index가 되어 변환한다. 즉,
``` 
base62.indexOf(2);
base62.indexOf(37);
base62.indexOf(17);
```
`cBr`이 최종값이 된다. 그리고 이 방식을 역으로 계산하여 얻은 sequence 값을 DB에서 조회하여 Original URL 을 찾는다.





#### 5.2. 어떻게 구현할 것인가?
구현해야 할 사항은 크게 두 가지로 볼 수 있다.
```
1. Original URL 입력 -> Shortening URL로 변환하여 반환
2. Shortening URL 입력 -> Original URL로 변환하여 반환 (리다이렉트)
```
2번에서는 리다이렉트라는 추가 기능이 있지만, 결국 둘 다 변환(converting)하는 기능이 핵심이다.  
그래서 URL 변환기인 UrlConverter 인터페이스를 만들고 ShortUrlConverter 와 OriginalUrlConverter 로 구현하였다.   
- 사용자의 입력 URL 이 Original URL 인 경우 UrlType.ORIGINAL_URL 값으로 설정하여 Shortening URL로 변환하는 ShortUrlConverter 를 이용하고,    
- 사용자의 입력 URL 이 Shortening URL 인 경우 UrlType.SHORT_URL 값으로 설정하여 OriginalUrlConverter 를 이용하도록 처리하였다.





#### 5.3. 이미 저장되어있는 Original URL 은 어떻게 가져올 것인가? 
Original URL이 unique한 값이긴 하지만, DB에서 WHERE 조건으로 사용하기에는 몇 가지 고려 사항이 있다.
```
1. URL 길이가 매우 긴 경우가 있다. 
2. 저장 건 수가 수 십억 건 이상으로 많아질 수 있다.
3. index 생성할 수 있는 길이의 제한이 있다. 또한 길이가 긴 만큼 비용도 많이 든다.
```    
위와 같 이유로, SHA-1 암호화로 Original URL 을 hashing 한 값을 컬럼으로 추가하여 Unique Index를 걸었고,  
사용자로부터 받은 Original URL 은 매번 SHA-1 암호화 과정을 거치게 된다.    
SHA-1 암호화한 값이 Original URL보다 긴 경우도 있지만, 매우 긴 URL 값에 대해서도 같은 크기로(40 bytes) 암호화가 되기 때문에  
사용자가 어떤 유형의 URL을 입력할 지 예상할 수 없는 것에 대한 대응 방안이었다.  
참고로, 12,000 byte 정도 길이의 문자열을 SHA-1 암호화했을 때에도 40 bytes가 나왔다. (Test case로 테스트함)






#### 5.4. 저장은 어떻게 할 것인가?
데이터베이스에 저장하는 방식을 선택했는데 데이터베이스는 H2 Database 를 이용하였다.  
* Shortening URL은 고유해야 하기 때문에 Shortening URL 의 재료가 되는 숫자형 값은 DB 에서 생성하는 자동 증가 하는 값을 이용하였다.(sequence 이용)  
* base62를 이용할 경우 만들 수 있는 1 ~ 8 자리의 값은 총 62^8 = 218,340,105,584,896 개이다.
* Shortening URL이 1자리인 것은 미관상 좋지 못하다고 생각하여 4자리 수 이상의 Shortening URL 을 만들기로 결정하고, sequence 값은 임의적으로 1,000,000 을 시작 값으로 설정하였다.
요구 조건에서 개수의 제한이 없었고, 최대 218조 개 이상의 값을 생성할 수 있기 때문에 100만 개는 감소는 미미한 것으로 보여진다.
* Original URL 값을 SHA-1 으로 암호화한 값을 담기 위한 컬럼을 추가하였고, Unique Index 를 걸었다.
* Original URL의 크기는 VARCHAR(4000)으로 제한하였다.
* 위 내용을 바탕으로 시퀀스, 테이블, 인덱스는 아래와 같이 생성하였다. 
```
시퀀스 생성
CREATE SEQUENCE SHORT_URL_SEQ START WITH 1000000 MAXVALUE 218340105584895;

테이블 생성
CREATE TABLE SHORT_URL (
	SEQUENCE BIGINT DEFAULT SHORT_URL_SEQ.NEXTVAL PRIMARY KEY,
	ORIGINAL_URL VARCHAR(4000) NOT NULL UNIQUE,
	ORIGINAL_URL_HASH VARCHAR(50) NOT NULL UNIQUE,
	REGISTRATION_DATE_TIME TIMESTAMP
);

인덱스 생성
CREATE INDEX ORIGINAL_URL_UNIQUE ON SHORT_URL(ORIGINAL_URL_HASH);
```



#### 5.5. 이미 생성되어 있는 Shortening URL 을 브라우저 URL 입력창이 아니라 사용자 입력창(input tag)에 넣을 수 있다.
사용자가 이미 생성되어 있는 Shortening URL 에 대해 다시 Shortening URL을 생성하려고 할 경우엔, Original URL 로 리다이렉트 하도록 처리하였다.
즉, 요구사항에 있던 Shortening URL을 브라우저 URL 입력창에 입력하였을 때 리다이렉트 하는 것과 동일하게 처리한 것이다.


      

#### 5.6. 그 외
* 존재하지 않는 Shortening URL을 입력할 경우엔 존재하지 않음을 사용자에게 알린다.
* URL 중에는 한글 URL도 있을 것이다. 이것은 리다이렉트할 때 문제가 되는데, 한글 인코딩 처리는 UrlEncodingUtil 가 한다.
* http:// 또는 https:// 형식의 URL을 받도록 하였다.
* 같은 URL이지만 끝에 slash(/)를 붙여서 Shortening URL이 두 개 생성되게 하는 것은 리소스 낭비이기 때문에 UrlCorrector가 끝 slash를 지운다.  
예) `https://www.naver.com` 또는 `https://www.naver.com/`를 입력하면 `https://www.naver.com`로 처리한다.


## 6. API 명세

| 내용 | URL | METHOD | PARAMETER | RESPONSE |
|:---:|:---:|:---:|:---:|:---|
| 메인 페이지 | / | GET |N/A | 200 OK |
| 단축 URL 생성 | /convert-url | POST |Type : String, <br /> 필수 : Y, <br /> name : url | { <br/> "code":0, <br/> "message":"success", <br/> "body": <br/> { <br/> "url":"http://localhost/emkB", <br/> "urlType":"ORIGINAL_URL", <br/> "newUrl":true <br/> } <br/> } |
| Original URL로 리다이렉트 <br /> (브라우저 창에 입력했을 경우) | /{shortUrl} | GET | Type: String, <br />  필수 : Y, <br /> pathVariable : shortUrl | 302 FOUND |
| Original URL로 리다이렉트 <br /> (input 창에 입력했을 경우) | /redirect-original-url | GET | Type : String, <br /> 필수 : Y, <br /> name : redirectUrl | 302 FOUND |
