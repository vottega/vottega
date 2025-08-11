# Vottega

여러 마이크로서비스로 구성된 실시간 투표 서비스입니다. MSA 아키텍처를 활용하여 각 도메인 서비스를 느슨하게 결합합니다.

## 서비스 구성


<img width="700" height="390" alt="image" src="https://github.com/user-attachments/assets/ac293a53-bc72-428d-87c5-c57b28685341" />

- **gateway** — 외부 트래픽의 진입점(API Gateway). 라우팅·인증 연계·서비스 보호 계층을 담당.  
- **discovery-service** — 서비스 레지스트리(Eureka Server). 각 서비스 인스턴스 등록/조회.  
- **auth-service** — 인증 관련 도메인. 로그인/토큰 발급/토큰 검증 등 인증 흐름 담당.  
- **user-service** — 사용자 도메인. 사용자 프로필/조회/관리.  
- **room-service** — 방(Room) 도메인. 방 및 참가자 생성/참여/상태 관리.
- **vote-service** — 투표 도메인. 투표 생성/참여/집계 등 투표 로직.  
- **client-connection-service** — 클라이언트와 백엔드 간 SSE 연결 관리

## 사용 기술

- **언어**
  - Kotlin
- **프레임워크**
  - Spring 기반 마이크로서비스
- **메시징**
  - Apache Kafka 
- **데이터베이스**
  - Maria DB, Redis 
