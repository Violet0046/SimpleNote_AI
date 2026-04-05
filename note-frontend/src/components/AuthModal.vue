<template>
  <div class="fixed inset-0 z-[99999] flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
    <div class="no-scrollbar max-h-[90vh] w-full max-w-md overflow-y-auto rounded-2xl bg-white shadow-2xl">
      <div class="sticky top-0 z-10 flex items-center justify-between border-b border-gray-100 bg-white p-6">
        <div class="flex items-center space-x-2">
          <div class="flex h-8 w-8 items-center justify-center rounded-full bg-red-500">
            <span class="text-sm font-bold text-white">X</span>
          </div>
          <h2 class="text-2xl font-bold text-gray-900">简单笔记</h2>
        </div>
        <button class="text-gray-400 transition-colors hover:text-gray-600" @click="$emit('close')">
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <div class="flex border-b border-gray-100">
        <button
          :class="[
            'relative flex-1 py-4 text-center font-medium transition-colors',
            activeTab === 'login' ? 'text-red-500' : 'text-gray-500 hover:text-gray-700',
          ]"
          @click="activeTab = 'login'"
        >
          {{ isLoginMode ? '登录' : '密码登录' }}
          <div v-if="activeTab === 'login'" class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-500"></div>
        </button>
        <button
          :class="[
            'relative flex-1 py-4 text-center font-medium transition-colors',
            activeTab === 'register' ? 'text-red-500' : 'text-gray-500 hover:text-gray-700',
          ]"
          @click="activeTab = 'register'"
        >
          {{ isLoginMode ? '注册' : '创建账号' }}
          <div v-if="activeTab === 'register'" class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-500"></div>
        </button>
      </div>

      <form class="p-6" @submit.prevent="handleSubmit">
        <div class="space-y-4">
          <div v-if="activeTab === 'login'" class="space-y-4">
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">用户名</label>
              <input
                v-model="loginForm.username"
                type="text"
                required
                class="w-full rounded-lg border border-gray-300 px-4 py-2.5 transition-colors focus:border-transparent focus:outline-none focus:ring-2 focus:ring-red-500"
                placeholder="请输入用户名"
              />
            </div>
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">密码</label>
              <input
                v-model="loginForm.password"
                type="password"
                required
                class="w-full rounded-lg border border-gray-300 px-4 py-2.5 transition-colors focus:border-transparent focus:outline-none focus:ring-2 focus:ring-red-500"
                placeholder="请输入密码"
              />
            </div>
          </div>

          <div v-else class="space-y-4">
            <div class="mb-2 flex flex-col items-center justify-center">
              <div class="group relative cursor-pointer" @click="triggerFileInput">
                <div class="flex h-20 w-20 items-center justify-center overflow-hidden rounded-full border-2 border-gray-100 bg-gray-50 shadow-sm transition-all group-hover:border-red-400 group-hover:shadow-md">
                  <img v-if="registerForm.avatarUrl" :src="registerForm.avatarUrl" class="h-full w-full object-cover" />
                  <div v-else class="flex flex-col items-center text-gray-400">
                    <svg class="mb-1 h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                    </svg>
                    <span class="text-[10px]">上传头像</span>
                  </div>
                </div>
                <div class="absolute inset-0 flex items-center justify-center rounded-full bg-black/30 opacity-0 backdrop-blur-[2px] transition-opacity group-hover:opacity-100">
                  <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  </svg>
                </div>
                <input ref="fileInput" type="file" class="hidden" accept="image/jpeg,image/png,image/gif,image/webp" @change="handleFileUpload" />
              </div>
              <span class="mt-2 text-[11px] text-gray-400">不上传将随机分配默认头像</span>
            </div>

            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">昵称（可选）</label>
              <input
                v-model="registerForm.nickname"
                type="text"
                class="w-full rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-2.5 text-[14px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
                placeholder="留空将自动生成"
              />
            </div>

            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">性别</label>
              <div class="mt-1 flex gap-3">
                <button
                  type="button"
                  class="flex-1 rounded-xl py-2 text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 1 ? 'bg-[#EBF3FF] text-[#1E90FF] ring-1 ring-[#1E90FF]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                  @click="registerForm.gender = 1"
                >
                  男
                </button>
                <button
                  type="button"
                  class="flex-1 rounded-xl py-2 text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 0 ? 'bg-[#FFECF0] text-[#FF4D85] ring-1 ring-[#FF4D85]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                  @click="registerForm.gender = 0"
                >
                  女
                </button>
                <button
                  type="button"
                  class="flex-1 rounded-xl py-2 text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 2 ? 'bg-gray-800 text-white ring-1 ring-gray-800' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                  @click="registerForm.gender = 2"
                >
                  保密
                </button>
              </div>
            </div>

            <div class="my-4 h-px bg-gray-100"></div>

            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">用户名</label>
              <input
                v-model="registerForm.username"
                type="text"
                required
                class="w-full rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-2.5 text-[14px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
                placeholder="用于登录的账号"
              />
            </div>
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">密码</label>
              <input
                v-model="registerForm.password"
                type="password"
                required
                class="w-full rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-2.5 text-[14px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
                placeholder="请输入密码"
              />
            </div>
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-700">确认密码</label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                required
                class="w-full rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-2.5 text-[14px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
                placeholder="请再次输入密码"
              />
            </div>
          </div>

          <div v-if="error" class="rounded-lg border border-red-200 bg-red-50 p-3">
            <p class="text-sm text-red-600">{{ error }}</p>
          </div>

          <button
            type="submit"
            :disabled="isLoading"
            class="mt-2 w-full rounded-lg bg-red-500 py-3 font-medium text-white transition-colors hover:bg-red-600 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {{ isLoading ? (activeTab === 'login' ? '登录中...' : '注册中...') : (activeTab === 'login' ? '登录' : '注册') }}
          </button>
        </div>
      </form>

      <div class="mt-2 border-t border-gray-50 p-6 pt-0 text-center">
        <p class="mt-4 text-sm text-gray-500">
          {{ isLoginMode ? '还没有账号？' : '已有账号？' }}
          <button class="font-medium text-red-500 hover:text-red-600" @click="switchMode">
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
import { uploadFile } from '@/modules/upload/upload.api'
import { post, get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { LoginRequest, RegisterRequest, UserInfo } from '@/types'

const emit = defineEmits<{ close: [] }>()
const authStore = useAuthStore()

const isLoading = ref(false)
const error = ref('')
const activeTab = ref<'login' | 'register'>('login')

const loginForm = reactive<LoginRequest>({
  username: '',
  password: '',
})

const registerForm = reactive<
  RegisterRequest & { confirmPassword: string; nickname: string; gender: number; avatarUrl: string }
>({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  gender: 2,
  avatarUrl: '',
})

const fileInput = ref<HTMLInputElement | null>(null)

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.size > 10 * 1024 * 1024) {
    error.value = '图片大小不能超过 10MB'
    return
  }

  try {
    isLoading.value = true
    error.value = ''

    registerForm.avatarUrl = await uploadFile(file)
    ElMessage.success('头像上传成功')
  } catch (err: any) {
    error.value = '图片上传失败，请检查网络或服务端配置'
  } finally {
    isLoading.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}

