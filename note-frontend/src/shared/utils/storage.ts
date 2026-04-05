export const USER_TOKEN_STORAGE_KEY = 'userToken'
export const LIKED_POSTS_STORAGE_KEY = 'likedPosts'

const hasLocalStorage = (): boolean =>
  typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'

export const getStorageItem = (key: string): string | null => {
  if (!hasLocalStorage()) return null

  try {
    return window.localStorage.getItem(key)
  } catch {
    return null
  }
}

export const setStorageItem = (key: string, value: string): void => {
  if (!hasLocalStorage()) return

  try {
    window.localStorage.setItem(key, value)
  } catch {
    // Ignore storage write failures.
  }
}

export const removeStorageItem = (key: string): void => {
  if (!hasLocalStorage()) return

  try {
    window.localStorage.removeItem(key)
  } catch {
    // Ignore storage remove failures.
  }
}

export const readJsonStorage = <T>(key: string, fallback: T): T => {
  const rawValue = getStorageItem(key)
  if (!rawValue) return fallback

  try {
    return JSON.parse(rawValue) as T
  } catch {
    return fallback
  }
}

export const writeJsonStorage = <T>(key: string, value: T): void => {
  setStorageItem(key, JSON.stringify(value))
}

export const tokenStorage = {
  get(): string {
    return getStorageItem(USER_TOKEN_STORAGE_KEY) || ''
  },
  set(token: string): void {
    setStorageItem(USER_TOKEN_STORAGE_KEY, token)
  },
  clear(): void {
    removeStorageItem(USER_TOKEN_STORAGE_KEY)
  },
}
