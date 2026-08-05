# My Music Moment - Backend

---

**My Music Moment**는 음악을 좋아하는 사람들이 자신의 음악 취향과 순간을 글로 공유하는 음악 공유 커뮤니티 플랫폼입니다.

이 저장소는 My Music Moment의 **백엔드 REST API 서버**입니다.

별도의 React 프로젝트로 구현된 프론트엔드에서 이 서버가 제공하는 API를 호출하여 서비스를 구성합니다.

---

## 목차

- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [주요 기능](#주요-기능)
- [API 개요](#api-개요)
- [인증 방식](#인증-방식)
- [실행 방법](#실행-방법)
- [프론트엔드 연동](#프론트엔드-연동)

---

## 기술 스택

- **Language**: Java 26 (toolchain)
- **Framework**: Spring Boot 4.1.0
  - Spring Web / Web MVC
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Database**: H2 (파일 기반, `./data/community`)
- **인증**: JWT (`io.jsonwebtoken` / jjwt 0.12.6) 기반 Access/Refresh Token
- **빌드 도구**: Gradle 9.5.1 (Gradle Wrapper 포함)
- **기타**: Lombok, H2 Console

---

## 프로젝트 구조

```
src/main
├── java/com/example/community
│   ├── CommunityApplication.java   # Spring Boot 애플리케이션 진입점
│   ├── common/                     # 공통 응답 포맷, 메시지, Enum, 페이지 요청
│   ├── config/                     # Security, CORS, 정적 리소스 설정
│   ├── controller/                 # REST 컨트롤러
│   ├── dto/                        # API 요청/응답 DTO
│   ├── entity/
│   │   ├── main/                   # 서비스 엔티티
│   │   └── history/                # 사용자·게시글·댓글 변경 이력 엔티티
│   ├── exception/                  # 커스텀 예외
│   ├── handler/                    # 전역 예외 처리
│   ├── repository/                 # Spring Data JPA 리포지토리
│   ├── security/                   # JWT 발급·검증 및 인증 필터
│   └── service/                    # 비즈니스 로직과 조회 지원 로직
└── resources
    ├── application.properties      # 데이터베이스, JWT, CORS, 업로드 설정
    ├── schema.sql                  # H2 스키마
    └── static/images/              # 기본 프로필 이미지
```

## 주요 기능

- **회원**: 회원가입, 로그인/로그아웃, 프로필(닉네임·이미지) 수정, 비밀번호 변경, 회원 탈퇴
- **인증**: JWT Access/Refresh Token 발급 및 재발급, 요청 필터를 통한 인증 처리
- **게시글**: 목록 조회, 작성, 상세 조회, 수정, 삭제, 임시저장, 좋아요 토글, 신고
- **댓글**: 댓글 목록 조회, 작성, 수정, 삭제
- **이미지**: 프로필 이미지 업로드 및 로컬 파일 제공(JPEG, PNG, GIF, WebP / max: 5MB)
- **관리자**: 신고된 게시글 목록/상세 조회, 게시글 블라인드 처리, 신고 반려 (`ROLE_ADMIN` 권한 필요)
- **이력 관리**: 사용자/게시글/댓글 변경 이력을 별도 히스토리 테이블에 기록
- **페이지네이션**: 게시글, 댓글, 관리자 신고 목록을 페이지당 10개씩 조회

## API 개요

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | `/` | 게시글 목록 경로로 이동하기 위한 정보 반환 | Access Token |
| GET | `/login` | 로그인 화면 정보 반환 | 공개 |
| POST | `/login` | 로그인 및 Access/Refresh Token 발급 | 공개 |
| POST | `/logout` | 저장된 Refresh Token 삭제 | Refresh Token |
| POST | `/token` | Access Token 재발급 | Refresh Token |
| GET | `/join` | 회원가입 화면 정보 반환 | 공개 |
| POST | `/join` | 회원가입 | 공개 |
| GET | `/me/profile` | 내 프로필 조회 | Access Token |
| PATCH | `/me/profile` | 내 프로필 수정 | Access Token |
| GET | `/me/password` | 비밀번호 수정 화면 정보 반환 | Access Token |
| PATCH | `/me/password` | 비밀번호 수정 | Access Token |
| DELETE | `/withdraw` | 회원 탈퇴 | Access Token |
| GET | `/musics/search?keyword={keyword}` | 음악 제목·아티스트 검색 | Access Token |
| POST | `/images` | 프로필 이미지 업로드 (`multipart/form-data`, 필드명 `image`) | 공개 |
| GET | `/images/{filename}` | 업로드 이미지 또는 기본 프로필 이미지 조회 | 공개 |
| GET | `/posts?page={page}` | 게시글 목록 조회 | Access Token |
| POST | `/posts` | 게시글 작성 | Access Token |
| GET | `/posts/new` | 새 게시글 작성 화면 및 임시 저장 정보 반환 | Access Token |
| GET | `/posts/temp` | 새 게시글 임시 저장 상세 조회 | Access Token |
| POST | `/posts/temp` | 새 게시글 임시 저장 | Access Token |
| GET | `/posts/{post_id}` | 게시글 상세 조회 및 사용자별 조회수 반영 | Access Token |
| PATCH | `/posts/{post_id}` | 게시글 수정 | Access Token |
| DELETE | `/posts/{post_id}` | 게시글 삭제 | Access Token |
| GET | `/posts/{post_id}/edit` | 게시글 수정 화면 정보 반환 | Access Token |
| POST | `/posts/{post_id}/like` | 게시글 좋아요 토글 | Access Token |
| POST | `/posts/{post_id}/report` | 게시글 신고 | Access Token |
| GET | `/posts/{post_id}/temp` | 게시글 수정용 임시 저장 상세 조회 | Access Token |
| POST | `/posts/{post_id}/temp` | 게시글 수정 내용 임시 저장 | Access Token |
| GET | `/posts/{post_id}/comments?page={page}` | 댓글 목록 조회 | Access Token |
| POST | `/posts/{post_id}/comments` | 댓글 작성 | Access Token |
| PATCH | `/posts/{post_id}/comments/{comment_id}` | 댓글 수정 | Access Token |
| DELETE | `/posts/{post_id}/comments/{comment_id}` | 댓글 삭제 | Access Token |
| GET | `/admin` | 관리자 화면 정보 반환 | Access Token + ADMIN |
| GET | `/admin/reports?page={page}` | 신고된 게시글 목록 조회 | Access Token + ADMIN |
| GET | `/admin/reports/{post_id}` | 게시글 신고 상세 조회 | Access Token + ADMIN |
| POST | `/admin/blind/{post_id}` | 게시글 블라인드 처리 | Access Token + ADMIN |
| POST | `/admin/reject/{post_id}` | 게시글 신고 반려 | Access Token + ADMIN |

일반 응답과 오류 응답은 다음 공통 형식을 사용합니다. 반환할 데이터가 없으면 `data`는 `null`입니다.

```json
{
  "message": "응답 메시지 코드",
  "data": {}
}
```

## 인증 방식

- 로그인 성공 시 Access Token(30분)과 Refresh Token(7일)이 발급됩니다.
- 인증이 필요한 요청에는 `Authorization: Bearer {access_token}` 헤더를 사용합니다.
- `AuthFilter`가 Access Token을 검증하고 사용자 ID와 권한을 `SecurityContext`에 설정합니다.
- `/admin/**` 경로는 Access Token에 `ROLE_ADMIN` 권한이 있어야 접근할 수 있습니다.
- Access Token 재발급 요청인 `POST /token`에는 `Authorization: Bearer {refresh_token}` 헤더를 사용합니다.
- 로그아웃 요청인 `POST /logout`에도 삭제할 Refresh Token을 같은 형식으로 전달합니다.
- Refresh Token은 발급 시 데이터베이스에 저장됩니다. 재발급 시 JWT 유효성과 데이터베이스 저장 여부를 모두 확인하며, 로그아웃 시 해당 토큰을 삭제합니다.

## 실행 방법

### 사전 요구 사항

- Java 26
- `JWT_SECRET` 환경 변수
- macOS/Linux에서는 Gradle Wrapper 실행 권한

### 환경 변수

JWT 서명에 사용할 충분히 긴 HMAC 키를 환경 변수로 설정합니다.

```bash
export JWT_SECRET='<32바이트 이상의 안전한 임의 문자열>'
```

또는 프로젝트 루트에 Git에서 제외되는 `.env` 파일을 생성할 수 있습니다.

```properties
JWT_SECRET=<32바이트 이상의 안전한 임의 문자열>
```

### 데이터베이스

- 기본 데이터베이스 URL은 `jdbc:h2:file:./data/community`입니다.
- 테이블 정의는 `src/main/resources/schema.sql`에 있습니다.
- 현재 `spring.sql.init.mode=never`, `spring.jpa.hibernate.ddl-auto=none`이므로 빈 데이터베이스에는 스키마가 자동 생성되지 않습니다. 처음 구성할 때는 `schema.sql`을 H2에 한 번 적용해야 합니다.
- H2 Console은 `/h2-console`에서 사용할 수 있으며 JDBC URL, 사용자명, 비밀번호는 각각 `jdbc:h2:file:./data/community`, `sa`, 빈 문자열입니다.

### 서버 실행

macOS/Linux:

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

서버는 별도 포트 설정이 없으므로 기본값인 `http://localhost:8080`에서 실행됩니다. 실행 시 사용자 업로드 디렉터리인 `./uploads/users`가 자동 생성됩니다.

### 테스트 실행

macOS/Linux:

```bash
./gradlew test
```

Windows:

```powershell
.\gradlew.bat test
```

## 프론트엔드 연동

- 이 서버는 별도로 구현된 React 프론트엔드 프로젝트와 연동되는 API 서버입니다.
- 기본 API 주소는 `http://localhost:8080`입니다.
- CORS 허용 오리진은 `cors.allowed-origins`에서 설정합니다.
- 기본 허용 오리진은 다음과 같습니다.
  - `http://localhost:5173`
  - `http://127.0.0.1:5173`
  - `http://localhost:5500`
  - `http://127.0.0.1:5500`
- 클라이언트가 인증 API를 호출할 때는 `Authorization` 헤더에 올바른 Access Token 또는 Refresh Token을 전달해야 합니다.


