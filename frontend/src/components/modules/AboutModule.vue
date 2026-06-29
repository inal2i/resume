<template>
  <div style="position:relative">
    <span class="tape" />
    <div class="label">ABOUT</div>
    <h3 class="title">О себе</h3>
    <p class="bio">Проектирую и поддерживаю масштабируемые микросервисные архитектуры в высоконагруженных системах. Ценю надёжность, производительность и чистый, поддерживаемый код. Вырос в крупных техкомпаниях и кросс-функциональных командах.</p>
    <div class="traits">
      <span class="trait">Ответственный</span>
      <span class="trait">Командный</span>
      <span class="trait">Самообучаемый</span>
      <span class="trait">Наставник</span>
      <span class="trait">Любит изящные решения</span>
    </div>
    <div class="devize">CALM MIND · CLEAN CODE · OPEN PARTY</div>
  </div>
</template>

<script setup>
defineProps(['module'])
</script>

<style scoped>
.tape { position: absolute; top: -10px; left: 26px; width: 74px; height: 20px; background: var(--tape); border: 1px solid var(--panel-line); transform: rotate(-3deg); display: block; }
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent); margin: 0 0 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 26px; line-height: .95; text-transform: uppercase; letter-spacing: .01em; margin: 2px 0 12px; color: var(--ink); }
.bio { color: var(--ink); font-size: 14px; line-height: 1.6; margin: 0 0 14px; }
.traits { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.trait { font-family: 'Manrope', sans-serif; font-size: 12px; font-weight: 600; padding: 6px 11px; border: 1px solid var(--panel-line); border-radius: 999px; color: var(--ink-dim); background: var(--chip); }
.devize { border-top: 1px solid var(--panel-line); padding-top: 14px; font-family: 'Big Shoulders Display', sans-serif; font-weight: 900; font-size: 21px; text-transform: uppercase; letter-spacing: .02em; color: var(--accent); }
</style>
