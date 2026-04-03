import './assets/main.css'
import './styles/global.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { useAuthStore } from './stores/auth'
import { useLikeStore } from './stores/like'
import imageErrorHandler from './directives/imageErrorHandler'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)

// 注册全局指令
app.directive('image-error', imageErrorHandler)

app.mount('#app')

// 应用挂载后初始化用户信息
const authStore = useAuthStore()
if (authStore.token) {
  authStore.fetchUserInfo()
  const likeStore = useLikeStore()
  likeStore.fetchUserLikedIds()
}
