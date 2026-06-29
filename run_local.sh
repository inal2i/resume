#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -f "$SCRIPT_DIR/.env" ]; then
  set -a; source "$SCRIPT_DIR/.env"; set +a
fi

cleanup() {
  echo ""
  echo "Останавливаю процессы..."
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
  wait "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
  echo "Готово."
}
trap cleanup EXIT INT TERM

echo "▶ Запускаю backend (Spring Boot :8080)..."
cd "$SCRIPT_DIR/backend"
./gradlew bootRun -q &
BACKEND_PID=$!

echo "▶ Запускаю frontend (Vite :5173)..."
cd "$SCRIPT_DIR/frontend"
npm run dev &
FRONTEND_PID=$!

echo ""
echo "  Backend : http://localhost:8080"
echo "  Frontend: http://localhost:5173"
echo ""
echo "  Ctrl+C для остановки"
echo ""

wait "$BACKEND_PID" "$FRONTEND_PID"
