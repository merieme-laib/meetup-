<template>
  <!-- Exactement le même template que LoginView.vue -->
  <!-- La seule différence est dans le script : isRegister démarre à true -->
  <div v-if="success" class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-10 max-w-sm w-full text-center">
      <div class="w-14 h-14 rounded-full flex items-center justify-center mx-auto mb-4" style="background-color: #E5EDE0">
        <CheckCircle :size="28" style="color: #7A9E6E" />
      </div>
      <h2 class="text-gray-900 mb-2 font-bold text-xl">
        {{ isRegister ? 'Bienvenue !' : 'Content de vous revoir !' }}
      </h2>
      <p class="text-gray-500 text-sm">Vous êtes connecté. Redirection...</p>
    </div>
  </div>

  <div v-else class="min-h-screen bg-white flex">

    <!-- Left panel -->
    <div class="hidden lg:flex flex-col w-5/12 text-white p-12 relative overflow-hidden" style="background-color: #1A3B0A">
      <div class="absolute -top-20 -right-20 w-64 h-64 rounded-full opacity-10" style="background-color: #7A9E6E" />
      <div class="absolute bottom-0 -left-10 w-48 h-48 rounded-full opacity-10" style="background-color: #7A9E6E" />
      <div class="relative flex-1">
        <RouterLink to="/" class="flex items-center gap-2 mb-14">
          <div class="w-9 h-9 rounded-lg flex items-center justify-center" style="background-color: #7A9E6E">
            <CalendarDays :size="18" class="text-white" />
          </div>
          <span style="font-weight: 800; font-size: 1.2rem; letter-spacing: -0.02em">
            meet<span style="color: #A8D87A">up</span>
          </span>
        </RouterLink>
        <h1 class="text-white mb-4" style="font-size: 1.9rem; font-weight: 900; line-height: 1.2; letter-spacing: -0.03em">
          {{ isRegister ? 'Rejoignez la communauté' : 'Content de vous revoir' }}
        </h1>
        <p class="text-sm mb-10 leading-relaxed" style="color: #A8C890">
          {{ isRegister ? "Créez votre compte pour accéder à des centaines d'évènements tech." : 'Connectez-vous pour accéder à votre espace et vos évènements favoris.' }}
        </p>
        <ul class="space-y-4">
          <li v-for="perk in perks" :key="perk" class="flex items-start gap-3">
            <div class="w-5 h-5 rounded-full flex items-center justify-center shrink-0 mt-0.5" style="background-color: #7A9E6E">
              <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
                <path d="M1 4l3 3 5-6" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </div>
            <span class="text-sm leading-relaxed" style="color: #C0DCA0">{{ perk }}</span>
          </li>
        </ul>
      </div>
      <div class="relative mt-8 pt-6 border-t" style="border-color: rgba(255,255,255,0.1)">
        <p class="text-xs" style="color: #7A9A60">🌿 Plateforme éco-responsable — Hébergement neutre en carbone</p>
      </div>
    </div>

    <!-- Right panel -->
    <div class="flex-1 flex flex-col justify-center px-6 py-12 bg-gray-50">
      <div class="max-w-md w-full mx-auto">
        <div class="lg:hidden flex items-center gap-2 mb-8">
          <RouterLink to="/" class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-lg flex items-center justify-center" style="background-color: #7A9E6E">
              <CalendarDays :size="16" class="text-white" />
            </div>
            <span class="text-gray-900" style="font-weight: 800; font-size: 1.1rem">meet<span style="color: #7A9E6E">up</span></span>
          </RouterLink>
        </div>

        <RouterLink to="/" class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 mb-6 transition-colors">
          <ArrowLeft :size="14" /> Retour à l'accueil
        </RouterLink>

        <div class="bg-white rounded-2xl border border-gray-200 shadow-sm p-8">
          <div class="mb-7">
            <h2 class="text-gray-900 mb-1" style="font-size: 1.4rem; font-weight: 800; letter-spacing: -0.02em">
              {{ isRegister ? 'Créer un compte' : 'Se connecter' }}
            </h2>
            <p class="text-sm text-gray-500">
              {{ isRegister ? 'Rejoignez la communauté meetup' : 'Bienvenue ! Entrez vos identifiants' }}
            </p>
          </div>

          <!-- Toggle pill -->
          <div class="flex bg-gray-100 rounded-lg p-1 mb-6">
            <button
              v-for="tab in toggleTabs" :key="tab.label"
              @click="switchMode(tab.value)"
              class="flex-1 py-2 rounded-md text-sm transition-all"
              :style="{
                backgroundColor: isRegister === tab.value ? 'white' : 'transparent',
                color: isRegister === tab.value ? '#1a1a1a' : '#737373',
                fontWeight: isRegister === tab.value ? 700 : 400,
                boxShadow: isRegister === tab.value ? '0 1px 3px rgba(0,0,0,0.1)' : undefined,
              }"
            >{{ tab.label }}</button>
          </div>

          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div v-if="isRegister" class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Prénom</label>
                <div :class="inputWrapClass('firstName')">
                  <User :size="15" class="text-gray-400 shrink-0" />
                  <input v-model="form.firstName" type="text" placeholder="Jean" class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder-gray-400" @input="clearError('firstName')" />
                </div>
                <p v-if="errors.firstName" class="text-xs text-red-500 mt-1">{{ errors.firstName }}</p>
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Nom</label>
                <div :class="inputWrapClass('lastName')">
                  <User :size="15" class="text-gray-400 shrink-0" />
                  <input v-model="form.lastName" type="text" placeholder="Dupont" class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder-gray-400" @input="clearError('lastName')" />
                </div>
                <p v-if="errors.lastName" class="text-xs text-red-500 mt-1">{{ errors.lastName }}</p>
              </div>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Adresse e-mail</label>
              <div :class="inputWrapClass('email')">
                <Mail :size="15" class="text-gray-400 shrink-0" />
                <input v-model="form.email" type="email" placeholder="vous@email.com" class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder-gray-400" @input="clearError('email')" />
              </div>
              <p v-if="errors.email" class="text-xs text-red-500 mt-1">{{ errors.email }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Mot de passe</label>
              <div :class="inputWrapClass('password')">
                <Lock :size="15" class="text-gray-400 shrink-0" />
                <input v-model="form.password" :type="showPassword ? 'text' : 'password'" placeholder="••••••••" class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder-gray-400" @input="clearError('password')" />
                <button type="button" @click="showPassword = !showPassword" class="text-gray-400 hover:text-gray-600">
                  <EyeOff v-if="showPassword" :size="14" /><Eye v-else :size="14" />
                </button>
              </div>
              <p v-if="errors.password" class="text-xs text-red-500 mt-1">{{ errors.password }}</p>
            </div>

            <div v-if="isRegister">
              <label class="block text-sm text-gray-700 mb-1.5 font-semibold">Confirmer le mot de passe</label>
              <div :class="inputWrapClass('confirm')">
                <Lock :size="15" class="text-gray-400 shrink-0" />
                <input v-model="form.confirm" :type="showPassword ? 'text' : 'password'" placeholder="••••••••" class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder-gray-400" @input="clearError('confirm')" />
              </div>
              <p v-if="errors.confirm" class="text-xs text-red-500 mt-1">{{ errors.confirm }}</p>
            </div>

            <div v-if="!isRegister" class="text-right">
              <button type="button" class="text-xs hover:underline" style="color: #7A9E6E">Mot de passe oublié ?</button>
            </div>

            <button type="submit" :disabled="loading"
              class="w-full py-3 rounded-lg text-white text-sm flex items-center justify-center gap-2 font-bold transition-colors mt-2"
              :style="{ backgroundColor: loading ? '#A8D47A' : '#7A9E6E' }"
              @mouseenter="e => { if (!loading) e.currentTarget.style.backgroundColor = '#5E7D58' }"
              @mouseleave="e => { if (!loading) e.currentTarget.style.backgroundColor = loading ? '#A8D47A' : '#7A9E6E' }"
            >
              <span v-if="loading" class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <span v-else>{{ isRegister ? 'Créer mon compte' : 'Se connecter' }}</span>
            </button>
          </form>

          <p v-if="isRegister" class="text-xs text-gray-400 text-center mt-4">
            En créant un compte, vous acceptez nos <a href="#" class="hover:underline" style="color: #7A9E6E">conditions</a> et notre <a href="#" class="hover:underline" style="color: #7A9E6E">politique de confidentialité</a>.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CalendarDays, Eye, EyeOff, Mail, Lock, User, ArrowLeft, CheckCircle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth.store'
import authService from '@/services/auth.service'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// La seule différence avec LoginView : démarre sur "Inscription"
const isRegister = ref(true)

const form = reactive({ firstName: '', lastName: '', email: '', password: '', confirm: '' })
const errors = reactive<Record<string, string>>({})
const showPassword = ref(false)
const loading = ref(false)
const success = ref(false)

const toggleTabs = [
  { label: 'Connexion',   value: false },
  { label: 'Inscription', value: true  },
]

const perks = [
  "Inscrivez-vous à des évènements tech gratuits et payants",
  "Likez et suivez vos évènements favoris",
  "Créez et gérez vos propres évènements",
  "Rejoignez une communauté de passionnés",
]

function switchMode(value: boolean) {
  isRegister.value = value
  Object.keys(errors).forEach(k => delete errors[k])
  form.firstName = ''; form.lastName = ''; form.email = ''; form.password = ''; form.confirm = ''
}

function clearError(field: string) { delete errors[field] }

function inputWrapClass(field: string) {
  return [
    'flex items-center gap-2 px-3 py-2.5 rounded-lg border text-sm transition-all focus-within:bg-white',
    errors[field] ? 'border-red-300 bg-red-50' : 'border-gray-300 bg-gray-50 focus-within:border-gray-900',
  ].join(' ')
}

function validate(): boolean {
  Object.keys(errors).forEach(k => delete errors[k])
  if (isRegister.value) {
    if (!form.firstName.trim()) errors.firstName = 'Le prénom est requis'
    if (!form.lastName.trim())  errors.lastName  = 'Le nom est requis'
  }
  if (!form.email.trim()) errors.email = "L'email est requis"
  else if (!/\S+@\S+\.\S+/.test(form.email)) errors.email = 'Email invalide'
  if (!form.password) errors.password = 'Le mot de passe est requis'
  else if (form.password.length < 6) errors.password = 'Minimum 6 caractères'
  if (isRegister.value && form.password !== form.confirm) errors.confirm = 'Les mots de passe ne correspondent pas'
  return Object.keys(errors).length === 0
}

async function handleSubmit() {
  if (!validate()) return
  loading.value = true
  try {
    if (isRegister.value) {
      const { user, token } = await authService.register(form.firstName, form.lastName, form.email, form.password)
      authStore.setAuth(user, token)
    } else {
      const { user, token } = await authService.login(form.email, form.password)
      authStore.setAuth(user, token)
    }
    success.value = true
    setTimeout(() => router.push((route.query.redirect as string) || '/'), 1000)
  } catch (err: any) {
    errors.email = err?.response?.data?.message || 'Une erreur est survenue'
  } finally {
    loading.value = false
  }
}
</script>