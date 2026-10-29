<script setup lang="ts">
import type { ArmySummary } from '~/types/api'

const api = useApi()
const toast = useToast()

const { data: armies, refresh } = await useAsyncData('armies', () => api<ArmySummary[]>('/api/armies'))

const open = ref(false)
const saving = ref(false)
const form = reactive({ name: '', faction: '', gameSystem: '', description: '' })

async function create() {
  saving.value = true
  try {
    await api('/api/armies', { method: 'POST', body: form })
    Object.assign(form, { name: '', faction: '', gameSystem: '', description: '' })
    open.value = false
    await refresh()
  } catch (e) {
    toast.add({ title: apiErrorMessage(e), color: 'error' })
  } finally {
    saving.value = false
  }
}

async function remove(army: ArmySummary) {
  if (!confirm(`Supprimer l'armée « ${army.name} » ?`)) return
  await api(`/api/armies/${army.id}`, { method: 'DELETE' })
  await refresh()
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold">Mes armées</h1>
        <p class="text-muted">Suis l'avancement de l'assemblage et de la peinture.</p>
      </div>

      <UModal v-model:open="open" title="Nouvelle armée">
        <UButton icon="i-lucide-plus">Nouvelle armée</UButton>
        <template #body>
          <UForm :state="form" class="space-y-4" @submit="create">
            <UFormField label="Nom" name="name" required>
              <UInput v-model="form.name" required maxlength="150" placeholder="Goff Klan" class="w-full" />
            </UFormField>
            <UFormField label="Faction" name="faction">
              <UInput v-model="form.faction" maxlength="150" placeholder="Orks" class="w-full" />
            </UFormField>
            <UFormField label="Jeu" name="gameSystem">
              <UInput v-model="form.gameSystem" maxlength="150" placeholder="Warhammer 40,000" class="w-full" />
            </UFormField>
            <UFormField label="Description" name="description">
              <UTextarea v-model="form.description" class="w-full" />
            </UFormField>
            <div class="flex justify-end gap-2">
              <UButton color="neutral" variant="ghost" @click="open = false">Annuler</UButton>
              <UButton type="submit" :loading="saving">Créer</UButton>
            </div>
          </UForm>
        </template>
      </UModal>
    </div>

    <UEmpty
      v-if="armies && !armies.length"
      icon="i-lucide-swords"
      title="Aucune armée"
      description="Crée ta première armée pour suivre son avancement."
    />

    <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <UCard v-for="army in armies" :key="army.id">
        <div class="flex items-start justify-between gap-2">
          <div>
            <h2 class="font-semibold">{{ army.name }}</h2>
            <p class="text-sm text-muted">
              {{ [army.faction, army.gameSystem].filter(Boolean).join(' · ') || '—' }}
            </p>
          </div>
          <UButton icon="i-lucide-trash-2" color="error" variant="ghost" size="sm" aria-label="Supprimer" @click="remove(army)" />
        </div>
        <div class="mt-4 space-y-1">
          <div class="flex justify-between text-sm">
            <span class="text-muted">{{ army.miniatureCount }} figurines</span>
            <span class="font-medium">{{ army.progress }} %</span>
          </div>
          <UProgress :model-value="army.progress" />
        </div>
      </UCard>
    </div>
  </div>
</template>
