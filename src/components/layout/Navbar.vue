<template>
  <header class="sticky top-0 z-50 bg-white border-b border-gray-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center gap-4 h-[60px]">

        <!-- Logo -->
        <RouterLink to="/" class="flex items-center gap-2 shrink-0 mr-2">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center" style="background-color: #6BAE38">
            <CalendarDays :size="16" class="text-white" />
          </div>
          <span class="text-gray-900 hidden sm:block" style="font-weight: 800; font-size: 1.15rem; letter-spacing: -0.02em">
            meet<span style="color: #7A9E6E">up</span>
          </span>
        </RouterLink>

        <!-- Search bar desktop -->
        <form @submit.prevent="handleSearch" class="hidden md:flex flex-1 max-w-lg">
          <div class="flex w-full border border-gray-300 rounded-lg overflow-hidden hover:border-gray-400 focus-within:border-gray-900 transition-colors">
            <div class="flex items-center gap-2 flex-1 px-3 py-2 bg-white">
              <Search :size="16" class="text-gray-400 shrink-0" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Rechercher des évènements"
                class="flex-1 bg-transparent text-sm text-gray-800 placeholder-gray-400 outline-none"
              />
            </div>
            <button
              type="submit"
              class="px-4 text-sm text-white shrink-0 transition-colors"
              style="background-color: #7A9E6E; font-weight: 600"
              @mouseenter="e => e.currentTarget.style.backgroundColor = '#5E7D58'"
              @mouseleave="e => e.currentTarget.style.backgroundColor = '#7A9E6E'"
            >
              Chercher
            </button>
          </div>
        </form>

        <!-- Nav links desktop -->
        <nav class="hidden lg:flex items-center gap-1 ml-2">
          <RouterLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            class="flex items-center gap-1 px-3 py-2 rounded-md text-sm transition-colors"
            :class="isActive(link.to)
              ? 'text-gray-900 bg-gray-100 font-semibold'
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'"
          >
            <component :is="link.icon" v-if="link.icon" :size="14" />
            {{ link.label }}
          </RouterLink>
        </nav>

        <div class="flex-1 lg:flex-none" />

        <!-- Right actions desktop -->
        <div class="hidden md:flex items-center gap-2">

          <!-- Connecté -->
          <template v-if="authStore.isLoggedIn">
            <RouterLink
              to="/evenements/creer"
              class="flex items-center gap-1.5 px-4 py-2 rounded-lg text-sm text-white transition-colors"
              style="background-color: #7A9E6E; font-weight: 600"
              @mouseenter="e => e.currentTarget.style.backgroundColor = '#5E7D58'"
              @mouseleave="e => e.currentTarget.style.backgroundColor = '#7A9E6E'"
            >
              <Plus :size="15" />
              Créer
            </RouterLink>

            <div class="relative" ref="dropdownRef">
              <button
                @click="dropdownOpen = !dropdownOpen"
                class="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <div class="w-7 h-7 rounded-full flex items-center justify-center text-white text-xs font-bold" style="background-color: #7A9E6E">
                  {{ userInitials }}
                </div>
                <span class="text-sm text-gray-700 max-w-[100px] truncate">
                  {{ authStore.user?.firstName }} {{ authStore.user?.lastName }}
                </span>
                <ChevronDown :size="13" class="text-gray-400" />
              </button>

              <div
                v-if="dropdownOpen"
                class="absolute right-0 mt-1 w-48 bg-white rounded-xl shadow-lg border border-gray-200 py-1 z-50"
              >
                <div class="px-4 py-2 border-b border-gray-100">
                  <p class="text-xs text-gray-400 truncate">{{ authStore.user?.email }}</p>
                </div>
                <RouterLink to="/profil" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50" @click="dropdownOpen = false">
                  <User :size="14" /> Mon compte
                </RouterLink>
                <RouterLink to="/evenements/creer" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50" @click="dropdownOpen = false">
                  <CalendarDays :size="14" /> Créer un évènement
                </RouterLink>
                <hr class="my-1 border-gray-100" />
                <button @click="handleLogout" class="w-full flex items-center gap-2 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50">
                  <LogOut :size="14" /> Se déconnecter
                </button>
              </div>
            </div>
          </template>

          <!-- Non connecté -->
          <template v-else>
            <RouterLink
              to="/connexion"
              class="px-4 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
              style="font-weight: 500"
            >
              Connexion
            </RouterLink>
            <RouterLink
              to="/inscription"
              class="px-4 py-2 rounded-lg text-sm text-white transition-colors"
              style="background-color: #7A9E6E; font-weight: 600"
              @mouseenter="e => e.currentTarget.style.backgroundColor = '#5E7D58'"
              @mouseleave="e => e.currentTarget.style.backgroundColor = '#7A9E6E'"
            >
              S'inscrire
            </RouterLink>
          </template>
        </div>

        <!-- Mobile menu button -->
        <button class="md:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100" @click="menuOpen = !menuOpen">
          <X v-if="menuOpen" :size="20" />
          <Menu v-else :size="20" />
        </button>

      </div>
    </div>

    <!-- Mobile Menu -->
    <div v-if="menuOpen" class="md:hidden border-t border-gray-200 bg-white">
      <div class="px-4 pt-3 pb-2">
        <form @submit.prevent="handleSearch">
          <div class="flex border border-gray-300 rounded-lg overflow-hidden">
            <div class="flex items-center gap-2 flex-1 px-3 py-2">
              <Search :size="15" class="text-gray-400" />
              <input v-model="searchQuery" type="text" placeholder="Rechercher..." class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
            </div>
            <button type="submit" class="px-3 text-white text-sm" style="background-color: #6BAE38">
              <Search :size="15" />
            </button>
          </div>
        </form>
      </div>

      <div class="px-4 py-2 space-y-1">
        <RouterLink
          v-for="link in mobileLinks"
          :key="link.to"
          :to="link.to"
          class="block px-3 py-2.5 rounded-lg text-sm"
          :class="isActive(link.to) ? 'bg-[#E5EDE0] text-[#4D6E47]' : 'text-gray-700 hover:bg-gray-50'"
          @click="menuOpen = false"
        >
          {{ link.label }}
        </RouterLink>

        <hr class="my-2 border-gray-100" />

        <template v-if="authStore.isLoggedIn">
          <RouterLink to="/evenements/creer" class="block px-3 py-2.5 rounded-lg text-sm text-[#4D6E47] hover:bg-[#E5EDE0]" @click="menuOpen = false">
            + Créer un évènement
          </RouterLink>
          <RouterLink to="/profil" class="block px-3 py-2.5 rounded-lg text-sm text-gray-700 hover:bg-gray-50" @click="menuOpen = false">
            Mon compte
          </RouterLink>
          <button @click="handleLogout" class="w-full text-left px-3 py-2.5 rounded-lg text-sm text-red-600 hover:bg-red-50">
            Se déconnecter
          </button>
        </template>

        <template v-else>
          <RouterLink to="/connexion" class="block px-3 py-2.5 rounded-lg text-sm text-gray-700 hover:bg-gray-50" @click="menuOpen = false">
            Connexion
          </RouterLink>
          <RouterLink to="/inscription" class="block px-3 py-2.5 rounded-lg text-sm text-center text-white" style="background-color: #7A9E6E" @click="menuOpen = false">
            S'inscrire
          </RouterLink>
        </template>
      </div>
    </div>

  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Plus, Menu, X, LogOut, User, ChevronDown, HelpCircle, CalendarDays } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const menuOpen = ref(false)
