import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: HomeView, // 🌟 核心改变：HomeView 现在成了带侧边栏的“父壳子”
      children: [
        {
          path: '', // 默认路径，刚进网站时右边显示的瀑布流
          name: 'home',
          component: () => import('../views/FeedView.vue') // （下一步我们就去创建这个文件）
        },
        {
          path: 'profile', // 点击“我”时，右边切换为个人主页
          name: 'profile',
          component: () => import('../views/UserProfileView.vue')
        },
        {
          path: 'user/:id', // 点击别人头像时，右边切换为别人的主页
          name: 'user',
          component: () => import('../views/UserView.vue')
        }
      ]
    },
    // post 详情现在使用模态框，不再需要路由
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/create',
      name: 'create',
      component: () => import('../views/CreateView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
  ],
})

export default router