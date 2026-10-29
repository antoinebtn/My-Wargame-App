<script setup lang="ts">
definePageMeta({ layout: 'auth' })

const { login } = useAuth()
const state = reactive({ email: '', password: '' })
const error = ref('')
const loading = ref(false)

async function onSubmit() {
  error.value = ''
  loading.value = true
  try {
    await login(state.email, state.password)
    await navigateTo('/')
  } catch (e) {
    error.value = apiErrorMessage(e, 'Connexion impossible')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <UForm :state="state" class="space-y-4" @submit="onSubmit">
    <h1 class="text-lg font-semibold">Connexion</h1>
    <UFormField label="Email" name="email" required>
      <UInput v-model="state.email" type="email" autocomplete="email" required class="w-full" />
    </UFormField>
    <UFormField label="Mot de passe" name="password" required>
      <UInput v-model="state.password" type="password" autocomplete="current-password" required class="w-full" />
    </UFormField>
    <UAlert v-if="error" color="error" variant="subtle" :title="error" />
    <UButton type="submit" block :loading="loading">Se connecter</UButton>
    <p class="text-center text-sm text-muted">
      Pas encore de compte ? <ULink to="/register" class="text-primary">Créer un compte</ULink>
    </p>
  </UForm>
</template>
