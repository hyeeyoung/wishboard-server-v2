-- KEYS[1]: 디바이스 기준 refresh 토큰 키 (RT:{deviceInfo}:{userId})
-- KEYS[2]: 레거시 refresh 토큰 키 ({userId})
-- KEYS[3]: 사용자 디바이스 목록 zset 키 (USER_DEVICE_LIST:{userId})
-- KEYS[4]: 회전 직후 grace 토큰 페어 키 (RT_ROTATE_GRACE:{deviceInfo}:{userId}:{oldTokenHash})
--
-- ARGV[1]: 요청으로 들어온 기존 refresh 토큰(비교 대상)
-- ARGV[2]: 새 refresh 토큰
-- ARGV[3]: refresh 토큰 TTL(ms)
-- ARGV[4]: zset score(현재 timestamp millis)
-- ARGV[5]: grace 값 (accessToken|refreshToken)
-- ARGV[6]: grace TTL(ms)

-- 1) 현재 refresh 토큰 조회: 디바이스 키 우선, 없으면 레거시 키에서 조회(하위 호환).
local current = redis.call('GET', KEYS[1])
local source = 1
if not current then
  current = redis.call('GET', KEYS[2])
  source = 2
end

-- 2) 토큰이 없으면 만료/미존재로 처리.
if not current then
  return 0
end

-- 3) CAS 비교: 요청 토큰과 현재 저장 토큰이 다르면 거절.
if current ~= ARGV[1] then
  return -1
end

-- 4) 디바이스 키에 refresh 토큰을 원자적으로 교체.
redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])

-- 5) 레거시 키에서 읽어온 경우, 마이그레이션 완료 후 레거시 키 삭제.
if source == 2 then
  redis.call('DEL', KEYS[2])
end

-- 6) 사용자 디바이스 목록 zset 메타데이터 갱신.
redis.call('ZADD', KEYS[3], ARGV[4], KEYS[1])

-- 7) 동시 유입 refresh 요청 완화를 위해 짧은 grace 매핑 저장.
redis.call('SET', KEYS[4], ARGV[5], 'PX', ARGV[6])
return 1
