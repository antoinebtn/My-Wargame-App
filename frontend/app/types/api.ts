// Mirrors the Spring Boot DTOs in backend/src/main/java/app/mywargame

export interface User {
  id: number
  email: string
  displayName: string
}

export interface AuthResponse {
  token: string
  user: User
}

export interface Brand {
  id: number
  name: string
  slug: string
}

export interface Paint {
  id: number
  brand: string
  brandSlug: string
  name: string
  code: string | null
  set: string | null
  hex: string
}

export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type PaintLevel = 'FULL' | 'HALF' | 'LOW' | 'EMPTY'

export interface InventoryItem {
  id: number
  paint: Paint
  quantity: number
  level: PaintLevel
  notes: string | null
}

export interface ArmySummary {
  id: number
  name: string
  faction: string | null
  gameSystem: string | null
  miniatureCount: number
  progress: number
}
