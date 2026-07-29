# My Music Moment - Backend

---

**My Music Moment**는 음악을 좋아하는 사람들이 자신의 음악 취향과 순간을 글로 공유하는 음악 공유 커뮤니티 플랫폼입니다.

이 저장소는 My Music Moment의 **백엔드 API 서버**입니다.

별도의 React 프로젝트로 구현된 프론트엔드에서 이 서버가 제공하는 REST API를 호출하여 서비스를 구성합니다.

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
  - Spring Web (MVC)
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Database**: H2 (파일 기반, `./data/community`)
- **인증**: JWT (`io.jsonwebtoken` / jjwt 0.12.6) 기반 Access/Refresh Token
- **빌드 도구**: Gradle (Gradle Wrapper 포함)
- **기타**: Lombok, H2 Console

---

## 프로젝트 구조

```
src/main/java/com/example/community
├── CommunityApplication.java   # 스프링 부트 엔트리 포인트
├── common/                     # 공통 응답 포맷, 메시지, Enum 등
├── config/                     # Spring Security 설정
├── controller/                 # REST 컨트롤러 (Auth/User/Post/Comment/Admin/Main)
├── dto/                        # 요청 DTO
├── entity/
│   ├── main/                   # 실제 서비스 엔티티 (user, post, comment, auth)
│   └── history/                # 수정/삭제 이력을 남기는 히스토리 엔티티
├── exception/                  # 커스텀 예외
├── handler/                    # 전역 예외 핸들러
├── repository/                 # Spring Data JPA 리포지토리 (main / history)
├── security/                   # JWT 발급/검증, 인증 필터
└── service/                    # 비즈니스 로직 (Auth/User/Post/Comment/Admin)
```

## 주요 기능

- **회원**: 회원가입, 로그인/로그아웃, 프로필(닉네임·이미지) 수정, 비밀번호 변경, 회원 탈퇴
- **인증**: JWT Access/Refresh Token 발급 및 재발급, 요청 필터를 통한 인증 처리
- **게시글**: 목록 조회(페이지네이션), 작성/임시 저장, 상세 조회(조회수 반영), 수정, 삭제, 좋아요 토글, 신고
- **댓글**: 대댓글을 포함한 댓글 작성/수정/삭제/조회
- **관리자**: 신고된 게시글 목록/상세 조회, 게시글 블라인드 처리, 신고 반려 (`ROLE_ADMIN` 권한 필요)
- **이력 관리**: 사용자/게시글/댓글 변경 이력을 별도 히스토리 테이블에 기록

## API 개요

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| GET | `/` | 루트 진입, 게시글 목록으로 리다이렉트 정보 반환 | - |
| GET/POST | `/login` | 로그인 페이지 로드 / 로그인 시도 | - |
| POST | `/logout` | 로그아웃 | O |
| POST | `/token` | Access Token 재발급 | - (Refresh Token) |
| GET/POST | `/join` | 회원가입 페이지 로드 / 회원가입 | - |
| GET/PATCH | `/me/profile` | 프로필 조회 / 수정 | O |
| GET/PATCH | `/me/password` | 비밀번호 수정 페이지 / 수정 | O |
| DELETE | `/withdraw` | 회원 탈퇴 | O |
| GET/POST | `/posts` | 게시글 목록 조회 / 작성 | O |
| GET | `/posts/new` | 게시글 작성 페이지 정보 | O |
| GET/PATCH/DELETE | `/posts/{post_id}` | 게시글 상세 조회 / 수정 / 삭제 | O |
| GET | `/posts/{post_id}/edit` | 게시글 수정 페이지 정보 | O |
| POST | `/posts/{post_id}/like` | 좋아요 토글 | O |
| POST | `/posts/{post_id}/report` | 게시글 신고 | O |
| POST | `/posts/temp` | 임시 게시글 저장 | O |
| POST | `/posts/{post_id}/temp` | 게시글 수정 임시 저장 | O |
| GET/POST | `/posts/{post_id}/comments` | 댓글 목록 조회 / 작성 | O |
| PATCH/DELETE | `/posts/{post_id}/comments/{comment_id}` | 댓글 수정 / 삭제 | O |
| GET | `/admin` | 관리자 페이지 진입 | O (ADMIN) |
| GET | `/admin/reports` | 신고된 게시글 목록 | O (ADMIN) |
| GET | `/admin/reports/{post_id}` | 게시글 신고 상세 | O (ADMIN) |
| POST | `/admin/blind/{post_id}` | 게시글 블라인드 처리 | O (ADMIN) |
| POST | `/admin/reject/{post_id}` | 신고 반려 처리 | O (ADMIN) |

모든 응답은 `{ "message": string, "data": object }` 형태의 공통 포맷(`ResponseFormat`)으로 반환됩니다.

## 인증 방식

- 로그인 성공 시 Access Token(30분)과 Refresh Token(7일)이 JWT로 발급됩니다.
- 이후 요청은 `Authorization: Bearer {access_token}` 헤더를 통해 인증합니다.
- `AuthFilter`가 매 요청마다 토큰을 검증하여 `SecurityContext`에 인증 정보를 설정합니다.
- `/admin/**` 경로는 `ROLE_ADMIN` 권한이 있어야 접근할 수 있습니다.
- Access Token 만료 시 `/token`에 Refresh Token으로 재발급을 요청합니다.

## 실행 방법

### 환경 변수

프로젝트 루트의 `.env` 파일(또는 `.env.properties`)에 JWT 서명 키를 설정해야 합니다.

```
JWT_SECRET=<HMAC 서명에 사용할 시크릿 키>
```

### 서버 실행

```bash
./gradlew bootRun
```

### 테스트 실행

```bash
./gradlew test
```

### 데이터베이스

- H2 파일 기반 데이터베이스(`./data/community`)를 사용하며, 스키마는 `src/main/resources/schema.sql`에 정의되어 있습니다.
- H2 콘솔은 `/h2-console` 경로에서 사용할 수 있습니다.

## 프론트엔드 연동

- 이 서버는 별도로 구현된 React 프론트엔드 프로젝트와 연동되는 API 서버입니다.
- CORS 설정(`SecurityConfig`)에는 로컬 개발 환경(`http://localhost:5173`, `http://localhost:5500` 등)이 허용되어 있습니다.

