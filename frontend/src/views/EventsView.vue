<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Prochains évènements</h1>

      <RouterLink
        v-if="authStore.isLoggedIn"
        to="/evenements/creer"
        class="hidden sm:block px-4 py-2 text-white text-sm font-semibold rounded-lg shadow-sm transition-colors hover:opacity-90"
        style="background-color: #7A9E6E"
      >
        Créer un évènement
      </RouterLink>
    </div>

    <div class="mb-10 overflow-hidden rounded-3xl border border-gray-200 bg-white shadow-sm">
      <div class="border-b border-gray-100 px-5 py-4 sm:px-6" style="background-color: #F2F5F0">
        <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p class="text-xs font-bold uppercase tracking-[0.18em]" style="color: #7A9E6E">Filtres</p>
            <h2 class="mt-0.5 text-xl font-extrabold text-gray-900">Affinez votre recherche</h2>
          </div>
          <p class="text-sm font-medium leading-none text-gray-500 sm:ml-8 sm:self-center">
            {{ filteredEvents.length }} résultat<span v-if="filteredEvents.length > 1">s</span>
            <span v-if="searchTerm"> pour "{{ searchTerm }}"</span>
          </p>
        </div>
      </div>

      <div class="px-5 pt-5 pb-10 sm:px-6">
        <div class="overflow-x-auto">
          <div class="flex min-w-max items-end gap-4">
            <label class="w-[180px] shrink-0">
              <span class="mb-2.5 block text-sm font-semibold text-gray-900">Quand</span>
              <select
                :value="selectedPeriod"
                class="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 text-sm font-medium text-gray-700 outline-none transition-colors focus:border-[#7A9E6E]"
                @change="setPeriodFilter(($event.target as HTMLSelectElement).value)"
              >
                <option v-for="option in periodOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </label>

            <label class="w-[220px] shrink-0">
              <span class="mb-2.5 block text-sm font-semibold text-gray-900">Type d'évènement</span>
              <select
                :value="selectedFormat"
                class="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 text-sm font-medium text-gray-700 outline-none transition-colors focus:border-[#7A9E6E]"
                @change="setFormatFilter(($event.target as HTMLSelectElement).value)"
              >
                <option v-for="option in formatOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </label>

            <label class="w-[240px] shrink-0">
              <span class="mb-2.5 block text-sm font-semibold text-gray-900">Catégorie</span>
              <select
                :value="selectedCategory"
                class="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 text-sm font-medium text-gray-700 outline-none transition-colors focus:border-[#7A9E6E]"
                @change="setCategoryFilter(($event.target as HTMLSelectElement).value)"
              >
                <option value="">Toutes les catégories</option>
                <option v-for="category in availableCategories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
            </label>

            <div class="flex-1"></div>

            <div v-if="hasActiveFilters" class="shrink-0">
              <span class="mb-2.5 block invisible text-sm font-semibold">Action</span>
              <button
                type="button"
                class="rounded-xl border border-gray-300 px-4 py-1.5 text-sm font-semibold text-gray-600 transition-colors hover:border-gray-400 hover:text-gray-900"
                @click="clearFilters"
              >
                Réinitialiser
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2" style="border-color: #7A9E6E"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 text-red-600 p-4 rounded-lg text-center">
      {{ error }}
    </div>

    <div v-else-if="events.length === 0" class="text-center py-20 text-gray-500 bg-gray-50 rounded-xl border border-gray-100">
      <p class="text-lg font-medium mb-2">Aucun évènement prévu pour le moment.</p>
      <p class="text-sm">Soyez le premier à en créer un !</p>
    </div>

    <div v-else-if="filteredEvents.length === 0" class="text-center py-20 text-gray-500 bg-gray-50 rounded-xl border border-gray-100">
      <p class="text-lg font-medium mb-2">Aucun évènement ne correspond à votre recherche.</p>
      <p class="text-sm">
        Essayez avec un autre mot-clé<span v-if="searchTerm"> pour "{{ searchTerm }}"</span>.
      </p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <EventCard v-for="event in filteredEvents" :key="event.id" :event="event" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'
import EventCard from '@/components/event/EventCard.vue'
import type { Event } from '@/types/event'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const events = ref<Event[]>([])
const isLoading = ref(true)
const error = ref('')

const periodOptions = [
  { value: '', label: 'Toutes les dates' },
  { value: 'week', label: 'Cette semaine' },
  { value: 'month', label: 'Ce mois-ci' }
]

