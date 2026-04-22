<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center p-4">
    <h1 class="text-9xl font-black text-[#6bae38] opacity-20 absolute z-0">404</h1>
    <div class="relative z-10">
      <h2 class="text-3xl font-bold text-gray-800 mb-4">Oups ! Tu sembles perdu.</h2>
      <p class="text-gray-500 mb-8 max-w-md mx-auto">
        Oups... La page que vous recherchez n'existe pas ou a été déplacée.
        Même les meilleurs explorateurs s'égarent parfois !
      </p>
      <RouterLink
          to="/"
          class="px-8 py-3 bg-[#6bae38] text-white rounded-xl font-bold hover:scale-105 transition-transform inline-block shadow-lg"
      >
        Retourner à l'accueil
      </RouterLink>
    </div>
  </div>
</template>