<template>
  <aside :class="['chat-panel', { 'mobile-open': state.chatOpen }]">
    <div class="chat-header">
      <div class="chat-title-block">
        <span class="chat-title">AI-СЕКРЕТАРЬ</span>
        <span v-if="modelName" class="chat-model">{{ modelName }}</span>
      </div>
      <div class="header-right">
        <div class="online-badge">
          <span class="pulse-dot" />
          ONLINE
        </div>
        <button class="close-chat-btn" @click="toggleChat">✕</button>
      </div>
    </div>

    <div ref="messagesEl" class="messages">
      <div v-if="state.messages.length === 0" class="welcome-msg">
        Я AI-секретарь Инала Туаева. Спроси про стек, опыт или оптимизацию — и я перестрою эту страницу под твой вопрос.
      </div>
      <template v-for="(msg, i) in state.messages" :key="i">
        <div v-if="msg.role === 'user'" class="msg-user">{{ msg.text }}</div>
        <template v-else>
          <div class="msg-assistant">{{ msg.text }}</div>
          <template v-if="msg.modules?.length">
            <div v-for="(m, j) in msg.modules" :key="j" class="inline-module">
              <!-- spawn: показываем содержимое прямо в чате -->
              <template v-if="m.kind === 'spawn'">
                <div class="im-label">// AI МОДУЛЬ</div>
                <div class="im-title">{{ m.title }}</div>
                <div class="im-lines">
                  <div
                    v-for="(ln, k) in parseLines(m.body)"
                    :key="k"
                    :class="ln.bullet ? 'im-bullet' : 'im-text'"
                  >
                    <span v-if="ln.bullet" class="im-bullet-mark">◆</span>{{ ln.text }}
                  </div>
                </div>
              </template>
              <!-- calendar: кнопка перехода к бронированию -->
              <template v-else-if="m.kind === 'calendar'">
                <button class="im-action-btn" @click="openCalendar">
                  📅 Открыть бронирование →
                </button>
              </template>
              <!-- uml: бейдж со ссылкой -->
              <template v-else-if="m.kind === 'uml'">
                <div class="im-badge" @click="closeChat">
                  📊 {{ m.title }} — нажмите чтобы увидеть ↗
                </div>
              </template>
              <!-- существующий модуль: ссылка для перехода -->
              <template v-else>
                <div class="im-ref" @click="closeChat">
                  ↑ {{ moduleLabel(m.kind) }} — нажмите чтобы увидеть
                </div>
              </template>
            </div>
          </template>
        </template>
      </template>
      <div v-if="state.loading" class="msg-loading">
        готовлю ответ<span class="loading-dots">…</span>
      </div>
    </div>

    <div class="input-area">
      <div class="input-row">
        <input
          v-model="state.input"
          :placeholder="currentPlaceholder"
          class="chat-input"
          @keydown.enter.prevent="send()"
        />
        <button class="send-btn" @click="send()">→</button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useStore, parseLines } from '../composables/useStore.js'

const { state, send, toggleChat, showCalendar } = useStore()
const messagesEl = ref(null)
const modelName = ref('')

const PLACEHOLDERS = [
  'Назначь встречу на этой неделе…',
  'Спроси про опыт и стек…',
  'Какую архитектуру выбрать для высоких нагрузок?',
  'Что умеешь лучше всего?',
  'Какие задачи решал с Kafka?',
  'Смени тему оформления сайта…',
  'Расскажи про оптимизацию баз данных…',
  'Покажи UML-диаграмму системы…',
  'Хочу такой же сайт',
]

const currentPlaceholder = ref(PLACEHOLDERS[0])
let placeholderIdx = 0
let placeholderTimer = null

onMounted(async () => {
  try {
    const res = await fetch('/api/info')
    if (res.ok) {
      const data = await res.json()
      modelName.value = data.model ?? ''
    }
  } catch {}

  placeholderTimer = setInterval(() => {
    placeholderIdx = (placeholderIdx + 1) % PLACEHOLDERS.length
    currentPlaceholder.value = PLACEHOLDERS[placeholderIdx]
  }, 3500)
})

onUnmounted(() => {
  clearInterval(placeholderTimer)
})

watch(
  () => state.messages.length,
  async () => {
    await nextTick()
    if (messagesEl.value) messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
)

const MODULE_LABELS = {
  stack: 'СТЕК', experience: 'ОПЫТ', powers: 'СПОСОБНОСТИ',
  about: 'О СЕБЕ', stats: 'СТАТИСТИКА', uml: 'UML', calendar: 'ВСТРЕЧА',
}
function moduleLabel(kind) {
  return MODULE_LABELS[kind] || kind.toUpperCase()
}

function closeChat() {
  state.chatOpen = false
}

function openCalendar() {
  showCalendar()
  state.chatOpen = false
}
</script>

<style scoped>
.chat-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100vh;
  width: clamp(300px, 30vw, 380px);
  display: flex;
  flex-direction: column;
  background: var(--panel);
  border-left: 1px solid var(--panel-line);
  box-shadow: -22px 0 60px rgba(0,0,0,.42);
  z-index: 50;
  transition: background .4s, border-color .4s;
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 18px;
  border-bottom: 1px solid var(--panel-line);
  flex: none;
}
.chat-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.chat-title {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  letter-spacing: .18em;
  color: var(--accent);
}
.chat-model {
  font-family: 'JetBrains Mono', monospace;
  font-size: 9px;
  letter-spacing: .1em;
  color: var(--ink-dim);
  opacity: .7;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.online-badge {
  display: flex;
  align-items: center;
  gap: 7px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  letter-spacing: .1em;
  color: var(--ink-dim);
}
.pulse-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--accent2);
  box-shadow: 0 0 8px var(--accent2);
  animation: pulseDot 1.8s infinite;
}
.close-chat-btn {
  display: none;
  background: none;
  border: 1px solid var(--panel-line);
  color: var(--ink-dim);
  border-radius: 7px;
  padding: 5px 10px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  cursor: pointer;
  transition: border-color .2s, color .2s;
}
.close-chat-btn:hover { border-color: var(--accent); color: var(--ink); }

