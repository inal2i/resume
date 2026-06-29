<template>
  <div>
    <div class="label">WEAPON · СТЕК</div>
    <h3 class="title">СТЕК</h3>

    <div class="section-label">CORE</div>
    <div class="chips">
      <span
        v-for="c in module.core" :key="c.name"
        class="tech-chip"
        @mouseenter="show(c, $event)"
        @mousemove="move($event)"
        @mouseleave="hide"
        @touchstart.prevent="showTouch(c, $event)"
      >{{ c.name }}</span>
    </div>

    <div class="section-label" style="margin-top:14px">DATA / INFRA</div>
    <div class="chips">
      <span
        v-for="c in module.infra" :key="c.name"
        class="tech-chip"
        @mouseenter="show(c, $event)"
        @mousemove="move($event)"
        @mouseleave="hide"
        @touchstart.prevent="showTouch(c, $event)"
      >{{ c.name }}</span>
    </div>

    <template v-if="module.ai?.length">
      <div class="section-label" style="margin-top:14px">AI / LLM</div>
      <div class="chips">
        <span
          v-for="c in module.ai" :key="c.name"
          class="tech-chip"
          @mouseenter="show(c, $event)"
          @mousemove="move($event)"
          @mouseleave="hide"
          @touchstart.prevent="showTouch(c, $event)"
        >{{ c.name }}</span>
      </div>
    </template>

    <template v-if="module.vibe?.length">
      <div class="section-label" style="margin-top:14px">VIBE CODING</div>
      <div class="chips">
        <span
          v-for="c in module.vibe" :key="c.name"
          class="tech-chip"
          @mouseenter="show(c, $event)"
          @mousemove="move($event)"
          @mouseleave="hide"
          @touchstart.prevent="showTouch(c, $event)"
        >{{ c.name }}</span>
      </div>
    </template>

    <Teleport to="body">
      <div
        v-if="hovered"
        class="stack-tooltip"
        :data-theme="state.theme"
        :style="tooltipStyle"
      >
        <div class="stack-tooltip__desc">{{ hovered.desc }}</div>
        <div class="stack-tooltip__label">ПРОЕКТЫ</div>
        <ul class="stack-tooltip__projects">
          <li v-for="p in hovered.projects" :key="p">{{ p }}</li>
        </ul>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from '../../composables/useStore.js'

defineProps(['module'])

const { state } = useStore()

const hovered = ref(null)
const cursorX = ref(0)
const cursorY = ref(0)

function show(chip, e) {
  hovered.value = chip
  cursorX.value = e.clientX
  cursorY.value = e.clientY
}

function move(e) {
  cursorX.value = e.clientX
  cursorY.value = e.clientY
}

function hide() {
  hovered.value = null
}

let touchHideHandler = null

function showTouch(chip, e) {
  const touch = e.touches[0]
  hovered.value = chip
  cursorX.value = touch.clientX
  cursorY.value = touch.clientY

  if (touchHideHandler) {
    document.removeEventListener('touchstart', touchHideHandler)
  }
  touchHideHandler = (ev) => {
    if (!ev.target.closest('.tech-chip')) {
      hovered.value = null
      document.removeEventListener('touchstart', touchHideHandler)
      touchHideHandler = null
    }
  }
  setTimeout(() => document.addEventListener('touchstart', touchHideHandler), 0)
}

const OFFSET    = 14
const TOOLTIP_W = 260
const TOOLTIP_H = 160

const tooltipStyle = computed(() => {
  const x  = cursorX.value
  const y  = cursorY.value
  const vw = window.innerWidth
  const vh = window.innerHeight

  let left = x + OFFSET
  if (left + TOOLTIP_W > vw - 8) left = x - TOOLTIP_W - OFFSET
  if (left < 8) left = 8

  let top = y + OFFSET
  if (top + TOOLTIP_H > vh - 8) top = y - TOOLTIP_H - OFFSET
  if (top < 8) top = 8

  return { left: `${left}px`, top: `${top}px` }
})
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent); margin: 0 0 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 26px; line-height: .95; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 12px; color: var(--ink); }
.section-label { font-family: 'JetBrains Mono', monospace; font-size: 10px; letter-spacing: .16em; color: var(--ink-dim); margin: 6px 0 8px; text-transform: uppercase; }
.chips { display: flex; flex-wrap: wrap; gap: 8px; }
.tech-chip { font-family: 'JetBrains Mono', monospace; font-size: 12px; padding: 6px 10px; border: 1px solid var(--panel-line); border-radius: 8px; color: var(--ink); background: var(--chip); cursor: default; transition: border-color .15s, color .15s; }
.tech-chip:hover { border-color: var(--accent); color: var(--accent); }
</style>

<style>
.stack-tooltip {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
  width: 260px;
  background: var(--panel-2, var(--panel));
  border: 1px solid var(--panel-line);
  border-left: 3px solid var(--accent);
  border-radius: 6px;
  padding: 12px 14px;
  box-shadow: 0 8px 32px rgba(0,0,0,.6);
  animation: stackTooltipIn .15s ease;
}
.stack-tooltip__desc {
  color: var(--ink);
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 8px;
}
.stack-tooltip__label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  letter-spacing: .1em;
  color: var(--ink-dim);
  margin-bottom: 5px;
  text-transform: uppercase;
}
.stack-tooltip__projects {
  margin: 0;
  padding-left: 16px;
  color: var(--accent);
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  line-height: 1.6;
}
.stack-tooltip__projects li { margin-bottom: 1px; }

@keyframes stackTooltipIn {
  from { opacity: 0; transform: translateY(4px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
