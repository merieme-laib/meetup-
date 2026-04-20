import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types/user'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // Restaure le user depuis localStorage au démarrage
  const savedUser = localStorage.getItem('user')
  const user = ref<User | null>(savedUser ? JSON.parse(savedUser) : null)
  const token = ref<string | null>(localStorage.getItem('token'))

  const isLoggedIn = computed(() => !!token.value && !!user.value)

  function setAuth(newUser: User, newToken: string) {
    user.value = newUser
    token.value = newToken
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/connexion')
  }

  return { user, token, isLoggedIn, setAuth, logout }
})
