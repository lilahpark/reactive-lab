# reactive-lab

WebFlux & Project Reactor 학습을 위한 개인 실습 프로젝트입니다.  
Reactive Streams의 핵심 개념과 주요 operator를 코드와 로그, 테스트로 검증하는 것을 목표로 합니다.

## Tech Stack
- Java 17
- Spring Boot (WebFlux)
- Project Reactor (Flux / Mono)
- Reactor Netty
- Gradle

## Learning Goals
- Flux / Mono 기본 동작 및 시그널 흐름 이해
- just / empty / justOrEmpty / fromCallable / defer 차이 정리
- map, filter, flatMap, concatWith 등 주요 operator 실습
- subscribeOn / publishOn 스케줄링 차이 이해
- blocking 코드(RestTemplate)와 non-blocking(WebClient) 비교
- StepVerifier를 이용한 리액티브 코드 테스트

## Project Structure
src/main/java
└─ io.github.lilahpark.reactivelab
├─ basics
├─ operators
├─ errorhandling
├─ scheduling
└─ webflux


## Notes
- 학습 목적의 프로젝트로, 각 예제는 동작 흐름을 이해하기 쉽게 단순화되어 있습니다.
- 코드에는 “왜 이 operator를 쓰는지”를 중심으로 주석을 추가합니다.
