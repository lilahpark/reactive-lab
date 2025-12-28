package com.reactive.section03.class02;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mono 활용 예제
 *  worldtimeapi.org Open API 를 이용해서 서울의 현재 시간을 조회한다.
 */
public class MonoExample03 {
    static final Logger log = LoggerFactory.getLogger(MonoExample03.class);


    public static void main(String[] args) {



        URI worldTimeUri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("worldtimeapi.org")
                .path("/api/timezone/Asia/Seoul")
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //모노는 http 리퀘스트를 처리하기 가장 좋은 publusher 이다
        //전달 받은 월드타입의 리스펀스가 모노의 데이터 소스가 됨
        // ** just는 이미 나온 결과를 담는 용도,
        //    fromCallable은 실행 자체를 지연시키는 용도
        Mono.fromCallable(() ->
                        //exchange 메서드로 worldTimeUri 전송
                        restTemplate.exchange(worldTimeUri, HttpMethod.GET, new HttpEntity<>(headers), String.class)
                )
                .subscribeOn(Schedulers.boundedElastic()) // RestTemplate은 blocking이니까 여기로
                .map(response -> {
                    DocumentContext jsonContext = JsonPath.parse(response.getBody());
                    return jsonContext.read("$.datetime", String.class);
                })
                .doOnSubscribe(s -> log.info("calling: {}", worldTimeUri))
                //map 에서 반환된 데이터는 최종 적으로서브크라이브로 전달
                .subscribe(
                        data -> log.info("# emitted data: {}", data),
                        error -> log.error("# error", error),
                        () -> log.info("# emitted onComplete signal")
                );
    }
}
