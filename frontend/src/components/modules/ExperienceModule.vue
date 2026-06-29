<template>
  <div>
    <div class="label">TURNAROUND</div>
    <h3 class="title">ОПЫТ</h3>
    <div class="entries">
      <div
        v-for="e in module.entries"
        :key="e.company"
        class="entry"
        @mouseenter="show(e, $event)"
        @mousemove="move($event)"
        @mouseleave="hide"
        @touchstart.prevent="showTouch(e, $event)"
      >
        <span class="entry-dot" />
        <div class="entry-range">{{ e.range }} · {{ e.dur }}</div>
        <div class="entry-company">{{ e.company }}</div>
        <div class="entry-role">{{ e.role }}</div>
      </div>
    </div>

    <Teleport to="body">
      <div
        v-if="hovered"
        class="exp-tooltip"
        :data-theme="state.theme"
        :style="tooltipStyle"
      >
        <div class="exp-tooltip__place">{{ hovered.place }}</div>
        <ul class="exp-tooltip__points">
          <li v-for="pt in hovered.points" :key="pt">{{ pt }}</li>
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

function show(entry, e) {
  hovered.value = entry
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

function showTouch(entry, e) {
  const touch = e.touches[0]
  hovered.value = entry
  cursorX.value = touch.clientX
  cursorY.value = touch.clientY

  if (touchHideHandler) {
    document.removeEventListener('touchstart', touchHideHandler)
  }
  touchHideHandler = (ev) => {
    if (!ev.target.closest('.entry')) {
      hovered.value = null
      document.removeEventListener('touchstart', touchHideHandler)
      touchHideHandler = null
    }
  }
  setTimeout(() => document.addEventListener('touchstart', touchHideHandler), 0)
}

const OFFSET    = 14
const TOOLTIP_W = 280
const TOOLTIP_H = 200

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
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 26px; line-height: .95; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 14px; color: var(--ink); }
.entries { display: flex; flex-direction: column; gap: 16px; }
.entry { border-left: 2px solid var(--panel-line); padding-left: 14px; position: relative; cursor: default; }
.entry-dot { position: absolute; left: -5px; top: 5px; width: 8px; height: 8px; border-radius: 50%; background: var(--accent); }
.entry-range { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .1em; color: var(--accent); }
.entry-company { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 20px; text-transform: uppercase; margin-top: 2px; color: var(--ink); }
.entry-role { font-size: 12.5px; color: var(--ink-dim); margin-top: 1px; }
</style>

<style>
.exp-tooltip {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
  width: 280px;
  background: var(--panel-2, var(--panel));
  border: 1px solid var(--panel-line);
  border-left: 3px solid var(--accent);
  border-radius: 6px;
  padding: 12px 14px;
  box-shadow: 0 8px 32px rgba(0,0,0,.6);
  animation: tooltipIn .15s ease;
}
.exp-tooltip__place {
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  letter-spacing: .1em;
  color: var(--ink-dim);
  margin-bottom: 6px;
  text-transform: uppercase;
}
.exp-tooltip__points {
  margin: 0;
  padding-left: 16px;
  color: var(--ink);
  font-size: 13px;
  line-height: 1.5;
}
.exp-tooltip__points li { margin-bottom: 3px; }

@keyframes tooltipIn {
  from { opacity: 0; transform: translateY(4px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
