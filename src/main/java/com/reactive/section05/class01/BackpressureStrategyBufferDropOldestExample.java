package com.reactive.section05.class01;

import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Unbounded request 일 경우, Downstream 에 Backpressure Buffer DROP_OLDEST 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
 *    버퍼 안에 있는 데이터 중에서 가장 먼저 버퍼로 들어온 오래된 데이터부터 Drop 시키는 전략
 */
public class BackpressureStrategyBufferDropOldestExample {

    public static void main(String[] args) {
        Flux
                // [1] 생산자: 0.3초마다 찍어냄 (빠름)
                .interval(Duration.ofMillis(300L))

                // 원본 데이터 확인용
                .doOnNext(data -> Logger.info("# emitted by original Flux: {}", data))

                // [2] 전략: "버퍼 꽉 찼어? 그럼 제일 오래 기다린 놈(Oldest) 내보내!"
                // - 버퍼 크기: 2개
                // - 전략: DROP_OLDEST
                //   -> 버퍼가 [0, 1] 상태일 때 2가 들어오면?
                //   -> "0번(제일 옛날 거) 나가!" -> 버퍼 [1, 2] 됨.
                //   -> 즉, 버퍼는 항상 '최신 데이터' 쪽으로 슬라이딩 됨.
                .onBackpressureBuffer(2,
                        dropped -> Logger.info("# Overflow & dropped: {}", dropped),
                        BufferOverflowStrategy.DROP_OLDEST)

                // 버퍼 생존자 확인용
                .doOnNext(data -> Logger.info("# emitted by Buffer: {}", data))

                // [3] 병목 구간 (소비자 쪽 스레드 분리, Prefetch 1로 제한)
                .publishOn(Schedulers.parallel(), false, 1)

                // [4] 소비자: 1초에 1개 처리 (느림)
                .subscribe(data -> {
                            TimeUtils.sleep(1000L);
                            Logger.onNext(data);
                        },
                        error -> Logger.onError(error));

        TimeUtils.sleep(3000L);
    }


    //전개 과정:
    //
    //데이터 0, 1 도착 👉 버퍼: [0, 1] (꽉 참)
    //
    //데이터 2 도착! (자리 없음)
    //
    //DROP_OLDEST 발동!
    //
    //제일 오래된 0을 버림. (dropped: 0)
    //
    //1을 앞으로 당기고, 2를 넣음.
    //
    //👉 버퍼 상태: [1, 2]
    //
    //데이터 3 도착!
    //
    //제일 오래된 1을 버림. (dropped: 1)
    //
    //👉 버퍼 상태: [2, 3]
    //
    //데이터 4 도착!
    //
    //제일 오래된 2를 버림. (dropped: 2)
    //
    //👉 버퍼 상태: [3, 4]

    //"스트리밍 데이터나 센서 데이터처럼, '지금 흐름'을 놓치지 않는 게 중요한 경우 쓴다.
    // 중간에 좀 끊겨도 되니까, 지금 현재 값에 가까운 데이터를 계속 보고 싶을 때 딱이다."
}
