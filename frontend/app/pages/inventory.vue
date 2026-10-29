<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import type { InventoryItem, PaintLevel } from '~/types/api'

const api = useApi()
const toast = useToast()

const { data: items, refresh } = await useAsyncData('inventory', () => api<InventoryItem[]>('/api/inventory'))

const LEVELS: Record<PaintLevel, { label: string, color: 'success' | 'info' | 'warning' | 'error' }> = {
  FULL: { label: 'Plein', color: 'success' },
  HALF: { label: 'À moitié', color: 'info' },
  LOW: { label: 'Presque vide', color: 'warning' },
  EMPTY: { label: 'Vide', color: 'error' }
}

const columns: TableColumn<InventoryItem>[] = [
  { id: 'swatch', header: '' },
  { id: 'name', header: 'Peinture' },
  { id: 'brand', header: 'Marque' },
  { accessorKey: 'quantity', header: 'Qté' },
  { accessorKey: 'level', header: 'Niveau' },
  { id: 'actions', header: '' }
]

async function remove(item: InventoryItem) {
  try {
    await api(`/api/inventory/${item.id}`, { method: 'DELETE' })
    await refresh()
  } catch (e) {
    toast.add({ title: apiErrorMessage(e), color: 'error' })
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold">Mes peintures</h1>
        <p class="text-muted">{{ items?.length ?? 0 }} références dans ta collection.</p>
      </div>
      <UButton to="/paints" icon="i-lucide-plus">Ajouter depuis le catalogue</UButton>
    </div>

    <UEmpty
      v-if="items && !items.length"
      icon="i-lucide-paintbrush"
      title="Aucune peinture pour l'instant"
      description="Parcours le catalogue et ajoute les pots que tu possèdes."
      :actions="[{ label: 'Ouvrir le catalogue', to: '/paints' }]"
    />

    <UTable v-else :data="items ?? []" :columns="columns">
      <template #swatch-cell="{ row }">
        <PaintSwatch :hex="row.original.paint.hex" />
      </template>
      <template #name-cell="{ row }">
        <div class="font-medium">{{ row.original.paint.name }}</div>
        <div class="text-xs text-muted">{{ row.original.paint.set }}</div>
      </template>
      <template #brand-cell="{ row }">
        {{ row.original.paint.brand }}
      </template>
      <template #level-cell="{ row }">
        <UBadge :color="LEVELS[row.original.level].color" variant="subtle">
          {{ LEVELS[row.original.level].label }}
        </UBadge>
      </template>
      <template #actions-cell="{ row }">
        <div class="text-right">
          <UButton icon="i-lucide-trash-2" color="error" variant="ghost" size="sm" aria-label="Retirer" @click="remove(row.original)" />
        </div>
      </template>
    </UTable>
  </div>
</template>