const formatOptions = [
  { value: '', label: 'Tous les formats' },
  { value: 'in-person', label: 'Évènement présentiel' },
  { value: 'online', label: 'Évènement en ligne' }
]

const defaultCategories = [
  'Développement',
  'Technologie',
  'Design',
  'Business',
  'Sécurité',
  'Agilité',
  'Data',
  'Cloud',
  'Sport',
  'Danse',
  'Musique',
  'Culture',
  'Loisirs',
  'Gaming',
  'Networking',
  'IA',
  'Associatif',
  'Bien-être'
]

const searchTerm = computed(() => {
  const q = route.query.q
  return typeof q === 'string' ? q.trim() : ''
})

const selectedCategory = computed(() => {
  const cat = route.query.cat
  return typeof cat === 'string' ? cat.trim() : ''
})

const selectedPeriod = computed(() => {
  const period = route.query.period
  return typeof period === 'string' ? period.trim() : ''
})

const selectedFormat = computed(() => {
  const format = route.query.format
  return typeof format === 'string' ? format.trim() : ''
})

const availableCategories = computed(() => {
  const categories = new Set(defaultCategories)

  events.value
    .map((event) => event.category?.trim())
    .filter((category): category is string => Boolean(category))
    .forEach((category) => categories.add(category))

  return Array.from(categories).sort((a, b) => a.localeCompare(b, 'fr'))
})

const hasActiveFilters = computed(() =>
  Boolean(searchTerm.value || selectedCategory.value || selectedPeriod.value || selectedFormat.value)
)

const filteredEvents = computed(() => {
  const normalizedSearch = normalizeText(searchTerm.value)
  const normalizedCategory = normalizeText(selectedCategory.value)
  const now = new Date()
  const startOfCurrentDay = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const endOfWeek = getEndOfWeek(now)
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59, 999)

  return events.value.filter((event) => {
    const matchesCategory = !normalizedCategory
      || normalizeText(event.category).includes(normalizedCategory)
    const matchesFormat = !selectedFormat.value
      || (selectedFormat.value === 'online' && isEventOnline(event))
      || (selectedFormat.value === 'in-person' && !isEventOnline(event))

    if (!matchesCategory) return false
    if (!matchesFormat) return false
    if (!matchesPeriodFilter(event.date, selectedPeriod.value, startOfCurrentDay, endOfWeek, endOfMonth)) return false
    if (!normalizedSearch) return true

    const searchableContent = normalizeText([
      event.title,
      event.description,
      event.category,
      event.location,
      event.city,
      isEventOnline(event) ? 'en ligne online distanciel' : ''
    ].join(' '))

    return searchableContent.includes(normalizedSearch)
  })
})

async function fetchEvents() {
  try {
    const response = await api.get('/events')
    events.value = response.data
  } catch {
    error.value = "Impossible de charger les évènements."
  } finally {
    isLoading.value = false
  }
}

function normalizeText(value: string | null | undefined) {
  return (value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
}

function isEventOnline(event: Event & { online?: boolean }) {
  return Boolean(event.isOnline || event.online)
}

onMounted(() => {
  fetchEvents()
})

function setPeriodFilter(period: string) {
  updateQuery({
    period: period || undefined
  })
}

function setCategoryFilter(category: string) {
  updateQuery({
    cat: category || undefined
  })
}

function setFormatFilter(format: string) {
  updateQuery({
    format: format || undefined
  })
}

function clearFilters() {
  updateQuery({
    q: undefined,
    cat: undefined,
    period: undefined,
    format: undefined
  })
}

function updateQuery(updates: Record<string, string | undefined>) {
  router.push({
    path: '/evenements',
    query: {
      ...route.query,
      ...updates
    }
  })
}

function matchesPeriodFilter(
  eventDateRaw: string,
  period: string,
  startOfCurrentDay: Date,
  endOfWeek: Date,
  endOfMonth: Date
) {
  const eventDate = new Date(eventDateRaw)

  if (Number.isNaN(eventDate.getTime())) return false
  if (!period) return true
  if (eventDate < startOfCurrentDay) return false
  if (period === 'week') return eventDate <= endOfWeek
  if (period === 'month') return eventDate <= endOfMonth
  return true
}

function getEndOfWeek(date: Date) {
  const day = date.getDay()
  const daysUntilSunday = day === 0 ? 0 : 7 - day
  return new Date(date.getFullYear(), date.getMonth(), date.getDate() + daysUntilSunday, 23, 59, 59, 999)
}
</script>
