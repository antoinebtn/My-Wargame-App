<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const { user, fetchMe, logout } = useAuth()
await fetchMe().catch(() => {})

const links: NavigationMenuItem[] = [
  { label: 'Tableau de bord', icon: 'i-lucide-layout-dashboard', to: '/dashboard' },
  { label: 'Catalogue', icon: 'i-lucide-palette', to: '/paints' },
  { label: 'Mes peintures', icon: 'i-lucide-paintbrush', to: '/inventory' },
  { label: 'Mes armées', icon: 'i-lucide-swords', to: '/armies' }
]
</script>

<template>
  <div class="min-h-screen">
    <UHeader title="My Wargame App" to="/dashboard">
      <UNavigationMenu :items="links" />

      <template #right>
        <span v-if="user" class="hidden text-sm text-muted sm:inline">{{ user.displayName }}</span>
        <UButton icon="i-lucide-log-out" color="neutral" variant="ghost" aria-label="Se déconnecter" @click="logout" />
      </template>

      <template #body>
        <UNavigationMenu :items="links" orientation="vertical" class="-mx-2.5" />
      </template>
    </UHeader>

    <UMain>
      <UContainer class="py-8">
        <slot />
      </UContainer>
    </UMain>
  </div>
</template>
