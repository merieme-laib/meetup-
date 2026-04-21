<template>
  <div
    v-if="modelValue"
    class="fixed inset-0 z-50 flex items-center justify-center px-4"
    style="background-color: rgba(0,0,0,0.45)"
    @click="$emit('update:modelValue', false)"
  >
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6" @click.stop>
      <h2 class="text-gray-900 font-black text-lg mb-2">{{ title }}</h2>
      <p class="text-gray-500 text-sm mb-6">{{ message }}</p>
      <div class="flex gap-3">
        <button
          @click="$emit('update:modelValue', false)"
          class="flex-1 py-2.5 rounded-lg border border-gray-200 text-sm font-semibold text-gray-600 hover:border-gray-400 transition-colors"
        >
          Annuler
        </button>
        <button
          @click="$emit('confirm')"
          class="flex-1 py-2.5 rounded-lg text-white text-sm font-bold transition-colors"
          :style="{ backgroundColor: confirmColor }"
        >
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: boolean
  title: string
  message: string
  confirmText?: string
  confirmColor?: string
}>()

defineEmits(['update:modelValue', 'confirm'])
</script>