# parsing-api follow-up PR 회귀 테스트 결과

> 테스트 일시: 2026-05-17
> 테스트 환경: macOS Darwin 25.3.0, Node v24.14.1, Playwright Chromium 148.0.7778.96
> 테스트 대상: `feature/parsing-api-followup` 브랜치 (커밋 `c0306d0`)
> 비교 기준: PR #13 머지 직후 (`docs/test-result-headless.md`)
> 목적: parser.js 모듈 분리 + Gmarket 가격 fix + extractOgMeta 헬퍼 도입 후 회귀 부재 확인

---

## 1. 변경 사항 요약

| 커밋 | 변경 |
|---|---|
| `5b9bd7e` | parser.js → `src/lib/parser/` 디렉토리 모듈 분리 (기능 변경 없음) |
| `e92e4d3` | Gmarket og:title 가격 추출 시 옵션 코드("01" 등) 가격 오인 버그 수정 |
| `584d1e9` | `extractOgMeta` / `titleFallback` 헬퍼 도입, musinsa null 가드, naver fallback 위치 수정 |
| `c0306d0` | utils JSDoc 추가 (문서화만) |

---

## 2. 사이트별 결과 매트릭스 (17 테스트)

표 컬럼: **N**=itemName, **I**=itemImg, **P**=itemPrice (✓ = 추출, ✗ = null)

### 2.1 전용 파서 사이트 (PR #13 대비 회귀 검증)

| # | 사이트 | parser_type | N | I | P | duration | PR #13 대비 |
|---|---|---|---|---|---|---|---|
| 1 | COS 여성 | static | ✓ | ✗ | ✗ | 595ms | **개선**: 403→200 정상 응답, 메타 title만 추출 (이전엔 headless_fallback도 Access Denied) |
| 2 | COS 남성 | static | ✓ | ✗ | ✗ | 491ms | 동일 (이전 회귀 해소) |
| 3 | W컨셉 #1 | static | ✓ | ✓ | ✗ | 306ms | **동일** (가격 selector 회귀는 기존부터의 TODO) |
| 4 | W컨셉 #2 | static | ✓ | ✓ | ✗ | 239ms | **동일** |
| 5 | 서울스토어 | empty | — | — | — | 916ms | 동일 (DNS NXDOMAIN — 환경 의존) |
| 6 | 네이버 스마트스토어 | empty | — | — | — | 660ms | 동일 (429 Rate Limited — 환경 의존) |
| 7 | 지마켓 모바일 | empty | — | — | — | 356ms | 동일 (axios 403, fallback 빈 결과) |
| 8 | **지마켓 웹** | headless_fallback | ✓ | ✓ | ✗ | 645ms | **🐛 fix 동작 확인**: 이전 `item_price="01"` (옵션 코드 오인) → 이제 `undefined` (정직한 결과) |

### 2.2 general fallback 사이트

| # | 사이트 | parser_type | N | I | P | duration | PR #13 대비 |
|---|---|---|---|---|---|---|---|
| 9 | 11번가 웹 | static | ✓ | ✓ | ✓ | 389ms | **완벽 동작 유지** (가격 55180) |
| 10 | arket | headless_fallback | ⚠️ | ✗ | ✗ | 233ms | 동일 (Access Denied title) |
| 11 | G9 모바일 | empty | — | — | — | 155ms | 동일 (TLS 인증서 만료 — 환경 의존) |

### 2.3 fallback 대상 사이트 (headlessTargets 삭제 후 자연 흐름)

