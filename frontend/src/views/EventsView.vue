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
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'
import EventCard from '@/components/event/EventCard.vue'
import type { Event } from '@/types/event'

const authStore = useAuthStore()
const route = useRoute()

const events = ref<Event[]>([])
const isLoading = ref(true)
const error = ref('')

const searchTerm = computed(() => {
  const q = route.query.q
  return typeof q === 'string' ? q.trim() : ''
})

const categoryFilter = computed(() => {
  const cat = route.query.cat
  return typeof cat === 'string' ? cat.trim() : ''
})

const filteredEvents = computed(() => {
  const normalizedSearch = normalizeText(searchTerm.value)
  const normalizedCategory = normalizeText(categoryFilter.value)

  return events.value.filter((event) => {
    const matchesCategory = !normalizedCategory
      || normalizeText(event.category).includes(normalizedCategory)

    if (!matchesCategory) return false
    if (!normalizedSearch) return true

    const searchableContent = normalizeText([
      event.title,
      event.description,
      event.category,
      event.location,
      event.city,
      event.isOnline ? 'en ligne online distanciel' : ''
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

onMounted(() => {
  fetchEvents()
})
</script>
