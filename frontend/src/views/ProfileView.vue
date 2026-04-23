<template>
  <!-- Non connecté -->
  <div v-if="!authStore.isLoggedIn" class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="bg-white rounded-2xl border border-gray-200 p-10 max-w-sm w-full text-center">
      <div class="w-14 h-14 rounded-xl bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <UserIcon :size="24" class="text-gray-400" />
      </div>
      <h2 class="text-gray-800 mb-2 font-extrabold">Accès restreint</h2>
      <p class="text-gray-500 text-sm mb-6">Connectez-vous pour accéder à votre espace compte.</p>
      <RouterLink to="/connexion" class="block w-full py-3 rounded-lg text-sm text-white text-center font-bold" style="background-color: #7A9E6E">
        Se connecter
      </RouterLink>
    </div>
  </div>

  <!-- Connecté -->
  <div v-else class="min-h-screen bg-gray-50">

    <!-- Header profil -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div class="flex flex-col sm:flex-row items-start sm:items-center gap-5">

          <!-- Avatar -->
          <div class="relative group cursor-pointer" @click="fileInputRef?.click()">
            <div class="w-16 h-16 rounded-2xl border-2 border-gray-100 overflow-hidden flex items-center justify-center text-white text-xl font-bold" style="background-color: #7A9E6E">
              <img v-if="avatarPreview" :src="avatarPreview" alt="avatar" class="w-full h-full object-cover" />
              <span v-else>{{ userInitials }}</span>
            </div>
            <div class="absolute inset-0 rounded-2xl flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity" style="background-color: rgba(0,0,0,0.45)">
              <Camera :size="18" class="text-white" />
            </div>
            <input ref="fileInputRef" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
          </div>

          <div class="flex-1">
            <h1 class="text-gray-900" style="font-weight: 900; font-size: 1.4rem; letter-spacing: -0.02em">
              {{ authStore.user?.firstName }} {{ authStore.user?.lastName }}
            </h1>
            <p class="text-gray-500 text-sm flex items-center gap-1.5 mt-1">
              <Mail :size="12" /> {{ authStore.user?.email }}
            </p>
          </div>

          <!-- Compteurs -->
          <div class="flex items-center gap-3">
            <div v-for="stat in profileStats" :key="stat.label" class="text-center px-4 py-2 rounded-xl border border-gray-200 bg-gray-50">
              <div class="font-black text-xl">{{ stat.value }}</div>
              <div class="text-xs text-gray-400 font-medium">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Corps -->
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      <!-- Message succès -->
      <div v-if="saveSuccess" class="mb-4 flex items-center gap-2 px-4 py-3 rounded-xl border text-sm" style="background-color: #E5EDE0; border-color: #A8C49E; color: #3D5C38">
        <CheckCircle :size="15" />
        Profil mis à jour avec succès !
      </div>

      <div class="flex flex-col lg:flex-row gap-6">

        <!-- Sidebar navigation -->
        <div class="lg:w-52 shrink-0">
          <nav class="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              class="w-full flex items-center gap-2.5 px-4 py-3 text-sm text-left transition-colors border-b border-gray-50 last:border-0"
              :style="{
                fontWeight: activeTab === tab.id ? 700 : 400,
                backgroundColor: activeTab === tab.id ? '#F2F5F0' : undefined,
                borderLeft: activeTab === tab.id ? '3px solid #7A9E6E' : '3px solid transparent',
                color: activeTab === tab.id ? '#1a1a1a' : '#6b7280',
              }"
            >
              <component :is="tab.icon" :size="14" :style="{ color: activeTab === tab.id ? '#7A9E6E' : undefined }" />
              <span class="truncate">{{ tab.label }}</span>
            </button>

            <div class="border-t border-gray-100">
              <button @click="handleLogout" class="w-full flex items-center gap-2.5 px-4 py-3 text-sm text-red-500 hover:bg-red-50 transition-colors">
                <LogOut :size="14" />
                Se déconnecter
              </button>
            </div>
          </nav>
        </div>

        <!-- Contenu -->
        <div class="flex-1 min-w-0">

          <!-- Vue d'ensemble -->
          <div v-if="activeTab === 'overview'" class="bg-white rounded-xl border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-5">
              <h2 class="font-extrabold text-lg">Mon profil</h2>
              <button
                @click="editMode = !editMode"
                class="flex items-center gap-1.5 px-4 py-2 rounded-lg text-sm transition-colors border font-semibold"
                :style="{
                  backgroundColor: editMode ? 'white' : '#F2F5F0',
                  borderColor: editMode ? '#d1d5db' : '#A8C49E',
                  color: editMode ? '#374151' : '#4D6E47',
                }"
              >
                <template v-if="editMode"><X :size="13" /> Annuler</template>
                <template v-else><Edit2 :size="13" /> Modifier</template>
              </button>
            </div>

            <!-- Mode édition -->
            <div v-if="editMode" class="space-y-4">
              <div v-for="field in editFields" :key="field.key">
                <label class="block text-sm text-gray-600 mb-1.5 font-semibold">{{ field.label }}</label>
                <input
                  v-model="editForm[field.key]"
                  :type="field.type"
                  :placeholder="field.placeholder"
                  class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm text-gray-800 focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Bio</label>
                <textarea
                  v-model="editForm.bio"
                  placeholder="Parlez de vous..."
                  rows="3"
                  class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm text-gray-800 focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white resize-none transition-all"
                />
              </div>
              <button @click="handleSave" class="flex items-center gap-2 px-5 py-2.5 rounded-lg text-sm text-white font-bold transition-colors" style="background-color: #7A9E6E">
                <Save :size="14" /> Sauvegarder
              </button>
            </div>

            <!-- Mode lecture -->
            <div v-else class="space-y-3">
              <div v-for="info in profileInfos" :key="info.label" class="flex items-center gap-3 py-3 border-b border-gray-50 last:border-0">
                <div class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0" style="background-color: #F2F5F0">
                  <component :is="info.icon" :size="14" style="color: #7A9E6E" />
                </div>
                <div>
                  <p class="text-xs text-gray-400 font-medium">{{ info.label }}</p>
                  <p class="text-sm text-gray-800 font-semibold">{{ info.value }}</p>
                </div>
              </div>
              <!-- <div v-if="authStore.user?.bio" class="pt-2">
                <p class="text-xs text-gray-400 mb-1 font-medium">Bio</p>
                <p class="text-sm text-gray-600">{{ authStore.user.bio }}</p>
              </div> -->
              <div class="pt-2">
                <p class="text-xs text-gray-400 mb-1 font-medium">Bio</p>
                <p class="text-sm text-gray-600">{{ authStore.user?.bio || 'Aucune bio renseignée' }}</p>
              </div>
            </div>
          </div>

          <!-- Inscriptions -->
          <div v-if="activeTab === 'registrations'" class="bg-white rounded-xl border border-gray-200 p-6">
            <h2 class="mb-5 font-extrabold text-lg">Mes inscriptions</h2>
            <div v-if="myRegistrations.length === 0" class="text-center py-12">
              <CheckCircle :size="36" class="text-gray-200 mx-auto mb-3" />
              <p class="text-gray-400 text-sm">Vous n'êtes inscrit à aucun évènement pour l'instant.</p>
              <RouterLink to="/evenements" class="inline-block mt-4 px-4 py-2 rounded-lg text-sm text-white font-semibold" style="background-color: #7A9E6E">
                Explorer les évènements
              </RouterLink>
            </div>
            <div v-else class="space-y-3">
              <div v-for="event in myRegistrations" :key="event.id"
                class="flex items-center justify-between p-4 rounded-xl border border-gray-100 hover:border-gray-200 transition-colors">
                <div class="flex-1 min-w-0">
                  <p class="font-semibold text-gray-900 text-sm truncate">{{ event.title }}</p>
                  <p class="text-xs text-gray-400 mt-0.5">{{ event.city }} · {{ event.category }}</p>
                </div>
                <RouterLink :to="`/evenements/${event.id}`"
                  class="px-3 py-1.5 text-xs font-semibold rounded-lg text-white ml-3"
                  style="background-color: #7A9E6E">
                  Voir
                </RouterLink>
              </div>
            </div>
          </div>

          <!-- Likes -->
          <div v-if="activeTab === 'liked'" class="bg-white rounded-xl border border-gray-200 p-6">
            <h2 class="mb-5 font-extrabold text-lg">Évènements likés</h2>
            <div v-if="myLikes.length === 0" class="text-center py-12">
              <Heart :size="36" class="text-gray-200 mx-auto mb-3" />
              <p class="text-gray-400 text-sm">Vous n'avez pas encore liké d'évènements.</p>
            </div>
            <div v-else class="space-y-3">
              <div v-for="event in myLikes" :key="event.id"
                class="flex items-center justify-between p-4 rounded-xl border border-gray-100 hover:border-gray-200 transition-colors">
                <div class="flex-1 min-w-0">
                  <p class="font-semibold text-gray-900 text-sm truncate">{{ event.title }}</p>
                  <p class="text-xs text-gray-400 mt-0.5">{{ event.city }} · {{ event.category }}</p>
                </div>
                <RouterLink :to="`/evenements/${event.id}`"
                  class="px-3 py-1.5 text-xs font-semibold rounded-lg text-white ml-3"
                  style="background-color: #7A9E6E">
                  Voir
                </RouterLink>
              </div>
            </div>
          </div>

          <!-- Mes évènements -->
          <div v-if="activeTab === 'created'" class="bg-white rounded-xl border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-5">
              <h2 class="font-extrabold text-lg">Mes évènements</h2>
              <RouterLink to="/evenements/creer" class="flex items-center gap-1.5 px-4 py-2 rounded-lg text-sm text-white font-semibold" style="background-color: #7A9E6E">
                <Plus :size="14" /> Créer
              </RouterLink>
            </div>
            <div v-if="myEvents.length === 0" class="text-center py-12">
              <CalendarDays :size="36" class="text-gray-200 mx-auto mb-3" />
              <p class="text-gray-400 text-sm">Vous n'avez pas encore créé d'évènement.</p>
              <RouterLink to="/evenements/creer" class="inline-block mt-4 px-4 py-2 rounded-lg text-sm text-white font-semibold" style="background-color: #7A9E6E">
                Créer mon premier évènement
              </RouterLink>
            </div>
            <div v-else class="space-y-3">
              <div v-for="event in myEvents" :key="event.id"
                class="flex items-center justify-between p-4 rounded-xl border border-gray-100 hover:border-gray-200 transition-colors">
                <div class="flex-1 min-w-0">
                  <p class="font-semibold text-gray-900 text-sm truncate">{{ event.title }}</p>
                  <p class="text-xs text-gray-400 mt-0.5">{{ event.city }} · {{ event.category }}</p>
                </div>
                <div class="flex items-center gap-2 ml-3">
                  <RouterLink :to="`/evenements/${event.id}/modifier`"
                    class="px-3 py-1.5 text-xs font-semibold rounded-lg border border-gray-200 text-gray-600 hover:border-gray-400 transition-colors">
                    Modifier
                  </RouterLink>
                  <RouterLink :to="`/evenements/${event.id}`"
                    class="px-3 py-1.5 text-xs font-semibold rounded-lg text-white transition-colors"
                    style="background-color: #7A9E6E">
                    Voir
                  </RouterLink>
                </div>
              </div>
            </div>
          </div>

          <!-- Paramètres -->
          <div v-if="activeTab === 'settings'" class="space-y-4">

            <!-- Notifications -->
            <div class="bg-white rounded-xl border border-gray-200 p-6">
              <h2 class="mb-4 font-extrabold text-lg">Notifications</h2>
              <div v-for="pref in notifPrefs" :key="pref" class="flex items-center justify-between py-3 border-b border-gray-50 last:border-0">
                <span class="text-sm text-gray-700">{{ pref }}</span>
                <div class="w-10 h-5 rounded-full relative cursor-pointer" style="background-color: #7A9E6E">
                  <span class="absolute right-0.5 top-0.5 w-4 h-4 bg-white rounded-full shadow" />
                </div>
              </div>
            </div>

            <!-- Changer l'email -->
            <div class="bg-white rounded-xl border border-gray-200 p-6">
              <h2 class="mb-4 font-extrabold text-lg">Changer l'adresse e-mail</h2>
              <div class="space-y-3">
                <div>
                  <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Nouvel e-mail</label>
                  <input v-model="emailForm.newEmail" type="email" placeholder="nouveau@email.com" autocomplete="off"
                    class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all" />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Confirmer avec votre mot de passe</label>
                  <input v-model="emailForm.password" type="password" placeholder="••••••••" autocomplete="new-password"
                    class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all" />
                </div>
                <p v-if="emailError" class="text-xs text-red-500">{{ emailError }}</p>
                <p v-if="emailSuccess" class="text-xs" style="color: #3D5C38">✓ Email modifié avec succès !</p>
                <button @click="handleUpdateEmail"
                  class="flex items-center gap-2 px-5 py-2.5 rounded-lg text-sm text-white font-bold"
                  style="background-color: #7A9E6E">
                  Mettre à jour l'e-mail
                </button>
              </div>
            </div>

            <!-- Changer le mot de passe -->
            <div class="bg-white rounded-xl border border-gray-200 p-6">
              <h2 class="mb-4 font-extrabold text-lg">Changer le mot de passe</h2>
              <div class="space-y-3">
                <div>
                  <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Mot de passe actuel</label>
                  <input v-model="passwordForm.currentPassword" type="password" placeholder="••••••••" autocomplete="current-password"
                    class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all" />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Nouveau mot de passe</label>
                  <input v-model="passwordForm.newPassword" type="password" placeholder="••••••••" autocomplete="new-password"
                    class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all" />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1.5 font-semibold">Confirmer le nouveau mot de passe</label>
                  <input v-model="passwordForm.confirmPassword" type="password" placeholder="••••••••" autocomplete="new-password"
                    class="w-full px-3 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-gray-900 bg-gray-50 focus:bg-white transition-all" />
                </div>
                <p v-if="passwordError" class="text-xs text-red-500">{{ passwordError }}</p>
                <p v-if="passwordSuccess" class="text-xs" style="color: #3D5C38">✓ Mot de passe modifié avec succès !</p>
                <button @click="handleUpdatePassword"
                  class="flex items-center gap-2 px-5 py-2.5 rounded-lg text-sm text-white font-bold"
                  style="background-color: #7A9E6E">
                  Changer le mot de passe
                </button>
              </div>
            </div>

            <!-- Zone dangereuse -->
            <div class="bg-white rounded-xl border border-red-100 p-6">
              <h3 class="text-red-600 mb-2 font-bold">Zone dangereuse</h3>
              <p class="text-sm text-gray-500 mb-4">La suppression de votre compte est définitive et irréversible.</p>
              <button class="px-4 py-2 border border-red-200 text-red-600 rounded-lg text-sm hover:bg-red-50 transition-colors font-semibold">
                Supprimer mon compte
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User as UserIcon, Mail, CheckCircle, Heart, CalendarDays,
  Settings, Edit2, Plus, LogOut, Save, X, Camera
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'

