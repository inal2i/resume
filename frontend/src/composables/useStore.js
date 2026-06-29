import { reactive, watch } from 'vue'

export const TITLES = [
  'INAL TUAEV · Backend Developer',
  'ROLE: Разработчик распределённых систем',
  'В проде с 2016.',
  'WEAPON: Java · Spring · Kafka · K8s',
  '9+ лет под нагрузкой.',
  'Открыт к сильным задачам →',
  'POWERS: Scale. Optimize. Mentor.',
  'CLEAN CODE · UNSTOPPABLE DRIVE',
  'Инал Туаев · Senior Backend Engineer',
]

export function calcExp() {
  const start = new Date(2016, 0, 1)
  const now = new Date()
  let years = now.getFullYear() - start.getFullYear()
  let months = now.getMonth() - start.getMonth()
  let days = now.getDate() - start.getDate()
  if (days < 0) { months--; days += new Date(now.getFullYear(), now.getMonth(), 0).getDate() }
  if (months < 0) { years--; months += 12 }
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  const ss = String(now.getSeconds()).padStart(2, '0')
  return `~${years}г ${months}м ${days}д ${hh}:${mm}:${ss}`
}

function initialModules() {
  return [
    {
      id: 'stack', kind: 'stack', size: 'normal',
      core: [
        { name: 'Java SE / EE',             desc: 'Основной язык backend: потоки, коллекции, сетевой стек, JEE-сервисы.',          projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'RedLab', 'Litota', 'ScrollCast'] },
        { name: 'Spring Framework',          desc: 'IoC, AOP, Security, Data — фундамент enterprise-приложений.',                  projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'RedLab', 'Litota'] },
        { name: 'Spring Boot',               desc: 'Быстрый старт микросервисов и монолитов с автоконфигурацией.',                 projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'RedLab', 'zpshka.ru', 'resume'] },
        { name: 'Apache Kafka',              desc: 'Брокер сообщений для async-обмена между распределёнными сервисами.',           projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс'] },
        { name: 'Многопоточность',           desc: 'Thread pools, locks, volatile, concurrent-коллекции — безопасный параллелизм.', projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'RedLab', 'Litota'] },
        { name: 'REST API',                  desc: 'Проектирование и реализация HTTP-интерфейсов; обратная совместимость.',        projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'RedLab', 'zpshka.ru', 'resume'] },
        { name: 'WebSocket',                 desc: 'Realtime-обмен: чат, live-обновления, push-уведомления.',                     projects: ['ScrollCast', 'resume'] },
        { name: 'Алгоритмы и структуры',     desc: 'Оптимизация поиска и обработки данных: поиск ускорен на порядок.',            projects: ['Т-Банк', 'Яндекс'] },
      ],
      infra: [
        { name: 'PostgreSQL',  desc: 'Основная реляционная СУБД; PostGIS, GIN/GiST, триграммный поиск.',      projects: ['ScrollCast', 'zpshka.ru', 'resume'] },
        { name: 'Redis',       desc: 'Кэш, хранилище сессий, pub/sub.',                                       projects: ['Т-Банк', 'Яндекс', 'Магнит OMNI'] },
        { name: 'MongoDB',     desc: 'Документоориентированная БД для гибких схем данных.',                   projects: ['RedLab', 'Litota'] },
        { name: 'SQL / JDBC',  desc: 'Сложные запросы и оптимизация планов выполнения.',                     projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс', 'ScrollCast', 'zpshka.ru'] },
        { name: 'Liquibase',   desc: 'Версионированные миграции схемы БД.',                                  projects: ['zpshka.ru', 'resume'] },
        { name: 'Docker',      desc: 'Контейнеризация: сборка образов и локальный запуск сервисов.',          projects: ['Магнит OMNI', 'Яндекс', 'zpshka.ru'] },
        { name: 'Kubernetes',  desc: 'Оркестрация: деплой, масштабирование, rolling updates.',               projects: ['Магнит OMNI', 'Т-Банк', 'Яндекс'] },
        { name: 'Ubuntu',      desc: 'Серверная ОС для self-hosted деплоя JAR/Docker-контейнеров.',           projects: ['zpshka.ru', 'resume'] },
      ],
      ai: [
        { name: 'OpenAI compatible APIs', desc: 'LLM с OpenAI-совместимым интерфейсом (GigaChat): чат, function calling, streaming.', projects: ['zpshka.ru', 'resume'] },
        { name: 'Spring AI',              desc: 'Java/Kotlin-фреймворк для LLM: tool calling, chat memory, advisors.',               projects: ['zpshka.ru', 'resume'] },
        { name: 'LangGraph4j',            desc: 'Граф состояний для multi-agent workflow: routing, условные рёбра, чекпоинты.',      projects: ['zpshka.ru'] },
        { name: 'RAG',                    desc: 'Retrieval Augmented Generation: ответы на основе базы знаний с поиском по эмбеддингам.', projects: ['zpshka.ru'] },
        { name: 'Embedding Models',       desc: 'Векторное представление текста для семантического поиска в RAG-пайплайне.',         projects: ['zpshka.ru'] },
        { name: 'Vector Store',           desc: 'Хранилище векторных эмбеддингов для быстрого similarity-поиска.',                  projects: ['zpshka.ru'] },
        { name: 'Langfuse',               desc: 'Трассировка, метрики и мониторинг LLM-вызовов через OpenTelemetry.',               projects: ['zpshka.ru'] },
        { name: 'OpenTelemetry',          desc: 'Сбор трейсов и метрик для observability всего стека.',                             projects: ['zpshka.ru'] },
      ],
      vibe: [
        { name: 'Claude Code', desc: 'AI-агент в терминале: code review, рефакторинг, написание кода в паре с LLM.', projects: ['resume', 'zpshka.ru'] },
        { name: 'OpenCoder',   desc: 'AI-ассистент для написания кода в IDE.',                                        projects: ['resume', 'zpshka.ru'] },
      ],
    },
    {
      id: 'experience', kind: 'experience', size: 'normal',
      entries: [
        { range: '2025 — н.в.', dur: '8 мес', company: 'Магнит OMNI', role: 'Старший разработчик', place: 'Иннополис',
          points: ['Высоконагруженные распределённые системы на Java', 'Оптимизация производительности и отказоустойчивость', 'Масштабирование сервисов под пиковые нагрузки'] },
        { range: '2023 — 2025', dur: '2 г 5 мес', company: 'Т-Банк', role: 'Старший разработчик', place: 'Москва',
          points: ['Ключевые модули микросервиса, обратно-совместимый API', 'Поиск ускорен на порядок рефакторингом алгоритмов и SQL', 'Лекции по Java/Финтех, тех-интервью и найм'] },
        { range: '2021 — 2022', dur: '1 г 10 мес', company: 'Яндекс', role: 'Старший разработчик', place: 'Москва · Маркет',
          points: ['Высоконагруженный критичный сервис маркетплейса', 'Self-service управление этапами заказа', 'Защита покупателей от недобросовестных продавцов'] },
        { range: '2019 — 2021', dur: '1 г 5 мес', company: 'RedLab', role: 'Руководитель backend-отдела', place: 'Ульяновск',
          points: ['Найм и развитие команды backend-разработчиков', 'Архитектура: масштабируемость и отказоустойчивость', 'Code review, CI/CD, менторство, 1:1 и IDP'] },
        { range: '2017 — 2019', dur: '2 г 8 мес', company: 'Litota Labs', role: 'Старший Java-разработчик', place: 'Ульяновск',
          points: ['Панель управления букмекерской конторы', 'Интеграции gambling-провайдеров', 'Микросервисы ставок, сессий и realtime-обмена'] },
        { range: '2016 — 2017', dur: '9 мес', company: 'ScrollCast', role: 'Java-разработчик (full-stack)', place: 'Владикавказ',
          points: ['Android-приложение с нуля + backend на Java EE', 'PostgreSQL/PostGIS, триграммный поиск, GIN/GiST', 'WebSocket, FCM, ffmpeg, Google Maps'] },
      ],
    },
    {
      id: 'powers', kind: 'powers', size: 'normal',
      items: [
        { n: '01', title: 'Распределённые системы', desc: 'Надёжные, масштабируемые микросервисы под высокую нагрузку.' },
        { n: '02', title: 'Оптимизация', desc: 'Рефакторинг алгоритмов и SQL: кратное ускорение критических запросов.' },
        { n: '03', title: 'Отказоустойчивость', desc: 'Стабильность критичных сервисов, профилактика и устранение инцидентов.' },
        { n: '04', title: 'Инженерная культура', desc: 'Code review, тесты, CI/CD, документация — чистый и поддерживаемый код.' },
      ],
    },
    { id: 'about', kind: 'about', size: 'normal' },
    { id: 'uml', kind: 'uml', size: 'wide', umlVisible: false, code: '', title: 'Схема', umlCodeOpen: false },
    { id: 'calendar', kind: 'calendar', size: 'wide', calendarVisible: false },
    {
      id: 'stats', kind: 'stats', size: 'normal',
      rows: [
        { label: 'Опыт', value: '9 лет 7 мес' },
        { label: 'Локация', value: 'Владикавказ' },
        { label: 'Языки', value: 'Русский · English B2' },
        { label: 'Образование', value: 'ВАС им. Будённого, СПб · 2010' },
        { label: 'Фокус', value: 'Java · Spring · Kafka · K8s' },
        { label: 'Статус', value: 'Открыт к сильным задачам' },
      ],
    },
  ]
}

