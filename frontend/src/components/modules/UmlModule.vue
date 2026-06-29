<template>
  <div>
    <div class="label">// UML · DIAGRAM</div>
    <div class="uml-header">
      <h3 class="title">{{ module.title }}</h3>
      <div class="uml-btns">
        <button class="uml-btn" @click="toggleUmlCode">
          {{ module.umlCodeOpen ? '▲ UML code' : '▼ UML code' }}
        </button>
        <button class="uml-btn" @click="hideUml">✕</button>
      </div>
    </div>
    <div ref="mermaidEl" class="mermaid-target" />
    <div v-if="module.umlCodeOpen" class="code-block">
      <pre class="code-pre">{{ module.code }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useStore } from '../../composables/useStore.js'

const props = defineProps(['module'])
const { state, hideUml, toggleUmlCode } = useStore()
const mermaidEl = ref(null)

async function renderMermaid(code) {
  if (!code) return
  for (let i = 0; i < 30; i++) {
    if (window.mermaid) break
    await new Promise(r => setTimeout(r, 100))
  }
  if (!window.mermaid) return
  await nextTick()
  const el = mermaidEl.value
  if (!el) return
  el.innerHTML = ''
  try {
    const isDark = state.theme !== 'dossier'
    window.mermaid.initialize({ startOnLoad: false, theme: isDark ? 'dark' : 'default', darkMode: isDark })
    el.className = 'mermaid'
    el.textContent = code.trim()
    await window.mermaid.run({ nodes: [el] })
    const svg = el.querySelector('svg')
    if (svg) { svg.style.maxWidth = '100%'; svg.style.height = 'auto'; svg.style.borderRadius = '8px' }
  } catch (e) {
    el.innerHTML = `<pre style="color:var(--accent);font-family:'JetBrains Mono',monospace;font-size:12px;padding:10px;white-space:pre-wrap;line-height:1.5">⚠ Ошибка Mermaid:\n${e.message}</pre>`
  }
}

watch(() => props.module.code, async (code) => {
  if (code && props.module.umlVisible) {
    await renderMermaid(code)
  }
}, { immediate: true })
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent2); margin: 0 0 8px; }
.uml-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 8px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 24px; line-height: 1; text-transform: uppercase; margin: 0; color: var(--ink); }
.uml-btns { display: flex; gap: 6px; flex-shrink: 0; }
.uml-btn { background: none; border: 1px solid var(--panel-line); color: var(--ink-dim); border-radius: 7px; padding: 5px 10px; font-family: 'JetBrains Mono', monospace; font-size: 11px; cursor: pointer; transition: border-color .2s, color .2s; }
.uml-btn:hover { border-color: var(--accent); color: var(--ink); }
.mermaid-target { background: var(--chip); border-radius: 10px; padding: 14px; min-height: 80px; overflow: auto; text-align: center; }
.code-block { margin-top: 10px; border-top: 1px solid var(--panel-line); padding-top: 10px; }
.code-pre { font-family: 'JetBrains Mono', monospace; font-size: 11px; color: var(--ink-dim); margin: 0; white-space: pre-wrap; word-break: break-all; line-height: 1.5; }
</style>
