<template>
  <div v-if="modelValue"
    class="fixed inset-0 z-50 flex items-center justify-center p-4"
    style="background-color: rgba(0,0,0,0.45)"
    @click.self="$emit('update:modelValue', false)">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[80vh] flex flex-col overflow-hidden">

      <!-- Header -->
      <div class="px-6 py-5 border-b border-gray-100 flex items-start justify-between gap-4 shrink-0">
        <div>
          <div class="flex items-center gap-2 mb-1">
            <Users :size="15" class="text-primary" />
            <span class="text-xs font-bold uppercase tracking-wider text-primary">Participants</span>
          </div>
          <h2 class="text-gray-900 font-black text-lg">{{ eventTitle }}</h2>
        </div>
        <button @click="$emit('update:modelValue', false)"
          class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors">
          <X :size="18" />
        </button>
      </div>

      <!-- Stats -->
      <div class="px-6 py-4 border-b border-gray-50 shrink-0">
        <div class="grid grid-cols-2 gap-3">
          <div class="rounded-xl text-center py-2.5 px-2 bg-primary-bg">
            <div class="font-black text-xl text-primary">{{ participants.length }}</div>
            <div class="text-xs text-gray-500 font-medium mt-0.5">Total</div>
          </div>
          <div class="rounded-xl text-center py-2.5 px-2 bg-primary-light">
            <div class="font-black text-xl text-primary-dark">{{ participants.length }}</div>
            <div class="text-xs text-gray-500 font-medium mt-0.5">Confirmés</div>
          </div>
        </div>
      </div>

      <!-- Recherche -->
      <div class="px-6 py-3 border-b border-gray-50 shrink-0">
        <div class="flex items-center gap-2 px-3 py-2 border border-gray-200 rounded-lg bg-gray-50 focus-within:border-gray-400 focus-within:bg-white transition-all">
          <Search :size="13" class="text-gray-400 shrink-0" />
          <input v-model="search" type="text" placeholder="Rechercher un participant..."
            class="flex-1 bg-transparent text-sm text-gray-700 outline-none placeholder-gray-400" />
        </div>
      </div>

      <!-- Liste -->
      <div class="flex-1 overflow-y-auto px-6 py-3">
        <div v-if="loading" class="flex justify-center py-8">
          <div class="w-6 h-6 border-2 border-gray-200 border-t-primary rounded-full animate-spin" />
        </div>
        <div v-else-if="filteredParticipants.length === 0" class="text-center py-10">
          <Users :size="32" class="text-gray-200 mx-auto mb-2" />
          <p class="text-gray-400 text-sm">
            {{ participants.length === 0 ? 'Aucun inscrit pour le moment.' : 'Aucun résultat.' }}
          </p>
        </div>
        <div v-else class="space-y-2">
          <div v-for="user in filteredParticipants" :key="user.id"
            class="flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-gray-50 transition-colors">
            <div class="w-9 h-9 rounded-lg flex items-center justify-center text-white text-sm font-bold shrink-0 bg-primary">
              {{ getInitials(user) }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm text-gray-900 font-semibold truncate">{{ user.firstName }} {{ user.lastName }}</p>
              <p class="text-xs text-gray-400 truncate">{{ user.email }}</p>
            </div>
            
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-gray-100 shrink-0 flex items-center justify-between">
        <p class="text-xs text-gray-400">
          {{ filteredParticipants.length }} participant{{ filteredParticipants.length > 1 ? 's' : '' }}
        </p>
        <button @click="$emit('update:modelValue', false)"
          class="px-5 py-2 rounded-lg text-sm border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors font-semibold">
          Fermer
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Users, X, Search } from 'lucide-vue-next'
import api from '@/services/api'

const props = defineProps<{
  modelValue: boolean
  eventId: number | null
  eventTitle: string
}>()

defineEmits(['update:modelValue'])

const participants = ref<any[]>([])
const loading = ref(false)
const search = ref('')

const filteredParticipants = computed(() => {
  if (!search.value.trim()) return participants.value
  const q = search.value.toLowerCase()
  return participants.value.filter(u =>
    `${u.firstName} ${u.lastName}`.toLowerCase().includes(q) ||
    u.email.toLowerCase().includes(q)
  )
})

function getInitials(user: any) {
  return `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase()
}

watch(() => props.modelValue, async (val) => {
  if (val && props.eventId) {
    loading.value = true
    search.value = ''
    try {
      const response = await api.get(`/events/${props.eventId}/registrations`)
      participants.value = response.data
    } catch {
      participants.value = []
    } finally {
      loading.value = false
    }
  }
})
</script>