const savedTheme = localStorage.getItem('resume-theme') || 'forge'

const state = reactive({
  theme: savedTheme,
  modules: initialModules(),
  messages: [],
  loading: false,
  hlId: null,
  expStr: calcExp(),
  titleIdx: 0,
  aiPromptOpen: false,
  aiGenerating: false,
  aiPaletteName: '',
  aiPromptInput: '',
  input: '',
  spawnCount: 0,
  htmlCount: 0,
  chatOpen: false,
})

watch(() => state.theme, (t) => localStorage.setItem('resume-theme', t))

if (savedTheme === 'ai') {
  const saved = localStorage.getItem('resume-ai-palette')
  if (saved) {
    try {
      const vars = JSON.parse(saved)
      const root = document.documentElement
      Object.entries(vars).forEach(([k, v]) => root.style.setProperty(k, v))
    } catch (_) {}
  }
  const savedName = localStorage.getItem('resume-ai-name')
  if (savedName) state.aiPaletteName = savedName
}

let hlTimer = null

function promote(id) {
  const i = state.modules.findIndex(m => m.id === id)
  if (i <= 0) return
  const arr = [...state.modules]
  const [m] = arr.splice(i, 1)
  arr.unshift(m)
  state.modules = arr
}

function highlight(id) {
  promote(id)
  state.hlId = id
  clearTimeout(hlTimer)
  hlTimer = setTimeout(() => { state.hlId = null }, 4800)
}

