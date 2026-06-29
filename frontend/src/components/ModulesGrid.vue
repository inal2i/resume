<template>
  <main class="modules-grid">
    <template v-for="module in visibleModules" :key="module.id">
      <article
        :data-hl="state.hlId === module.id ? '1' : ''"
        :data-spawn="module.kind === 'spawn' ? '1' : ''"
        :style="{ columnSpan: module.size === 'wide' ? 'all' : 'unset' }"
        class="module-card"
      >
        <span class="dot" />
        <StackModule v-if="module.kind === 'stack'" :module="module" />
        <ExperienceModule v-else-if="module.kind === 'experience'" :module="module" />
        <PowersModule v-else-if="module.kind === 'powers'" :module="module" />
        <AboutModule v-else-if="module.kind === 'about'" :module="module" />
        <StatsModule v-else-if="module.kind === 'stats'" :module="module" />
        <UmlModule v-else-if="module.kind === 'uml' && module.umlVisible" :module="module" />
        <CalendarModule v-else-if="module.kind === 'calendar' && module.calendarVisible" :module="module" />
        <HtmlModule v-else-if="module.kind === 'html'" :module="module" />
        <SpawnModule v-else-if="module.kind === 'spawn'" :module="module" />
      </article>
    </template>
  </main>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from '../composables/useStore.js'
import StackModule from './modules/StackModule.vue'
import ExperienceModule from './modules/ExperienceModule.vue'
import PowersModule from './modules/PowersModule.vue'
import AboutModule from './modules/AboutModule.vue'
import StatsModule from './modules/StatsModule.vue'
import UmlModule from './modules/UmlModule.vue'
import CalendarModule from './modules/CalendarModule.vue'
import HtmlModule from './modules/HtmlModule.vue'
import SpawnModule from './modules/SpawnModule.vue'

const { state } = useStore()

const visibleModules = computed(() =>
  state.modules.filter(m =>
    (m.kind !== 'uml' || m.umlVisible) &&
    (m.kind !== 'calendar' || m.calendarVisible)
  )
)
</script>

<style scoped>
.modules-grid {
  position: relative;
  z-index: 1;
  column-width: 330px;
  column-gap: 22px;
  padding: 6px clamp(20px,5vw,64px) 0;
}
.module-card {
  background: var(--panel);
  border: 1px solid var(--panel-line);
  border-radius: 14px;
  padding: 22px;
  margin: 0 0 22px;
  break-inside: avoid;
  position: relative;
  color: var(--ink);
  box-shadow: 0 12px 30px rgba(0,0,0,.26);
  transition: transform .35s ease, box-shadow .35s ease, border-color .4s;
}
.module-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 40px rgba(0,0,0,.4);
}
.dot {
  position: absolute;
  top: 14px;
  right: 16px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--accent);
  opacity: .5;
}
</style>
