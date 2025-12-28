package com.reactive.section03.class02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * Flux 에서의 Operator 체인 사용 예제
 */
public class FluxExample02 {
    static final Logger log = LoggerFactory.getLogger(FluxExample02.class);

    public static void main(String[] args) {
        Flux.fromArray(new Integer[]{3, 6, 7, 9})
                .filter(num -> num > 6)
                .map(num -> num * 2)
                .subscribe(multiply -> log.info("# multiply: {}", multiply));
    }
}