function trimToMax(modules, max = 5) {
  const isVisible = m =>
    (m.kind !== 'uml' || m.umlVisible) &&
    (m.kind !== 'calendar' || m.calendarVisible)
  const visible = modules.filter(isVisible)
  if (visible.length <= max) return modules
  const keepIds = new Set(visible.slice(0, max).map(m => m.id))
  return modules.filter(m => !isVisible(m) || keepIds.has(m.id))
}

function spawn(title, body, size = 'normal') {
  const id = `spawn-${++state.spawnCount}`
  let arr = [{ id, kind: 'spawn', title, body, size }, ...state.modules]
  state.modules = trimToMax(arr, 5)
  state.hlId = id
  clearTimeout(hlTimer)
  hlTimer = setTimeout(() => { state.hlId = null }, 4800)
}

function showUml(title, code, size = 'wide') {
  state.modules = state.modules.map(m =>
    m.kind === 'uml' ? { ...m, umlVisible: true, title, code, size, umlCodeOpen: false } : m
  )
  const i = state.modules.findIndex(m => m.kind === 'uml')
  if (i > 0) {
    const arr = [...state.modules]
    const [m] = arr.splice(i, 1)
    arr.unshift(m)
    state.modules = trimToMax(arr, 5)
  }
}

function hideUml() {
  state.modules = state.modules.map(m =>
    m.kind === 'uml' ? { ...m, umlVisible: false } : m
  )
}

