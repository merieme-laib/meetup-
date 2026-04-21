<template>
  <div class="group bg-white rounded-xl border border-gray-200 hover:border-gray-300 hover:shadow-lg transition-all duration-200 overflow-hidden flex flex-col">
    
    <!-- Image -->
    <div class="relative overflow-hidden" style="height: 180px">
      <img
        v-if="event.imageUrl"
        :src="event.imageUrl"
        :alt="event.title"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
      <div v-else class="w-full h-full bg-gray-100 flex items-center justify-center text-gray-400 text-sm">
        Pas d'image
      </div>

      <!-- Badge date -->
      <div class="absolute top-3 left-3">
        <div class="bg-white rounded-lg shadow-sm overflow-hidden text-center w-11">
          <div class="text-white text-xs py-0.5 font-bold" style="background-color: #7A9E6E">
            {{ monthAbbr }}
          </div>
          <div class="text-gray-900 py-0.5 font-black" style="font-size: 1.1rem; line-height: 1.3">
            {{ dayNum }}
          </div>
        </div>
      </div>

      <!-- Bouton like -->
      <button
        @click.prevent="handleLike"
        class="absolute top-3 right-3 w-8 h-8 rounded-full flex items-center justify-center transition-all shadow-sm"
        :style="{
          backgroundColor: isLiked ? '#ef4444' : 'white',
          color: isLiked ? 'white' : '#6b7280'
        }"
      >
        <Heart :size="14" :fill="isLiked ? 'currentColor' : 'none'" />
      </button>

      <!-- Badge en ligne -->
      <div v-if="event.online || event.isOnline" class="absolute bottom-3 left-3">
        <span class="flex items-center gap-1 px-2 py-1 bg-white/95 text-gray-700 rounded-md text-xs shadow-sm font-semibold">
          <Wifi :size="11" style="color: #7A9E6E" />
          En ligne
        </span>
      </div>
    </div>

    <!-- Contenu -->
    <div class="p-4 flex flex-col flex-1">
      
      <!-- Catégorie + Prix -->
      <div class="flex items-center gap-2 mb-2">
        <span class="px-2 py-0.5 rounded text-xs font-semibold"
          :style="{ backgroundColor: catStyle.bg, color: catStyle.text }">
          {{ event.category }}
        </span>
        <span class="px-2 py-0.5 rounded text-xs font-semibold"
          :style="event.price === 0 || !event.price
            ? 'background-color: #E0ECD9; color: #3D5C38'
            : 'background-color: #f3f4f6; color: #4b5563'">
          {{ event.price === 0 || !event.price ? 'Gratuit' : event.price + '€' }}
        </span>
      </div>

      <!-- Titre -->
      <RouterLink :to="`/evenements/${event.id}`">
        <h3 class="text-gray-900 mb-2 line-clamp-2 group-hover:text-[#4D6E47] transition-colors font-bold">
          {{ event.title }}
        </h3>
      </RouterLink>

      <!-- Description -->
      <p class="text-sm text-gray-500 line-clamp-2 mb-3 flex-1 leading-relaxed">
        {{ event.description }}
      </p>

      <!-- Lieu -->
      <div class="flex items-center gap-1.5 text-xs text-gray-500 mt-auto mb-3">
        <MapPin :size="12" class="text-gray-400 shrink-0" />
        <span class="truncate">
          {{ (event.online || event.isOnline) ? 'Évènement en ligne' : `${event.location}, ${event.city}` }}
        </span>
      </div>

      <!-- Footer -->
      <div class="flex items-center justify-between pt-3 border-t border-gray-100">
        <div class="flex items-center gap-3">
          <span class="flex items-center gap-1 text-xs text-gray-500">
            <Heart :size="11" :class="isLiked ? 'fill-red-400 text-red-400' : 'text-gray-400'" />
            {{ event.likesCount || 0 }}
          </span>
          <span class="flex items-center gap-1 text-xs text-gray-500">
            <Users :size="11" class="text-gray-400" />
            {{ event.participantsCount || 0 }}
            <span v-if="event.maxParticipants">/ {{ event.maxParticipants }}</span>
          </span>
        </div>

        <!-- Bouton inscription -->
        <button
          v-if="authStore.isLoggedIn"
          @click.prevent="handleRegister"
          :disabled="isFull && !isRegistered"
          class="px-3 py-1.5 rounded-lg text-xs transition-all font-semibold"
          :style="isRegistered
            ? 'background-color: transparent; color: #4D6E47; border: 1px solid #A8C49E'
            : isFull
            ? 'background-color: #f3f4f6; color: #9ca3af; cursor: not-allowed'
            : 'background-color: #7A9E6E; color: white'"
        >
          {{ isRegistered ? 'Inscrit ✓' : isFull ? 'Complet' : "S'inscrire" }}
        </button>

        <RouterLink
          v-else
          to="/connexion"
          class="px-3 py-1.5 rounded-lg text-xs text-white font-semibold hover:opacity-90 transition-all"
          style="background-color: #7A9E6E"
        >
          S'inscrire
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Heart, MapPin, Users, Wifi } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'

