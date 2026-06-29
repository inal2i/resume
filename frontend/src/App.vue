<template>
  <div :data-theme="state.theme" class="resume-root">
    <div class="bg-grid" />
    <ResumeHeader />
    <ModulesGrid />
    <AiChatPanel />
    <button class="chat-fab" aria-label="Открыть чат" @click="toggleChat">
      <span class="chat-fab-icon">💬</span>
    </button>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useStore, startTimers } from './composables/useStore.js'
import ResumeHeader from './components/ResumeHeader.vue'
import ModulesGrid from './components/ModulesGrid.vue'
import AiChatPanel from './components/AiChatPanel.vue'

const { state, toggleChat } = useStore()
let stopTimers
onMounted(() => { stopTimers = startTimers() })
onUnmounted(() => { stopTimers?.() })
</script>

<style>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: 'Manrope', system-ui, sans-serif; -webkit-font-smoothing: antialiased; }

/* AI-тема: дефолтные CSS-переменные для ai-palette */
:root {
  --ai-bg: #0d0b1a; --ai-bg2: #1a1030; --ai-panel: #130f22; --ai-panel2: #1e1635;
  --ai-line: rgba(160,100,255,0.18); --ai-ink: #f0ecff; --ai-dim: #8878b8;
  --ai-accent: #c084fc; --ai-accent2: #67e8f9; --ai-chip: #1c1530;
  --ai-tape: rgba(192,132,252,0.22); --ai-glow: rgba(192,132,252,0.5);
}

/* FORGE → Darcula (IntelliJ IDEA Dark) */
[data-theme] {
  --bg: #2b2b2b; --bg2: #313335;
  --panel: #3c3f41; --panel-2: #454749;
  --panel-line: rgba(169,183,198,0.14);
  --ink: #a9b7c6; --ink-dim: #6e8099;
  --accent: #5faaef; --accent2: #cc7832;
  --chip: #353739; --tape: rgba(95,170,239,0.22);
  --glow: rgba(95,170,239,0.4);
}

/* DOSSIER → IntelliJ IDEA Light */
[data-theme="dossier"] {
  --bg: #ffffff; --bg2: #f2f2f2;
  --panel: #f8f8f8; --panel-2: #ededed;
  --panel-line: rgba(0,0,0,0.12);
  --ink: #1a1a1a; --ink-dim: #737373;
  --accent: #4477ce; --accent2: #cc7832;
  --chip: #ebebeb; --tape: rgba(68,119,206,0.18);
  --glow: rgba(68,119,206,0.32);
}

[data-theme="neon"] {
  --bg: #070c10; --bg2: #0a141b;
  --panel: #0f1820; --panel-2: #13212b;
  --panel-line: rgba(74,214,208,0.18);
  --ink: #e9f6f7; --ink-dim: #88a4a8;
  --accent: #46d6d0; --accent2: #86a8ff;
  --chip: #102029; --tape: rgba(74,214,208,0.22);
  --glow: rgba(74,214,208,0.5);
}

[data-theme="ai"] {
  --bg: var(--ai-bg); --bg2: var(--ai-bg2);
  --panel: var(--ai-panel); --panel-2: var(--ai-panel2);
  --panel-line: var(--ai-line);
  --ink: var(--ai-ink); --ink-dim: var(--ai-dim);
  --accent: var(--ai-accent); --accent2: var(--ai-accent2);
  --chip: var(--ai-chip); --tape: var(--ai-tape);
  --glow: var(--ai-glow);
}

@keyframes pulseDot { 0%, 100% { opacity: 1 } 50% { opacity: .3 } }
@keyframes hlpulse {
  0%   { box-shadow: 0 0 0 2px var(--accent), 0 0 0 var(--glow) }
  25%  { box-shadow: 0 0 0 2px var(--accent), 0 0 46px var(--glow) }
  100% { box-shadow: 0 0 0 1px var(--accent), 0 0 16px var(--glow) }
}
@keyframes spawnIn { from { opacity: 0; transform: translateY(16px) } to { opacity: 1; transform: none } }

[data-hl="1"] { animation: hlpulse 2.6s ease-out forwards; border-color: var(--accent) !important; }
[data-spawn="1"] { animation: spawnIn .5s ease; border-left: 3px solid var(--accent) !important; }
</style>

<style scoped>
.resume-root {
  position: relative;
  min-height: 100vh;
  background: radial-gradient(1100px 620px at 70% -12%, var(--bg2), transparent 62%), var(--bg);
  color: var(--ink);
  padding: 0 clamp(300px, 30vw, 380px) 56px 0;
  overflow-x: hidden;
  transition: background .4s, color .4s;
}
.bg-grid {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background-image:
    linear-gradient(var(--panel-line) 1px, transparent 1px),
    linear-gradient(90deg, var(--panel-line) 1px, transparent 1px);
  background-size: 46px 46px;
  opacity: .5;
  z-index: 0;
}

/* FAB для открытия чата — только на мобильном */
.chat-fab {
  display: none;
  position: fixed;
  bottom: 24px;
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--accent);
  border: none;
  box-shadow: 0 4px 20px rgba(0,0,0,.4);
  cursor: pointer;
  z-index: 60;
  align-items: center;
  justify-content: center;
  transition: transform .2s, box-shadow .2s;
}
.chat-fab:hover { transform: scale(1.08); box-shadow: 0 6px 28px rgba(0,0,0,.5); }
.chat-fab-icon { font-size: 24px; line-height: 1; }

@media (max-width: 768px) {
  .resume-root {
    padding-right: 0;
  }
  .chat-fab {
    display: flex;
  }
}
</style>
