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
              @click="handleParticipate"
              :disabled="!authStore.isLoggedIn || isParticipating"
              class="w-full py-3.5 px-4 text-white font-bold rounded-xl shadow-sm transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
              style="background-color: #7A9E6E">
              {{ isParticipating ? 'Inscription en cours...' : "Participer à l'évènement" }}
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
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const event = ref<any>(null)
const isLoading = ref(true)
const error = ref('')
const isParticipating = ref(false)

const eventId = route.params.id

// 1. Récupérer les détails de l'évènement
async function fetchEventDetails() {
  try {
    const response = await api.get(`/events/${eventId}`)
    event.value = response.data
  } catch (e: any) {
    if (e.response && e.response.status === 404) {
      error.value = "Cet évènement n'existe pas ou a été supprimé."
    } else {
      error.value = "Erreur lors de la récupération de l'évènement."
    }
  } finally {
    isLoading.value = false
  }
}

// 2. Participer à l'évènement
async function handleParticipate() {
  if (!authStore.isLoggedIn) return
  
  isParticipating.value = true
  try {
    const response = await api.post(`/events/${eventId}/participate`)
    event.value.participantsCount = response.data.newCount
    alert("🎉 Inscription réussie ! On a hâte de vous y voir !")
  } catch (e: any) {
    alert("Oups, une erreur est survenue lors de l'inscription.")
  } finally {
    isParticipating.value = false
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