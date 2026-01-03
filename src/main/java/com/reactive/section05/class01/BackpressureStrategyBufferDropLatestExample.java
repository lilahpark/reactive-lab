package com.reactive.section05.class01;

import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class BackpressureStrategyBufferDropLatestExample {

    public static void main(String[] args) {
        Flux
                // [1] 생산자: 0.3초마다 데이터 생산 (빠름)
                .interval(Duration.ofMillis(300L))

                // 원본 데이터가 뭐였는지 확인용
                .doOnNext(data -> Logger.info("# emitted by original Flux: {}", data))

                // [2] 버퍼 설정 (여기가 핵심!)
                // - 버퍼 크기: 2개 (의자 2개뿐)
                // - 전략: DROP_LATEST (버퍼가 꽉 찼는데 새 손님이 왔다?)
                //   -> "의자에 앉은 사람 중 가장 늦게 온 사람(막내) 나와! 새 손님 앉혀!"
                //   -> 즉, 버퍼의 가장 '끝' 데이터를 계속 교체함.
                .onBackpressureBuffer(2,
                        dropped -> Logger.info("# Overflow & dropped: {}", dropped),
                        BufferOverflowStrategy.DROP_LATEST)

                // 버퍼를 통과한 데이터 확인용
                .doOnNext(data -> Logger.info("# emitted by Buffer: {}", data))

                // [3] 스레드 분리 및 Prefetch 조절
                // - false: 에러 지연 여부
                // - 1: 소비자(Subscriber)가 한 번에 1개씩만 가져가도록 강제함.
                //      (소비자가 굼뜨게 만들어서 버퍼를 일부러 꽉 채우려는 의도)
                .publishOn(Schedulers.parallel(), false, 1)

                // [4] 소비자: 1초마다 처리 (느림)
                .subscribe(data -> {
                            TimeUtils.sleep(1000L);
                            Logger.onNext(data);
                        },
                        error -> Logger.onError(error));

        TimeUtils.sleep(3000L);
    }

    //이번 코드는 "버퍼를 쓰긴 쓰는데, 꽉 찼을 때 누굴 희생시킬래?" 를 결정하는 아주 디테일한 전략입니다.
    //
    //여기서 사용된 DROP_LATEST 전략은 "구관이 명관이다(옛날 게 중요하다)" 라는 철학을 가진 녀석입니다.
    //
    //헷갈리기 쉬운 Buffer 전략을 완벽하게 이해할 수 있도록 필기 노트로 정리해 드립니다.
    //
    //📝 필기 노트: "새로 온 놈 받으려면, 방금 앉은 놈 나가!" (DROP_LATEST)
    //이 전략의 핵심은 버퍼 안에서 가장 최근에 들어온 녀석(막내)을 쫓아내고, 지금 문 두드리는 녀석을 앉히는 것입니다.
    //
    //Java
    //
    //package com.reactive.section05.class01;
    //
    //import com.reactive.utils.Logger;
    //import com.reactive.utils.TimeUtils;
    //import reactor.core.publisher.BufferOverflowStrategy;
    //import reactor.core.publisher.Flux;
    //import reactor.core.scheduler.Schedulers;
    //
    //import java.time.Duration;
    //
    //public class BackpressureBufferDropLatestExample {
    //
    //    public static void main(String[] args) {
    //        Flux
    //                // [1] 생산자: 0.3초마다 데이터 생산 (빠름)
    //                .interval(Duration.ofMillis(300L))
    //
    //                // 원본 데이터가 뭐였는지 확인용
    //                .doOnNext(data -> Logger.info("# emitted by original Flux: {}", data))
    //
    //                // [2] 버퍼 설정 (여기가 핵심!)
    //                // - 버퍼 크기: 2개 (의자 2개뿐)
    //                // - 전략: DROP_LATEST (버퍼가 꽉 찼는데 새 손님이 왔다?)
    //                //   -> "의자에 앉은 사람 중 가장 늦게 온 사람(막내) 나와! 새 손님 앉혀!"
    //                //   -> 즉, 버퍼의 가장 '끝' 데이터를 계속 교체함.
    //                .onBackpressureBuffer(2,
    //                        dropped -> Logger.info("# Overflow & dropped: {}", dropped),
    //                        BufferOverflowStrategy.DROP_LATEST)
    //
    //                // 버퍼를 통과한 데이터 확인용
    //                .doOnNext(data -> Logger.info("# emitted by Buffer: {}", data))
    //
    //                // [3] 스레드 분리 및 Prefetch 조절
    //                // - false: 에러 지연 여부
    //                // - 1: 소비자(Subscriber)가 한 번에 1개씩만 가져가도록 강제함.
    //                //      (소비자가 굼뜨게 만들어서 버퍼를 일부러 꽉 채우려는 의도)
    //                .publishOn(Schedulers.parallel(), false, 1)
    //
    //                // [4] 소비자: 1초마다 처리 (느림)
    //                .subscribe(data -> {
    //                        TimeUtils.sleep(1000L);
    //                        Logger.onNext(data);
    //                    },
    //                    error -> Logger.onError(error));
    //
    //        TimeUtils.sleep(3000L);
    //    }
    //}
    // 머릿속 시뮬레이션 (이게 중요함!)
    //상황 설정:
    //
    //소비자 속도: 1초에 1개 처리.
    //
    //생산자 속도: 0.3초에 1개 생산.
    //
    //버퍼 크기: 2개.
    //
    //타임라인 전개:
    //
    //데이터 0: 소비자가 바로 가져감. (처리 중...)
    //
    //데이터 1 (0.3초): 버퍼에 저장. [1]
    //
    //데이터 2 (0.6초): 버퍼에 저장. [1, 2] (버퍼 꽉 참!)
    //
    //데이터 3 (0.9초): 들어오려는데 자리가 없음.
    //
    //DROP_LATEST 발동!
    //
    //버퍼 [1, 2] 중에서 가장 Latest(최근)인 2를 버림.
    //
    //그 자리에 3을 앉힘.
    //
    //버퍼 상태: [1, 3]
    //
    //데이터 4 (1.2초): 또 자리가 없음.
    //
    //버퍼 [1, 3] 중에서 Latest인 3을 버림.
    //
    //그 자리에 4를 앉힘.
    //
    //버퍼 상태: [1, 4]
    //
    //소비자 (1.0초): 드디어 0 처리 끝! 다음 거 주세요.
    //
    //버퍼 맨 앞의 1을 가져감. (1은 살아남았음!)
}
