package com.reactive.section05.class01;


import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Unbounded request 일 경우, Downstream 에 Backpressure Error 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, Exception을 발생 시키는 전략
 */
public class BackpressureStrategyErrorExample {

    public static void main(String[] args) {
        Flux
                // [1] 생산자: 고속 기관총
                // 0.001초(1ms)마다 데이터를 타타타타 발행함. (엄청 빠름)
                .interval(Duration.ofMillis(1L))

                // [2] 전략 설정: "참지 말고 터져라"
                // 다운스트림(소비자)이 처리를 못 해서 대기열이 꽉 차면?
                // -> 기다려주거나 버리지 말고, 즉시 에러(Exception)를 뱉고 종료해라!
                .onBackpressureError()

                // 데이터 발행되는지 확인용 로그
                .doOnNext(Logger::doOnNext)

                // [3] 병목 구간의 시작 (스레드 분리)
                // 여기서부터 생산자 스레드와 소비자 스레드가 나뉨.
                // 이 사이에 보이지 않는 '작은 대기실(Buffer)'이 생기는데,
                // 생산자가 너무 빨라서 이 대기실에 데이터를 욱여넣기 시작함.
                .publishOn(Schedulers.parallel())

                // [4] 소비자: 느림보 거북이
                .subscribe(data -> {
                            // 처리하는데 0.005초(5ms)나 걸림.
                            // 생산자는 1ms마다 주는데 얘는 5ms마다 먹음. -> 5배 느림!
                            TimeUtils.sleep(5L);
                            Logger.onNext(data);
                        },
                        // [5] 사고 현장 수습 (에러 로그)
                        // 대기실이 꽉 차서 더 이상 못 받을 때 여기서 'OverflowException' 발생!
                        error -> Logger.onError(error));

        // 메인 스레드 퇴근 방지용 (2초 동안 구경)
        TimeUtils.sleep(2000L);
    }
}