.messages {
  flex: 1;
  overflow: auto;
  padding: 16px 16px 4px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.welcome-msg {
  font-size: 13px;
  line-height: 1.55;
  color: var(--ink-dim);
  background: var(--panel-2);
  border-left: 2px solid var(--accent);
  padding: 12px 14px;
  border-radius: 12px;
}
.msg-user {
  align-self: flex-end;
  max-width: 88%;
  background: var(--accent);
  color: #15110a;
  padding: 10px 13px;
  border-radius: 14px 14px 4px 14px;
  font-size: 13.5px;
  line-height: 1.45;
}
.msg-assistant {
  align-self: flex-start;
  max-width: 92%;
  background: var(--panel-2);
  border-left: 2px solid var(--accent);
  padding: 10px 13px;
  border-radius: 14px 14px 14px 4px;
  font-size: 13.5px;
  line-height: 1.5;
  color: var(--ink);
}
.msg-loading {
  align-self: flex-start;
  background: var(--panel-2);
  border-left: 2px solid var(--accent2);
  padding: 10px 13px;
  border-radius: 14px;
  font-size: 12.5px;
  color: var(--ink-dim);
  font-family: 'JetBrains Mono', monospace;
}
.loading-dots { animation: pulseDot 1s infinite; }

/* ── встроенные модули в чате (видны на мобильном) ── */
.inline-module {
  align-self: flex-start;
  width: 92%;
  background: var(--chip);
  border: 1px solid var(--panel-line);
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 13px;
}
.im-label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  letter-spacing: .14em;
  color: var(--accent2);
  margin-bottom: 6px;
}
.im-title {
  font-family: 'Big Shoulders Display', sans-serif;
  font-weight: 800;
  font-size: 18px;
  line-height: 1;
  text-transform: uppercase;
  color: var(--ink);
  margin-bottom: 8px;
}
.im-lines { display: flex; flex-direction: column; gap: 5px; }
.im-bullet {
  display: flex;
  gap: 8px;
  font-size: 12.5px;
  line-height: 1.45;
  color: var(--ink);
}
.im-bullet-mark { color: var(--accent); flex: none; }
.im-text { font-size: 12.5px; line-height: 1.5; color: var(--ink-dim); }
.im-action-btn {
  width: 100%;
  background: color-mix(in srgb, var(--accent) 12%, var(--chip));
  border: 1px solid color-mix(in srgb, var(--accent) 30%, transparent);
  border-radius: 9px;
  padding: 11px 14px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  color: var(--accent);
  cursor: pointer;
  text-align: left;
  transition: background .2s;
}
.im-action-btn:hover { background: color-mix(in srgb, var(--accent) 20%, var(--chip)); }
.im-badge, .im-ref {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11.5px;
  color: var(--accent);
  cursor: pointer;
  letter-spacing: .04em;
}
.im-badge:hover, .im-ref:hover { text-decoration: underline; }

/* только на мобильном: показываем inline-module и скрываем на десктопе */
@media (min-width: 769px) {
  .inline-module { display: none; }
  .close-chat-btn { display: none !important; }
  .chat-panel { display: flex !important; }
}

/* мобильный: панель скрыта по умолчанию, открывается на весь экран */
@media (max-width: 768px) {
  .chat-panel {
    display: none;
    width: 100%;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    height: 100dvh;
    border-left: none;
    box-shadow: none;
    z-index: 100;
  }
  .chat-panel.mobile-open {
    display: flex;
  }
  .close-chat-btn {
    display: block;
  }
}

.input-area {
  flex: none;
  padding: 12px 14px 16px;
}
.input-row {
  display: flex;
  align-items: center;
  gap: 9px;
  background: var(--chip);
  border: 1px solid var(--panel-line);
  border-radius: 12px;
  padding: 7px 7px 7px 14px;
}
.chat-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--ink);
  font-family: 'Manrope', sans-serif;
  font-size: 14px;
  min-width: 0;
}
.chat-input::placeholder { color: var(--ink-dim); }
.send-btn {
  background: var(--accent);
  color: #15110a;
  border: none;
  border-radius: 9px;
  padding: 10px 15px;
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  flex: none;
  transition: filter .2s;
}
.send-btn:hover { filter: brightness(1.08); }
</style>
