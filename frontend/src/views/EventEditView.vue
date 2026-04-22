<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-2xl mx-auto px-4 sm:px-6 py-10">

      <!-- Loading -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="w-8 h-8 border-2 border-gray-200 border-t-[#7A9E6E] rounded-full animate-spin" />
      </div>

      <!-- Not found -->
      <div v-else-if="!found" class="text-center py-20">
        <p class="text-gray-500 font-medium mb-4">Évènement introuvable</p>
        <RouterLink to="/evenements" class="text-sm font-semibold underline" style="color: #7A9E6E">
          Retour aux évènements
        </RouterLink>
      </div>

      <template v-else>
        <!-- Header -->
        <div class="mb-8">
          <RouterLink :to="`/evenements/${route.params.id}`" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 mb-4 transition-colors">
            <ArrowLeft :size="14" /> Retour à l'évènement
          </RouterLink>
          <h1 class="text-gray-900 font-black text-2xl" style="letter-spacing: -0.03em">Modifier l'évènement</h1>
          <p class="text-gray-500 text-sm mt-1">Mettez à jour les informations de votre évènement</p>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">

          <!-- Infos générales -->
          <div class="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            <h2 class="text-gray-900 font-bold text-base" style="letter-spacing: -0.02em">Informations générales</h2>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Titre de l'évènement *</label>
              <input v-model="form.title" type="text" placeholder="Ex: Vue.js Paris Meetup #42" :class="inputClass('title')" />
              <p v-if="errors.title" class="text-xs text-red-500 mt-1">{{ errors.title }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Description *</label>
              <textarea v-model="form.description" rows="5" placeholder="Décrivez votre évènement..." :class="inputClass('description')" style="resize: vertical" />
              <p v-if="errors.description" class="text-xs text-red-500 mt-1">{{ errors.description }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Catégorie *</label>
              <select v-model="form.category" :class="inputClass('category')">
                <option value="" disabled>Choisir une catégorie</option>
                <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
              </select>
              <p v-if="errors.category" class="text-xs text-red-500 mt-1">{{ errors.category }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Image (URL)</label>
              <input v-model="form.imageUrl" type="url" placeholder="https://..." :class="inputClass('imageUrl')" />
              <div v-if="form.imageUrl" class="mt-2 rounded-lg overflow-hidden h-32">
                <img :src="form.imageUrl" alt="preview" class="w-full h-full object-cover" @error="form.imageUrl = ''" />
              </div>
            </div>
          </div>

          <!-- Date & Lieu -->
          <div class="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            <h2 class="text-gray-900 font-bold text-base" style="letter-spacing: -0.02em">Date & Lieu</h2>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Date et heure *</label>
              <input v-model="form.date" type="datetime-local" :class="inputClass('date')" />
              <p v-if="errors.date" class="text-xs text-red-500 mt-1">{{ errors.date }}</p>
            </div>

            <div class="flex items-center gap-3">
              <button type="button" @click="form.isOnline = false"
                class="flex-1 py-2.5 rounded-lg border text-sm font-semibold transition-colors"
                :style="!form.isOnline ? 'border-color: #7A9E6E; background-color: #F2F5F0; color: #3D5C38' : 'border-color: #e5e7eb; color: #6b7280'"
              >📍 En présentiel</button>
              <button type="button" @click="form.isOnline = true"
                class="flex-1 py-2.5 rounded-lg border text-sm font-semibold transition-colors"
                :style="form.isOnline ? 'border-color: #7A9E6E; background-color: #F2F5F0; color: #3D5C38' : 'border-color: #e5e7eb; color: #6b7280'"
              >🌐 En ligne</button>
            </div>

            <div v-if="!form.isOnline" class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Ville *</label>
                <input v-model="form.city" type="text" placeholder="Paris" :class="inputClass('city')" />
                <p v-if="errors.city" class="text-xs text-red-500 mt-1">{{ errors.city }}</p>
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Adresse</label>
                <input v-model="form.location" type="text" placeholder="10 rue de la Paix" :class="inputClass('location')" />
              </div>
            </div>
          </div>

          <!-- Tarif & Places -->
          <div class="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            <h2 class="text-gray-900 font-bold text-base" style="letter-spacing: -0.02em">Tarif & Places</h2>
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Prix (€)</label>
                <input v-model.number="form.price" type="number" min="0" placeholder="0 = Gratuit" :class="inputClass('price')" />
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Places max</label>
                <input v-model.number="form.maxParticipants" type="number" min="1" placeholder="Illimité" :class="inputClass('maxParticipants')" />
              </div>
            </div>
          </div>

          <!-- Danger zone -->
          <div class="bg-white rounded-2xl border border-red-100 p-6">
            <h2 class="text-red-600 font-bold text-base mb-3">Zone dangereuse</h2>
            <p class="text-gray-500 text-sm mb-4">La suppression est irréversible. Tous les participants seront notifiés.</p>
            <button
              type="button"
              @click="handleDelete"
              class="flex items-center gap-2 px-4 py-2.5 rounded-lg border border-red-200 text-red-600 text-sm font-semibold hover:bg-red-50 transition-colors"
            >
              <Trash2 :size="14" /> Supprimer l'évènement
            </button>
          </div>

          <!-- Actions -->
          <div class="flex gap-3">
            <RouterLink
              :to="`/evenements/${route.params.id}`"
              class="flex-1 py-3 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:border-gray-400 text-center transition-colors"
            >
              Annuler
            </RouterLink>
            <button
              type="submit"
              :disabled="saving"
              class="flex-1 py-3 rounded-lg text-white text-sm font-bold flex items-center justify-center gap-2 transition-colors"
              :style="{ backgroundColor: saving ? '#A8D47A' : '#7A9E6E' }"
            >
              <span v-if="saving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <span v-else>Enregistrer les modifications</span>
            </button>
          </div>
        </form>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Trash2 } from 'lucide-vue-next'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const saving = ref(false)
const found = ref(false)

const categories = ['Développement', 'Technologie', 'Design', 'Business', 'Sécurité', 'Agilité', 'Data', 'Cloud']

const form = reactive({
  title: '',
  description: '',
  category: '',
  imageUrl: '',
  date: '',
  isOnline: false,
  city: '',
  location: '',
  price: 0,
  maxParticipants: undefined as number | undefined,
})

const errors = reactive<Record<string, string>>({})

onMounted(async () => {
  try {
    const response = await api.get(`/events/${route.params.id}`)
    const event = response.data
    form.title = event.title
    form.description = event.description
    form.category = event.category
    form.imageUrl = event.imageUrl || ''
    form.date = event.date ? event.date.substring(0, 16) : ''
    form.isOnline = event.online || event.isOnline || false
    form.city = event.city || ''
    form.location = event.location || ''
    form.price = event.price || 0
    form.maxParticipants = event.maxParticipants || undefined
    found.value = true
  } catch {
    found.value = false
  } finally {
    loading.value = false
  }
})

function inputClass(field: string) {
  return [
    'w-full px-3 py-2.5 rounded-lg border text-sm text-gray-800 placeholder-gray-400 outline-none transition-all',
    'focus:border-gray-900 focus:bg-white',
    errors[field] ? 'border-red-300 bg-red-50' : 'border-gray-200 bg-gray-50',
  ].join(' ')
}

function validate() {
  Object.keys(errors).forEach(k => delete errors[k])
  if (!form.title.trim()) errors.title = 'Le titre est requis'
  if (!form.description.trim()) errors.description = 'La description est requise'
  if (!form.category) errors.category = 'La catégorie est requise'
  if (!form.date) errors.date = 'La date est requise'
  if (!form.isOnline && !form.city.trim()) errors.city = 'La ville est requise'
  return Object.keys(errors).length === 0
}

async function handleSubmit() {
  if (!validate()) return
  saving.value = true
  try {
    await api.put(`/events/${route.params.id}`, {
      title: form.title,
      description: form.description,
      category: form.category,
      imageUrl: form.imageUrl,
      date: form.date + ':00',
      online: form.isOnline,
      city: form.isOnline ? 'En ligne' : form.city,
      location: form.isOnline ? 'En ligne' : form.location,
      price: form.price,
      maxParticipants: form.maxParticipants || null,
    })
    router.push(`/evenements/${route.params.id}`)
  } catch {
    errors.title = 'Une erreur est survenue'
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  if (!confirm('Supprimer cet évènement ? Cette action est irréversible.')) return
  try {
    await api.delete(`/events/${route.params.id}`)
    router.push('/evenements')
  } catch {
    alert('Une erreur est survenue lors de la suppression.')
  }
}
</script>