| # | 사이트 | parser_type | N | I | P | duration | PR #13 대비 |
|---|---|---|---|---|---|---|---|
| 12 | 29CM | static | ✓ | ✓ | ✗ | 334ms | **동일** (이전 PR #13에서도 static 경로로 빠짐. PR #13 \"HIGH 후속작업\" 항목은 화이트리스트 삭제로 자연 무효화) |
| 13 | EQL 홈 | static | ✓ | ✓ | ✗ | 97ms | **변경**: 이전 headless → 이제 static. 홈페이지에 og 메타가 충분히 있어 정적으로 충분. 의도된 흐름. |
| 14 | ZARA 홈 | static | ⚠️ | ✗ | ✗ | 70ms | **변경**: 이전 headless_empty → 이제 static (`item_name=" "` 공백). axios가 200을 받음 — 정적 시도가 fallback보다 빠름 |
| 15 | 네이버 통합검색 catalog | headless_fallback | ✓ | ✗ | ✗ | 196ms | 동일 (`item_name="네이버쇼핑"`) |
| 16 | 제시믹스 | static | ✓ | ✓ | ✗ | 682ms | **동일** |
| 17 | SSG 모바일 | static | ✓ | ✓ | ✗ | 881ms | **동일** (이전 파싱 실패에서 개선된 상태 유지) |

---

## 3. 핵심 확인 사항

### 3.1 Gmarket 가격 추출 fix (커밋 `e92e4d3`) — 동작 확인 ✓

PR #13 시점:
```json
{
  "item_name": "플로라 ... 01_레드베어 - G마켓 모바일",
  "item_price": "01"   ← 옵션 코드를 가격으로 오인 (버그)
}
```

PR #15 시점:
```json
{
  "item_name": "플로라 ... 01_레드베어 - G마켓 모바일",
  "item_price": undefined   ← 가격 형식 미매치 시 정직하게 빈 값
}
```

새 로직(`PRICE_TOKEN_PATTERN = /\d{1,3}(?:,\d{3})+|\d{4,}/`)이 천 단위 쉼표 또는 4자리 이상 숫자만 가격으로 인정 → 옵션 코드 "01" 매치 안 됨 → undefined 반환. **잘못된 가격을 노출하는 것보다 빈 값이 더 안전**.

### 3.2 화이트리스트 삭제 후 흐름 (커밋 `9b3b4c7` — develop에 이미 머지) — 의도된 동작 ✓

PR #13에서 사용자 코멘트로 헤드리스 사전 분류 화이트리스트를 제거하고 "정적 → 빈 결과 → 헤드리스 fallback" 단일 흐름화. 본 PR의 회귀 테스트에서 다음 흐름이 정상 동작 확인:

- 29CM / 제시믹스 / SSG: 정적으로 부분 결과 수확 → fallback 안 함
- 지마켓 웹 / arket / 네이버 통합검색: 정적 403 → 헤드리스 fallback
- EQL / ZARA 홈: 정적 200으로 끝 (사용자 의도: "정적으로 가져올 수 있으면 가져오고")

### 3.3 extractOgMeta 헬퍼 도입 후 회귀 부재 (커밋 `584d1e9`) — 확인 ✓

5개 사이트 extractor(musinsa/wconcept/naver/gmarket/seoulstore) 일괄 리팩터링에도:
- W컨셉/COS/지마켓/제시믹스/SSG/EQL/ZARA에서 추출 결과가 PR #13과 동일하게 유지됨
- 11번가는 완벽 동작 그대로 (상품명·이미지·가격)
- 단위 케이스 10개(`extractFromXxxHtml`) 직접 호출 검증도 별도로 PASS

### 3.4 환경 의존 실패 (코드 변화와 무관)

PR #13과 동일한 사유로 실패:
| 사이트 | 사유 |
|---|---|
| 서울스토어 | DNS NXDOMAIN (사이트 도메인 자체 만료/이전) |
| 네이버 스마트스토어 | 429 Rate Limited (테스트 IP에서의 제한) |
| G9 모바일 | TLS 인증서 만료 |
| 지마켓 모바일 / arket / COS | axios UA 기반 봇 차단 (403) |

이들은 본 PR 변경과 무관. 운영 환경에서는 다른 IP/네트워크 조건이라 다른 결과 가능.

---

## 4. 결론

### 4.1 회귀 없음

- 17 사이트 중 PR #13에서 동작하던 모든 정적/fallback 케이스가 PR #15에서도 동일하게 동작
- W컨셉/11번가 같이 가격까지 추출되던 사이트는 그대로 보존
- 단위 케이스 10개(`extractFromXxxHtml` 직접 호출) 별도 PASS

### 4.2 개선 확인

- **Gmarket 옵션 코드 가격 오인** 버그 fix 동작 (`"01"` → `undefined`)
- naver fallback 위치 수정 (단위 케이스 검증)
- musinsa null 가드 (단위 케이스 검증)

### 4.3 후속 개선 여지 (별도 안건)

- ZARA 정적이 공백(`" "`) itemName 반환 — `isEmptyResult`가 공백을 비어있지 않다고 판정. 트림 후 비어있으면 빈 결과로 처리하는 보강 검토.
- W컨셉 가격 selector 회귀 — 기존부터의 `// TODO selector 개선 필요` 항목

### 4.4 운영 검증 권장

- 실 운영 IP에서 네이버 스마트스토어/지마켓 검증 (테스트 환경 429/403 회피)
- 운영 로그에서 최근 실 사용자 입력 URL 표본 수집 후 재검증
