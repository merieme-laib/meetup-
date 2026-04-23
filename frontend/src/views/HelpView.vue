<template>
  <div class="max-w-5xl mx-auto py-20 px-6">

    <div class="text-center mb-16">
      <span class="bg-[#E0ECD9] text-[#4D6E47] text-xs font-bold px-4 py-1.5 rounded-full uppercase tracking-wider">
        Centre de support & À Propos
      </span>
      <h1 class="text-5xl font-black text-gray-900 mt-6 mb-4">Besoin d'un coup de main ?</h1>
      <p class="text-gray-500 text-lg">Découvrez l'histoire de Meetup et trouvez les réponses à vos questions.</p>
    </div>

    <div class="grid lg:grid-cols-3 gap-12">

      <div class="lg:col-span-1">
        <div class="bg-white border-2 border-[#F2F5F0] p-8 rounded-3xl sticky top-8 shadow-sm">
          <div class="w-12 h-12 bg-[#6bae38] text-white rounded-2xl flex items-center justify-center mb-6 shadow-lg shadow-[#6bae38]/20">
            <Users :size="24" />
          </div>
          <h2 class="text-2xl font-black text-gray-900 mb-4">L'équipe Meetup Lyon 1</h2>
          <p class="text-gray-700 leading-relaxed mb-6">
            Nous sommes un groupe de <strong>6 étudiants en Master Informatique à l'Université Claude Bernard Lyon 1</strong>.
            Convaincus que les rencontres simples et le contact humain sont les vrais moteurs de l'épanouissement et de l'innovation,
            nous avons fondé Meetup pour faciliter ces moments de partage.
          </p>
          <p class="text-gray-600 text-sm leading-relaxed mb-6">
            Si notre expertise nous porte naturellement vers l'écosystème <strong>numérique</strong>, Meetup est conçu pour favoriser <strong>tous les partages de savoirs</strong>, quel que soit le domaine.
          </p>
          <div class="pt-6 border-t border-gray-100 space-y-3">
            <div class="flex items-center gap-3 text-sm text-gray-500">
              <div class="w-1.5 h-1.5 rounded-full bg-[#6bae38]"></div>
              Ouvert à tous les passionnés
            </div>
            <div class="flex items-center gap-3 text-sm text-gray-500">
              <div class="w-1.5 h-1.5 rounded-full bg-[#6bae38]"></div>
              Basé à Lyon, France
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-2">
        <h3 class="text-xl font-bold text-gray-900 mb-8 flex items-center gap-3">
          <HelpCircle class="text-[#6bae38]" /> Questions fréquentes
        </h3>

        <div class="space-y-4">
          <div v-for="faq in faqs" :key="faq.q"
               class="group bg-white border border-gray-200 p-6 rounded-2xl transition-all hover:border-[#6bae38] hover:shadow-md cursor-default">
            <div>
              <h4 class="font-bold text-gray-900 group-hover:text-[#4D6E47] transition-colors mb-2">
                {{ faq.q }}
              </h4>
              <p class="text-gray-600 text-sm leading-relaxed">
                {{ faq.a }}
              </p>
            </div>
          </div>
        </div>

        <div class="mt-12 bg-gray-900 text-white p-8 rounded-3xl flex flex-col md:flex-row items-center justify-between gap-6">
          <div>
            <h4 class="font-bold text-lg">Vous ne trouvez pas votre réponse ?</h4>
            <p class="text-gray-400 text-sm">Notre équipe d'étudiants est là pour vous aider.</p>
          </div>
          <a href="mailto:service@meetup.fr" class="bg-[#6bae38] hover:bg-[#5E7D58] px-6 py-3 rounded-xl font-bold transition-colors whitespace-nowrap">
            Nous contacter
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Users, HelpCircle } from 'lucide-vue-next'

const faqs = [
  { q: "Quel type d'événement puis-je organiser ?", a: "Absolument tout ! Des ateliers tech, des rencontres sportives, des conférences littéraires ou des simples cafés entre passionnés. Notre plateforme est un outil neutre au service de votre communauté." },
  { q: "Comment s'inscrire à un événement ?", a: "Créez votre compte en quelques secondes, parcourez les catégories et cliquez sur 'Participer à l'évènement' sur la page de l'événement qui vous intéresse." },
  { q: "Comment annuler ma participation à un événement ?", a: "Rendez-vous dans l'onglet 'Mon Compte' puis dans la section 'Inscriptions'. Cliquez sur le bouton 'Voir' de l'événement concerné, puis cliquez simplement sur le bouton '✓ Inscrit - Se désinscrire' pour valider votre désinscription." },
  { q: "Comment puis-je créer un événement ?", a: "C'est très simple ! Deux options s'offrent à vous : soit en cliquant sur le bouton vert 'Créer un événement' en bas de la page d'accueil, soit via l'onglet 'Mon Compte' > 'Mes événements' puis en cliquant sur le bouton '+ Créer'."},
  { q: "Est-ce que l'utilisation est payante ?", a: "Non. Notre priorité est de faciliter l'organisation d'événements simplement et gratuitement." },
]
</script>