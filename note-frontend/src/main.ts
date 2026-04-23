import './assets/main.css'
import './styles/global.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { useAuthStore } from './stores/auth'
import { useThemeStore } from './stores/theme'
import imageErrorHandler from './directives/imageErrorHandler'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)

// 注册全局指令
app.directive('image-error', imageErrorHandler)

const themeStore = useThemeStore(pinia)
themeStore.initializeTheme()

app.mount('#app')

// 应用挂载后初始化用户信息
const authStore = useAuthStore(pinia)
if (authStore.token) {
  authStore.fetchUserInfo()
}
