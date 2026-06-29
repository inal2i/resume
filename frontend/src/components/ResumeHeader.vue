<template>
  <header class="header">
    <div class="top-row">
      <div class="theme-block">
        <div class="theme-switcher">
          <button
            :class="['theme-btn', { active: state.theme === 'forge' || state.theme === 'neon' }]"
            @click="state.theme = 'forge'"
          >🌑<span class="btn-label"> DARK</span></button>
          <button
            :class="['theme-btn', { active: state.theme === 'dossier' }]"
            @click="state.theme = 'dossier'"
          >☀️<span class="btn-label"> LIGHT</span></button>
          <button
            :class="['theme-btn', { active: state.theme === 'ai' }]"
            @click="toggleAiPrompt"
          >{{ aiThemeLabel }}</button>
        </div>
        <Transition name="ai-prompt">
          <div v-if="state.aiPromptOpen" :class="['ai-prompt-row', { generating: state.aiGenerating }]">
            <span class="ai-star">✦</span>
            <input
              v-model="state.aiPromptInput"
              placeholder="закат на Марсе, хакерский терминал…"
              class="ai-prompt-input"
              :disabled="state.aiGenerating"
              @keydown.enter.prevent="!state.aiGenerating && applyAiTheme(state.aiPromptInput)"
            />
            <button class="ai-gen-btn" :disabled="state.aiGenerating" @click="applyAiTheme(state.aiPromptInput)">
              <template v-if="state.aiGenerating">
                <span class="gen-dot d1">.</span><span class="gen-dot d2">.</span><span class="gen-dot d3">.</span>
              </template>
              <template v-else>GEN</template>
            </button>
          </div>
        </Transition>
      </div>
    </div>

    <div class="hero-row">
      <div class="name-block">
        <h1 class="name">Инал<br>Туаев</h1>
        <div class="accent-bar" />
      </div>
      <div class="bio-block">
        <p class="tagline">«Решить сложную задачу простым способом — довольно сложная задача.»</p>
        <div class="chips-row">
          <span class="chip">ROLE — Backend Developer</span>
          <span class="chip">WEAPON — Java · Spring · Kafka</span>
          <span class="chip">EXP — {{ state.expStr }}</span>
        </div>
        <div class="links-row">
          <a href="mailto:inal4353@mail.ru" class="link-pill">✉ inal4353@mail.ru</a>
          <a href="https://t.me/inal2i" target="_blank" rel="noopener" class="link-pill">Telegram ↗</a>
          <a href="https://github.com/inal2i" target="_blank" rel="noopener" class="link-pill">GitHub ↗</a>
          <a href="https://zpshka.ru" target="_blank" rel="noopener" class="link-pill">zpshka.ru ↗</a>
          <a href="/media/InalTuaev.pdf" download class="link-pill link-pill--cv">⬇ Скачать резюме</a>
          <button class="link-pill link-pill--book" @click="showCalendar">📅 Забронировать встречу</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from '../composables/useStore.js'

const { state, applyAiTheme, showCalendar } = useStore()

const aiThemeLabel = computed(() => {
  if (state.aiGenerating) return '⏳ GEN…'
  if (state.theme === 'ai' && state.aiPaletteName) return '✦ ' + state.aiPaletteName.slice(0, 12)
  return '✦ AI'
})

function toggleAiPrompt() {
  state.aiPromptOpen = !state.aiPromptOpen
  state.aiPromptInput = ''
}
</script>

<style scoped>
.header {
  position: relative;
  z-index: 1;
  padding: clamp(26px,5vw,54px) clamp(20px,5vw,64px) 24px;
}
.header::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url('/inal_reversed.png');
  background-size: cover;
  background-position: right top;
  background-repeat: no-repeat;
  opacity: 0.40;
  pointer-events: none;
  z-index: 0;
  mask-image: linear-gradient(to left, black 15%, transparent 70%);
  -webkit-mask-image: linear-gradient(to left, black 15%, transparent 70%);
}

