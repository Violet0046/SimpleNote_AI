<template>
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md transform transition-all duration-300 scale-100 opacity-100">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-100">
        <div class="flex items-center space-x-2">
          <div class="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center">
            <span class="text-white font-bold text-sm">X</span>
          </div>
          <h2 class="text-2xl font-bold text-gray-900">小红书</h2>
        </div>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 transition-colors"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <!-- 切换标签 -->
      <div class="flex border-b border-gray-100">
        <button
          @click="activeTab = 'login'"
          :class="[
            'flex-1 py-4 text-center font-medium transition-colors relative',
            activeTab === 'login'
              ? 'text-red-500'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          {{ isLoginMode ? '登录' : '密码登录' }}
          <div
            v-if="activeTab === 'login'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-500"
          ></div>
        </button>
        <button
          @click="activeTab = 'register'"
          :class="[
            'flex-1 py-4 text-center font-medium transition-colors relative',
            activeTab === 'register'
              ? 'text-red-500'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          {{ isLoginMode ? '注册' : '注册账号' }}
          <div
            v-if="activeTab === 'register'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-500"
          ></div>
        </button>
      </div>

      <!-- 表单内容 -->
      <form @submit.prevent="handleSubmit" class="p-6">
        <div class="space-y-4">
          <!-- 登录表单 -->
          <div v-if="activeTab === 'login'" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
              <input
                v-model="loginForm.username"
                type="text"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                placeholder="请输入用户名"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
              <input
                v-model="loginForm.password"
                type="password"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                placeholder="请输入密码"
              >
            </div>
          </div>

          <!-- 注册表单 -->
          <div v-else class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
              <input
                v-model="registerForm.username"
                type="text"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                placeholder="请输入用户名"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
              <input
                v-model="registerForm.password"
                type="password"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                placeholder="请输入密码"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-colors"
                placeholder="请再次输入密码"
              >
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-sm text-red-600">{{ error }}</p>
          </div>

          <!-- 提交按钮 -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full py-3 bg-red-500 text-white font-medium rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {{ isLoading ? (activeTab === 'login' ? '登录中...' : '注册中...') : (activeTab === 'login' ? '登录' : '注册') }}
          </button>
        </div>

        <!-- 其他登录方式 -->
        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-200"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-gray-500">或</span>
            </div>
          </div>

          <div class="mt-4 grid grid-cols-2 gap-3">
            <button
              type="button"
              class="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <svg class="w-5 h-5 text-gray-400 mr-2" fill="currentColor" viewBox="0 0 24 24">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              微信
            </button>
            <button
              type="button"
              class="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <svg class="w-5 h-5 text-gray-400 mr-2" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12.24 10.285V14.4h6.806c-.275 1.765-2.056 5.174-6.806 5.174-4.095 0-7.439-3.389-7.439-7.574s3.345-7.574 7.439-7.574c2.33 0 3.891.989 4.785 1.849l3.254-3.138C18.189 1.186 15.479 0 12.24 0c-6.635 0-12 5.365-12 12s5.365 12 12 12c6.926 0 11.52-4.869 11.52-11.726 0-.788-.085-1.39-.189-1.989H12.24z"/>
              </svg>
              QQ
            </button>
          </div>
        </div>
      </form>

      <!-- 底部链接 -->
      <div class="p-6 pt-0 text-center">
        <p class="text-sm text-gray-500">
          {{ isLoginMode ? '还没有账号？' : '已有账号？' }}
          <button
            @click="switchMode"
            class="text-red-500 hover:text-red-600 font-medium"
          >
            {{ isLoginMode ? '立即注册' : '立即登录' }}
          </button>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { post, get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { LoginRequest, RegisterRequest } from '@/types'

interface Props {
  close?: () => void
}

const emit = defineEmits<{
  close: []
}>()

const authStore = useAuthStore()
const isLoading = ref(false)
const error = ref('')
const activeTab = ref<'login' | 'register'>('login')

// 登录表单
const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive<RegisterRequest & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: ''
})

// 计算是否是登录模式
const isLoginMode = computed(() => activeTab.value === 'login')

// 监听标签切换，清空错误
watch(activeTab, () => {
  error.value = ''
})

// 切换模式
const switchMode = () => {
  activeTab.value = activeTab.value === 'login' ? 'register' : 'login'
  error.value = ''
}

// 处理登录
const handleLogin = async () => {
  try {
    isLoading.value = true
    error.value = ''

    // 使用 URLSearchParams 发送 form-data 格式
    const params = new URLSearchParams()
    params.append('username', loginForm.username)
    params.append('password', loginForm.password)

    const response = await post('/user/login', params)

    if (response.code === 1) {
      // 存储 token
      authStore.setToken(response.data)

      // 获取用户信息
      const userResponse = await get('/user/info/detail')
      if (userResponse.code === 1) {
        authStore.setUserInfo(userResponse.data)
        ElMessage.success('登录成功！')
        emit('close')
        setTimeout(() => {
          window.location.reload()
        }, 500)
      } else {
        ElMessage.error('获取用户信息失败')
      }
    } else {
      error.value = response.msg || '登录失败'
    }
  } catch (err: any) {
    error.value = err.message || '网络错误，请稍后重试'
  } finally {
    isLoading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  // 密码确认验证
  if (registerForm.password !== registerForm.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  try {
    isLoading.value = true
    error.value = ''

    // 使用 URLSearchParams 发送 form-data 格式
    const params = new URLSearchParams()
    params.append('username', registerForm.username)
    params.append('password', registerForm.password)

    const response = await post('/user/register', params)

    if (response.code === 1) {
      ElMessage.success('注册成功！请登录')
      // 切换到登录标签
      activeTab.value = 'login'
      // 清空表单
      loginForm.username = registerForm.username
      loginForm.password = ''
      error.value = ''
    } else {
      error.value = response.msg || '注册失败'
    }
  } catch (err: any) {
    error.value = err.message || '网络错误，请稍后重试'
  } finally {
    isLoading.value = false
  }
}

// 提交表单
const handleSubmit = () => {
  if (activeTab.value === 'login') {
    handleLogin()
  } else {
    handleRegister()
  }
}
</script>