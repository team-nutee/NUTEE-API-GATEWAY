# NUTEE-API-GATEWAY
![NUTEE 구조도](https://user-images.githubusercontent.com/47442178/108618442-96779080-7461-11eb-819e-c8dd855a8070.jpg)
### 서비스 주요 기능
- NUTEE 통합 서비스 공통기능 모듈

### 주요 사용 기술
- Spring Boot
- Spring Cloud
- Spring Security
- Json Web Token(JWT)


### 서비스 설계시 고려사항
- 각 서비스로의 매핑 이전에 JWT를 통한 공통 인증기능 구현
- Hystrix를 통한 서킷 브레이커
