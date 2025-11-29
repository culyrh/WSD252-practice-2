# 🧩 WSD Practice 2 — Java & Spring(Boot)

사용자 인증 및 회원 관리 기능을 연습하기 위한 REST API 실습입니다.<br>
회원가입, 로그인, 회원조회, 회원수정, 회원삭제 등 기본적인 CRUD 기능과
공통 응답 포맷, 인터셉터 기반 요청 로깅(미들웨어), 예외 처리가 구현되어 있습니다.

<br>

## 기술 스택
| 항목 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.12 |
| Build Tool | Gradle |
| Architecture | MVC (Controller + DTO) |

<br>

## 프로젝트 구조
```
src/main/java/com/example/wsdpractice/
├── controller/
│   └── UserController.java          # REST API 엔드포인트 (8개)
├── dto/
│   ├── UserDto.java                 # 사용자 데이터 클래스
│   └── ApiResponse.java             # 공통 응답 형식
├── config/
│   ├── ApiLogger.java               # 요청/응답 로깅
│   └── WebConfig.java               # 인터셉터 등록
├── WsdpracticeApplication.java      # 메인 애플리케이션
│
└── postman_tests/                   # POSTMAN 테스트 스크린샷 
```
<br>

## API 목록

### POST (2개)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/v1/users/register` | 회원가입 | 201 Created / 400 Bad Request |
| POST | `/api/v1/users/login` | 로그인 (Header 방식) | 200 OK / 401 Unauthorized / 404 Not Found |

### GET (2개)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| GET | `/api/v1/users` | 전체 사용자 조회 | 200 OK / 500 Internal Server Error |
| GET | `/api/v1/users/{id}` | 특정 사용자 조회 | 200 OK / 404 Not Found |

### PUT (2개)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| PUT | `/api/v1/users/{id}` | 사용자 정보 수정 | 200 OK / 404 Not Found |
| PUT | `/api/v1/users/{id}/password` | 비밀번호 변경 (Header 방식) | 200 OK / 400 Bad Request / 401 Unauthorized / 404 Not Found |

### DELETE (2개)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| DELETE | `/api/v1/users/{id}` | 특정 사용자 삭제 | 200 OK / 404 Not Found |
| DELETE | `/api/v1/users` | 전체 사용자 삭제 | 200 OK / 500 Internal Server Error |
<br>

## HTTP 응답 코드 사용

### 2xx (성공)
- **200 OK**: 정상 처리
- **201 Created**: 리소스 생성 성공

### 4xx (클라이언트 오류)
- **400 Bad Request**: 잘못된 요청 (필수 필드 누락, 유효하지 않은 값)
- **401 Unauthorized**: 인증 실패 (비밀번호 불일치)
- **404 Not Found**: 리소스를 찾을 수 없음

### 5xx (서버 오류)
- **500 Internal Server Error**: 서버 내부 오류

- <br>

## 미들웨어

### ApiLogger
- 모든 API 요청/응답 로깅
- HTTP 메서드, URI, 상태 코드 기록

**로그 예시:**
```
---------- 요청 정보 ----------
메서드: POST
URI: /api/v1/users/login
------------------------------
---------- 응답 정보 ----------
상태 코드: 200
------------------------------
```
<br>

## 응답 형식

모든 API는 통일된 응답 포맷을 사용합니다.

### 성공 응답
```json
{
  "status": "success",
  "data": { ... },
  "message": null
}
```

### 에러 응답
```json
{
  "status": "error",
  "data": null,
  "message": "에러 메시지"
}
```