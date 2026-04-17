<template>
  <div class="min-h-screen bg-white">

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="w-8 h-8 border-2 border-gray-200 border-t-[#7A9E6E] rounded-full animate-spin" />
    </div>

    <!-- Not found -->
    <div v-else-if="!event" class="flex flex-col items-center justify-center min-h-screen gap-4">
      <CalendarX :size="48" class="text-gray-300" />
      <p class="text-gray-500 font-medium">Évènement introuvable</p>
      <RouterLink to="/evenements" class="text-sm font-semibold underline" style="color: #7A9E6E">
        Retour aux évènements
      </RouterLink>
    </div>

    <template v-else>
      <!-- Hero image -->
      <div class="relative h-64 md:h-80 overflow-hidden">
        <img :src="event.imageUrl" :alt="event.title" class="w-full h-full object-cover" />
        <div class="absolute inset-0" style="background: linear-gradient(to top, rgba(0,0,0,0.6) 0%, transparent 60%)" />
        <div class="absolute top-4 left-4">
          <RouterLink
            to="/evenements"
            class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-semibold text-white backdrop-blur-sm transition-colors"
            style="background-color: rgba(0,0,0,0.35)"
          >
            <ArrowLeft :size="14" /> Retour
          </RouterLink>
        </div>
        <div class="absolute bottom-4 left-4 right-4">
          <div class="flex items-center gap-2 flex-wrap">
            <span class="text-xs font-bold px-2 py-1 rounded-md" style="background-color: #7A9E6E; color: white">
              {{ event.category }}
            </span>
            <span
              class="text-xs font-bold px-2 py-1 rounded-md"
              :style="event.price === 0 ? 'background-color: #E0ECD9; color: #3D5C38' : 'background-color: #FEF3DC; color: #7A4E10'"
            >
              {{ event.price === 0 ? 'Gratuit' : `${event.price}€` }}
            </span>
            <span v-if="event.isOnline" class="text-xs font-bold px-2 py-1 rounded-md" style="background-color: rgba(255,255,255,0.15); color: white; backdrop-filter: blur(4px)">
              En ligne
            </span>
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="max-w-5xl mx-auto px-4 sm:px-6 py-8">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

          <!-- Main -->
          <div class="lg:col-span-2 space-y-8">
            <div>
              <h1 class="text-gray-900 font-black text-2xl md:text-3xl mb-3" style="letter-spacing: -0.03em; line-height: 1.2">
                {{ event.title }}
              </h1>
              <div class="flex items-center gap-4 text-sm text-gray-500 flex-wrap">
                <div class="flex items-center gap-1.5">
                  <CalendarDays :size="15" style="color: #7A9E6E" />
                  <span>{{ formatDate(event.date) }}</span>
                </div>
                <div class="flex items-center gap-1.5">
                  <Clock :size="15" style="color: #7A9E6E" />
                  <span>{{ formatTime(event.date) }}</span>
                </div>
                <div class="flex items-center gap-1.5">
                  <MapPin :size="15" style="color: #7A9E6E" />
                  <span>{{ event.isOnline ? 'En ligne' : event.city }}</span>
                </div>
              </div>
            </div>

            <!-- Description -->
            <div>
              <h2 class="text-gray-900 font-bold text-lg mb-3" style="letter-spacing: -0.02em">À propos</h2>
              <p class="text-gray-600 leading-relaxed text-sm whitespace-pre-line">{{ event.description }}</p>
            </div>

            <!-- Location -->
            <div v-if="!event.isOnline">
              <h2 class="text-gray-900 font-bold text-lg mb-3" style="letter-spacing: -0.02em">Lieu</h2>
              <div class="flex items-start gap-3 p-4 rounded-xl border border-gray-100" style="background-color: #F2F5F0">
                <div class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0" style="background-color: #E0ECD9">
                  <MapPin :size="16" style="color: #5E7D58" />
                </div>
                <div>
                  <p class="text-gray-900 font-semibold text-sm">{{ event.location }}</p>
                  <p class="text-gray-500 text-xs mt-0.5">{{ event.city }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar -->
          <div class="space-y-4">
            <!-- Registration card -->
            <div class="rounded-2xl border border-gray-100 p-5 shadow-sm">
              <div class="flex items-center justify-between mb-4">
                <div>
                  <p class="text-gray-900 font-black text-2xl" style="letter-spacing: -0.03em">
                    {{ event.price === 0 ? 'Gratuit' : `${event.price}€` }}
                  </p>
                  <p class="text-gray-400 text-xs mt-0.5">par participant</p>
                </div>
                <div class="flex items-center gap-1.5 text-sm text-gray-500">
                  <Users :size="15" />
                  <span>{{ event.participantsCount }} inscrits</span>
                </div>
              </div>

              <!-- Progress bar -->
              <div v-if="event.maxParticipants" class="mb-4">
                <div class="flex justify-between text-xs text-gray-400 mb-1">
                  <span>{{ event.participantsCount }} / {{ event.maxParticipants }} places</span>
                  <span>{{ Math.round((event.participantsCount / event.maxParticipants) * 100) }}%</span>
                </div>
                <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    class="h-full rounded-full transition-all"
                    style="background-color: #7A9E6E"
                    :style="{ width: `${Math.min((event.participantsCount / event.maxParticipants) * 100, 100)}%` }"
                  />
                </div>
              </div>

              <button
                @click="handleRegister"
                class="w-full py-3 rounded-lg text-white text-sm font-bold transition-colors mb-3"
                :style="{ backgroundColor: event.isRegistered ? '#E0ECD9' : '#7A9E6E', color: event.isRegistered ? '#3D5C38' : 'white' }"
              >
                {{ event.isRegistered ? '✓ Inscrit' : "S'inscrire" }}
              </button>

              <button
                @click="handleLike"
                class="w-full py-2.5 rounded-lg border text-sm font-semibold flex items-center justify-center gap-2 transition-colors"
                :style="event.isLiked ? 'border-color: #7A9E6E; color: #5E7D58; background-color: #F2F5F0' : 'border-color: #e5e7eb; color: #374151'"
              >
                <Heart :size="15" :fill="event.isLiked ? '#7A9E6E' : 'none'" />
                {{ event.isLiked ? 'Aimé' : 'J\'aime' }} · {{ event.likesCount }}
              </button>
            </div>

            <!-- Share -->
            <div class="rounded-2xl border border-gray-100 p-5">
              <h3 class="text-gray-900 font-bold text-sm mb-3">Partager</h3>
              <button
                @click="copyLink"
                class="w-full flex items-center gap-2 px-3 py-2 rounded-lg border border-gray-200 text-sm text-gray-600 hover:border-gray-400 transition-colors"
              >
                <Link2 :size="14" />
                {{ copied ? 'Lien copié !' : 'Copier le lien' }}
              </button>
            </div>

            <!-- Edit button (if creator) -->
            <RouterLink
              v-if="isCreator"
              :to="`/evenements/${event.id}/modifier`"
              class="flex items-center justify-center gap-2 w-full py-2.5 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:border-gray-400 transition-colors"
            >
              <Pencil :size="14" /> Modifier l'évènement
            </RouterLink>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CalendarDays, Clock, MapPin, Users, Heart, Link2, Pencil, CalendarX } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth.store'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const copied = ref(false)

