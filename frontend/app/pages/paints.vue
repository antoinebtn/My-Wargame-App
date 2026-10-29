<script setup lang="ts">
import type { TableColumn } from '@nuxt/ui'
import type { Brand, InventoryItem, Page, Paint } from '~/types/api'

const PAGE_SIZE = 50
const api = useApi()
const toast = useToast()

const brand = ref<string>()
const search = ref('')
const query = ref('')
const page = ref(1)

let debounce: ReturnType<typeof setTimeout> | undefined
watch(search, (value) => {
  clearTimeout(debounce)
  debounce = setTimeout(() => (query.value = value.trim()), 300)
})
watch([brand, query], () => (page.value = 1))

const { data: brands } = await useAsyncData('brands', () => api<Brand[]>('/api/brands'))
const brandItems = computed(() => (brands.value ?? []).map(b => ({ label: b.name, value: b.slug })))

const { data: paints, status } = await useAsyncData(
  'paints',
  () => api<Page<Paint>>('/api/paints', {
    query: { brand: brand.value ?? '', q: query.value, page: page.value - 1, size: PAGE_SIZE }
  }),
  { watch: [brand, query, page] }
)

const columns: TableColumn<Paint>[] = [
  { accessorKey: 'hex', header: '' },
  { accessorKey: 'name', header: 'Nom' },
  { accessorKey: 'brand', header: 'Marque' },
  { accessorKey: 'set', header: 'Gamme' },
  { accessorKey: 'code', header: 'Réf.' },
  { id: 'actions', header: '' }
]

async function addToInventory(paint: Paint) {
  try {
    const item = await api<InventoryItem>('/api/inventory', { method: 'POST', body: { paintId: paint.id } })
    toast.add({ title: `${paint.name} ajoutée`, description: `Tu en as maintenant ${item.quantity}.`, color: 'success' })
  } catch (e) {
    toast.add({ title: apiErrorMessage(e), color: 'error' })
  }
}
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">Catalogue de peintures</h1>
      <p class="text-muted">{{ paints?.totalElements ?? 0 }} peintures — ajoute celles que tu possèdes à ton inventaire.</p>
    </div>

    <div class="flex flex-col gap-3 sm:flex-row">
      <UInput v-model="search" icon="i-lucide-search" placeholder="Nom ou référence…" class="sm:w-80" />
      <USelectMenu
        v-model="brand"
        :items="brandItems"
        value-key="value"
        placeholder="Toutes les marques"
        clear
        class="sm:w-64"
      />
    </div>

    <UTable :data="paints?.content ?? []" :columns="columns" :loading="status === 'pending'">
      <template #hex-cell="{ row }">
        <PaintSwatch :hex="row.original.hex" />
      </template>
      <template #actions-cell="{ row }">
        <div class="text-right">
          <UButton icon="i-lucide-plus" size="sm" variant="soft" @click="addToInventory(row.original)">
            Ajouter
          </UButton>
        </div>
      </template>
    </UTable>

    <div v-if="(paints?.totalPages ?? 0) > 1" class="flex justify-center">
      <UPagination v-model:page="page" :total="paints?.totalElements ?? 0" :items-per-page="PAGE_SIZE" />
    </div>
  </div>
</template>
