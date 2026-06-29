<template>
  <div>
    <div class="label">POWERS &amp; ABILITIES</div>
    <h3 class="title">Способности</h3>
    <div class="items">
      <div v-for="p in module.items" :key="p.n" class="item">
        <div class="num">{{ p.n }}</div>
        <div>
          <div class="item-title">{{ p.title }}</div>
          <div class="item-desc">{{ p.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps(['module'])
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent); margin: 0 0 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 26px; line-height: .95; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 14px; color: var(--ink); }
.items { display: flex; flex-direction: column; gap: 14px; }
.item { display: flex; gap: 14px; align-items: flex-start; }
.num { font-family: 'JetBrains Mono', monospace; color: var(--accent); font-size: 13px; font-weight: 700; min-width: 24px; }
.item-title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 18px; text-transform: uppercase; letter-spacing: .02em; color: var(--ink); }
.item-desc { color: var(--ink-dim); font-size: 13.5px; line-height: 1.45; margin-top: 3px; }
</style>
