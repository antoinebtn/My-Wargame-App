export default defineNuxtConfig({
  compatibilityDate: '2026-10-01',
  // Private dashboard behind a login: a client-rendered SPA keeps auth simple.
  ssr: false,
  modules: ['@nuxt/ui'],
  css: ['~/assets/css/main.css'],
  colorMode: {
    preference: 'dark'
  },
  app: {
    head: {
      title: 'My Wargame App',
      htmlAttrs: { lang: 'fr' }
    }
  },
  runtimeConfig: {
    public: {
      // Override with NUXT_PUBLIC_API_BASE
      apiBase: 'http://localhost:8080'
    }
  },
  devtools: { enabled: false }
})
