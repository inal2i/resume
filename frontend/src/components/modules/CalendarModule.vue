<template>
  <div>
    <div class="label">// CALENDAR · BOOK A MEETING</div>
    <div class="header-row">
      <h3 class="title">ВСТРЕЧА</h3>
      <button class="close-btn" @click="hideCalendar">✕</button>
    </div>

    <div class="dates-row">
      <button
        v-for="d in upcomingDates"
        :key="d.iso"
        class="date-btn"
        :class="{ active: selectedDate === d.iso }"
        @click="selectDate(d.iso)"
      >
        <span class="date-weekday">{{ d.weekday }}</span>
        <span class="date-num">{{ d.num }}</span>
      </button>
    </div>

    <div v-if="selectedDate" class="slots-section">
      <div v-if="loadingSlots" class="hint">загрузка слотов…</div>
      <div v-else-if="slots.length === 0" class="hint">Нет свободных слотов на этот день</div>
      <div v-else class="slots-grid">
        <button
          v-for="slot in slots"
          :key="slot"
          class="slot-btn"
          :class="{ active: selectedSlot === slot }"
          @click="pickSlot(slot)"
        >{{ slot }}</button>
      </div>
    </div>

    <transition name="form-in">
      <div v-if="showForm && selectedSlot" class="form-section">
        <div class="form-when">{{ formattedDate }} · {{ selectedSlot }}</div>
        <input v-model="form.name"  class="inp" placeholder="Ваше имя" autocomplete="name" />
        <input v-model="form.email" class="inp" placeholder="Email" type="email" autocomplete="email" />
        <input v-model="form.topic" class="inp" placeholder="Тема встречи" />
        <textarea v-model="form.description" class="inp inp-textarea" placeholder="Описание (необязательно) — ссылки, детали…" rows="2" />
        <div v-if="bookResult" class="result" :class="bookResult.ok ? 'ok' : 'err'">
          {{ bookResult.msg }}
        </div>
        <button class="book-btn" :disabled="booking || !!bookResult?.ok" @click="book">
          {{ booking ? 'БРОНИРУЮ…' : 'ЗАБРОНИРОВАТЬ' }}
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useStore } from '../../composables/useStore.js'

const { hideCalendar } = useStore()

const WEEKDAYS = ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб']
const MONTHS   = ['янв','фев','мар','апр','май','июн','июл','авг','сен','окт','ноя','дек']

function toIso(d) {
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

const upcomingDates = computed(() => {
  const result = []
  const cur = new Date()
  cur.setHours(0, 0, 0, 0)
  while (result.length < 7) {
    const dow = cur.getDay()
    if (dow !== 0 && dow !== 6) {
      result.push({
        iso: toIso(cur),
        weekday: WEEKDAYS[dow],
        num: `${cur.getDate()} ${MONTHS[cur.getMonth()]}`,
      })
    }
    cur.setDate(cur.getDate() + 1)
  }
  return result
})

const selectedDate = ref(null)
const slots        = ref([])
const loadingSlots = ref(false)
const selectedSlot = ref(null)
const showForm     = ref(false)
const booking      = ref(false)
const bookResult   = ref(null)
const form         = ref({ name: '', email: '', topic: '', description: '' })

const formattedDate = computed(() => {
  if (!selectedDate.value) return ''
  const [y, m, d] = selectedDate.value.split('-')
  const dt = new Date(+y, +m - 1, +d)
  return `${dt.getDate()} ${MONTHS[dt.getMonth()]} ${y}`
})

async function selectDate(iso) {
  selectedDate.value = iso
  selectedSlot.value = null
  showForm.value = false
  bookResult.value = null
  slots.value = []
  loadingSlots.value = true
  try {
    const res = await fetch(`/api/calendar/slots?date=${iso}`)
    const data = await res.json()
    slots.value = data.slots || []
  } catch {
    slots.value = []
  } finally {
    loadingSlots.value = false
  }
}

function pickSlot(slot) {
  selectedSlot.value = slot
  showForm.value = true
  bookResult.value = null
}

async function book() {
  if (!form.value.name.trim() || !form.value.email.trim() || !form.value.topic.trim()) {
    bookResult.value = { ok: false, msg: 'Заполните все поля' }
    return
  }
  booking.value = true
  bookResult.value = null
  try {
    const res = await fetch('/api/calendar/book', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        date: selectedDate.value,
        time: selectedSlot.value,
        name: form.value.name,
        email: form.value.email,
        topic: form.value.topic,
        description: form.value.description || null,
      }),
    })
    const data = await res.json()
    if (data.success) {
      bookResult.value = { ok: true, msg: `Встреча забронирована на ${formattedDate.value} · ${selectedSlot.value}. Увидимся!` }
      slots.value = slots.value.filter(s => s !== selectedSlot.value)
    } else {
      bookResult.value = { ok: false, msg: 'Ошибка бронирования. Попробуйте другой слот.' }
    }
  } catch {
    bookResult.value = { ok: false, msg: 'Ошибка сети. Попробуйте позже.' }
  } finally {
    booking.value = false
  }
}
</script>

