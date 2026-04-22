<template>
  <div class="min-h-screen bg-white">

    <!-- ─── Hero ─── -->
    <section
      class="border-b border-gray-100 py-16 md:py-24 relative overflow-hidden"
      :style="{
        backgroundImage: `url('https://images.unsplash.com/photo-1772050138768-2107c6e62a03?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080')`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }"
    >
      <!-- Overlay -->
      <div class="absolute inset-0" style="background: linear-gradient(135deg, rgba(255,255,255,0.92) 0%, rgba(235,245,230,0.88) 100%)" />

      <div class="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 text-center">
        <p class="text-sm mb-4 uppercase tracking-widest font-bold" style="color: #7A9E6E">
          La plateforme meetup tech en France
        </p>
        <h1 class="text-gray-900 mb-6" style="font-size: clamp(2rem, 5vw, 3.2rem); font-weight: 900; line-height: 1.15; letter-spacing: -0.03em">
          Trouvez votre prochain<br />
          <span style="color: #7A9E6E">évènement tech</span>
        </h1>
        <p class="text-gray-500 mb-10 max-w-xl mx-auto" style="font-size: 1.05rem; line-height: 1.7">
          Workshops, conférences, meetups, hackathons — découvrez des évènements passionnants près de chez vous ou en ligne.
        </p>

        <!-- Search -->
        <form @submit.prevent="handleSearch" class="max-w-2xl mx-auto">
          <div class="flex border-2 border-gray-900 rounded-xl overflow-hidden shadow-sm bg-white">
            <div class="flex-1 flex items-center gap-3 px-4 py-3 bg-white">
              <Search :size="18" class="text-gray-400 shrink-0" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Titre, technologie, ville, organisateur..."
                class="flex-1 bg-transparent text-gray-900 placeholder-gray-400 outline-none text-sm"
              />
            </div>
            <button
              type="submit"
              class="px-6 text-white shrink-0 font-bold text-sm transition-colors"
              style="background-color: #7A9E6E"
              @mouseenter="setTargetBackground($event, '#5E7D58')"
              @mouseleave="setTargetBackground($event, '#7A9E6E')"
            >
              Rechercher
            </button>
          </div>
        </form>

        <!-- Quick filters -->
        <div class="flex flex-wrap items-center justify-center gap-2 mt-6">
          <RouterLink
            v-for="tag in quickFilters"
            :key="tag"
            :to="`/evenements?q=${encodeURIComponent(tag)}`"
            class="px-3 py-1.5 border border-gray-300 text-gray-600 rounded-full text-sm hover:border-gray-500 hover:text-gray-900 transition-colors font-medium"
            style="background-color: rgba(255,255,255,0.7)"
          >
            {{ tag }}
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ─── Stats ─── -->
    <section class="border-b border-gray-100 py-8" style="background-color: #F2F5F0">
      <div class="max-w-5xl mx-auto px-4 sm:px-6">
        <div class="grid grid-cols-3 gap-6 divide-x divide-gray-200">
          <div v-for="stat in stats" :key="stat.label" class="text-center px-4">
            <div class="flex items-center justify-center gap-2 mb-1">
              <component :is="stat.icon" :size="18" style="color: #7A9E6E" />
              <span class="font-black text-2xl text-gray-900">{{ stat.value }}</span>
            </div>
            <p class="text-gray-500 text-xs font-medium">{{ stat.label }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ─── Categories ─── -->
    <section class="py-14 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-end justify-between mb-8">
          <div>
            <h2 class="text-gray-900 font-extrabold text-xl" style="letter-spacing: -0.02em">
              Parcourir par catégorie
            </h2>
            <p class="text-gray-500 text-sm mt-1">Explorez par domaine d'intérêt</p>
          </div>
          <RouterLink to="/evenements" class="flex items-center gap-1 text-sm font-semibold" style="color: #7A9E6E">
            Voir tout <ChevronRight :size="14" />
          </RouterLink>
        </div>
        <div class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-3">
          <RouterLink
              v-for="cat in categories"
              :key="cat.name"
              :to="`/evenements?cat=${encodeURIComponent(cat.name)}`"
              class="flex flex-col items-center gap-3 rounded-xl border border-gray-200 bg-white hover:border-[#6bae38] hover:bg-[#F2F5F0] transition-all group text-center p-4"
          >
            <svg
                viewBox="0 0 24 24"
                class="w-8 h-8 transition-transform group-hover:scale-110"
                fill="none"
                stroke="#6bae38"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                v-html="cat.svg"
            ></svg>

            <span class="text-xs text-gray-700 group-hover:text-[#4D6E47] transition-colors font-bold uppercase tracking-tight">
              {{ cat.name }}
            </span>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ─── Événements récents ─── -->
    <section class="py-14" style="background-color: #F2F5F0">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-end justify-between mb-8">
          <div>
            <div class="flex items-center gap-2 mb-1">
              <TrendingUp :size="16" style="color: #7A9E6E" />
              <span class="text-xs uppercase tracking-wider font-bold" style="color: #7A9E6E">Récemment ajoutés</span>
            </div>
            <h2 class="text-gray-900 font-extrabold text-xl" style="letter-spacing: -0.02em">
              Évènements à la une
            </h2>
          </div>
          <RouterLink to="/evenements" class="flex items-center gap-1 text-sm font-semibold" style="color: #7A9E6E">
            Voir tout <ArrowRight :size="14" />
          </RouterLink>
        </div>

        <!-- Cards grille -->
        <!-- <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          <div
            v-for="event in mockEvents"
            :key="event.id"
            class="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:border-[#A8C49E] hover:shadow-md transition-all group cursor-pointer"
            @click="router.push(`/evenements/${event.id}`)"
          >
            <div class="h-40 overflow-hidden">
              <img :src="event.imageUrl" :alt="event.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
            </div>
            <div class="p-4">
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-semibold px-2 py-0.5 rounded" style="background-color: #E0ECD9; color: #3D5C38">
                  {{ event.category }}
                </span>
                <span class="text-xs font-semibold px-2 py-0.5 rounded" :style="event.price === 0 ? 'background-color: #E0ECD9; color: #3D5C38' : 'background-color: #FEF3DC; color: #7A4E10'">
                  {{ event.price === 0 ? 'Gratuit' : `${event.price}€` }}
                </span>
              </div>
              <h3 class="text-gray-900 font-bold text-sm mb-1 line-clamp-2 group-hover:text-[#4D6E47] transition-colors">
                {{ event.title }}
              </h3>
              <p class="text-gray-500 text-xs mb-3 line-clamp-2">{{ event.description }}</p>
              <div class="flex items-center justify-between text-xs text-gray-400">
                <div class="flex items-center gap-1">
                  <CalendarDays :size="12" />
                  <span>{{ formatDate(event.date) }}</span>
                </div>
                <div class="flex items-center gap-1">
                  <MapPin :size="12" />
                  <span>{{ event.isOnline ? 'En ligne' : event.city }}</span>
                </div>
                <div class="flex items-center gap-1">
                  <Users :size="12" />
                  <span>{{ event.participantsCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div> -->

        
        <!-- Cards grille -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          <EventCard v-for="event in events" :key="event.id" :event="event" />
        </div>

        
        <div class="text-center mt-8">
          <RouterLink
            to="/evenements"
            class="inline-flex items-center gap-2 px-6 py-3 rounded-lg text-white text-sm font-bold transition-colors"
            style="background-color: #7A9E6E"
            @mouseenter="setTargetBackground($event, '#5E7D58')"
            @mouseleave="setTargetBackground($event, '#7A9E6E')"
          >
            Voir tous les évènements
            <ArrowRight :size="15" />
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ─── CTA organisateur ─── -->
    <section class="py-16 border-t border-gray-100">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 text-center">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl mb-6" style="background-color: #E5EDE0">
          <Plus :size="24" style="color: #5E7D58" />
        </div>
        <h2 class="text-gray-900 mb-3 font-extrabold text-2xl" style="letter-spacing: -0.02em">
          Organisez votre prochain évènement
        </h2>
        <p class="text-gray-500 mb-8 max-w-md mx-auto" style="line-height: 1.7">
          Créez votre évènement gratuitement et touchez des milliers de participants passionnés de tech.
        </p>
        <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
          <RouterLink
            to="/evenements/creer"
            class="flex items-center gap-2 px-7 py-3.5 rounded-lg text-white text-sm font-bold transition-colors"
            style="background-color: #7A9E6E"
            @mouseenter="setTargetBackground($event, '#5E7D58')"
            @mouseleave="setTargetBackground($event, '#7A9E6E')"
          >
            <Plus :size="16" />
            Créer un évènement
          </RouterLink>
          <RouterLink
            to="/evenements"
            class="px-7 py-3.5 rounded-lg border border-gray-300 text-gray-700 text-sm font-semibold hover:border-gray-500 hover:text-gray-900 transition-colors"
          >
            Explorer les évènements
          </RouterLink>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
// import { ref } from 'vue'
// import { useRouter } from 'vue-router'
// import {
//   Search, ArrowRight, MapPin, Users, CalendarDays,
//   TrendingUp, Plus, ChevronRight
// } from 'lucide-vue-next'

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search, ArrowRight, MapPin, Users, CalendarDays,
  TrendingUp, Plus, ChevronRight
} from 'lucide-vue-next'
import api from '@/services/api'
import EventCard from '@/components/event/EventCard.vue'

const router = useRouter()
const searchQuery = ref('')

const quickFilters = ['Gratuit', 'En ligne', 'Ce week-end', 'Paris', 'Lyon']

const categories = [
  { name: 'Développement', svg: '<path d="M18 5a2 2 0 0 1 2 2v8.526a2 2 0 0 0 .212.897l1.068 2.127a1 1 0 0 1-.9 1.45H3.62a1 1 0 0 1-.9-1.45l1.068-2.127A2 2 0 0 0 4 15.526V7a2 2 0 0 1 2-2z"/><path d="M20.054 15.987H3.946"/>'},
  { name: 'Technologie', svg: '<path d="m10.065 12.493-6.18 1.318a.934.934 0 0 1-1.108-.702l-.537-2.15a1.07 1.07 0 0 1 .691-1.265l13.504-4.44"/><path d="m13.56 11.747 4.332-.924"/><path d="m16 21-3.105-6.21"/><path d="M16.485 5.94a2 2 0 0 1 1.455-2.425l1.09-.272a1 1 0 0 1 1.212.727l1.515 6.06a1 1 0 0 1-.727 1.213l-1.09.272a2 2 0 0 1-2.425-1.455z"/><path d="m6.158 8.633 1.114 4.456"/><path d="m8 21 3.105-6.21"/><circle cx="12" cy="13" r="2"/>'},
  { name: 'Design', svg: '<path d="M12 22a1 1 0 0 1 0-20 10 9 0 0 1 10 9 5 5 0 0 1-5 5h-2.25a1.75 1.75 0 0 0-1.4 2.8l.3.4a1.75 1.75 0 0 1-1.4 2.8z"/><circle cx="13.5" cy="6.5" r=".5"/><circle cx="17.5" cy="10.5" r=".5"/><circle cx="6.5" cy="12.5" r=".5"/><circle cx="8.5" cy="7.5" r=".5"/>'},
  { name: 'Business', svg: '<path d="m11 17 2 2a1 1 0 1 0 3-3"/><path d="m14 14 2.5 2.5a1 1 0 1 0 3-3l-3.88-3.88a3 3 0 0 0-4.24 0l-.88.88a1 1 0 1 1-3-3l2.81-2.81a5.79 5.79 0 0 1 7.06-.87l.47.28a2 2 0 0 0 1.42.25L21 4"/><path d="m21 3 1 11h-2"/><path d="M3 3 2 14l6.5 6.5a1 1 0 1 0 3-3"/><path d="M3 4h8"/>'},
  { name: 'Sécurité', svg: '<circle cx="12" cy="16" r="1"/><rect x="3" y="10" width="18" height="12" rx="2"/><path d="M7 10V7a5 5 0 0 1 10 0v3"/>'},
  { name: 'Agilité', svg: '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>'},
  { name: 'Data', svg: '<ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5V19A9 3 0 0 0 21 19V5"/><path d="M3 12A9 3 0 0 0 21 12"/>'},
  { name: 'Cloud', svg: '<path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/>'}
]

const stats = [
  { label: 'Évènements publiés', value: '200+', icon: CalendarDays },
  { label: 'Participants actifs', value: '15k+', icon: Users },
  { label: 'Villes couvertes',   value: '30+',  icon: MapPin },
]

// Données mock temporaires — à remplacer par les appels API plus tard
// const mockEvents = ref([
//   {
//     id: 1,
//     title: 'Vue.js Paris Meetup #42',
//     description: 'Soirée dédiée aux dernières nouveautés de Vue 3, Pinia et Nuxt 4.',
//     date: '2025-04-15T19:00:00',
//     city: 'Paris',
//     isOnline: false,
//     imageUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400&q=80',
//     price: 0,
//     category: 'Développement',
//     participantsCount: 87,
//     likesCount: 34,
//   },
//   {
//     id: 2,
//     title: 'Workshop Spring Boot & Docker',
//     description: 'Apprenez à conteneuriser une application Spring Boot avec Docker Compose.',
//     date: '2025-04-20T14:00:00',
//     city: 'Lyon',
//     isOnline: false,
//     imageUrl: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=400&q=80',
//     price: 15,
//     category: 'Technologie',
//     participantsCount: 32,
//     likesCount: 21,
//   },
//   {
//     id: 3,
//     title: 'Introduction à la Cybersécurité',
//     description: 'Découvrez les bases de la sécurité informatique et les bonnes pratiques.',
//     date: '2025-04-25T18:30:00',
//     city: 'En ligne',
//     isOnline: true,
//     imageUrl: 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400&q=80',
//     price: 0,
//     category: 'Sécurité',
//     participantsCount: 156,
//     likesCount: 89,
//   },
// ])


const events = ref<any[]>([])

onMounted(async () => {
  try {
    const response = await api.get('/events')
    events.value = response.data.slice(0, 3) // On affiche que 3 sur l'accueil
  } catch (e) {
    console.error('Erreur chargement événements', e)
  }
})

function handleSearch() {
  const q = searchQuery.value.trim()
  router.push(q ? `/evenements?q=${encodeURIComponent(q)}` : '/evenements')
}

// function formatDate(dateStr: string): string {
//   return new Date(dateStr).toLocaleDateString('fr-FR', {
//     day: 'numeric',
//     month: 'short',
//     year: 'numeric',
//   })
// }

function setTargetBackground(event: MouseEvent, color: string) {
  const target = event.currentTarget as HTMLElement | null
  if (!target) return
  target.style.backgroundColor = color
}
</script>