# Selector hint 부활 — 회귀 테스트 결과

> 테스트 일시: 2026-05-19
> 환경: macOS Darwin 25.3.0, Node v24.14.1, Playwright Chromium 148.x
> 브랜치: `hotfix/headless-price-selector-hint` (base: main)
> 비교 기준: PR #23 머지 직후 (`docs/test-result-pr23.md`)

---

## 1. 도입 배경

PR #23 회귀 테스트에서 발견된 한계: 메타태그 기반 사이트들에서 `needsPriceFallback` 분기는 정확히 발동하지만, 헤드리스가 정적과 동일한 og 메타만 추출해 **가격 보강 효과 0%**.

원인: og:price 가 없는 사이트는 가격이 사이트 내부 DOM (`<span class="price">29,000원</span>` 등) 에 위치. cheerio / Playwright 둘 다 같은 HTML 을 받지만 selector 를 모르므로 추출 못 함.

→ 헤드리스 페이지에서 **DOM selector 로 가격 노드를 직접 시도** 하는 단계 추가.

## 2. 설계 원칙

PR #15 의 사용자 결정("URL 사전 분류 화이트리스트 없음, 모든 페이지가 같은 흐름") 과 호환되게:

- **URL 분기 없음**: 모든 헤드리스 호출에서 동일하게 시도
- **결과 조건부**: `result.item_price` 가 비어있을 때만 selector 시도 → 정적/og 로 잡힌 경우 무비용
- **가격 형식 필터**: 천 단위 쉼표 또는 4자리 이상 숫자만 인정 → 옵션 코드("01") 오인 방지
- **사이트별 + 일반 hint**: specific selector 먼저 → 못 찾으면 `[class*="price" i]` 등 일반 selector

## 3. 결과 매트릭스

### 3.1 가격 보강 성공

| 사이트 | PR #23 가격 | 이번 가격 | 매치된 selector | duration |
|---|---|---|---|---|
| **EQL** | ✗ | **128,000원** | `[class*="price" i]` | 2.2s |
| **제시믹스** | ✗ | **52,000원** | `[class*="price" i]` | 3.1s |
| **W컨셉** | ✗ | **45,000원** | `[class*="price" i]` | 5.5s |

→ 메타에 가격 없는 한국 쇼핑몰 다수가 일반 `[class*="price"]` 패턴으로 가격 노드를 노출. **사이트별 specific selector 없이도 추출 가능** 확인.

### 3.2 false positive 가능성

| 사이트 | 추출 값 | 의심 |
|---|---|---|
| SSG (노브랜드 미네랄워터 2L\*6입) | **1,980** | 정가는 보통 6,000~7,000원대. `[class*="price"]` 가 첫 매치 노드를 잡았는데 첫 노드가 가격 외 다른 숫자(예: 단위가) 일 가능성. 운영 dev 에서 사이트별 specific selector 검증 후 PRICE_SELECTORS_BY_SITE 에 추가 권장 |

→ general selector 가 잡은 첫 매치를 가격으로 채택하는 휴리스틱은 false positive 위험이 있음. 운영 모니터링으로 분포 확인 후 사이트별 selector 추가가 보완책.

### 3.3 selector hint 로도 안 잡히는 케이스

| 사이트 | 사유 |
|---|---|
| 29CM `product.29cm.co.kr/catalog/...` | SPA — og:meta 가 일반 사이트명만 노출되고 상품 정보는 클라이언트 렌더링. 헤드리스 페이지에서도 가격 노드가 늦게 그려져 매치 실패. PR #13 에서 화이트리스트 제거된 이력. `waitForSelector` 도입 / 더 긴 timeout 검토 필요 (별도 안건) |

### 3.4 회귀 검증

| 사이트 | parser_type | 가격 | 비고 |
|---|---|---|---|
| 11번가 | `static` | 62,000원 | 헤드리스 호출 없이 정적 경로 그대로. **회귀 없음** ✓ |

## 4. 단위 검증

`extractPriceFromText` 헬퍼 케이스 9/9 통과:

| 입력 | 결과 | 비고 |
|---|---|---|
| `"12,500원"` | `"12500"` | 정상 |
| `"1,234,567"` | `"1234567"` | 정상 |
| `"9900원"` | `"9900"` | 4자리 이상 숫자 정상 매치 |
| `"9999"` | `"9999"` | 정상 |
| `"옵션: 01"` | `undefined` | 옵션 코드 거부 (4자리 미만) |
| `"size 03"` | `undefined` | 동일 |
| `""`, `null` | `undefined` | 안전 처리 |
| `"총 35,000원 (배송비 별도)"` | `"35000"` | 첫 매치만 추출 |

## 5. 성능 영향

| 케이스 | 이전 (PR #23) | 이번 |
|---|---|---|
| 정적 완전 성공 (11번가) | static, ~450ms | 동일 |
| 정적 부분 성공 + 가격 결손 | static_with_price_fallback, 헤드리스 호출만 (효과 0) | 같은 헤드리스 호출 + selector 시도 (effect: 가격 추출) |
| 정적 실패 | headless_fallback | 동일 |

selector 시도는 헤드리스 페이지에 이미 로드된 DOM 에 대한 `page.locator().textContent({ timeout: 1500 })` 호출. selector 당 최대 1.5초 timeout, 매치되면 즉시 break → 평균 추가 비용 1~3초.

## 6. 결론

### 6.1 효과 확인

PR #23 §3.2 에서 "필드별 fallback 의 실효성 한계" 로 지적된 부분이 selector hint 부활로 **3개 사이트(EQL/제시믹스/W컨셉) 에서 즉시 해소**. 단순 `[class*="price"]` 일반 selector 만으로도 한국 쇼핑몰 다수에서 가격 추출 가능.

### 6.2 후속 안건

- **SSG false positive 검증** — 사이트별 specific selector 를 `PRICE_SELECTORS_BY_SITE.ssg` 에 추가
- **29CM SPA 케이스** — `waitForSelector` 또는 더 긴 timeout 필요 (별도 안건)
- **운영 dev 에서 parser_type 분포 모니터링** — `static_with_price_fallback` 비율, 가격 추출 성공률, false positive 발생 빈도

### 6.3 PR #15 사용자 결정과의 호환

> "URL 이 사전 분류 화이트리스트에 해당하는지 판정하는게 아니야. 정적 방식으로 가져올 수 있으면 가져오고 그게 아니라면 동적 방식으로 수행하도록 하는 거야."

본 도입은 위 결정과 충돌하지 않음:
- 모든 URL 이 동일 흐름 (정적 → 빈/부분 결과 → 헤드리스 fallback)
- 화이트리스트 분기 없음
- selector hint 는 **헤드리스 도달 후의 추출 보강 도구**일 뿐, 라우팅 도구가 아님
