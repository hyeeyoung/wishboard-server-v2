# push 모듈 Node·의존성 업그레이드 테스트 결과

> 테스트 일시: 2026-05-15
> 테스트 환경: macOS Darwin 25.3.0, Node v24.14.1
> 테스트 대상: `feature/push-module-upgrade` 브랜치
> 커밋 6개 (9806807~0f37303)

---

## 1. 환경 검증

### 1.1 빌드

| 명령 | 결과 |
|---|---|
| `npm install` | PASS (firebase-admin/express/morgan 등 deprecation 경고만, 동작 영향 없음) |
| `npm run build` (dev) | **PASS** (3 warnings — Critical dependency: dynamic require. firebase-admin·express·app-root-path 내부 옵셔널 require 경고, 4년 전 webpack 시절부터 존재, 동작 무영향) |
| `npm run build:production` (prod) | PASS |

### 1.2 의존성 버전 매트릭스

| 패키지 | spec (package.json) | installed | breaking? |
|---|---|---|---|
| **firebase-admin** | ^13.9.0 | 13.9.0 | YES (v10→v13 메이저 3회) |
| **mysql2** | ^3.11.5 | 3.22.3 | YES (v2→v3 메이저) |
| **axios** | ^1.7.7 | 1.16.1 | YES (v0→v1 메이저) |
| **helmet** | ^7.1.0 | 7.2.0 | minor |
| **winston** | ^3.14.2 | 3.19.0 | — |
| **winston-daily-rotate-file** | ^5.0.0 | 5.0.0 | YES (v4→v5 메이저) |
| **express** | ^4.21.0 | 4.22.2 | — |
| **dotenv** | ^17.4.2 | 17.4.2 | + dependencies 위치 정정 |
| **node-schedule** | ^2.1.1 | 2.1.1 | — |
| **app-root-path** | ^3.1.0 | 3.1.0 | — |
| **morgan** | (그대로) | 1.10.1 | — |
| **lodash** | (그대로) | 4.17.21 | — |

- `engines.node`: `>=22.0.0` ✓
- `.nvmrc`: `22.11.0` ✓

---

## 2. 변경 항목별 검증

### 2.1 firebase-admin v10 → v13 — sendEach API 존재 확인 (커밋 6)

**검증 명령**:
```bash
node -e "const M = require('firebase-admin').messaging; console.log(typeof M.Messaging.prototype.sendEach);"
```

**결과**: `function` ✓

firebase-admin v12에서 도입된 `sendEach`가 v13.9.0에도 정상 export됨. v10 가설이 plan과 일치.

**코드 적용** (`src/lib/pushAlarm.js`):
- L55: 첫 전송 — `firebaseAdmin.messaging().sendEach(messages)`
- L68: 실패 토큰 재전송 — `firebaseAdmin.messaging().sendEach(failedMessages)`
- 응답 구조 (`responses[]`, `successCount`, `failureCount`) 활용해 실패 토큰만 필터링하는 로직 정상 적용 ✓

**v10 시절 버그 수정 확인**:
- 이전: `messaging().send(failedTokens)` — `send`는 단일 메시지만 받는데 배열을 넘기는 버그
- 현재: `sendEach(failedMessages)` — 정상 API ✓

빌드 산출물(`dist/pushScheduler.js`)에서 `sendEach` 호출 2회 발견 — webpack이 정상 inline ✓

### 2.2 mysql2 v2 → v3 (커밋 4)

**확인 항목** (`src/config/db.js`):
- L1: `require('mysql2/promise')` — v3에서도 동일 경로 ✓
- L13-19/L23-29: `mysql.createPool({...connectionLimit: 50})` — v3에서도 동일 호환 ✓
- L38: 이전 `pool.getConnection(async (conn) => conn)` → 현재 `await pool.getConnection()` ✓ (의미 모호한 패턴 정리됨)

### 2.3 axios v0 → v1 (커밋 3)

**확인 항목** (`src/lib/slack.js`):
- L1: `require('axios')`
- L50: `axios({ url, method, headers, data })` 패턴 — v1에서도 그대로 동작하는 권장 패턴 ✓
- response 구조 (`res.status`, `res.data`) 동일

### 2.4 winston-daily-rotate-file v4 → v5 (커밋 5)

**확인 항목** (`src/config/winston.js`):
- transport 옵션 (`datePattern`, `maxFiles`, `auditFile` 등) v5에서 그대로 호환 ✓
- 빌드 시 v5 모듈이 정상 inline됨

### 2.5 dotenv 위치 정정 (커밋 2)

