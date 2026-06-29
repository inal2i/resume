<template>
  <div>
    <div class="label">QUICK STATS</div>
    <h3 class="title">Сводка</h3>
    <div class="rows">
      <div v-for="r in module.rows" :key="r.label" class="row">
        <span class="row-label">{{ r.label }}</span>
        <span class="row-value">{{ r.label === 'Опыт' ? state.expStr : r.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useStore } from '../../composables/useStore.js'

defineProps(['module'])
const { state } = useStore()
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent); margin: 0 0 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 26px; line-height: .95; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 10px; color: var(--ink); }
.rows { display: flex; flex-direction: column; }
.row { display: flex; justify-content: space-between; gap: 12px; padding: 9px 0; border-bottom: 1px solid var(--panel-line); }
.row-label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .08em; text-transform: uppercase; color: var(--ink-dim); }
.row-value { font-size: 13px; font-weight: 600; text-align: right; color: var(--ink); }
</style>
