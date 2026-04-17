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
      
      <RouterLink v-for="event in events" :key="event.id" :to="`/evenements/${event.id}`"
           class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-all duration-200 flex flex-col group cursor-pointer">

        <div class="h-48 bg-gray-200 relative overflow-hidden">
          <img v-if="event.imageUrl" :src="event.imageUrl" :alt="event.title" 
               class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
          <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400">
            <span class="text-sm">Pas d'image</span>
          </div>
          
          <div v-if="event.isOnline" class="absolute top-3 left-3 bg-white px-2.5 py-1 rounded-md text-xs font-bold text-gray-700 shadow-sm">
            💻 En ligne
          </div>
          <div v-if="event.category" class="absolute top-3 right-3 bg-black/60 backdrop-blur-sm text-white px-2.5 py-1 rounded-md text-xs font-semibold">
            {{ event.category }}
          </div>
        </div>

        <div class="p-5 flex-1 flex flex-col">
          <div class="text-[#7A9E6E] text-sm font-semibold mb-1 uppercase tracking-wide">
            {{ formatDate(event.date) }}
          </div>
          
          <h2 class="text-lg font-bold text-gray-900 mb-2 line-clamp-2 group-hover:text-[#7A9E6E] transition-colors">
            {{ event.title }}
          </h2>

          <div class="text-gray-600 text-sm mb-4 line-clamp-2 flex-1">
            {{ event.description }}
          </div>

          <div class="mt-auto pt-4 border-t border-gray-50 flex justify-between items-center text-sm">
            <span class="text-gray-500 truncate pr-2">
              📍 {{ event.city || 'Lieu non renseigné' }}
            </span>
            <span class="font-bold text-gray-900 bg-gray-100 px-2 py-1 rounded">
              {{ event.price === 0 || !event.price ? 'Gratuit' : event.price + ' €' }}
            </span>
          </div>
        </div>

      </RouterLink>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()

const events = ref<any[]>([])
const isLoading = ref(true)
const error = ref('')

async function fetchEvents() {
  try {
    const response = await fetch('http://localhost:8080/api/events')
    if (!response.ok) throw new Error('Impossible de charger les évènements.')
    events.value = await response.json()
  } catch (e: any) {
    error.value = e.message
  } finally {
    isLoading.value = false
  }
}

function formatDate(dateString: string) {
  if (!dateString) return 'Date inconnue'
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date).replace(',', ' à')
}

onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;  
  overflow: hidden;
}
</style>