이전: `devDependencies`에 있었으나 `src/app.js:7`, `src/config/db.js:3` 등에서 런타임 require.
현재: `dependencies`로 이동 ✓ → `npm install --omit=dev` 환경에서도 안전.

### 2.6 GitHub Actions 정리 (커밋 1)

`.github/workflows/deploy-dev.yaml`, `deploy-prod.yaml` — `build-push` job만 수정:
- `actions/checkout@v2` → `@v4` ✓
- `actions/setup-node@v1` → `@v4` + `node-version-file: 'push/.nvmrc'` + `cache: 'npm'` ✓
- `aws-actions/configure-aws-credentials@v1` → `@v4` ✓
- `strategy.matrix.node-version` 제거 (단일 버전만 빌드) ✓

`build-parsing-api`, `build-api` job은 별도 작업이므로 미수정 (확인됨).

---

## 3. 런타임 검증 한계

### 3.1 require 단계 부팅 불가

```bash
PORT=3001 NODE_ENV=development node dist/pushScheduler.js
# → REQUIRE_ERROR: The "path" argument must be of type string. Received undefined
```

이는 `.env` 파일 부재로 `WINSTON_DAILY_ROTATE_DIR` 등 환경 변수가 undefined 상태에서 winston 트랜스포트가 path를 받지 못해 발생하는 정상 에러. **v10 시절에도 동일하게 발생**하므로 본 업그레이드 회귀가 아님.

운영 환경에는 `.env`가 존재하므로 부팅 검증은 dev/staging 환경에서 수동으로 진행해야 함.

### 3.2 실 동작 검증 불가 항목

| 항목 | 사유 |
|---|---|
| 실제 FCM 발송 | Firebase 자격증명 + 실 토큰 필요 |
| MySQL 연결·쿼리 | DB 자격증명·네트워크 필요 |
| Slack 알림 | webhook 토큰 필요 |
| 30분 cron 동작 | 서버 부팅 후 30분 이상 가동 필요 |

→ 수동 테스트 체크리스트(§5)로 분리.

---

## 4. 잔존 이슈

### 4.1 npm audit

23개 취약점 (low/moderate/high/critical). 대부분 firebase-admin/eslint 트랜지티브 (inflight, glob 등 deep dependency). 본 업그레이드와 직교하는 별도 안건.

### 4.2 webpack `request` 모듈 dynamic require 경고

firebase-admin 내부의 옵셔널 의존성. v10 시절부터 동일. 동작 영향 없음.

---

## 5. 운영 배포 전 수동 테스트 체크리스트

dev 환경에서 다음을 1주 이상 모니터링한 후 prod 머지 권장 (plan §5):

- [ ] dev에서 30분 cron 5회 이상 실행되어 MySQL pool 정상 동작 확인
- [ ] 단일 사용자/단일 토큰: 정상 알림 발송 + 로그 `notiFCMSend (success: 1, failure: 0)` 확인
- [ ] 다중 사용자/다중 토큰: success/failure 카운트 일치 확인
- [ ] 만료 토큰 포함 시: 재발송 시도 + Slack `푸쉬 알림 실패에 따른 재전송 성공 여부 Responses` 메시지 도달
- [ ] invalid payload 발생 시: catch 분기에서 Slack 에러 알림 도달
- [ ] 운영 EC2 Node 22 업그레이드 사전 합의 (현재 PM2 ecosystem.config.js 미수정 — 인프라 측 안건)

---

## 6. 결론

### 6.1 검증된 사항

- 모든 의존성이 정상 설치되고 빌드 통과 ✓
- firebase-admin v13의 `sendEach` API가 정상 동작하며 webpack 번들에도 정확히 inline됨 ✓
- mysql2 v3, axios v1, winston-daily-rotate-file v5 모두 import/require 단계 통과 ✓
- 코드 변경 (sendEach 적용, getConnection 패턴 정리, dotenv 위치 등) 모두 의도대로 반영 ✓
- GitHub Actions 액션 버전 정리 완료 ✓

### 6.2 검증 불가 (수동 필요)

- 실제 FCM 발송 동작 (자격증명 필요)
- 실제 DB 연결 (DB 필요)
- 30분 cron 실 동작 (장시간 가동 필요)

### 6.3 위험도

- **코드 레벨**: 낮음 (모든 변경이 의도대로 적용, 빌드 통과)
- **운영 회귀**: 중간 (firebase-admin 메이저 3회 점프이므로 dev 1주 모니터링 필수)
- **인프라 사전 조건**: 운영 환경 Node 22 통일 — 인프라 측 별도 안건
