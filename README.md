# wishboard-server-v2

<!-- Optional: Add a logo/image here if available -->
<!-- <img width="150" alt="app_logo" src="path_to_logo.svg"> -->

<br>

**위시리스트 관리를 위한 백엔드 서버입니다.** <!-- Brief slogan/description -->

<!-- Optional: Timeline / Team, if applicable -->
<!-- Timeline: YYYY.MM.DD ~ -->
<!-- TEAM: Project Team / Your Name -->

<br>

## 목차

- [wishboard-server-v2 정보](#wishboard-server-v2-정보)
- [모듈 설명](#모듈-설명)
  - [.github (CI/CD)](#github-cicd)
  - [api (메인 API 서버)](#api-메인-api-서버)
  - [parsing-api (상품 정보 파싱 API 서버)](#parsing-api-상품-정보-파싱-api-서버)
  - [push (푸시 알림 서버)](#push-푸시-알림-서버)
- [개발 환경](#개발-환경)
- [API 문서](#api-문서)
- [설정](#설정)
- [ERD](#erd)
- [아키텍처](#아키텍처)
- [디렉토리 구조](#디렉토리-구조)

<!-- - [Contributors](#contributors) --> <!-- Placeholder, can be added if info is available -->

<br>

## wishboard-server-v2 정보

이 프로젝트는 사용자가 위시리스트를 만들고 관리할 수 있는 플랫폼인 Wishboard 애플리케이션의 백엔드 서버입니다. 사용자 인증, 데이터 저장 및 프론트엔드 애플리케이션을 위한 API를 처리합니다.
Wishboard 애플리케이션은 [Google Play Store](https://play.google.com/store/apps/details?id=com.hyeeyoung.wishboard&hl=ko) 및 [App Store](https://apps.apple.com/kr/app/%EC%9C%84%EC%8B%9C%EB%B3%B4%EB%93%9C-wish-board/id6443808936)에서 사용할 수 있습니다.

<br>

## 모듈 설명

### .github (CI/CD)

GitHub Actions 워크플로우를 사용하여 CI/CD (지속적 통합/지속적 배포) 파이프라인을 관리합니다. 이를 통해 코드 변경 사항을 자동으로 빌드, 테스트 및 배포할 수 있습니다.

- **주요 워크플로우**:
    - `deploy-dev.yaml`: `develop` 브랜치에 푸시되거나 수동으로 트리거될 때, 변경된 모듈(api, parsing-api, push)을 빌드하여 개발 환경(AWS S3)에 배포합니다.
    - `deploy-prod.yaml`: `main` 브랜치에 푸시되거나 수동으로 트리거될 때, 변경된 모듈을 빌드하여 운영 환경(AWS S3)에 배포합니다.
- **주요 사용 액션**:
    - `actions/checkout@v3`: 리포지토리 코드를 체크아웃합니다.
    - `dorny/paths-filter@v3`: 특정 경로의 파일 변경 사항을 감지하여 조건부로 워크플로우 단계를 실행합니다.
    - `actions/setup-java@v3`: Java (Corretto JDK) 환경을 설정합니다. (api 모듈)
    - `actions/setup-node@v1`: Node.js 환경을 설정합니다. (parsing-api, push 모듈)
    - `aws-actions/configure-aws-credentials@v1`: AWS 자격 증명을 구성하여 S3 등에 접근할 수 있도록 합니다.

### api (메인 API 서버)

Spring Boot 기반으로 구축된 메인 백엔드 API 서버입니다. Wishboard 애플리케이션의 핵심 비즈니스 로직을 처리합니다.

- **주요 기능**: 사용자 인증, 위시리스트 아이템 관리, 폴더 관리, 장바구니, 알림 설정 등
- **기술 스택 및 의존성**:
    - **언어/프레임워크**: Java 21, Spring Boot 3.3.3, Spring Security, Spring Data JPA
    - **데이터베이스**: MySQL
    - **캐시**: Redis, Caffeine (로컬 캐시)
    - **검색 및 ORM**: QueryDSL
    - **API 문서화**: Swagger (OpenAPI)
    - **클라우드 서비스**: AWS S3 (이미지 등 파일 저장)
    - **인증**: JWT (JSON Web Token)
    - **모니터링 및 로깅**: Sentry, Spring Boot Actuator, Micrometer (Prometheus), Logback
    - **빌드 도구**: Gradle
    - **기타**: ModelMapper, Spring WebFlux (부분적 사용), Spring Mail
- **아키텍처**:
    - **도메인 주도 설계(DDD) 기반**: 핵심 비즈니스 로직을 도메인 모델에 집중시켜 복잡성을 관리합니다.
    - **패키지 구조**: 기능적 모듈화(예: `auth`, `item`, `folder`, `user` 등)를 따르며, 각 모듈은 DDD의 계층형 아키텍처 스타일을 반영합니다.
        - `com.wishboard.server.<도메인명>.presentation`:
            - `Controller`: HTTP 요청을 받아 Application Service에 처리를 위임하고, 결과를 HTTP 응답으로 반환합니다. (예: `ItemController.java`)
            - `dto`: 프레젠테이션 계층에서 사용하는 데이터 전송 객체 (요청/응답 DTO)를 정의합니다.
            - `docs`: API 문서화 관련 클래스 (Swagger 등)를 포함할 수 있습니다.
        - `com.wishboard.server.<도메인명>.application`:
            - `service`: 애플리케이션의 유스케이스를 구현합니다. 도메인 객체와 인프라스트럭처 계층을 조정하여 비즈니스 로직을 수행합니다. (예: `ItemService.java`)
            - `dto`: 애플리케이션 서비스에서 사용하는 데이터 전송 객체를 정의합니다.
        - `com.wishboard.server.<도메인명>.domain`:
            - `model`: 도메인 엔티티, 값 객체(VO) 등 핵심 도메인 모델을 정의합니다. (예: `Item.java`, `Folder.java`)
            - `repository`: 도메인 객체의 영속성을 위한 인터페이스를 정의합니다. (예: `ItemRepository.java`)
        - `com.wishboard.server.<도메인명>.infrastructure`:
            - 리포지토리의 실제 구현체(JPA, QueryDSL 등 활용), 외부 서비스(S3, 메일 서버 등)와의 연동 로직 등을 포함합니다. (예: `ItemRepositoryImpl.java`, `S3FileStorageClient.java`)
    - **API**: RESTful API를 통해 클라이언트(모바일 앱 등)와 통신합니다.
    - **예외 처리**: `@ControllerAdvice`를 사용하여 전역적으로 예외를 처리하고, 일관된 오류 응답 형식을 제공합니다.
    - **공통 관심사**: Spring AOP를 활용하여 로깅, 트랜잭션 관리 등 공통 관심사를 모듈화하여 처리합니다.
    - **설정 및 공통 유틸리티**: `com.wishboard.server.config` 패키지에서 각종 설정(보안, Swagger, Redis, S3 등)을 관리하고, `com.wishboard.server.common` 패키지에서 공통적으로 사용되는 유틸리티, 예외 클래스, 응답 DTO 등을 제공합니다.

### parsing-api (상품 정보 파싱 API 서버)

Node.js 및 Express 기반으로 구축된 API 서버로, 제공된 URL로부터 상품 정보를 파싱(스크래핑)하는 역할을 담당합니다.

- **주요 기능**: 외부 쇼핑몰 상품 페이지 URL을 입력받아 상품명, 가격, 이미지 URL 등의 정보를 추출하여 반환합니다.
- **기술 스택 및 의존성**:
    - **언어/프레임워크**: Node.js (18.x), Express.js
    - **웹 스크래핑**: Cheerio (HTML 파싱), Axios (HTTP 요청)
    - **로깅**: Winston, Morgan
    - **보안**: Helmet, HPP
    - **요청 제한**: express-rate-limit
    - **빌드 도구**: Webpack
    - **기타**: dotenv, app-root-path
- **아키텍처**:
    - 특정 엔드포인트 (`/item/parse`)로 요청을 받으면, 해당 URL의 HTML을 가져와 Cheerio를 사용하여 메타 태그(Open Graph 등) 및 주요 HTML 요소에서 상품 정보를 추출합니다.
    - 오류 처리 및 로깅 미들웨어를 사용하여 안정성을 높입니다.

### push (푸시 알림 서버)

Node.js 및 Express 기반으로 구축된 서버로, 사용자에게 다양한 조건에 따라 푸시 알림을 전송하는 역할을 담당합니다.

- **주요 기능**: 상품 가격 변동 알림, 재입고 알림, 폴더 아이템 관련 알림 등 사용자 설정에 따른 푸시 메시지 발송.
- **기술 스택 및 의존성**:
    - **언어/프레임워크**: Node.js (18.x), Express.js
    - **푸시 알림**: Firebase Admin SDK (FCM - Firebase Cloud Messaging)
    - **스케줄링**: node-schedule (주기적인 알림 작업 수행)
    - **데이터베이스**: MySQL2 (알림 대상 사용자 및 상품 정보 조회)
    - **로깅**: Winston, Morgan
    - **보안**: Helmet, HPP
    - **빌드 도구**: Webpack
    - **기타**: dotenv, app-root-path, axios
- **아키텍처**:
    - `node-schedule`을 사용하여 특정 시간 간격으로 알림 조건을 확인하고, 조건에 부합하는 사용자에게 FCM을 통해 푸시 알림을 전송합니다.
    - API 엔드포인트를 통해 즉시 알림을 트리거할 수도 있습니다. (예: 특정 이벤트 발생 시)
    - MySQL 데이터베이스와 연동하여 알림 대상 및 관련 데이터를 관리합니다.

<br>

## 개발 환경

- **Core Stack (api 모듈 기준):**
    - Java 21
    - Spring Boot 3.3.3
    - Gradle
- **Core Stack (parsing-api, push 모듈 기준):**
    - Node.js 18.x
    - Express.js
    - Webpack
- **Database & Cache:**
    - MySQL
    - Redis
- **Cloud Services:**
    - AWS S3 (for file storage)
- **API & Documentation:**
    - Swagger (OpenAPI) - for `api` module
- **DevOps & Monitoring:**
    - Docker (로컬 개발 환경)
    - Sentry (에러 트래킹)
    - Spring Boot Actuator (애플리케이션 모니터링 - `api` module)
    - Micrometer (Prometheus) (메트릭 수집 - `api` module)
    - Winston (로깅 - `parsing-api`, `push` modules)

<br>

## API 문서

메인 API 서버(`api` 모듈)의 API 문서는 Swagger (OpenAPI)를 사용하여 생성됩니다.
애플리케이션 실행 후 다음 주소에서 확인할 수 있습니다:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
(애플리케이션이 다른 포트에서 실행되는 경우 포트를 조정하십시오.)

<br>

## 설정

애플리케이션 설정(데이터베이스 연결, AWS 자격 증명, Redis 호스트/포트 등)은 다음 파일을 통해 관리됩니다:

- **api 모듈**: `api/src/main/resources/application.yml` (및 `application-{profile}.yml` 파일들)
- **parsing-api 모듈**: `parsing-api/.env`
- **push 모듈**: `push/.env`

<br>

## ERD

<!-- Placeholder for ERD image or description -->
<!-- e.g., <img width="800" alt="ERD" src="path_to_erd_image.png"> -->
데이터베이스 스키마 및 엔티티 관계에 대한 자세한 내용은 여기에 문서화될 예정입니다.

<br>

## 아키텍처

<!-- Placeholder for Architecture diagram or description -->
<!-- e.g., <img width="800" alt="Architecture Diagram" src="path_to_architecture_diagram.png"> -->
시스템 아키텍처, 구성 요소 및 상호 작용에 대한 개요는 여기에 제공될 예정입니다. (각 모듈별 아키텍처는 [모듈 설명](#모듈-설명) 섹션 참고)

<br>

## 디렉토리 구조

```
wishboard-server-v2/
├── .github/                    # GitHub Actions 워크플로우 (CI/CD)
│   └── workflows/
│       ├── deploy-dev.yaml     # 개발 환경 배포 워크플로우
│       └── deploy-prod.yaml    # 운영 환경 배포 워크플로우
├── api/                        # 메인 API 서버 (Spring Boot)
│   ├── build.gradle
│   ├── src/main/java/          # Java 소스 코드
│   └── src/main/resources/     # 설정 파일, 정적 리소스
├── parsing-api/                # 상품 정보 파싱 API 서버 (Node.js)
│   ├── package.json
│   ├── src/                    # JavaScript 소스 코드
│   └── webpack.config.js
├── push/                       # 푸시 알림 서버 (Node.js)
│   ├── package.json
│   ├── src/                    # JavaScript 소스 코드
│   └── webpack.config.js
├── README.md                   # 프로젝트 개요 및 문서
├── deploy.sh                   # 배포 스크립트 (EC2 내부에서 사용)
├── v2-pm2-run-dev.js           # PM2 개발 환경 실행 설정
└── v2-pm2-run-prod.js          # PM2 운영 환경 실행 설정
...
```

*(이것은 단순화된 표현입니다. 실제 구조에 대한 자세한 내용은 `ls()` 명령 출력을 참조하거나 'tree'와 같은 도구를 사용하여 생성하십시오.)*

<br>

---
*위 문서는 google 비동기 코딩 에이전트 jules가 작성했습니다.*
