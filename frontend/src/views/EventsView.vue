<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Prochains évènements</h1>
      
      <RouterLink v-if="authStore.isLoggedIn" to="/evenements/creer"
        class="hidden sm:block px-4 py-2 text-white text-sm font-semibold rounded-lg shadow-sm transition-colors hover:opacity-90"
        style="background-color: #7A9E6E">
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

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <EventCard v-for="event in events" :key="event.id" :event="event" />
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'
import EventCard from '@/components/event/EventCard.vue'

const authStore = useAuthStore()

const events = ref<any[]>([])
const isLoading = ref(true)
const error = ref('')

async function fetchEvents() {
  try {
    const response = await api.get('/events')
    events.value = response.data
  } catch (e: any) {
    error.value = "Impossible de charger les évènements."
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchEvents()
})
</script>
