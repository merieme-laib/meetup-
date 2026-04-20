<template>
  <div class="max-w-2xl mx-auto p-6 mt-8 bg-white rounded-xl shadow-sm border border-gray-100">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Créer un nouvel évènement</h1>

    <div v-if="errorMessage" class="mb-6 p-4 bg-red-50 text-red-700 rounded-lg text-sm">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="submitEvent" class="space-y-5">
      
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Titre de l'évènement *</label>
        <input v-model="eventData.title" type="text" required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] focus:border-[#7A9E6E] outline-none"
          placeholder="Ex: Soirée jeux de société" />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Description *</label>
        <textarea v-model="eventData.description" required rows="4"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] focus:border-[#7A9E6E] outline-none"
          placeholder="Décrivez votre évènement..."></textarea>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date et heure *</label>
          <input v-model="eventData.date" type="datetime-local" required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Catégorie *</label>
          <select v-model="eventData.category" required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none bg-white">
            <option value="" disabled>Choisir une catégorie</option>
            <option value="Tech">Tech</option>
            <option value="Sport">Sport</option>
            <option value="Loisirs">Loisirs</option>
            <option value="Culture">Culture</option>
          </select>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Ville *</label>
          <input v-model="eventData.city" type="text" required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none"
            placeholder="Ex: Lyon" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Adresse précise</label>
          <input v-model="eventData.location" type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none"
            placeholder="Ex: 12 rue de la Paix" />
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Prix (€)</label>
          <input v-model="eventData.price" type="number" min="0" step="0.5"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Max participants</label>
          <input v-model="eventData.maxParticipants" type="number" min="1"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none"
            placeholder="Laissez vide si illimité" />
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Lien de l'image (URL)</label>
        <input v-model="eventData.imageUrl" type="url"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A9E6E] outline-none"
          placeholder="https://..." />
      </div>

      <div class="flex items-center gap-2">
        <input v-model="eventData.isOnline" type="checkbox" id="online"
          class="w-4 h-4 text-[#7A9E6E] border-gray-300 rounded focus:ring-[#7A9E6E]" />
        <label for="online" class="text-sm text-gray-700">C'est un évènement en ligne</label>
      </div>

      <div class="pt-4 flex justify-end">
        <button type="submit" :disabled="isLoading"
          class="px-6 py-2.5 text-white font-semibold rounded-lg shadow-sm transition-colors disabled:opacity-50"
          style="background-color: #7A9E6E">
          {{ isLoading ? 'Création en cours...' : 'Créer l\'évènement' }}
        </button>
      </div>

    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)
const errorMessage = ref('')

const eventData = ref({
  title: '',
  description: '',
  date: '',
  location: '',
  city: '',
  price: 0,
  maxParticipants: null as number | null,
  category: '',
  imageUrl: '',
  isOnline: false,
  creatorId: authStore.user?.id || 1
})

async function submitEvent() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await api.post('/events', eventData.value)
    
    if (response.status !== 200 && response.status !== 201) {
      throw new Error('Erreur lors de la création de l\'évènement.')
    }

    router.push('/evenements')

  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || error.message || "Une erreur inattendue s'est produite."
  } finally {
    isLoading.value = false
  }
}
</script>