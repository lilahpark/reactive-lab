package com.reactive.section05.class01;

import com.reactive.utils.Logger;
import com.reactive.utils.TimeUtils;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * Subscriber가 처리 가능한 만큼의 request 갯수를 조절하는 Backpressure 예제
 */
public class BackpressureExample02 {

    // [전역 변수] 지금 몇 개 받았는지 세는 카운터
    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 5) // 1, 2, 3, 4, 5 준비 완료
                .doOnNext(Logger::doOnNext)
                .doOnRequest(Logger::doOnRequest)
                .subscribe(new BaseSubscriber<Integer>() {

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        // 1. 전략 변경: "하나씩 말고, 일단 2개 먼저 줘봐!" (최초 2개 요청)
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        // 데이터 하나 받을 때마다 카운트 증가
                        count++;

                        // 받은 데이터 로그 출력 (일단 처리는 바로 함)
                        Logger.onNext(value);

                        // 2. "2개 다 받았니?" (조건 체크)
                        if (count == 2) {
                            // 3. 2개를 다 받았으면 여기서 '무거운 처리'를 한다고 가정 (2초 쉼)
                            // -> 즉, 2개 단위로 끊어서 쉰다!
                            TimeUtils.sleep(2000L);

                            // 4. "방금 받은 2개 처리 끝! 다음 2개 더 줘!" (재요청)
                            request(2);

                            // 5. 카운터 초기화 (다시 0부터 세야 하니까)
                            count = 0;
                        }
                    }
                });
    }
}