const authStore = useAuthStore()
const router = useRouter()

const tabs = [
  { id: 'overview',      label: "Vue d'ensemble",  icon: UserIcon },
  { id: 'registrations', label: 'Inscriptions',    icon: CheckCircle },
  { id: 'liked',         label: 'Likes',           icon: Heart },
  { id: 'created',       label: 'Mes évènements',  icon: CalendarDays },
  { id: 'settings',      label: 'Paramètres et sécurité',      icon: Settings },
] as const

type TabId = (typeof tabs)[number]['id']

const activeTab = ref<TabId>('overview')
const editMode = ref(false)
const saveSuccess = ref(false)
const avatarPreview = ref<string | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const myEvents = ref<any[]>([])
const myRegistrations = ref<any[]>([])
const myLikes = ref<any[]>([])

const editForm = reactive({
  firstName: authStore.user?.firstName || '',
  lastName: authStore.user?.lastName || '',
  email: authStore.user?.email || '',
  bio: authStore.user?.bio || '',
})

const editFields = [
  { key: 'firstName' as const, label: 'Prénom',  type: 'text',  placeholder: 'Jean' },
  { key: 'lastName'  as const, label: 'Nom',     type: 'text',  placeholder: 'Dupont' },
]

const notifPrefs = [
  "Rappels d'évènements (24h avant)",
  'Nouveaux évènements dans mes catégories',
  'Mises à jour de mes inscriptions',
  'Newsletter hebdomadaire',
]

