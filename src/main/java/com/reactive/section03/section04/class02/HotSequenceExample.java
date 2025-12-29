package com.reactive.section03.section04.class02;

import com.reactive.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

public class HotSequenceExample {

    static final Logger log = LoggerFactory.getLogger(HotSequenceExample.class);

    /**
     * Hot Sequence 예제
     *
     * - 기본 Flux는 Cold → 구독할 때마다 처음부터 다시 시작
     * - share()를 쓰면 하나의 스트림을 여러 Subscriber가 같이 봄
     * - 늦게 들어온 Subscriber는 이미 방출된 데이터는 못 받음
     *   (실시간 스트림 느낌)
     */
    public static void main(String[] args) {
        // Singer A ~ E를 1초 간격으로 방출하는 Flux
        // 원래는 Cold Publisher
        Flux<String> concertFlux =
                Flux.fromStream(Stream.of("Singer A", "Singer B", "Singer C", "Singer D", "Singer E"))
                        .delayElements(Duration.ofSeconds(1))
                        // share() -> 실시간 스트림, 늦게 구독하면 이전 데이터 못 받음
                        // - 여러 Subscriber가 같은 Flux를 공유
                        // - 첫 Subscriber가 구독하는 순간부터 데이터 방출 시작
                        // - 나중에 구독하면 이미 지나간 데이터는 못 봄
                        .share();  //  share() 원본 Flux를 여러 Subscriber가 공유한다.
        // 첫 번째 구독자
        // 공연 시작부터 시청
        concertFlux
                .subscribe(singer -> log.info("# Subscriber1 is watching {}'s song.", singer));

        // 2.5초 대기
        // 이 동안 이미 앞부분 데이터는 흘러감
        TimeUtils.sleep(2500);

        // 두 번째 구독자
        // 이미 시작된 공연에 중간 입장
        concertFlux.subscribe(
                singer -> log.info("# Subscriber2 is watching {}'s song.", singer)
        );

        // 프로그램 종료 안 되게 대기
        TimeUtils.sleep(3000);
    }
}
