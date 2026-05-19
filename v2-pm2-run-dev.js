module.exports = {
    /**
     * PM2 process definitions for Wishboard v2 (dev environment)
     *
     * ▸ wishboard-v2-api-server-dev         : Java Spring Boot (single instance, fork mode)
     * ▸ wishboard-v2-parsing-api-server-dev : Node.js Parsing API (1 instance fork)
     * ▸ wishboard-v2-push-scheduler-dev     : Node.js Push Scheduler (1 instance fork)
     *
     * 디렉토리 구조 (current/):
     *   ├── api/          server-2.0.0.jar
     *   ├── parsing-api/  server.js + node_modules/playwright(+core)
     *   └── push/         pushScheduler.js + vendors-*.js
     */
    apps: [
        /* ───────────────────────── Java API ───────────────────────── */
        {
            name: 'wishboard-v2-api-server-dev',
            script: 'java',
            args: [
                '-Xmx512m',
                '-Xms128m',
                '-jar',
                '-Duser.timezone=Asia/Seoul',
                '/home/ubuntu/wishboard-v2/dev/current/api/server-2.0.0.jar', // TODO 버전 변경 시 여기 수정
                '--spring.profiles.active=dev'
            ],
            cwd: '/home/ubuntu/wishboard-v2/dev/current/api',
            exec_mode: 'fork',
            instances: 1,
            namespace: 'api-server',
            version: '2.0.0', // TODO 버전 변경 시 여기 수정
            autorestart: true,
            watch: false
        },

        /* ──────────────────────── Node Parsing API ─────────────────────── */
        {
            name: 'wishboard-v2-parsing-api-server-dev',
            script: './server.js',
            interpreter: 'node',
            cwd: '/home/ubuntu/wishboard-v2/dev/current/parsing-api',
            namespace: 'parsing-api-server',
            version: '2.0.0', // TODO 버전 변경 시 여기 수정
            instances: 1,
            // Playwright 가 Chromium child process 를 spawn 할 때 stdio/IPC 가
            // PM2 cluster IPC 와 충돌해 worker 가 ready 직후 종료되는 문제로
            // fork mode 사용 (#26 / #27 참조). instances:1 이라 cluster 의 장점도 없음.
            exec_mode: 'fork',
            wait_ready: true, // 마스터 프로세스에게 ready 이벤트 대기
            listen_timeout: 50000,  // ms ... ready 이벤트를 기다릴 시간값
            kill_timeout: 5000,  // ms ... SIGINT 시그널을 보낸 후 프로세스가 종료되지 않았을 때 SIGKILL 시그널을 보내기까지의 대기 시간
            autorestart: false,
            watch: false
        },

        /* ───────────────────────── Node Push Scheduler ───────────────────────── */
        {
            name: 'wishboard-v2-push-scheduler-dev',
            script: './pushScheduler.js',
            interpreter: 'node',
            cwd: '/home/ubuntu/wishboard-v2/dev/current/push',
            namespace: 'push-scheduler',
            version: '2.0.0',  // TODO 버전 변경 시 여기 수정
            instances: 1,
            exec_mode: 'fork',
            wait_ready: true,
            listen_timeout: 50000,
            kill_timeout: 5000,
            autorestart: false,
            watch: false
        }
    ]
};
