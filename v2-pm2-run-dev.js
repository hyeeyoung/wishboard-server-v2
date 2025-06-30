module.exports = {
    /**
     * PM2 process definitions for Wishboard v2 (dev environment)
     *
     * ▸ wishboard-v2-api-server-dev      : Java Spring Boot (single instance, fork mode)
     * ▸ wishboard-v2-parsing-api-server-dev : Node.js Parsing API (1 instance cluster)
     * ▸ wishboard-v2-push-scheduler-dev  : Java Push‑Scheduler (1 instance cluster)
     *
     * NOTE
     * ────────────────────────────────────────────────────────────────
     * 1) "cwd" is kept identical across apps; adjust if you relocate binaries.
     * 2) "version" fields are placeholders – bump on every tagged release.
     * 3) If push‑scheduler is actually a Node service, switch `script` to the
     *    entry JS file and set `interpreter: 'node'`, `exec_mode: 'cluster'`.
     */
    apps: [
        /* ───────────────────────── Java API ───────────────────────── */
        {
            name: 'wishboard-v2-api-server-dev',
            script: 'java',
            args: [
                '-jar',
                '/home/ubuntu/wishboard-v2/dev/current/server-2.0.0.jar' // TODO 버전 변경 시 여기 수정
            ],
            cwd: '/home/ubuntu/wishboard-v2/dev/current',
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
            cwd: '/home/ubuntu/wishboard-v2/dev/current',
            namespace: 'parsing-api-server',
            version: '2.0.0', // TODO 버전 변경 시 여기 수정
            instances: 1,
            exec_mode: 'cluster',
            wait_ready: true, // 마스터 프로세스에게 ready 이벤트 대기
            listen_timeout: 50000,  // ms ... ready 이벤트를 기다릴 시간값
            kill_timeout: 5000,  // ms ... SIGINT 시그널을 보낸 후 프로세스가 종료되지 않았을 때 SIGKILL 시그널을 보내기까지의 대기 시간
            autorestart: false,
            watch: false
        },

        /* ───────────────────────── Java Push Scheduler ───────────────────────── */
        {
            name: 'wishboard-v2-push-scheduler-dev',
            script: './pushScheduler.js',
            interpreter: 'node',
            cwd: '/home/ubuntu/wishboard-v2/dev/current',
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