const userInitials = computed(() => {
  if (!authStore.user) return '?'
  return `${authStore.user.firstName?.[0] ?? ''}${authStore.user.lastName?.[0] ?? ''}`.toUpperCase()
})

const profileStats = computed(() => [
  { value: myEvents.value.length, label: 'Évènements' },
  { value: myRegistrations.value.length, label: 'Inscriptions' },
  { value: myLikes.value.length, label: 'Likes' },
])

const profileInfos = computed(() => [
  { icon: UserIcon, label: 'Prénom',  value: authStore.user?.firstName || '—' },
  { icon: UserIcon, label: 'Nom',     value: authStore.user?.lastName  || '—' },
  { icon: Mail,     label: 'E-mail',  value: authStore.user?.email     || '—' },
])

onMounted(async () => {
  if (!authStore.isLoggedIn) return
  try {
    const [eventsRes, registrationsRes, likesRes] = await Promise.all([
      api.get('/events'),
      api.get('/users/me/registrations'),
      api.get('/users/me/likes'),
    ])
    myEvents.value = eventsRes.data.filter(
      (e: any) => e.creatorId === authStore.user?.id
    )
    myRegistrations.value = registrationsRes.data
    myLikes.value = likesRes.data
  } catch (e) {
    console.error('Erreur chargement profil', e)
  }
})

function handleAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  avatarPreview.value = URL.createObjectURL(file)
}

async function handleSave() {
  try {
    const response = await api.put('/users/me', {
      firstName: editForm.firstName,
      lastName: editForm.lastName,
      bio: editForm.bio,
    })
    // Met à jour le store avec les nouvelles infos
    authStore.setAuth(response.data, authStore.token!)
    editMode.value = false
    saveSuccess.value = true
    setTimeout(() => (saveSuccess.value = false), 3000)
  } catch (e) {
    console.error('Erreur sauvegarde profil', e)
  }
}

function handleLogout() {
  authStore.logout()
  router.push('/')
}

const emailForm = reactive({ newEmail: '', password: '' })
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const emailError = ref('')
const emailSuccess = ref(false)
const passwordError = ref('')
const passwordSuccess = ref(false)

async function handleUpdateEmail() {
  emailError.value = ''
  emailSuccess.value = false
  if (!emailForm.newEmail || !emailForm.password) {
    emailError.value = 'Tous les champs sont requis'
    return
  }
  try {
    const response = await api.put('/users/me/email', {
      newEmail: emailForm.newEmail,
      password: emailForm.password,
    })
    authStore.setAuth(response.data.user, response.data.token)
    editForm.email = response.data.user.email
    emailSuccess.value = true
    emailForm.newEmail = ''
    emailForm.password = ''
    setTimeout(() => emailSuccess.value = false, 3000)
  } catch (e: any) {
    emailError.value = e.response?.data?.error || 'Une erreur est survenue'
  }
}

async function handleUpdatePassword() {
  passwordError.value = ''
  passwordSuccess.value = false
  if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    passwordError.value = 'Tous les champs sont requis'
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordError.value = 'Les mots de passe ne correspondent pas'
    return
  }
  if (passwordForm.newPassword.length < 6) {
    passwordError.value = 'Le mot de passe doit faire au moins 6 caractères'
    return
  }
  try {
    await api.put('/users/me/password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    })
    passwordSuccess.value = true
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    setTimeout(() => passwordSuccess.value = false, 3000)
  } catch (e: any) {
    passwordError.value = e.response?.data?.error || 'Une erreur est survenue'
  }
}
</script>