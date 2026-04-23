import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/evenements', name: 'events', component: () => import('@/views/EventsView.vue') },
    { path: '/evenements/creer', name: 'event-create', component: () => import('@/views/EventCreateView.vue'), meta: { requiresAuth: true } },
    { path: '/evenements/:id/modifier', name: 'event-edit', component: () => import('@/views/EventEditView.vue'), meta: { requiresAuth: true } },
    { path: '/evenements/:id', name: 'event-detail', component: () => import('@/views/EventDetailView.vue') },
    { path: '/connexion', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/inscription', name: 'register', component: () => import('@/views/RegisterView.vue') },
    { path: '/profil', name: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } },
    { path: '/aide', name: 'help', component: () => import('@/views/HelpView.vue') },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue') }
  ]
})

router.beforeEach((to) => {
  // Import dynamique pour éviter le problème d'initialisation de Pinia
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router