// Mock event — à remplacer par un appel API
const event = ref<any>(null)

const mockEvents = [
  {
    id: 1,
    title: 'Vue.js Paris Meetup #42',
    description: 'Soirée dédiée aux dernières nouveautés de Vue 3, Pinia et Nuxt 4.\n\nAu programme :\n• Talk 1 : Les nouveautés de Vue 3.5\n• Talk 2 : Pinia en production — retour d\'expérience\n• Talk 3 : Migration vers Nuxt 4\n\nNetworking et pizzas offerts après les talks !',
    date: '2025-04-15T19:00:00',
    location: '10 rue de la Paix',
    city: 'Paris',
    isOnline: false,
    imageUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80',
    price: 0,
    maxParticipants: 100,
    category: 'Développement',
    creatorId: 1,
    participantsCount: 87,
    likesCount: 34,
    isRegistered: false,
    isLiked: false,
  },
  {
    id: 2,
    title: 'Workshop Spring Boot & Docker',
    description: 'Apprenez à conteneuriser une application Spring Boot avec Docker Compose.\n\nContenu :\n• Introduction à Docker\n• Créer un Dockerfile pour Spring Boot\n• Docker Compose multi-services\n• Bonnes pratiques en production',
    date: '2025-04-20T14:00:00',
    location: 'Campus Digital',
    city: 'Lyon',
    isOnline: false,
    imageUrl: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=800&q=80',
    price: 15,
    maxParticipants: 30,
    category: 'Technologie',
    creatorId: 2,
    participantsCount: 32,
    likesCount: 21,
    isRegistered: false,
    isLiked: false,
  },
  {
    id: 3,
    title: 'Introduction à la Cybersécurité',
    description: 'Découvrez les bases de la sécurité informatique et les bonnes pratiques.\n\nThèmes abordés :\n• Les types d\'attaques courantes\n• Sécurisation des applications web\n• OWASP Top 10\n• Outils de pentest débutant',
    date: '2025-04-25T18:30:00',
    location: 'En ligne',
    city: 'En ligne',
    isOnline: true,
    imageUrl: 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800&q=80',
    price: 0,
    maxParticipants: undefined,
    category: 'Sécurité',
    creatorId: 3,
    participantsCount: 156,
    likesCount: 89,
    isRegistered: false,
    isLiked: false,
  },
]

const isCreator = computed(() => {
  if (!event.value || !authStore.user) return false
  return event.value.creatorId === authStore.user.id
})

onMounted(() => {
  const id = Number(route.params.id)
  const found = mockEvents.find(e => e.id === id)
  event.value = found ? { ...found } : null
  loading.value = false
})

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  })
}

function formatTime(dateStr: string): string {
  return new Date(dateStr).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
}

function handleRegister() {
  if (!authStore.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (event.value) event.value.isRegistered = !event.value.isRegistered
}

function handleLike() {
  if (!authStore.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (event.value) {
    event.value.isLiked = !event.value.isLiked
    event.value.likesCount += event.value.isLiked ? 1 : -1
  }
}

async function copyLink() {
  await navigator.clipboard.writeText(window.location.href)
  copied.value = true
  setTimeout(() => copied.value = false, 2000)
}
</script>
