package com.reactive.section05.class01;

import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * Subscriber가 처리 가능한 만큼의 request 개수를 조절하는 Backpressure 예제
 */
public class BackpressureExample01 {

    //코드 구조도
    //코드는 '데이터가 흐르는 길'을 기준으로 짠다.
    // Flux.range(1, 5)         // [3] 최상류 (근원지)
    //    .doOnNext(...)        // [2] 데이터 내려올 때 구경함 (Downstream 관여)
    //    .doOnRequest(...)     // [1] 요청 올라갈 때 구경함 (Upstream 관여)
    //    .subscribe(...)       // [0] 최하류 (구독자)

    public static void main(String[] args) {
        // [Publisher 영역]
        Flux.range(1, 5) // 1부터 5까지 데이터를 발행할 준비
                .doOnNext(Logger::doOnNext)       // 3. Publisher가 데이터를 내려보낼 때 로그 찍힘 (Emitting)
                .doOnRequest(Logger::doOnRequest) // 2. Subscriber가 데이터 달라고 조를 때 로그 찍힘 (Request)

                // [Subscriber 영역]
                .subscribe(new BaseSubscriber<Integer>() {

                    // 구독이 시작되는 시점 (제일 먼저 실행됨)
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        // 1. "일단 1개만 줘봐!" (최초 요청)
                        // -> 이 요청이 위쪽의 doOnRequest를 건드림
                        request(1);
                    }

                    // 데이터를 하나 받아서 처리하는 곳
                    @Override
                    protected void hookOnNext(Integer value) {
                        // 4. 데이터 처리 중... (여기서 2초 동안 멈춤!)
                        // -> 로그 시간을 보면 2초씩 차이나는 이유가 여기 있음
                        TimeUtils.sleep(2000L);

                        // 5. 처리 완료 로그 (Subscriber가 실제 처리를 끝냄)
                        Logger.onNext(value);

                        // 6. "방금 받은 거 처리 끝났다. 다음 거 1개 더 줘!" (재요청)
                        // -> 이 코드가 실행되어야 다시 위쪽의 doOnRequest가 호출됨
                        // -> 만약 이 코드가 없으면? 1번 데이터만 받고 프로그램이 멈춰버림 (중요!)
                        request(1);
                    }
                });
    }
}
