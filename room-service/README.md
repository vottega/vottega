# room-service

방(Room) 도메인을 담당하는 마이크로서비스입니다. 방 생성 업데이트, 참가자 생성 업데이트 등 방과 참가자에 관련된 핵심 기능을 제공합니다.

방 업데이트 상황을 Kafka로 보내 sse-server와 vote-service에서 실시간 업데이트가 됩니다.

---

## 🧰 기술 스택

- Language: Kotlin
- Framework : Spring MVC
- Database : MariaDB
- Message Queue : Kafka

---

## 📦 실행

### 로컬 실행
docker-compose up -d --build

### 로컬 카프카 실행 (ZooKeeper Version)
./script/room-service.sh init local

### Swagger 주소
room/api