const props = defineProps<{
  event: any
}>()

const authStore = useAuthStore()
const isLiked = ref(props.event.isLiked || false)
const isRegistered = ref(props.event.isRegistered || false)

const isFull = computed(() =>
  props.event.maxParticipants && props.event.participantsCount >= props.event.maxParticipants
)

const CATEGORY_COLORS: Record<string, { bg: string; text: string }> = {
  'Développement': { bg: '#E0ECD9', text: '#3D5C38' },
  'Technologie':   { bg: '#DCF0E8', text: '#1E6B4A' },
  'Design':        { bg: '#FCE8F0', text: '#8B1A4A' },
  'Business':      { bg: '#FEF3DC', text: '#7A4E10' },
  'Sécurité':      { bg: '#FDECEC', text: '#8B1A1A' },
  'Agilité':       { bg: '#E8F8EC', text: '#1A6B2E' },
  'Data':          { bg: '#E8F5FC', text: '#1A5A7A' },
  'Cloud':         { bg: '#E8EEFF', text: '#2A3E8B' },
  'Tech':          { bg: '#DCF0E8', text: '#1E6B4A' },
  'Sport':         { bg: '#FEF3DC', text: '#7A4E10' },
  'Loisirs':       { bg: '#F0E8FF', text: '#5A2E8B' },
  'Culture':       { bg: '#FCE8F0', text: '#8B1A4A' },
}

const catStyle = computed(() =>
  CATEGORY_COLORS[props.event.category] || { bg: '#f3f4f6', text: '#555555' }
)

const dateObj = computed(() => {
  try { return new Date(props.event.date) } catch { return null }
})

const dayNum = computed(() => {
  if (!dateObj.value) return ''
  return dateObj.value.getDate().toString()
})

const monthAbbr = computed(() => {
  if (!dateObj.value) return ''
  return dateObj.value.toLocaleDateString('fr-FR', { month: 'short' }).toUpperCase().replace('.', '')
})

async function handleLike() {
  if (!authStore.isLoggedIn) return
  try {
    if (isLiked.value) {
      await api.delete(`/events/${props.event.id}/like`)
      isLiked.value = false
      props.event.likesCount = Math.max(0, props.event.likesCount - 1)
    } else {
      const response = await api.post(`/events/${props.event.id}/like`)
      isLiked.value = true
      props.event.likesCount = response.data.likesCount
    }
  } catch (e) {
    console.error('Erreur like', e)
  }
}

async function handleRegister() {
  if (!authStore.isLoggedIn) return
  try {
    if (isRegistered.value) {
      await api.delete(`/events/${props.event.id}/register`)
      isRegistered.value = false
      props.event.participantsCount = Math.max(0, props.event.participantsCount - 1)
    } else {
      const response = await api.post(`/events/${props.event.id}/register`)
      isRegistered.value = true
      props.event.participantsCount = response.data.participantsCount
    }
  } catch (e) {
    console.error('Erreur inscription', e)
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
