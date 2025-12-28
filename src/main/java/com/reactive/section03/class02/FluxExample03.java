package com.reactive.section03.class02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 2개의 Mono를 연결해서 Flux로 변환하는 예제
 * 이 코드는
 * “null은 흘리지 말고, 값이 있으면 순서대로 이어서 emit하라”
 * 라는 의도를 가진 Flux 예제다.
 */
public class FluxExample03 {
    static final Logger log = LoggerFactory.getLogger(FluxExample03.class);

    public static void main(String[] args) {

        //자바 제네릭 타입 추론 결과 담기
        Flux<Object> flux =
                // 1) Mono.justOrEmpty(null)
                // - 값이 null이면: Mono.empty() 반환
                // - 값이 있으면: Mono.just(value) 반환
                // - 즉, null-safe Mono 생성 연산자
                Mono.justOrEmpty(null)

                        // 2) Mono.justOrEmpty("Jobs")
                        // - null이 아니므로 Mono.just("Jobs") 생성
                        // 3) concatWith(...)
                        // - 앞 Publisher가 완료(onComplete)된 후
                        // - 뒤 Publisher를 이어서 구독
                        // - 순서를 보장하는 연결 연산자
                        .concatWith(Mono.justOrEmpty("Jobs"));
        // 4) subscribe(...)
        // - 구독 시점에 실제 실행 시작
        // - onNext 시그널을 받아 로그 출력
        flux.subscribe(data -> log.info("# result: {}", data));
    }

}
