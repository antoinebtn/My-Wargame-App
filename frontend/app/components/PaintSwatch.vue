<script setup lang="ts">
defineProps<{ hex: string }>()
</script>

<template>
  <span
    class="inline-block size-6 shrink-0 rounded-full ring-1 ring-default"
    :style="{ backgroundColor: hex }"
    :title="hex"
  />
</template>
