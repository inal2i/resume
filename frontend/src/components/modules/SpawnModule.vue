<template>
  <div>
    <div class="label">// СГЕНЕРИРОВАНО AI</div>
    <h3 class="title">{{ module.title }}</h3>
    <div class="lines">
      <template v-for="(ln, i) in lines" :key="i">
        <div v-if="ln.bullet" class="line-bullet">
          <span class="bullet-mark">◆</span>
          <span>{{ ln.text }}</span>
        </div>
        <div v-else class="line-text">{{ ln.text }}</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { parseLines } from '../../composables/useStore.js'

const props = defineProps(['module'])
const lines = computed(() => parseLines(props.module.body))
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .14em; text-transform: uppercase; color: var(--accent2); margin: 0 0 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 24px; line-height: 1; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 12px; color: var(--ink); }
.lines { display: flex; flex-direction: column; gap: 7px; }
.line-bullet { display: flex; gap: 9px; font-size: 13.5px; line-height: 1.5; color: var(--ink); }
.bullet-mark { color: var(--accent); flex: none; }
.line-text { font-size: 13.5px; line-height: 1.55; color: var(--ink-dim); }
</style>
