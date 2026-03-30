import api from './api'
import type { User } from '@/types/user'

interface AuthResponse {
  user: User
  token: string
}

const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const res = await api.post('/auth/login', { email, password })
    return res.data
  },

  async register(
    firstName: string,
    lastName: string,
    email: string,
    password: string
  ): Promise<AuthResponse> {
    const res = await api.post('/auth/register', { firstName, lastName, email, password })
    return res.data
  },

  async getMe(): Promise<User> {
    const res = await api.get('/users/me')
    return res.data
  },
}

export default authService