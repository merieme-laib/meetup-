<template>
  <div class="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-3">
    <div class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0 bg-primary-bg">
      <component :is="icon" :size="16" :style="{ color: iconColor }" />
    </div>
    <div>
      <div class="font-black text-xl" :style="{ color: iconColor }">{{ value }}</div>
      <div class="text-xs text-gray-400 font-medium">{{ label }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon: any
  iconColor: string
  value: number
  label: string
}>()
</script>
