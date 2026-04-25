<template>
  <div class="bg-white border border-gray-200 rounded-2xl overflow-hidden hover:border-gray-300 hover:shadow-sm transition-all">
    <div class="flex flex-col sm:flex-row">
      <!-- Image -->
      <div class="sm:w-36 h-32 sm:h-auto shrink-0 relative overflow-hidden bg-gray-100">
        <img v-if="event.imageUrl" :src="event.imageUrl" :alt="event.title" class="w-full h-full object-cover" />
        <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs">Pas d'image</div>
      </div>

      <!-- Contenu -->
      <div class="flex-1 p-4 min-w-0 flex flex-col gap-2">
        <div>
          <span class="text-xs px-2 py-0.5 rounded font-semibold bg-primary-light text-primary-dark">
            {{ event.category }}
          </span>
          <h3 class="text-gray-900 mt-1.5 font-bold text-sm leading-snug">{{ event.title }}</h3>
        </div>

        <!-- Meta -->
        <div class="flex flex-wrap gap-x-4 gap-y-1 text-xs text-gray-500">
          <span class="flex items-center gap-1">
            <CalendarDays :size="11" /> {{ formatDate(event.date) }}
          </span>
          <span class="flex items-center gap-1">
            <MapPin :size="11" /> {{ event.online || event.isOnline ? 'En ligne' : event.city }}
          </span>
        </div>

        <!-- Stats -->
        <div class="flex items-center gap-4 text-xs text-gray-500">
          <span class="flex items-center gap-1">
            <Heart :size="11" class="text-primary" />
            <span class="font-semibold text-gray-700">{{ event.likesCount || 0 }}</span> likes
          </span>
          <span class="flex items-center gap-1">
            <Users :size="11" class="text-primary" />
            <span class="font-semibold text-gray-700">{{ event.participantsCount || 0 }}</span>
            <span v-if="event.maxParticipants">/ {{ event.maxParticipants }}</span>
            inscrits
          </span>
        </div>

        <!-- Barre de progression -->
        <div v-if="event.maxParticipants" class="flex items-center gap-2">
          <div class="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all"
              :style="{
                width: `${Math.min((event.participantsCount / event.maxParticipants) * 100, 100)}%`,
                backgroundColor: fillColor
              }" />
          </div>
          <span class="text-xs text-gray-400 font-semibold shrink-0">{{ fillPct }}%</span>
        </div>

        <!-- Actions -->
        <div class="flex items-center gap-2 pt-1 mt-auto flex-wrap">
            <button @click="showParticipants = true"
                class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-primary-light text-primary-dark border border-primary/30">
                <Users :size="12" /> Participants
                <span v-if="event.participantsCount > 0" class="ml-0.5 px-1.5 py-0.5 rounded-full text-white text-xs bg-primary font-bold">
                    {{ event.participantsCount }}
                </span>
                </button>

                <ParticipantsModal
                v-model="showParticipants"
                :eventId="event.id"
                :eventTitle="event.title"
                />
          <RouterLink :to="`/evenements/${event.id}`"
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors font-semibold">
            <Eye :size="12" /> Voir
          </RouterLink>
          <RouterLink :to="`/evenements/${event.id}/modifier`"
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs border border-amber-200 text-amber-700 hover:bg-amber-50 transition-colors font-semibold">
            <Edit2 :size="12" /> Modifier
          </RouterLink>
          <button @click="$emit('delete', event.id)"
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs border border-red-200 text-red-600 hover:bg-red-50 transition-colors font-semibold ml-auto">
            <Trash2 :size="12" /> Supprimer
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { CalendarDays, MapPin, Heart, Users, Eye, Edit2, Trash2 } from 'lucide-vue-next'
import ParticipantsModal from '@/components/event/ParticipantsModal.vue'

const props = defineProps<{ event: any }>()
defineEmits(['delete'])

const fillPct = computed(() => {
  if (!props.event.maxParticipants) return 0
  return Math.round((props.event.participantsCount / props.event.maxParticipants) * 100)
})

const fillColor = computed(() => {
  if (fillPct.value >= 90) return '#EF4444'
  if (fillPct.value >= 70) return '#F59E0B'
  return '#7A9E6E'
})

const showParticipants = ref(false)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric', month: 'short', year: 'numeric'
  })
}
</script>
