package com.reactive.section05.class01;

import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Unbounded request 일 경우, Downstream 에 Backpressure Latest 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
 *    버퍼 밖에서 폐기되지 않고 대기하는 가장 나중에(최근에) emit 된 데이터부터 버퍼에 채우는 전략
 */
public class BackpressureStrategyLatestExample {

    public static void main(String[] args) {
        Flux
                // [1] 생산자: 1ms마다 데이터를 찍어내는 초고속 공장
                .interval(Duration.ofMillis(1L))

                // [2] 전략: "가장 최신 것만 남겨라!" (Latest)
                // -> 버퍼가 꽉 찼을 때 데이터가 들어오면?
                // -> "일단 대기실(임시 저장소)에 너 잠깐 있어."
                // -> 근데 소비자가 아직 안 가져갔는데 또 새 데이터가 오면?
                // -> "야, 너 비켜! 더 최신인 애가 왔어." (기존 대기자를 버리고, 새 놈을 대기시킴)
                // -> 결과적으로 소비자가 가져갈 때는 '삭제된 중간 데이터들'은 건너뛰고 '가장 마지막 데이터'를 가져감.
                .onBackpressureLatest()

                // [3] 버퍼 위치 (병목 구간)
                .publishOn(Schedulers.parallel())

                // [4] 소비자: 5ms마다 처리하는 느림보
                .subscribe(data -> {
                            TimeUtils.sleep(5L);
                            Logger.onNext(data);
                        },
                        error -> Logger.onError(error));

        TimeUtils.sleep(2000L);
    }

    //1. Drop 전략 (방금 배운 것)
    //상황: 버퍼 꽉 참. 100번 데이터 도착.
    //
    //행동: "자리 없어! 돌아가!" (문전박대)
    //
    //결과: 100번은 그냥 허공으로 사라짐. 나중에 자리가 나면 그때 도착한 150번이 들어옴.
    //
    //비유: 만원 버스. 기사님이 문 닫아버림. 정류장에서 기다리던 사람은 못 탐.
    //
    //2. Latest 전략 (지금 코드)
    //상황: 버퍼 꽉 참. 100번 데이터 도착.
    //
    //행동: "일단 문 앞 VIP 대기석에 앉아 있어."
    //
    //추가 상황: 소비자가 아직 안 왔는데 101번 도착.
    //
    //행동: "100번 너 나가. 101번이 더 최신이야. 101번이 대기석 앉아." (덮어쓰기)
    //
    //결과: 소비자가 손을 내밀면, 100번은 이미 버려졌고 가장 따끈따끈한 101번을 줌.
    //
    //비유: 스트리밍 영상. 버퍼링 걸렸을 때, 멈춘 화면(과거)부터 재생하는 게 아니라 **현재 라이브 시점(최신)**으로 점프해버림.
}
