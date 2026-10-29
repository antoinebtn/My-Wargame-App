<script setup lang="ts">
const { isLoggedIn } = useAuth()
</script>

<template>
  <div class="min-h-screen">
    <UHeader to="/">
      <template #title>
        <span class="flex items-center gap-2">
          <UIcon name="i-lucide-swords" class="text-primary" />
          My Wargame App
        </span>
      </template>

      <template #right>
        <UButton v-if="isLoggedIn" to="/dashboard" icon="i-lucide-layout-dashboard">Mon espace</UButton>
        <template v-else>
          <UButton to="/login" color="neutral" variant="ghost">Connexion</UButton>
          <UButton to="/register" class="hidden sm:inline-flex">Créer un compte</UButton>
        </template>
      </template>

      <template #body>
        <div class="flex flex-col gap-2">
          <UButton v-if="isLoggedIn" to="/dashboard" icon="i-lucide-layout-dashboard" block>Mon espace</UButton>
          <template v-else>
            <UButton to="/register" block>Créer un compte</UButton>
            <UButton to="/login" color="neutral" variant="subtle" block>Connexion</UButton>
          </template>
        </div>
      </template>
    </UHeader>

    <UMain>
      <slot />
    </UMain>

    <UFooter>
      <template #left>
        <p class="text-sm text-muted">
          My Wargame App · Catalogue de peintures issu de
          <ULink to="https://github.com/Arcturus5404/miniature-paints" target="_blank" class="text-primary">Arcturus5404/miniature-paints</ULink>
        </p>
      </template>
    </UFooter>
  </div>
</template>
