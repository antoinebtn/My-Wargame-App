const PUBLIC_PAGES = ['/login', '/register']

export default defineNuxtRouteMiddleware((to) => {
  const { isLoggedIn } = useAuth()
  const isPublic = PUBLIC_PAGES.includes(to.path)

  if (!isLoggedIn.value && !isPublic) {
    return navigateTo('/login')
  }
  if (isLoggedIn.value && isPublic) {
    return navigateTo('/')
  }
})