@media (max-width: 640px) {
  .header::before {
    background-position: 70% top;
    mask-image: linear-gradient(to bottom, black 40%, transparent 90%);
    -webkit-mask-image: linear-gradient(to bottom, black 40%, transparent 90%);
  }
}
.header > * {
  position: relative;
  z-index: 1;
}
.top-row {
  position: absolute;
  top: clamp(20px,3.5vw,36px);
  right: clamp(20px,5vw,64px);
  z-index: 2;
}
.theme-block {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.theme-switcher {
  display: flex;
  gap: 4px;
  background: var(--chip);
  border: 1px solid var(--panel-line);
  border-radius: 10px;
  padding: 4px;
}
.theme-btn {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  letter-spacing: .12em;
  padding: 6px 12px;
  border: 1px solid transparent;
  border-radius: 7px;
  background: transparent;
  color: var(--ink-dim);
  cursor: pointer;
  transition: background .2s, color .2s;
}
.theme-btn:hover { color: var(--ink); }
.theme-btn.active { background: var(--accent); color: #15110a; border-color: var(--accent); }

@media (max-width: 640px) {
  .theme-switcher {
    padding: 3px;
    gap: 2px;
  }
  .theme-btn {
    font-size: 13px;
    padding: 4px 7px;
    letter-spacing: 0;
  }
  .btn-label { display: none; }
}

/* ── row ── */
.ai-prompt-row {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--chip);
  border: 1px solid var(--panel-line);
  border-radius: 10px;
  padding: 7px 10px;
  width: 320px;
  box-shadow: 0 8px 32px rgba(0,0,0,.35);
  transform-origin: top right;
}

/* ── ai-prompt transition (must come after .ai-prompt-row to win cascade) ── */
.ai-prompt-enter-active { transition: opacity .2s ease, transform .22s cubic-bezier(.16,1,.3,1); }
.ai-prompt-leave-active { transition: opacity .14s ease-in, transform .14s ease-in; }
.ai-prompt-enter-from,
.ai-prompt-leave-to   { opacity: 0; transform: translateY(-8px) scaleY(.9); }
.ai-prompt-row.generating {
  border-color: var(--accent);
  animation: aiGenGlow 1.4s ease-in-out infinite;
}

.ai-star { font-family: 'JetBrains Mono', monospace; font-size: 11px; color: var(--accent); flex: none; }
.ai-prompt-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--ink);
  font-family: 'Manrope', sans-serif;
  font-size: 13px;
  min-width: 0;
  transition: opacity .2s;
}
.ai-prompt-input:disabled { opacity: .45; }
.ai-gen-btn {
  background: var(--accent);
  color: #15110a;
  border: none;
  border-radius: 7px;
  padding: 6px 12px;
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  font-size: 12px;
  cursor: pointer;
  flex: none;
  min-width: 46px;
  text-align: center;
  transition: filter .2s, opacity .2s;
}
.ai-gen-btn:hover:not(:disabled) { filter: brightness(1.1); }
.ai-gen-btn:disabled { opacity: .7; cursor: default; }

/* ── generating dots ── */
.gen-dot {
  font-size: 16px;
  line-height: 1;
  animation: genDotBlink 1.2s infinite;
}
.d1 { animation-delay: 0s; }
.d2 { animation-delay: .18s; }
.d3 { animation-delay: .36s; }

@keyframes genDotBlink {
  0%, 60%, 100% { opacity: .15; }
  30% { opacity: 1; }
}
@keyframes aiGenGlow {
  0%, 100% { box-shadow: 0 0 6px var(--glow); }
  50%  { box-shadow: 0 0 18px var(--glow); }
}

.hero-row {
  display: flex;
  gap: clamp(20px,4vw,52px);
  flex-wrap: wrap;
  align-items: flex-end;
  margin-top: 16px;
}
.name-block {}
.name {
  font-family: 'Big Shoulders Display', sans-serif;
  font-weight: 900;
  font-size: clamp(58px, 11vw, 150px);
  line-height: .78;
  letter-spacing: -.01em;
  text-transform: uppercase;
  color: var(--ink);
}
.accent-bar {
  height: 13px;
  width: min(280px, 62%);
  background: var(--accent);
  transform: skewX(-14deg);
  margin: 16px 0 0 5px;
}
.bio-block {
  max-width: 460px;
  padding-bottom: 6px;
}
.tagline {
  font-style: italic;
  font-size: clamp(16px, 1.7vw, 21px);
  line-height: 1.4;
  color: var(--accent);
  margin: 0 0 16px;
}
.chips-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  padding: 6px 10px;
  border: 1px solid var(--panel-line);
  border-radius: 6px;
  color: var(--ink-dim);
  background: var(--chip);
}
.links-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}
.link-pill {
  font-family: 'Manrope', sans-serif;
  font-size: 13px;
  font-weight: 600;
  padding: 9px 14px;
  border: 1px solid var(--panel-line);
  border-radius: 999px;
  color: var(--ink);
  background: var(--chip);
  text-decoration: none;
  transition: border-color .2s, color .2s;
}
.link-pill:hover { border-color: var(--accent); color: var(--accent); }
.link-pill--cv { border-color: var(--accent); color: var(--accent); }
.link-pill--cv:hover { background: var(--accent); color: #15110a; }
.link-pill--book { border: 1px solid var(--panel-line); color: var(--ink); background: var(--chip); cursor: pointer; font-family: 'Manrope', sans-serif; font-size: 13px; font-weight: 600; }
.link-pill--book:hover { border-color: var(--accent2); color: var(--accent2); }
</style>
