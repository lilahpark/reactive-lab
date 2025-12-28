package com.reactive.section03.class02;

import reactor.core.publisher.Mono;

/**
 * Mono 기본 개념 예제
 *  원본 데이터의 emit 없이 complete signal 만 emit 한다.
 */
import static reactor.netty.http.HttpConnectionLiveness.log;

public class MonoExample02 {
    public static void main(String[] args) {
        Mono.empty()
                .subscribe(
                        //1.상위 업스트림인 모노 에서 0개 또는 1개 데이터를 에밋하면
                        //서브스크라브의 첫번째 파라미터 람다 표현식에서 데이터를 받아서 처리
                        data -> log.info("# emitted data: {}", data), //업스트림
                        error -> log.error("# error", error),
                        //2.에러가 발생하면 두 번째 람다 표현식 받아서 처리
                        () -> log.info("# emitted onComplete signal")
                        //3.상위 업스트림에서 모든 데이터를 에밋 하고 난 후
                        //컴플리트 시그널 전송
                );
    }
}
