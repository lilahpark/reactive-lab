package com.reactive.section03.class02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * 여러개의 Flux를 연결해서 하나의 Flux로 결합하는 예제
 */
public class FluxExample04 {
    static final Logger log = LoggerFactory.getLogger(FluxExample04.class);

    public static void main(String[] args) {
        Flux.concat(
                        Flux.just("Venus"),
                        Flux.just("Earth"),
                        Flux.just("Mars"))
                .collectList()
                .subscribe(planetList -> log.info("# Solar System: {}", planetList));
    }
}