function showCalendar() {
  state.modules = state.modules.map(m =>
    m.kind === 'calendar' ? { ...m, calendarVisible: true } : m
  )
  const i = state.modules.findIndex(m => m.kind === 'calendar')
  if (i > 0) {
    const arr = [...state.modules]
    const [m] = arr.splice(i, 1)
    arr.unshift(m)
    state.modules = trimToMax(arr, 5)
  }
}

function hideCalendar() {
  state.modules = state.modules.map(m =>
    m.kind === 'calendar' ? { ...m, calendarVisible: false } : m
  )
}

function spawnHtml(title, html, size = 'normal') {
  const id = `html-${++state.htmlCount}`
  let arr = [{ id, kind: 'html', title, html, size }, ...state.modules]
  state.modules = trimToMax(arr, 5)
  state.hlId = id
  clearTimeout(hlTimer)
  hlTimer = setTimeout(() => { state.hlId = null }, 4800)
}

function toggleUmlCode() {
  state.modules = state.modules.map(m =>
    m.kind === 'uml' ? { ...m, umlCodeOpen: !m.umlCodeOpen } : m
  )
}

function toggleChat() {
  state.chatOpen = !state.chatOpen
}

function applyActionsAndCapture(actions) {
  const captured = []
  if (!Array.isArray(actions)) return captured
  actions.slice(0, 3).forEach(a => {
    if (!a?.type) return
    if (a.type === 'theme') {
      if (['forge', 'dossier', 'neon'].includes(a.theme)) state.theme = a.theme
      else if (a.theme === 'ai' && a.prompt) applyAiTheme(a.prompt)
    } else if (a.type === 'highlight' && a.target) {
      const m = state.modules.find(m => m.id === a.target)
      if (m) captured.push({ ...m })
      highlight(a.target)
    } else if (a.type === 'promote' && a.target) {
      const m = state.modules.find(m => m.id === a.target)
      if (m) captured.push({ ...m })
      promote(a.target)
    } else if (a.type === 'uml' && a.code) {
      showUml(a.title || 'Схема', a.code, a.size || 'wide')
      captured.push({ kind: 'uml', title: a.title || 'Схема', code: a.code, umlVisible: true })
    } else if (a.type === 'resize' && a.target) {
      state.modules = state.modules.map(m =>
        m.id === a.target ? { ...m, size: a.size || 'normal' } : m
      )
    } else if (a.type === 'spawn' && (a.title || a.body)) {
      spawn(a.title || 'НОВЫЙ МОДУЛЬ', a.body || '', a.size || 'normal')
      captured.push({ ...state.modules[0] })
    } else if (a.type === 'calendar') {
      showCalendar()
      captured.push({ kind: 'calendar' })
    } else if (a.type === 'html' && a.html) {
      spawnHtml(a.title || 'ВИЗУАЛИЗАЦИЯ', a.html, a.size || 'normal')
      captured.push({ ...state.modules[0] })
    }
  })
  return captured
}

function fallback(text) {
  const t = text.toLowerCase()
  let target = null
  let reply = ''
  let modules = []

  if (/kafka|кафк|очеред|брокер|стрим/.test(t)) {
    target = 'stack'; reply = 'Kafka — часть боевого набора: асинхронный обмен в распределённых сервисах. Подсветил стек.'
  } else if (/оптимиз|произво|скорост|латен|\bмс\b/.test(t)) {
    target = 'powers'; reply = 'Боевой пример: поиск ускорен на порядок — рефакторинг алгоритмов и SQL-запросов.'
  } else if (/опыт|работ|компан|яндекс|банк|магнит|стаж/.test(t)) {
    target = 'experience'; reply = '9 лет 7 месяцев: Магнит OMNI, Т-Банк, Яндекс, RedLab, Litota, ScrollCast. Поднял таймлайн наверх.'
  } else if (/навык|способ|skill|умее|сил|лид|команд|менто/.test(t)) {
    target = 'powers'; reply = 'Ключевые способности: распределённые системы, оптимизация, лидерство и менторство.'
  } else if (/о себе|кто|личн|about|семь|характер/.test(t)) {
    target = 'about'; reply = 'Коротко: масштабируемые микросервисы, чистый код, менторство. Подробности — в модуле ABOUT.'
  } else if (/встреч|созвон|поговор|оффер|предложен|нанять|нанима|interview|собес/.test(t)) {
    showCalendar()
    return { reply: 'Открыл модуль бронирования — выбери удобный день и слот.', modules: [{ kind: 'calendar' }] }
  } else if (/тем|настроен|дизайн|theme|оформл|вид|цвет/.test(t)) {
    const next = state.theme === 'forge' ? 'dossier' : (state.theme === 'dossier' ? 'neon' : 'forge')
    state.theme = next
    return { reply: 'Сменил настроение оформления. Переключай темы кнопками сверху: FORGE / DOSSIER / NEON.', modules: [] }
  } else {
    target = 'stats'; reply = 'Я AI-секретарь Инала Туаева. Спроси про стек, опыт или оптимизацию — перестрою страницу под тебя.'
  }

  if (target) {
    const m = state.modules.find(m => m.id === target)
    if (m) modules = [{ ...m }]
    highlight(target)
  }
  return { reply, modules }
}

