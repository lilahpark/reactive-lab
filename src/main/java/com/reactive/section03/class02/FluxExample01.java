package com.reactive.section03.class02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;


public class FluxExample01 {
    static final Logger log = LoggerFactory.getLogger(FluxExample01.class);


    public static void main(String[] args) {
        //Flux.just는 동기(synchronous) Publisher
        Flux.just(6, 9, 13)
//                .map(num -> num % 2)
//                .subscribe(remainder -> log.info("# remainder: {}", remainder));
                .doOnNext(v -> log.info("before map: {}", v))
                .map(v -> v % 2)
                .doOnNext(v -> log.info("after map: {}", v))
                .subscribe();
    }
}
