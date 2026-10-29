<script setup lang="ts">
import type { ButtonProps, PageFeatureProps } from '@nuxt/ui'

definePageMeta({ layout: 'public' })
useHead({ title: 'My Wargame App — ton hobby de figurines, organisé' })

const { isLoggedIn } = useAuth()

const heroLinks = computed<ButtonProps[]>(() => isLoggedIn.value
  ? [{ label: 'Ouvrir mon espace', to: '/dashboard', size: 'xl', trailingIcon: 'i-lucide-arrow-right' }]
  : [
      { label: 'Commencer gratuitement', to: '/register', size: 'xl', trailingIcon: 'i-lucide-arrow-right' },
      { label: 'J\'ai déjà un compte', to: '/login', size: 'xl', color: 'neutral', variant: 'subtle' }
    ])

const features: PageFeatureProps[] = [
  {
    icon: 'i-lucide-palette',
    title: 'Un catalogue de près de 10 000 peintures',
    description: 'Citadel, Vallejo, Army Painter, Scale75, AK, Tamiya… 32 marques avec leur gamme, leur référence et leur couleur.'
  },
  {
    icon: 'i-lucide-paintbrush',
    title: 'Ton inventaire, toujours à jour',
    description: 'Ajoute tes pots en un clic, note leur niveau et sache avant de partir en boutique ce qu\'il te reste vraiment.'
  },
  {
    icon: 'i-lucide-swords',
    title: 'L\'avancement de chaque armée',
    description: 'Chaque figurine passe de la grappe au socle. Tu vois d\'un coup d\'œil où en est ta liste d\'armée.'
  }
]

const stages = [
  { label: 'Non assemblée', icon: 'i-lucide-package' },
  { label: 'Assemblée', icon: 'i-lucide-puzzle' },
  { label: 'Sous-couche', icon: 'i-lucide-spray-can' },
  { label: 'Peinte', icon: 'i-lucide-paintbrush' },
  { label: 'Soclée', icon: 'i-lucide-mountain' }
]

// Illustrative content for the hero preview card
const sampleArmies = [
  { name: 'Goff Klan', system: 'Warhammer 40,000', progress: 72 },
  { name: 'Ost de Minas Tirith', system: 'Le Seigneur des Anneaux', progress: 45 },
  { name: 'Légion Nighthaunt', system: 'Age of Sigmar', progress: 18 }
]
const samplePaints = ['#9A1115', '#0D407F', '#4D6729', '#C39E81', '#DDDDDD', '#A07409', '#231F20', '#5D0009']
</script>

<template>
  <div>
    <UPageHero
      headline="Pour les figurinistes et les wargamers"
      title="Ton hobby de figurines, enfin organisé"
      description="Répertorie tes peintures, suis l'assemblage et la peinture de tes armées, et garde toujours un œil sur ce qu'il reste à faire sur ta table de peinture."
      orientation="horizontal"
      :links="heroLinks"
    >
      <UCard class="w-full" aria-label="Aperçu de l'application">
        <div class="space-y-5">
          <div>
            <p class="mb-2 text-sm font-medium text-muted">Mes peintures</p>
            <div class="flex flex-wrap gap-2">
              <PaintSwatch v-for="hex in samplePaints" :key="hex" :hex="hex" />
            </div>
          </div>
          <USeparator />
          <div class="space-y-4">
            <p class="text-sm font-medium text-muted">Mes armées</p>
            <div v-for="army in sampleArmies" :key="army.name" class="space-y-1">
              <div class="flex justify-between text-sm">
                <span>
                  <span class="font-medium">{{ army.name }}</span>
                  <span class="text-muted"> · {{ army.system }}</span>
                </span>
                <span class="font-medium">{{ army.progress }} %</span>
              </div>
              <UProgress :model-value="army.progress" size="sm" />
            </div>
          </div>
        </div>
      </UCard>
    </UPageHero>

    <UPageSection
      headline="Fonctionnalités"
      title="Tout ce qu'il faut entre deux parties"
      :features="features"
    />

    <UPageSection
      headline="Suivi d'avancement"
      title="De la grappe au socle"
      description="Chaque étape compte pour 25 % : ton armée progresse à chaque figurine assemblée, sous-couchée, peinte ou soclée."
    >
      <div class="grid grid-cols-2 gap-3 sm:grid-cols-5">
        <div
          v-for="(stage, i) in stages"
          :key="stage.label"
          class="flex flex-col items-center gap-2 rounded-lg border border-default p-4 text-center"
        >
          <UIcon :name="stage.icon" class="size-6 text-primary" />
          <span class="text-sm font-medium">{{ stage.label }}</span>
          <span class="text-xs text-muted">{{ i * 25 }} %</span>
        </div>
      </div>
    </UPageSection>

    <UPageSection>
      <UPageCTA
        title="Prêt à vider ta pile de la honte ?"
        description="Crée ton compte en quelques secondes et ajoute ta première armée."
        variant="subtle"
        :links="heroLinks"
      />
    </UPageSection>
  </div>
</template>
