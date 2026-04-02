import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
        },
        {
          path: 'user/:id',
          name: 'user',
          component: () => import('../views/UserView.vue'),
        },
        {
          path: 'user/profile',
          name: 'profile',
          component: () => import('../views/UserProfileView.vue'),
        },
        // post 详情现在使用模态框，不再需要路由
        {
          path: 'about',
          name: 'about',
          component: () => import('../views/AboutView.vue'),
        },
      ],
    },
    {
      path: '/create',
      name: 'create',
      component: () => import('../views/CreateView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
  ],
})

export default router