<style scoped>
.label { font-family: 'JetBrains Mono', monospace; font-size: 11px; letter-spacing: .18em; text-transform: uppercase; color: var(--accent2); margin: 0 0 8px; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.title { font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 24px; line-height: 1; text-transform: uppercase; margin: 0; color: var(--ink); }
.close-btn { background: none; border: 1px solid var(--panel-line); color: var(--ink-dim); border-radius: 7px; padding: 5px 10px; font-family: 'JetBrains Mono', monospace; font-size: 11px; cursor: pointer; transition: border-color .2s, color .2s; }
.close-btn:hover { border-color: var(--accent); color: var(--ink); }

.dates-row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.date-btn { background: var(--chip); border: 1px solid var(--panel-line); border-radius: 10px; padding: 8px 12px; cursor: pointer; transition: border-color .2s, background .2s; display: flex; flex-direction: column; align-items: center; gap: 2px; }
.date-btn:hover { border-color: var(--accent); }
.date-btn.active { border-color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--chip)); }
.date-weekday { font-family: 'JetBrains Mono', monospace; font-size: 10px; letter-spacing: .1em; color: var(--accent); text-transform: uppercase; }
.date-num { font-family: 'Manrope', sans-serif; font-size: 13px; font-weight: 600; color: var(--ink); white-space: nowrap; }

.slots-section { margin-bottom: 16px; }
.hint { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: var(--ink-dim); padding: 8px 0; }
.slots-grid { display: flex; gap: 8px; flex-wrap: wrap; }
.slot-btn { background: var(--chip); border: 1px solid var(--panel-line); border-radius: 8px; padding: 8px 14px; font-family: 'JetBrains Mono', monospace; font-size: 13px; color: var(--ink); cursor: pointer; transition: border-color .2s, background .2s; }
.slot-btn:hover { border-color: var(--accent); }
.slot-btn.active { border-color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--chip)); color: var(--accent); }

.form-section { border-top: 1px solid var(--panel-line); padding-top: 16px; display: flex; flex-direction: column; gap: 10px; }
.form-when { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: var(--accent); letter-spacing: .08em; }
.inp { background: var(--chip); border: 1px solid var(--panel-line); border-radius: 8px; padding: 10px 13px; font-family: 'Manrope', sans-serif; font-size: 14px; color: var(--ink); outline: none; transition: border-color .2s; width: 100%; box-sizing: border-box; }
.inp::placeholder { color: var(--ink-dim); }
.inp:focus { border-color: var(--accent); }
.inp-textarea { resize: vertical; min-height: 60px; line-height: 1.5; }

.result { font-family: 'Manrope', sans-serif; font-size: 13px; padding: 10px 13px; border-radius: 8px; }
.result.ok  { background: color-mix(in srgb, var(--accent) 10%, var(--chip)); color: var(--accent); border: 1px solid color-mix(in srgb, var(--accent) 30%, transparent); }
.result.err { background: color-mix(in srgb, #f04 10%, var(--chip)); color: #f04; border: 1px solid color-mix(in srgb, #f04 30%, transparent); }

.book-btn { background: var(--accent); border: none; border-radius: 9px; padding: 11px 20px; font-family: 'Big Shoulders Display', sans-serif; font-weight: 800; font-size: 15px; letter-spacing: .08em; color: var(--bg); cursor: pointer; transition: opacity .2s; align-self: flex-start; }
.book-btn:disabled { opacity: .5; cursor: default; }
.book-btn:not(:disabled):hover { opacity: .85; }

.form-in-enter-active { transition: opacity .25s ease, transform .25s ease; }
.form-in-enter-from   { opacity: 0; transform: translateY(8px); }
</style>
