/** $fetch bound to the backend, with the JWT attached. A 401 sends the user back to the login page. */
export function useApi() {
  const config = useRuntimeConfig()
  const { token, logout } = useAuth()

  return $fetch.create({
    baseURL: config.public.apiBase,
    onRequest({ options }) {
      if (token.value) {
        options.headers.set('Authorization', `Bearer ${token.value}`)
      }
    },
    async onResponseError({ response }) {
      if (response.status === 401) {
        await logout()
      }
    }
  })
}