const dropdownOpen = ref(false)
const searchQuery = ref('')
const dropdownRef = ref<HTMLElement | null>(null)

const navLinks = [
  { to: '/evenements', label: 'Évènements', icon: null },
  { to: '/aide', label: 'Aide', icon: HelpCircle },
]

const mobileLinks = [
  { to: '/', label: 'Accueil' },
  { to: '/evenements', label: 'Évènements' },
  { to: '/aide', label: 'Aide' },
]

const userInitials = computed(() => {
  if (!authStore.user) return '?'
  return `${authStore.user.firstName?.[0] ?? ''}${authStore.user.lastName?.[0] ?? ''}`.toUpperCase()
})

function isActive(path: string) {
  return route.path === path
}

function handleSearch() {
  const q = searchQuery.value.trim()
  router.push(q ? `/evenements?q=${encodeURIComponent(q)}` : '/evenements')
  searchQuery.value = ''
  menuOpen.value = false
}

function handleLogout() {
  authStore.logout()
  dropdownOpen.value = false
  menuOpen.value = false
  router.push('/')
}

function handleClickOutside(e: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    dropdownOpen.value = false
  }
}

onMounted(() => document.addEventListener('mousedown', handleClickOutside))
onUnmounted(() => document.removeEventListener('mousedown', handleClickOutside))
</script>