async function send(text) {
  text = (text || state.input || '').trim()
  if (!text || state.loading) return
  const msgs = [...state.messages, { role: 'user', text }]
  state.messages = msgs
  state.input = ''
  state.loading = true
  try {
    const res = await fetch('/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ messages: msgs.map(m => ({ role: m.role, text: m.text })) }),
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const data = await res.json()
    const reply = data?.reply || '…'
    state.messages = [...state.messages, { role: 'assistant', text: reply, modules: [] }]
    state.loading = false
    if (Array.isArray(data?.actions)) {
      const captured = applyActionsAndCapture(data.actions)
      if (captured.length > 0) {
        const allMsgs = [...state.messages]
        allMsgs[allMsgs.length - 1] = { ...allMsgs[allMsgs.length - 1], modules: captured }
        state.messages = allMsgs
      }
    }
  } catch (_) {
    const { reply, modules } = fallback(text)
    state.messages = [...state.messages, { role: 'assistant', text: reply, modules }]
    state.loading = false
  }
}

async function applyAiTheme(prompt) {
  if (!prompt || state.aiGenerating) return
  state.aiGenerating = true
  state.aiPromptOpen = false
  try {
    const res = await fetch('/api/theme', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt }),
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const p = await res.json()
    const root = document.documentElement
    const map = {
      '--ai-bg': p.bg, '--ai-bg2': p.bg2, '--ai-panel': p.panel, '--ai-panel2': p.panel2,
      '--ai-line': p.line, '--ai-ink': p.ink, '--ai-dim': p.dim, '--ai-accent': p.accent,
      '--ai-accent2': p.accent2, '--ai-chip': p.chip,
      '--ai-tape': p.line || 'rgba(255,255,255,.12)',
      '--ai-glow': p.glow || p.accent,
    }
    const applied = {}
    Object.entries(map).forEach(([k, v]) => { if (v) { root.style.setProperty(k, v); applied[k] = v } })
    localStorage.setItem('resume-ai-palette', JSON.stringify(applied))
    localStorage.setItem('resume-ai-name', prompt)
    state.theme = 'ai'
    state.aiPaletteName = prompt
  } catch (_) {}
  state.aiGenerating = false
}

export function parseLines(body) {
  return String(body || '').split('\n').map(l => l.trim()).filter(Boolean).map(l => {
    if (l.startsWith('- ') || l.startsWith('• ') || l.startsWith('* '))
      return { bullet: true, text: l.slice(2).trim() }
    return { bullet: false, text: l }
  })
}

export function useStore() {
  return {
    state,
    send,
    promote,
    highlight,
    showUml,
    hideUml,
    toggleUmlCode,
    showCalendar,
    hideCalendar,
    applyAiTheme,
    toggleChat,
  }
}

export function startTimers() {
  document.title = TITLES[0]
  const expTimer = setInterval(() => { state.expStr = calcExp() }, 1000)
  const titleTimer = setInterval(() => {
    state.titleIdx = (state.titleIdx + 1) % TITLES.length
    document.title = TITLES[state.titleIdx]
  }, 18000)
  return () => { clearInterval(expTimer); clearInterval(titleTimer) }
}
