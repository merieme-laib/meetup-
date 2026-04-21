<template>

  
  <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- En haut du template -->
    <ConfirmModal
      v-model="showUnregisterModal"
      title="Se désinscrire ?"
      message="Voulez-vous vraiment vous désinscrire de cet évènement ?"
      confirmText="Se désinscrire"
      confirmColor="#ef4444"
      @confirm="confirmUnregister"
    />
    
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
          <span v-if="event.online" class="bg-blue-50 text-blue-600 px-3 py-1 rounded-full font-medium">
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
        
        <!-- Contenu principal -->
        <div class="lg:col-span-2 space-y-8">
          <section>
            <h2 class="text-2xl font-bold text-gray-900 mb-4">À propos de cet évènement</h2>
            <div class="prose max-w-none text-gray-600 whitespace-pre-line leading-relaxed">
              {{ event.description }}
            </div>
          </section>

          <section v-if="!event.online">
            <h2 class="text-xl font-bold text-gray-900 mb-3">Lieu</h2>
            <div class="flex items-start gap-3 p-4 rounded-xl border border-gray-100" style="background-color: #F2F5F0">
              <div class="text-xl">📍</div>
              <div>
                <div class="font-semibold text-gray-900">{{ event.location }}</div>
                <div class="text-sm text-gray-500">{{ event.city }}</div>
              </div>
            </div>
          </section>
        </div>

        <!-- Sidebar -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-2xl shadow-lg border border-gray-100 p-6 sticky top-24">
            
            <div class="flex justify-between items-center mb-6">
              <span class="text-gray-600 font-medium">Prix</span>
              <span class="text-2xl font-bold text-gray-900">
                {{ event.price === 0 || !event.price ? 'Gratuit' : event.price + ' €' }}
              </span>
            </div>

            <div class="space-y-4 mb-6">
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

            <!-- Bouton inscription -->
            <button 
              @click="handleRegister"
              :disabled="loadingRegister"
              class="w-full py-3.5 px-4 font-bold rounded-xl shadow-sm transition-all"
              :style="{
                backgroundColor: isRegistered ? '#E0ECD9' : '#7A9E6E',
                color: isRegistered ? '#3D5C38' : 'white',
                opacity: loadingRegister ? 0.7 : 1
              }">
              {{ loadingRegister ? 'En cours...' : isRegistered ? '✓ Inscrit — Se désinscrire' : "Participer à l'évènement" }}
            </button>

            <!-- Bouton like -->
            <button
              @click="handleLike"
              :disabled="loadingLike"
              class="w-full mt-3 py-3 px-4 font-semibold rounded-xl border transition-all flex items-center justify-center gap-2"
              :style="{
                borderColor: isLiked ? '#7A9E6E' : '#e5e7eb',
                color: isLiked ? '#5E7D58' : '#6b7280',
                backgroundColor: isLiked ? '#F2F5F0' : 'white'
              }">
              {{ isLiked ? '❤️ Aimé' : '🤍 J\'aime' }} · {{ event.likesCount || 0 }}
            </button>
            
            <p v-if="!authStore.isLoggedIn" class="text-xs text-center text-gray-500 mt-3">
              Connectez-vous pour pouvoir participer.
            </p>

            <!-- Bouton modifier si créateur -->
            <RouterLink
              v-if="authStore.user && authStore.user.id === event.creatorId"
              :to="`/evenements/${event.id}/modifier`"
              class="w-full mt-3 py-2.5 px-4 font-semibold rounded-xl border border-gray-200 text-gray-600 hover:border-gray-400 transition-all flex items-center justify-center gap-2 text-sm">
              ✏️ Modifier l'évènement
            </RouterLink>
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
import ConfirmModal from '@/components/ui/ConfirmModal.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const event = ref<any>(null)
  const isLoading = ref(true)
  const error = ref('')
const isRegistered = ref(false)
const isLiked = ref(false)
const loadingRegister = ref(false)
const loadingLike = ref(false)

const eventId = route.params.id

const showUnregisterModal = ref(false)

// async function fetchEventDetails() {
//   try {
//     const response = await api.get(`/events/${eventId}`)
//     event.value = response.data
//   } catch (e: any) {
//     if (e.response && e.response.status === 404) {
//       error.value = "Cet évènement n'existe pas ou a été supprimé."
//     } else {
//       error.value = "Erreur lors de la récupération de l'évènement."
//     }
//   } finally {
//     isLoading.value = false
//   }
// }

async function handleRegister() {
  if (!authStore.isLoggedIn) {
    router.push({ path: '/connexion', query: { redirect: route.fullPath } })
    return
  }
  if (isRegistered.value) {
    showUnregisterModal.value = true
    return
  }
  loadingRegister.value = true
  try {
    const response = await api.post(`/events/${eventId}/register`)
    event.value.participantsCount = response.data.participantsCount
    isRegistered.value = true
  } catch (e: any) {
    if (e.response?.status === 400) {
      isRegistered.value = true
    } else {
      alert("Une erreur est survenue.")
    }
  } finally {
    loadingRegister.value = false
  }
}

async function confirmUnregister() {
  showUnregisterModal.value = false
  loadingRegister.value = true
  try {
    await api.delete(`/events/${eventId}/register`)
    event.value.participantsCount = Math.max(0, event.value.participantsCount - 1)
    isRegistered.value = false
  } catch {
    alert("Une erreur est survenue.")
  } finally {
    loadingRegister.value = false
  }
}

async function handleLike() {
  if (!authStore.isLoggedIn) {
    router.push({ path: '/connexion', query: { redirect: route.fullPath } })
    return
  }
  loadingLike.value = true
  try {
    if (isLiked.value) {
      await api.delete(`/events/${eventId}/like`)
      event.value.likesCount = Math.max(0, event.value.likesCount - 1)
      isLiked.value = false
    } else {
      const response = await api.post(`/events/${eventId}/like`)
      event.value.likesCount = response.data.likesCount
      isLiked.value = true
    }
  } catch (e: any) {
    alert("Une erreur est survenue.")
  } finally {
    loadingLike.value = false
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

onMounted(async () => {
  try {
    const response = await api.get(`/events/${eventId}`)
    event.value = response.data
    isRegistered.value = response.data.isRegistered || false
    isLiked.value = response.data.isLiked || false
  } catch (e: any) {
    if (e.response?.status === 404) {
      error.value = "Cet évènement n'existe pas ou a été supprimé."
    } else {
      error.value = "Erreur lors de la récupération de l'évènement."
    }
  } finally {
    isLoading.value = false
  }
})
</script>
