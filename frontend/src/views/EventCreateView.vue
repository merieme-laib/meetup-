<template>
  <div class="min-h-screen bg-gray-50">

    <!-- Succès -->
    <div v-if="saved" class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div class="bg-white rounded-2xl border border-gray-200 p-10 max-w-sm w-full text-center">
        <div class="w-14 h-14 rounded-full flex items-center justify-center mx-auto mb-4" style="background-color: #E5EDE0">
          <CheckCircle :size="28" style="color: #7A9E6E" />
        </div>
        <h2 class="text-gray-900 mb-2 font-black">Évènement publié !</h2>
        <p class="text-gray-500 text-sm">Redirection en cours...</p>
      </div>
    </div>

    <template v-else>
      <!-- Header -->
      <div class="bg-white border-b border-gray-200 py-6">
        <div class="max-w-3xl mx-auto px-4 sm:px-6">
          <RouterLink to="/evenements" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 mb-2 transition-colors">
            <ArrowLeft :size="14" /> Retour
          </RouterLink>
          <h1 class="text-gray-900 font-black text-2xl" style="letter-spacing: -0.03em">Créer un évènement</h1>
          <p class="text-gray-500 text-sm mt-0.5">Publiez votre évènement en quelques étapes</p>
        </div>
      </div>

      <div class="max-w-3xl mx-auto px-4 sm:px-6 py-8">

        <!-- Stepper -->
        <div class="flex items-center mb-8">
          <template v-for="(s, i) in steps" :key="s">
            <div class="flex items-center" :class="i < steps.length - 1 ? 'flex-1' : ''">
              <button @click="i <= currentStep && (currentStep = i)" class="flex items-center gap-2">
                <div
                  class="w-7 h-7 rounded-full flex items-center justify-center text-xs shrink-0 transition-all font-bold"
                  :style="{
                    backgroundColor: i < currentStep ? '#7A9E6E' : i === currentStep ? '#1a1a1a' : '#E5E7EB',
                    color: i <= currentStep ? 'white' : '#9CA3AF',
                  }"
                >
                  <CheckCircle v-if="i < currentStep" :size="13" />
                  <span v-else>{{ i + 1 }}</span>
                </div>
                <span class="hidden sm:block text-xs"
                  :style="{
                    fontWeight: i === currentStep ? 700 : 400,
                    color: i === currentStep ? '#1a1a1a' : i < currentStep ? '#6B7280' : '#9CA3AF'
                  }"
                >{{ s }}</span>
              </button>
              <div v-if="i < steps.length - 1" class="flex-1 h-px mx-2" :style="{ backgroundColor: i < currentStep ? '#7A9E6E' : '#E5E7EB' }" />
            </div>
          </template>
        </div>

        <!-- Contenu -->
        <div class="bg-white rounded-xl border border-gray-200 p-6 mb-5">
          <h2 class="text-gray-900 font-extrabold mb-5">{{ steps[currentStep] }}</h2>

          <!-- Étape 0 : Informations -->
          <div v-if="currentStep === 0" class="space-y-5">
            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Titre *</label>
              <input v-model="form.title" type="text" placeholder="Ex: Vue.js Paris Meetup #42" :class="inputClass('title')" />
              <p v-if="errors.title" class="text-xs text-red-500 mt-1">{{ errors.title }}</p>
              <p class="text-xs text-gray-400 mt-1">{{ form.title.length }}/100 caractères</p>
            </div>
            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Description *</label>
              <textarea v-model="form.description" rows="5" placeholder="Décrivez votre évènement : programme, intervenants, objectifs..." :class="inputClass('description')" style="resize: vertical" />
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
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Places max</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <Users :size="15" class="text-gray-400" />
                  <input v-model.number="form.maxParticipants" type="number" min="1" placeholder="Illimité" class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
                </div>
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Prix (€)</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <EuroIcon :size="15" class="text-gray-400" />
                  <input v-model.number="form.price" type="number" min="0" step="0.5" placeholder="0 = Gratuit" class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
                </div>
                <p class="text-xs text-gray-400 mt-1">0 = Gratuit</p>
              </div>
            </div>
          </div>

          <!-- Étape 1 : Date & Lieu -->
          <div v-if="currentStep === 1" class="space-y-5">
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Date *</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <CalendarDays :size="15" class="text-gray-400" />
                  <input v-model="form.date" type="date" class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
                </div>
                <p v-if="errors.date" class="text-xs text-red-500 mt-1">{{ errors.date }}</p>
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Début *</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <Clock :size="15" class="text-gray-400" />
                  <input v-model="form.time" type="time" class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
                </div>
                <p v-if="errors.time" class="text-xs text-red-500 mt-1">{{ errors.time }}</p>
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Fin</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <Clock :size="15" class="text-gray-400" />
                  <input v-model="form.endTime" type="time" class="flex-1 bg-transparent text-sm text-gray-800 outline-none" />
                </div>
              </div>
            </div>

            <div class="flex items-center justify-between p-4 border border-gray-200 rounded-lg bg-gray-50">
              <div class="flex items-center gap-3">
                <Wifi :size="16" style="color: #7A9E6E" />
                <div>
                  <p class="text-sm text-gray-900 font-semibold">Évènement en ligne</p>
                  <p class="text-xs text-gray-500">Zoom, Meet, Teams...</p>
                </div>
              </div>
              <div @click="form.isOnline = !form.isOnline"
                class="w-10 h-5 rounded-full relative cursor-pointer transition-colors"
                :style="{ backgroundColor: form.isOnline ? '#7A9E6E' : '#D1D5DB' }">
                <span class="absolute top-0.5 w-4 h-4 bg-white rounded-full shadow transition-all" :class="form.isOnline ? 'right-0.5' : 'left-0.5'" />
              </div>
            </div>

            <template v-if="!form.isOnline">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Nom du lieu *</label>
                <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <MapPin :size="15" class="text-gray-400" />
                  <input v-model="form.location" type="text" placeholder="WeWork, Station F..." class="flex-1 bg-transparent text-sm text-gray-800 outline-none placeholder-gray-400" />
                </div>
                <p v-if="errors.location" class="text-xs text-red-500 mt-1">{{ errors.location }}</p>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Ville *</label>
                  <input v-model="form.city" type="text" placeholder="Paris" :class="inputClass('city')" />
                  <p v-if="errors.city" class="text-xs text-red-500 mt-1">{{ errors.city }}</p>
                </div>
                <div>
                  <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Adresse</label>
                  <input v-model="form.address" type="text" placeholder="6 Rue Ménars" :class="inputClass('')" />
                </div>
              </div>
            </template>
          </div>

          <!-- Étape 2 : Médias & Tags -->
          <div v-if="currentStep === 2" class="space-y-6">
            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Image de couverture (URL)</label>
              <div class="flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                <ImageIcon :size="15" class="text-gray-400" />
                <input v-model="form.imageUrl" type="url" placeholder="https://..." class="flex-1 bg-transparent text-sm text-gray-800 outline-none placeholder-gray-400" />
              </div>
              <div v-if="form.imageUrl" class="mt-3 h-36 rounded-xl overflow-hidden border border-gray-200">
                <img :src="form.imageUrl" alt="preview" class="w-full h-full object-cover" @error="form.imageUrl = ''" />
              </div>
              <div class="mt-3">
                <p class="text-xs text-gray-400 mb-2">Suggestions rapides :</p>
                <div class="flex gap-2">
                  <button v-for="img in defaultImages" :key="img" type="button" @click="form.imageUrl = img"
                    class="w-14 h-14 rounded-lg overflow-hidden border-2 transition-all"
                    :style="{ borderColor: form.imageUrl === img ? '#7A9E6E' : '#e5e7eb' }">
                    <img :src="img" alt="" class="w-full h-full object-cover" />
                  </button>
                </div>
              </div>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">
                Tags <span class="text-gray-400 text-xs">({{ form.tags.length }}/8)</span>
              </label>
              <div class="flex gap-2">
                <div class="flex-1 flex items-center gap-2 px-3 py-2.5 border border-gray-300 rounded-lg bg-gray-50 focus-within:border-gray-900 focus-within:bg-white transition-all">
                  <Tag :size="15" class="text-gray-400" />
                  <input v-model="tagInput" type="text" placeholder="Ajoutez un tag et appuyez sur Entrée"
                    class="flex-1 bg-transparent text-sm text-gray-800 outline-none placeholder-gray-400"
                    maxlength="20" @keydown.enter.prevent="addTag" />
                </div>
                <button type="button" @click="addTag" class="px-4 py-2.5 rounded-lg text-white text-sm font-semibold" style="background-color: #7A9E6E">
                  <Plus :size="15" />
                </button>
              </div>
              <div v-if="form.tags.length > 0" class="flex flex-wrap gap-2 mt-3">
                <span v-for="tag in form.tags" :key="tag" class="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold" style="background-color: #E5EDE0; color: #4D6E47">
                  #{{ tag }}
                  <button type="button" @click="form.tags = form.tags.filter(t => t !== tag)" class="hover:text-red-500 transition-colors">
                    <X :size="11" />
                  </button>
                </span>
              </div>
            </div>
          </div>

          <!-- Étape 3 : Aperçu -->
          <div v-if="currentStep === 3" class="space-y-5">
            <div class="flex items-start gap-3 p-4 border border-amber-200 rounded-lg" style="background-color: #FFFBEB">
              <Info :size="15" class="text-amber-600 shrink-0 mt-0.5" />
              <p class="text-sm text-amber-700">Vérifiez toutes les informations avant de publier.</p>
            </div>

            <div class="border border-gray-200 rounded-xl overflow-hidden">
              <div class="h-44 overflow-hidden bg-gray-100">
                <img v-if="form.imageUrl" :src="form.imageUrl" alt="" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-sm">Pas d'image</div>
              </div>
              <div class="p-4">
                <div class="flex items-center gap-2 mb-2">
                  <span class="px-2 py-0.5 rounded text-xs font-semibold" style="background-color: #E5EDE0; color: #4D6E47">{{ form.category }}</span>
                  <span v-if="form.isOnline" class="px-2 py-0.5 rounded text-xs font-semibold" style="background-color: #E8F8EC; color: #1A6B2E">En ligne</span>
                </div>
                <h3 class="text-gray-900 font-bold mb-2">{{ form.title || 'Titre non renseigné' }}</h3>
                <div class="space-y-1 text-xs text-gray-500">
                  <div v-if="form.date" class="flex items-center gap-1.5">
                    <CalendarDays :size="11" style="color: #7A9E6E" />
                    {{ form.date }} · {{ form.time }}<span v-if="form.endTime"> — {{ form.endTime }}</span>
                  </div>
                  <div class="flex items-center gap-1.5">
                    <MapPin :size="11" style="color: #7A9E6E" />
                    {{ form.isOnline ? 'En ligne' : `${form.location}, ${form.city}` }}
                  </div>
                  <div class="flex items-center gap-1.5">
                    <Users :size="11" style="color: #7A9E6E" />
                    {{ form.maxParticipants ? form.maxParticipants + ' places' : 'Places illimitées' }} ·
                    <span :style="{ color: form.price === 0 ? '#3D5C38' : '#7A4E10', fontWeight: 600 }">
                      {{ form.price === 0 ? 'Gratuit' : form.price + '€' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div class="space-y-2">
              <div v-for="check in checklist" :key="check.label" class="flex items-center gap-2 text-sm">
                <CheckCircle v-if="check.ok" :size="14" style="color: #7A9E6E" class="shrink-0" />
                <AlertCircle v-else :size="14" class="text-amber-500 shrink-0" />
                <span :style="{ color: check.ok ? '#374151' : '#D97706' }">{{ check.label }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation -->
        <div class="flex items-center justify-between">
          <div>
            <button v-if="currentStep > 0" @click="currentStep--"
              class="flex items-center gap-2 px-5 py-2.5 border border-gray-300 text-gray-700 rounded-lg text-sm hover:bg-gray-50 transition-colors font-semibold">
              <ArrowLeft :size="14" /> Précédent
            </button>
          </div>
          <div class="flex items-center gap-3">
            <button v-if="currentStep < steps.length - 1" @click="nextStep"
              class="flex items-center gap-2 px-5 py-2.5 rounded-lg text-white text-sm font-bold transition-colors"
              style="background-color: #1a1a1a">
              Suivant <ArrowRight :size="14" />
            </button>
            <button v-else @click="handleSubmit" :disabled="loading"
              class="flex items-center gap-2 px-6 py-2.5 rounded-lg text-white text-sm font-bold transition-colors"
              :style="{ backgroundColor: loading ? '#A8D47A' : '#7A9E6E' }">
              <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <template v-else>
                <Send :size="14" /> Publier
              </template>
            </button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeft, ArrowRight, CalendarDays, Clock, MapPin, Users,
  Wifi, CheckCircle, AlertCircle, Info, Plus, X, Tag, Send, Image as ImageIcon
} from 'lucide-vue-next'
import { Euro as EuroIcon } from 'lucide-vue-next'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth.store'

const router = useRouter()
const authStore = useAuthStore()

const steps = ['Informations', 'Date & Lieu', 'Médias & Tags', 'Aperçu']
const currentStep = ref(0)
const loading = ref(false)
const saved = ref(false)
const tagInput = ref('')

const categories = ['Développement', 'Technologie', 'Design', 'Business', 'Sécurité', 'Agilité', 'Data', 'Cloud']

const defaultImages = [
  'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400&q=80',
  'https://images.unsplash.com/photo-1505373877841-8d25f7d46678?w=400&q=80',
  'https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&q=80',
  'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400&q=80',
]

const form = reactive({
  title: '',
  description: '',
  category: '',
  imageUrl: '',
  date: '',
  time: '',
  endTime: '',
  isOnline: false,
  city: '',
  location: '',
  address: '',
  price: 0,
  maxParticipants: undefined as number | undefined,
  tags: [] as string[],
})

const errors = reactive<Record<string, string>>({})

const checklist = computed(() => [
  { label: 'Titre renseigné', ok: form.title.length >= 5 },
  { label: 'Description suffisante', ok: form.description.length >= 20 },
  { label: 'Date et heure définies', ok: !!form.date && !!form.time },
  { label: 'Lieu renseigné', ok: form.isOnline || (!!form.location && !!form.city) },
  { label: 'Catégorie choisie', ok: !!form.category },
])

function inputClass(field: string) {
  return [
    'w-full px-3 py-2.5 rounded-lg border text-sm text-gray-800 placeholder-gray-400 outline-none transition-all',
    'focus:border-gray-900 focus:bg-white',
    errors[field] ? 'border-red-300 bg-red-50' : 'border-gray-300 bg-gray-50',
  ].join(' ')
}

function validateStep() {
  Object.keys(errors).forEach(k => delete errors[k])
  if (currentStep.value === 0) {
    if (!form.title.trim() || form.title.length < 5) errors.title = 'Titre requis (min. 5 caractères)'
    if (!form.description.trim() || form.description.length < 20) errors.description = 'Description requise (min. 20 caractères)'
    if (!form.category) errors.category = 'Catégorie requise'
  }
  if (currentStep.value === 1) {
    if (!form.date) errors.date = 'Date requise'
    if (!form.time) errors.time = 'Heure de début requise'
    if (!form.isOnline && !form.location.trim()) errors.location = 'Lieu requis'
    if (!form.isOnline && !form.city.trim()) errors.city = 'Ville requise'
  }
  return Object.keys(errors).length === 0
}

function nextStep() {
  if (validateStep()) currentStep.value++
}

function addTag() {
  const tag = tagInput.value.trim()
  if (tag && !form.tags.includes(tag) && form.tags.length < 8) {
    form.tags.push(tag)
    tagInput.value = ''
  }
}

async function handleSubmit() {
  loading.value = true
  try {
    const dateTime = `${form.date}T${form.time}:00`
    await api.post('/events', {
      title: form.title,
      description: form.description,
      date: dateTime,
      location: form.isOnline ? 'En ligne' : form.location,
      city: form.isOnline ? 'En ligne' : form.city,
      online: form.isOnline,
      imageUrl: form.imageUrl,
      price: form.price,
      maxParticipants: form.maxParticipants || null,
      category: form.category,
      creatorId: authStore.user?.id,
    })
    saved.value = true
    setTimeout(() => router.push('/evenements'), 1500)
  } catch (e) {
    errors.title = 'Une erreur est survenue lors de la création'
    currentStep.value = 0
  } finally {
    loading.value = false
  }
}
</script>
