#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -f "$SCRIPT_DIR/.env" ]; then
  set -a; source "$SCRIPT_DIR/.env"; set +a
fi

SERVER="historeel@historeel.ru"
DEPLOY_DIR="/home/historeel/resume"
JAR_NAME="resume.jar"
APP_PORT=8081

# ── 1. Сборка фронтенда ──────────────────────────────────────────────────────
echo "▶ Сборка фронтенда..."
cd "$SCRIPT_DIR/frontend"
npm run build

# ── 2. Сборка backend JAR ────────────────────────────────────────────────────
echo "▶ Сборка backend JAR..."
cd "$SCRIPT_DIR/backend"
./gradlew bootJar -q

JAR_PATH=$(find "$SCRIPT_DIR/backend/build/libs" -name "*.jar" ! -name "*plain*" | head -1)
echo "  JAR: $JAR_PATH"

# ── 3. Копируем JAR и .env на сервер ─────────────────────────────────────────
echo "▶ Копирование на сервер..."
ssh "$SERVER" "mkdir -p $DEPLOY_DIR"
scp "$JAR_PATH" "$SERVER:$DEPLOY_DIR/$JAR_NAME"
scp "$SCRIPT_DIR/.env" "$SERVER:$DEPLOY_DIR/.env"

# ── 4. Перезапуск приложения ─────────────────────────────────────────────────
echo "▶ Запуск приложения..."
ssh "$SERVER" bash -s -- "$DEPLOY_DIR" "$JAR_NAME" "$APP_PORT" << 'ENDSSH'
set -e
DEPLOY_DIR="$1"
JAR_NAME="$2"
APP_PORT="$3"

if [ -f "$DEPLOY_DIR/.env" ]; then
  set -a; source "$DEPLOY_DIR/.env"; set +a
fi

if pgrep -f "java.*$JAR_NAME" > /dev/null 2>&1; then
    echo "  Останавливаю старый процесс..."
    pkill -f "java.*$JAR_NAME" || true
    sleep 2
fi

nohup java -jar "$DEPLOY_DIR/$JAR_NAME" \
    --server.port="$APP_PORT" \
    > "$DEPLOY_DIR/app.log" 2>&1 < /dev/null &
echo "$!" > "$DEPLOY_DIR/app.pid"
echo "✓ Готово. PID: $(cat $DEPLOY_DIR/app.pid)"
ENDSSH