const isLoginMode = computed(() => activeTab.value === 'login')

watch(activeTab, () => {
  error.value = ''
})

const switchMode = () => {
  activeTab.value = activeTab.value === 'login' ? 'register' : 'login'
  error.value = ''
}

const handleLogin = async () => {
  try {
    isLoading.value = true
    error.value = ''

    const params = new URLSearchParams()
    params.append('username', loginForm.username)
    params.append('password', loginForm.password)

    const response = await post<string>('/user/login', params)

    if (response.code === 1) {
      authStore.setToken(response.data)

      const userResponse = await get<UserInfo>('/user/me')

      if (userResponse.code === 1) {
        authStore.setUserInfo(userResponse.data)
        ElMessage.success('登录成功')
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

const handleRegister = async () => {
  if (registerForm.password !== registerForm.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  try {
    isLoading.value = true
    error.value = ''

    const params = new URLSearchParams()
    params.append('username', registerForm.username)
    params.append('password', registerForm.password)
    params.append('gender', registerForm.gender.toString())
    if (registerForm.nickname) params.append('nickname', registerForm.nickname)
    if (registerForm.avatarUrl) params.append('avatarUrl', registerForm.avatarUrl)

    const response = await post('/user/register', params)

    if (response.code === 1) {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
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

const handleSubmit = () => {
  if (activeTab.value === 'login') {
    void handleLogin()
  } else {
    void handleRegister()
  }
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
