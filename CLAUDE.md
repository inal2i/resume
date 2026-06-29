# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Интерактивный сайт-резюме Inala Tuaeva в стиле RPG «character sheet». AI-гид управляет страницей через структурированный JSON: переставляет модули, меняет темы, создаёт UML-диаграммы и открывает бронирование встречи.

## Commands

### Local development (both services)
```bash
./run_local.sh        # backend :8080 + frontend :5173
```

### Backend only
```bash
cd backend && ./gradlew bootRun
```

### Frontend only
```bash
cd frontend && npm run dev
```

### Build & deploy
```bash
cd frontend && npm run build   # компилирует в backend/src/main/resources/static/
cd backend && ./gradlew bootJar
./deploy.sh                    # SSH-деплой на historeel@historeel.ru:8081
```

## Architecture

### Request flow
```
Browser → Vite proxy (/api → :8080) → Spring Boot
```
В production фронтенд собирается в `backend/src/main/resources/static/` и раздаётся вместе с JAR.

### Backend (`backend/src/main/java/dev/inal/resume/`)
- **`agents/ResumeAgent`** — отправляет весь transcript диалога в GigaChat, ожидает JSON `{"reply":"…","actions":[…]}`. Парсит JSON вручную (ищет первые `{` / `}`).
- **`service/ThemeService`** — генерирует AI-палитру цветов через GigaChat.
- **`service/CalendarService`** — Google Calendar FreeBusy API (сервисный аккаунт из `resources/google-calendar-sa.json`), бронирование слотов, отправка email-подтверждения через Gmail SMTP.
- **`api/ChatController`** — `POST /api/chat`, `POST /api/theme`.
- **`api/CalendarController`** — `GET /api/calendar/slots?date=`, `POST /api/calendar/book`.
- **`config/WebConfig`** — CORS для dev-прокси.

### Frontend (`frontend/src/`)
- **`composables/useStore.js`** — единственный реактивный стор (Vue `reactive`). Содержит весь стейт страницы: список модулей, тему, историю чата. Экспортирует `useStore()` и `startTimers()`.
- **`App.vue`** — корневой компонент, монтирует `ResumeHeader` + `ModulesGrid` + `AiChatPanel`.
- **`components/ModulesGrid.vue`** — рендерит список модулей по `kind`.
- **`components/modules/`** — компоненты модулей: `StackModule`, `ExperienceModule`, `PowersModule`, `AboutModule`, `StatsModule`, `UmlModule` (Mermaid), `CalendarModule`, `SpawnModule` (динамически создаваемые AI).

### Module action system
AI отвечает массивом `actions`, которые `useStore.applyActionsAndCapture()` применяет:
- `highlight` / `promote` — переместить модуль наверх + подсветка
- `theme` — сменить тему (`forge` / `dossier` / `neon`)
- `spawn` — создать новый модуль с произвольным контентом
- `uml` — показать Mermaid-диаграмму в `UmlModule`
- `resize` — изменить ширину модуля (`normal` / `wide`)
- `calendar` — показать модуль бронирования

На странице максимум 5 видимых модулей (`trimToMax`). При отказе `/api/chat` срабатывает локальный `fallback()` на регулярках.

## Environment variables

| Переменная | По умолчанию | Обязательна в prod |
|---|---|---|
| `GIGACHAT_CLIENT_ID` | из `application.yml` | нет |
| `GIGACHAT_CLIENT_SECRET` | из `application.yml` | нет |
| `MAIL_PASSWORD` | — | **да** |

Файл `backend/src/main/resources/google-calendar-sa.json` (сервисный аккаунт Google) должен присутствовать в репозитории.
