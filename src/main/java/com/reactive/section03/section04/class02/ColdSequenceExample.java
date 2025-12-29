package com.reactive.section03.section04.class02;

import com.reactive.section03.class02.FluxExample04;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Arrays;

/**
 * Cold Sequence 예제
 *
 * - Cold Publisher는 Subscriber마다 처음부터 다시 시작
 * - 같은 Flux를 여러 번 subscribe 해도
 *   각 Subscriber는 독립적으로 전체 데이터를 받음
 */
public class ColdSequenceExample {

    static final Logger log = LoggerFactory.getLogger(ColdSequenceExample.class);


    public static void main(String[] args) {
        // RED, YELLOW, PINK 데이터를 가진 Cold Flux
        // subscribe 할 때마다 처음부터 데이터 방출
        Flux<String> coldFlux = Flux.fromIterable(Arrays.asList("RED", "YELLOW", "PINK"))
                .map(String::toLowerCase);

        // 첫 번째 Subscriber
        // red → yellow → pink 순서로 모두 수신
        coldFlux.subscribe(
                country -> log.info("# Subscriber1: {}", country)
        );
        log.info("-------------------------");
        // 두 번째 Subscriber
        // 첫 번째와 상관없이 다시 처음부터 수신
        coldFlux.subscribe(
                country -> log.info("# Subscriber2: {}", country)
        );
    }
}
