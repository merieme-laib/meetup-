<template>
  <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    
    <div v-if="isLoading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2" style="border-color: #7A9E6E"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 text-red-600 p-6 rounded-xl text-center shadow-sm">
      <h2 class="text-xl font-bold mb-2">Oups !</h2>
      <p>{{ error }}</p>
      <button @click="router.push('/evenements')" class="mt-4 text-[#7A9E6E] font-semibold hover:underline">
        ← Retour aux évènements
      </button>
    </div>

    <div v-else-if="event">
      
      <div class="mb-6">
        <div class="text-[#7A9E6E] font-bold uppercase tracking-wide mb-2">
          {{ formatDate(event.date) }}
        </div>
        <h1 class="text-4xl font-extrabold text-gray-900 mb-4">{{ event.title }}</h1>
        <div class="flex items-center gap-3 text-sm text-gray-600">
          <span v-if="event.category" class="bg-gray-100 px-3 py-1 rounded-full font-medium">
            {{ event.category }}
          </span>
          <span v-if="event.isOnline" class="bg-blue-50 text-blue-600 px-3 py-1 rounded-full font-medium">
            💻 Évènement en ligne
          </span>
        </div>
      </div>

      <div class="w-full h-[400px] rounded-2xl overflow-hidden mb-10 bg-gray-100 shadow-sm border border-gray-200">
        <img v-if="event.imageUrl" :src="event.imageUrl" :alt="event.title" class="w-full h-full object-cover" />
        <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
          Aucune image disponible
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-10">
        
        <div class="lg:col-span-2 space-y-8">
          
          <section>
            <h2 class="text-2xl font-bold text-gray-900 mb-4">À propos de cet évènement</h2>
            <div class="prose max-w-none text-gray-600 whitespace-pre-line leading-relaxed">
              {{ event.description }}
            </div>
          </section>

        </div>

        <div class="lg:col-span-1">
          <div class="bg-white rounded-2xl shadow-lg border border-gray-100 p-6 sticky top-24">
            
            <div class="flex justify-between items-center mb-6">
              <span class="text-gray-600 font-medium">Prix</span>
              <span class="text-2xl font-bold text-gray-900">
                {{ event.price === 0 || !event.price ? 'Gratuit' : event.price + ' €' }}
              </span>
            </div>

            <div class="space-y-4 mb-8">
              <div class="flex items-start gap-3">
                <div class="text-xl">📍</div>
                <div>
                  <div class="font-medium text-gray-900">{{ event.city || 'Lieu non renseigné' }}</div>
                  <div class="text-sm text-gray-500">{{ event.location }}</div>
                </div>
              </div>
              
              <div class="flex items-start gap-3">
                <div class="text-xl">👥</div>
                <div>
                  <div class="font-medium text-gray-900">Participants</div>
                  <div class="text-sm text-gray-500">
                    {{ event.participantsCount || 0 }} inscrit(s)
                    <span v-if="event.maxParticipants">/ {{ event.maxParticipants }} max</span>
                  </div>
                </div>
              </div>
            </div>

            <button 
              class="w-full py-3.5 px-4 text-white font-bold rounded-xl shadow-sm transition-all transform hover:scale-[1.02] active:scale-[0.98]"
              style="background-color: #7A9E6E">
              Participer à l'évènement
            </button>
            
            <p v-if="!authStore.isLoggedIn" class="text-xs text-center text-gray-500 mt-3">
              Connectez-vous pour pouvoir participer.
            </p>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const event = ref<any>(null)
const isLoading = ref(true)
const error = ref('')

// On récupère l'ID depuis l'URL (ex: /evenements/1 -> id = 1)
const eventId = route.params.id

async function fetchEventDetails() {
  try {
    const response = await fetch(`http://localhost:8080/api/events/${eventId}`)
    
    if (response.status === 404) {
      throw new Error("Cet évènement n'existe pas ou a été supprimé.")
    }
    if (!response.ok) {
      throw new Error("Erreur lors de la récupération de l'évènement.")
    }

    const data = await response.json()
    event.value = data
    
  } catch (e: any) {
    error.value = e.message
  } finally {
    isLoading.value = false
  }
}

function formatDate(dateString: string) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date).replace(',', ' à')
}

onMounted(() => {
  fetchEventDetails()
})
</script>