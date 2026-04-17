<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-2xl mx-auto px-4 sm:px-6 py-10">

      <!-- Header -->
      <div class="mb-8">
        <RouterLink to="/evenements" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 mb-4 transition-colors">
          <ArrowLeft :size="14" /> Retour aux évènements
        </RouterLink>
        <h1 class="text-gray-900 font-black text-2xl" style="letter-spacing: -0.03em">Créer un évènement</h1>
        <p class="text-gray-500 text-sm mt-1">Partagez votre évènement avec la communauté tech</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-6">

        <!-- Infos générales -->
        <div class="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
          <h2 class="text-gray-900 font-bold text-base" style="letter-spacing: -0.02em">Informations générales</h2>

          <div>
            <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Titre de l'évènement *</label>
            <input
              v-model="form.title"
              type="text"
              placeholder="Ex: Vue.js Paris Meetup #42"
              :class="inputClass('title')"
            />
            <p v-if="errors.title" class="text-xs text-red-500 mt-1">{{ errors.title }}</p>
          </div>

          <div>
            <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Description *</label>
            <textarea
              v-model="form.description"
              rows="5"
              placeholder="Décrivez votre évènement, le programme, ce que les participants vont apprendre..."
              :class="inputClass('description')"
              style="resize: vertical"
            />
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
            <input
              v-model="form.imageUrl"
              type="url"
              placeholder="https://..."
              :class="inputClass('imageUrl')"
            />
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
            <button
              type="button"
              @click="form.isOnline = false"
              class="flex-1 py-2.5 rounded-lg border text-sm font-semibold transition-colors"
              :style="!form.isOnline ? 'border-color: #7A9E6E; background-color: #F2F5F0; color: #3D5C38' : 'border-color: #e5e7eb; color: #6b7280'"
            >
              📍 En présentiel
            </button>
            <button
              type="button"
              @click="form.isOnline = true"
              class="flex-1 py-2.5 rounded-lg border text-sm font-semibold transition-colors"
              :style="form.isOnline ? 'border-color: #7A9E6E; background-color: #F2F5F0; color: #3D5C38' : 'border-color: #e5e7eb; color: #6b7280'"
            >
              🌐 En ligne
            </button>
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

        <!-- Actions -->
        <div class="flex gap-3">
          <RouterLink
            to="/evenements"
            class="flex-1 py-3 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:border-gray-400 text-center transition-colors"
          >
            Annuler
          </RouterLink>
          <button
            type="submit"
            :disabled="loading"
            class="flex-1 py-3 rounded-lg text-white text-sm font-bold flex items-center justify-center gap-2 transition-colors"
            :style="{ backgroundColor: loading ? '#A8D47A' : '#7A9E6E' }"
          >
            <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            <span v-else>Publier l'évènement</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'

const router = useRouter()
const loading = ref(false)

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
  loading.value = true
  try {
    // TODO: appel API POST /api/events
    await new Promise(r => setTimeout(r, 800))
    router.push('/evenements')
  } catch {
    errors.title = 'Une erreur est survenue'
  } finally {
    loading.value = false
  }
}
</script>