const DEFAULT_API_BASE_URL = 'http://localhost:8080'

const trimTrailingSlash = (url: string): string => url.replace(/\/+$/, '')

export const API_BASE_URL = trimTrailingSlash(
  import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL,
)
