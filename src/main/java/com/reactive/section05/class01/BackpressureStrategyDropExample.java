package com.reactive.section05.class01;


import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Unbounded request 일 경우, Downstream 에 Backpressure Drop 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 먼저 emit 된 데이터를 Drop 시키는 전략
 *  터져 죽는(Error) 전략"의 쿨한 대안
 */
public class BackpressureStrategyDropExample {

    public static void main(String[] args) {
        Flux
                // [1] 생산자: 여전히 폭주 기관차
                // 0.001초(1ms)마다 데이터를 쏟아냄.
                .interval(Duration.ofMillis(1L))

                // [2] 전략: "대기실 꽉 찼어? 그럼 넌 나가라!" (입구 컷)
                // -> 아까(Error)랑 다르게 시스템이 안 죽음. 대신 데이터를 버림.
                // -> 람다식 부분: "버려진 애들 이름(dropped)이라도 장부에 적어놔" (로그 남기기)
                .onBackpressureDrop(dropped -> Logger.info("# dropped: {}", dropped))

                // [3] 대기실(Buffer) 위치
                // 생산자와 소비자 사이의 완충 지대.
                // 여기가 꽉 차는 순간, 위의 Drop 전략이 발동됨.
                .publishOn(Schedulers.parallel())

                // [4] 소비자: 여전히 느림보
                // 생산자는 1ms마다 주는데, 얘는 5ms마다 하나씩 처리함.
                .subscribe(data -> {
                            TimeUtils.sleep(5L);
                            // 버려지지 않고 살아남은 행운의 데이터들만 여기서 처리됨
                            Logger.onNext(data);
                        },
                        error -> Logger.onError(error)); // 에러 발생 안 하므로 실행 안 됨

        TimeUtils.sleep(2000L);
    }
}
