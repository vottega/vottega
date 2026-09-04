# sse-server

SSE 연결을 담당하는 서버입니다.
방 업데이트 상황, 투표 상황을 Kafka에서 받아와 실시간으로 참가자와 사용자에게 Sink를 이용해 전파합니다.
참가자 연결 상황을 Kafka로 내보내 Room-service와 Vote-service 상태를 실시간으로 업데이트합니다.

---

## 🧰 기술 스택

- Language: Kotlin
- Framework : Spring Webflux
- Database : Redis
- Message Queue : Kafka

---

## 📦 실행

### 로컬 실행
docker-compose up -d --build

### Swagger 주소
sse/api
