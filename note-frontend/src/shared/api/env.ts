const DEFAULT_API_BASE_URL = 'http://localhost:8080'
const DEFAULT_AI_API_BASE_URL = 'http://127.0.0.1:8000'

const trimTrailingSlash = (url: string): string => url.replace(/\/+$/, '')

export const API_BASE_URL = trimTrailingSlash(
  import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL,
)

export const AI_API_BASE_URL = trimTrailingSlash(
  import.meta.env.VITE_AI_API_BASE_URL || DEFAULT_AI_API_BASE_URL,
)
