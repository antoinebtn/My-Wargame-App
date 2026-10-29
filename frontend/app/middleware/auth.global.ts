// Reachable without an account; the guest-only ones bounce logged-in users to their dashboard.
const PUBLIC_PAGES = ['/', '/login', '/register']
const GUEST_ONLY_PAGES = ['/login', '/register']

export default defineNuxtRouteMiddleware((to) => {
  const { isLoggedIn } = useAuth()

  if (!isLoggedIn.value && !PUBLIC_PAGES.includes(to.path)) {
    return navigateTo('/login')
  }
  if (isLoggedIn.value && GUEST_ONLY_PAGES.includes(to.path)) {
    return navigateTo('/dashboard')
  }
})
