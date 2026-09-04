# gateway

게이트웨이를 담당하는 마이크로서비스입니다. 인증이 필요한 api들은 auth-server에 인증을 요청해서 해당 결과를 가지고 X-Client-Role, X-User-Id, X-Participant-Id, X-Room-Id 헤더에 정보를 넣어 마이크로서비스들에 요청을 전달합니다.

---

## 🧰 기술 스택

- Language: Kotlin
- Framework : Spring Webflux

---

## 📦 실행

### 로컬 실행
docker-compose up -d --build
