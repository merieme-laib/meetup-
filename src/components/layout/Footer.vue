<template>
  <footer class="bg-gray-900 text-gray-400">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-8">

        <!-- Brand -->
        <div class="md:col-span-2">
          <RouterLink to="/" class="flex items-center gap-2 mb-4">
            <div class="w-8 h-8 rounded-lg flex items-center justify-center" style="background-color: #7A9E6E">
              <CalendarDays :size="16" class="text-white" />
            </div>
            <span class="text-white" style="font-weight: 800; font-size: 1.1rem; letter-spacing: -0.02em">
              meet<span style="color: #7A9E6E">up</span>
            </span>
          </RouterLink>
          <p class="text-sm leading-relaxed text-gray-500 max-w-xs">
            La plateforme de référence pour organiser et participer à des évènements tech en France.
          </p>
          <div class="flex gap-3 mt-5">
            <a
              v-for="social in socials"
              :key="social.label"
              :href="social.href"
              class="w-8 h-8 rounded-lg flex items-center justify-center bg-gray-800 hover:bg-gray-700 transition-colors"
              :aria-label="social.label"
            >
              <component :is="social.icon" :size="15" />
            </a>
          </div>
        </div>

        <!-- Plateforme -->
        <div>
          <h4 class="text-white text-sm mb-4 font-bold">Plateforme</h4>
          <ul class="space-y-2.5 text-sm">
            <li><RouterLink to="/" class="hover:text-white transition-colors">Accueil</RouterLink></li>
            <li><RouterLink to="/evenements" class="hover:text-white transition-colors">Explorer</RouterLink></li>
            <li><RouterLink to="/evenements/creer" class="hover:text-white transition-colors">Créer un évènement</RouterLink></li>
            <li><RouterLink to="/aide" class="hover:text-white transition-colors">Centre d'aide</RouterLink></li>
          </ul>
        </div>

        <!-- Catégories -->
        <div>
          <h4 class="text-white text-sm mb-4 font-bold">Catégories</h4>
          <ul class="space-y-2.5 text-sm">
            <li v-for="cat in categories" :key="cat">
              <RouterLink :to="`/evenements?cat=${encodeURIComponent(cat)}`" class="hover:text-white transition-colors">
                {{ cat }}
              </RouterLink>
            </li>
          </ul>
        </div>

        <!-- Compte -->
        <div>
          <h4 class="text-white text-sm mb-4 font-bold">Compte</h4>
          <ul class="space-y-2.5 text-sm">
            <li><RouterLink to="/connexion" class="hover:text-white transition-colors">Connexion</RouterLink></li>
            <li><RouterLink to="/inscription" class="hover:text-white transition-colors">S'inscrire</RouterLink></li>
            <li><RouterLink to="/profil" class="hover:text-white transition-colors">Mon espace</RouterLink></li>
          </ul>
        </div>

      </div>

      <!-- Bas du footer -->
      <div class="border-t border-gray-800 mt-10 pt-6 flex flex-col sm:flex-row items-center justify-between gap-3">
        <p class="text-xs text-gray-600">© 2026 MeetUp. Tous droits réservés.</p>
        <p class="text-xs text-gray-600">Construit avec Java & JavaScript · Fait en France 🌿</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { CalendarDays, Github, Twitter, Linkedin } from 'lucide-vue-next'

const categories = ['Développement', 'Technologie', 'Design', 'Business', 'Sécurité', 'Data']

const socials = [
  { label: 'GitHub',   href: '#', icon: Github },
  { label: 'Twitter',  href: '#', icon: Twitter },
  { label: 'LinkedIn', href: '#', icon: Linkedin },
]
</script>