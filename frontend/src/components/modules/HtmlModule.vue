<template>
  <div class="html-module">
    <div class="label">// {{ module.title }}</div>
    <iframe
      :srcdoc="framedHtml"
      sandbox="allow-scripts"
      class="html-frame"
      :style="{ height: frameHeight + 'px' }"
      ref="frameRef"
      @load="onLoad"
    />
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useStore } from '../../composables/useStore.js'

const props = defineProps(['module'])
const { state } = useStore()
const frameRef = ref(null)
const frameHeight = ref(60)

const VAR_NAMES = ['--bg','--bg2','--panel','--panel-2','--panel-line','--ink','--ink-dim','--accent','--accent2','--chip','--tape','--glow']

const IFRAME_SCRIPTS = `<script>
(function(){
  window.addEventListener('message',function(e){
    if(e.data&&e.data.__themeVars){
      var r=document.documentElement;
      Object.keys(e.data.__themeVars).forEach(function(k){r.style.setProperty(k,e.data.__themeVars[k]);});
    }
  });
  function reportHeight(){
    var h=Math.max(document.body.scrollHeight,document.documentElement.scrollHeight);
    parent.postMessage({__iframeH:h},'*');
  }
  window.addEventListener('load',reportHeight);
  new MutationObserver(reportHeight).observe(document.documentElement,{subtree:true,childList:true,attributes:true,characterData:true});
  setTimeout(reportHeight,400);
})();
<\/script>`

const BASE_STYLES = `<style>
:root{
  --bg:#2b2b2b;--bg2:#313335;--panel:#3c3f41;--panel-2:#454749;
  --panel-line:rgba(169,183,198,0.14);--ink:#a9b7c6;--ink-dim:#6e8099;
  --accent:#5faaef;--accent2:#cc7832;--chip:#353739;
  --tape:rgba(95,170,239,0.22);--glow:rgba(95,170,239,0.4);
}
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:'JetBrains Mono',monospace;background:transparent;color:var(--ink);font-size:13px;padding:6px;overflow:hidden}
canvas{max-width:100%;display:block}
svg{max-width:100%;display:block}
table{border-collapse:collapse;width:100%}
td,th{border:1px solid var(--panel-line);padding:4px 8px}
th{background:rgba(255,255,255,.06);color:var(--accent)}
a{color:var(--accent2)}
</style>`

const framedHtml = computed(() => {
  const html = props.module.html || ''
  if (/^\s*<!doctype|^\s*<html/i.test(html)) {
    return html.includes('</body>')
      ? html.replace('</body>', IFRAME_SCRIPTS + '</body>')
      : html + IFRAME_SCRIPTS
  }
  return `<!DOCTYPE html><html><head><meta charset="utf-8">${BASE_STYLES}</head><body>${html}${IFRAME_SCRIPTS}</body></html>`
})

function readThemeVars() {
  const style = getComputedStyle(document.documentElement)
  const vars = {}
  VAR_NAMES.forEach(v => { vars[v] = style.getPropertyValue(v).trim() })
  return vars
}

function sendThemeVars() {
  nextTick(() => {
    frameRef.value?.contentWindow?.postMessage({ __themeVars: readThemeVars() }, '*')
  })
}

function onLoad() {
  sendThemeVars()
}

watch(() => state.theme, sendThemeVars)

function onMessage(e) {
  if (frameRef.value && e.source === frameRef.value.contentWindow && typeof e.data?.__iframeH === 'number') {
    frameHeight.value = Math.min(Math.max(e.data.__iframeH + 8, 60), 600)
  }
}

onMounted(() => window.addEventListener('message', onMessage))
onUnmounted(() => window.removeEventListener('message', onMessage))
</script>

<style scoped>
.label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  letter-spacing: .14em;
  text-transform: uppercase;
  color: var(--accent2);
  margin: 0 0 10px;
}
.html-frame {
  width: 100%;
  border: 1px solid var(--panel-line);
  border-radius: 8px;
  background: transparent;
  display: block;
  overflow: hidden;
  transition: height .25s ease;
}
</style>
