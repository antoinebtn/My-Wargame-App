import type { AuthResponse, User } from '~/types/api'

export function useAuth() {
  const config = useRuntimeConfig()
  const token = useCookie<string | null>('mwa_token', { maxAge: 60 * 60 * 24 * 7, sameSite: 'lax' })
  const user = useState<User | null>('auth-user', () => null)
  const isLoggedIn = computed(() => !!token.value)

  async function authenticate(path: string, body: Record<string, string>) {
    const res = await $fetch<AuthResponse>(path, { baseURL: config.public.apiBase, method: 'POST', body })
    token.value = res.token
    user.value = res.user
  }

  async function fetchMe() {
    if (!token.value || user.value) return
    user.value = await useApi()<User>('/api/me')
  }

  async function logout() {
    token.value = null
    user.value = null
    await navigateTo('/login')
  }

  return {
    token,
    user,
    isLoggedIn,
    login: (email: string, password: string) => authenticate('/api/auth/login', { email, password }),
    register: (email: string, password: string, displayName: string) =>
      authenticate('/api/auth/register', { email, password, displayName }),
    fetchMe,
    logout
  }
}

/** Pulls the message out of a Spring ProblemDetail response, if any. */
export function apiErrorMessage(error: unknown, fallback = 'Une erreur est survenue') {
  const data = (error as { data?: { detail?: string } })?.data
  return data?.detail || fallback
}
