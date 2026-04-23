import { defineStore } from 'pinia'

import { getStorageItem, setStorageItem } from '@/shared/utils/storage'

type ThemeMode = 'light' | 'dark'

const THEME_STORAGE_KEY = 'themeMode'

const resolveInitialTheme = (): ThemeMode => {
  const storedTheme = getStorageItem(THEME_STORAGE_KEY)
  if (storedTheme === 'dark' || storedTheme === 'light') {
    return storedTheme
  }

  if (typeof window !== 'undefined' && window.matchMedia?.('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }

  return 'light'
}

const applyThemeClass = (mode: ThemeMode) => {
  if (typeof document === 'undefined') return

  document.documentElement.classList.toggle('dark-theme', mode === 'dark')
  document.documentElement.style.colorScheme = mode
}

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: 'light' as ThemeMode,
    initialized: false,
  }),

  getters: {
    isDarkMode: (state) => state.mode === 'dark',
  },

  actions: {
    initializeTheme() {
      const initialTheme = resolveInitialTheme()
      this.mode = initialTheme
      this.initialized = true
      applyThemeClass(initialTheme)
      setStorageItem(THEME_STORAGE_KEY, initialTheme)
    },

    setTheme(mode: ThemeMode) {
      this.mode = mode
      this.initialized = true
      applyThemeClass(mode)
      setStorageItem(THEME_STORAGE_KEY, mode)
    },

    toggleTheme() {
      this.setTheme(this.mode === 'dark' ? 'light' : 'dark')
    },
  },
})
