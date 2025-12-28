package com.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HelloReactor {

    public static void main(String[] args) {
        Flux.just("hello", "Reactor")
                .map(String::toLowerCase)
                .log()
                .subscribe();
    }
    //리엑터에서 사용하는 연산자 : 아퍼레이트 ( just, mpa)
    //이 저스트와 맵이 체인을 형성 하면서 생성 -> 시퀀스
    //리액터의 세 가지 흐름
    // 퍼블리셔로 생성, 맵으로 가공 서브스크라이브로 전달
}
