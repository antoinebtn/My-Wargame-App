<script setup lang="ts">
import type { ArmySummary, InventoryItem } from '~/types/api'

const api = useApi()
const { user } = useAuth()

const { data } = await useAsyncData('dashboard', async () => {
  const [inventory, armies] = await Promise.all([
    api<InventoryItem[]>('/api/inventory'),
    api<ArmySummary[]>('/api/armies')
  ])
  return { inventory, armies }
})

const stats = computed(() => {
  const armies = data.value?.armies ?? []
  const minis = armies.reduce((sum, a) => sum + a.miniatureCount, 0)
  const weighted = armies.reduce((sum, a) => sum + a.progress * a.miniatureCount, 0)
  return [
    { label: 'Pots de peinture', value: data.value?.inventory.reduce((s, i) => s + i.quantity, 0) ?? 0, icon: 'i-lucide-paintbrush', to: '/inventory' },
    { label: 'Armées', value: armies.length, icon: 'i-lucide-swords', to: '/armies' },
    { label: 'Avancement global', value: `${minis ? Math.round(weighted / minis) : 0} %`, icon: 'i-lucide-trending-up', to: '/armies' }
  ]
})
</script>

<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold">Bonjour{{ user ? ` ${user.displayName}` : '' }} 👋</h1>
      <p class="text-muted">Voici où en est ton hobby.</p>
    </div>

    <div class="grid gap-4 sm:grid-cols-3">
      <UPageCard v-for="s in stats" :key="s.label" :to="s.to" :icon="s.icon" :title="String(s.value)" :description="s.label" />
    </div>
  </div>
</template>
