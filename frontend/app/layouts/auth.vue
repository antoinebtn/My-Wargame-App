<template>
  <div class="flex min-h-screen flex-col items-center justify-center gap-6 px-4">
    <div class="flex items-center gap-2 text-2xl font-bold">
      <UIcon name="i-lucide-swords" class="text-primary" />
      My Wargame App
    </div>
    <UCard class="w-full max-w-sm">
      <slot />
    </UCard>
  </div>
</template>
