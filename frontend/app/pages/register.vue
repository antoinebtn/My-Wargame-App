<script setup lang="ts">
definePageMeta({ layout: 'auth' })

const { register } = useAuth()
const state = reactive({ displayName: '', email: '', password: '' })
const error = ref('')
const loading = ref(false)

async function onSubmit() {
  error.value = ''
  loading.value = true
  try {
    await register(state.email, state.password, state.displayName)
    await navigateTo('/dashboard')
  } catch (e) {
    error.value = apiErrorMessage(e, 'Inscription impossible')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <UForm :state="state" class="space-y-4" @submit="onSubmit">
    <h1 class="text-lg font-semibold">Créer un compte</h1>
    <UFormField label="Pseudo" name="displayName" required>
      <UInput v-model="state.displayName" required maxlength="100" class="w-full" />
    </UFormField>
    <UFormField label="Email" name="email" required>
      <UInput v-model="state.email" type="email" autocomplete="email" required class="w-full" />
    </UFormField>
    <UFormField label="Mot de passe" name="password" help="8 caractères minimum" required>
      <UInput v-model="state.password" type="password" autocomplete="new-password" required minlength="8" class="w-full" />
    </UFormField>
    <UAlert v-if="error" color="error" variant="subtle" :title="error" />
    <UButton type="submit" block :loading="loading">Créer mon compte</UButton>
    <p class="text-center text-sm text-muted">
      Déjà inscrit ? <ULink to="/login" class="text-primary">Se connecter</ULink>
    </p>
  </UForm>
</template>
