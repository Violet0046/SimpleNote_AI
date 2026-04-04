<template>
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md transform transition-all duration-300 scale-100 opacity-100 max-h-[90vh] overflow-y-auto no-scrollbar">
      <div class="flex items-center justify-between p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
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

      <form @submit.prevent="handleSubmit" class="p-6">
        <div class="space-y-4">
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

          <div v-else class="space-y-4">
            
            <div class="flex flex-col items-center justify-center mb-2">
              <div class="relative group cursor-pointer" @click="triggerFileInput">
                <div class="w-20 h-20 rounded-full overflow-hidden border-2 border-gray-100 shadow-sm bg-gray-50 flex items-center justify-center transition-all group-hover:border-red-400 group-hover:shadow-md">
                  <img v-if="registerForm.avatarUrl" :src="registerForm.avatarUrl" class="w-full h-full object-cover" />
                  <div v-else class="flex flex-col items-center text-gray-400">
                    <svg class="w-6 h-6 mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
                    <span class="text-[10px]">传头像</span>
                  </div>
                </div>
                <div class="absolute inset-0 bg-black/30 backdrop-blur-[2px] flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity rounded-full">
                  <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                </div>
                <input type="file" ref="fileInput" class="hidden" accept="image/jpeg,image/png,image/gif,image/webp" @change="handleFileUpload" />
              </div>
              <span class="text-[11px] text-gray-400 mt-2">不上传将随机分配默认头像</span>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">昵称 (选填)</label>
              <input
                v-model="registerForm.nickname"
                type="text"
                class="w-full px-4 py-2.5 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[14px] text-gray-800 placeholder-gray-400"
                placeholder="如果不填，将自动生成"
              >
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">性别</label>
              <div class="flex gap-3 mt-1">
                <button 
                  type="button"
                  @click="registerForm.gender = 1"
                  class="flex-1 py-2 rounded-xl text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 1 ? 'bg-[#EBF3FF] text-[#1E90FF] ring-1 ring-[#1E90FF]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                >
                  ♂ 男生
                </button>
                <button 
                  type="button"
                  @click="registerForm.gender = 2"
                  class="flex-1 py-2 rounded-xl text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 2 ? 'bg-[#FFECF0] text-[#FF4D85] ring-1 ring-[#FF4D85]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                >
                  ♀ 女生
                </button>
                <button 
                  type="button"
                  @click="registerForm.gender = 0"
                  class="flex-1 py-2 rounded-xl text-[13px] font-medium transition-all duration-200"
                  :class="registerForm.gender === 0 ? 'bg-gray-800 text-white ring-1 ring-gray-800' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                >
                  保密
                </button>
              </div>
            </div>

            <div class="h-px bg-gray-100 my-4"></div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">账号 (必填)</label>
              <input
                v-model="registerForm.username"
                type="text"
                required
                class="w-full px-4 py-2.5 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[14px] text-gray-800 placeholder-gray-400"
                placeholder="用于登录的账号"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
              <input
                v-model="registerForm.password"
                type="password"
                required
                class="w-full px-4 py-2.5 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[14px] text-gray-800 placeholder-gray-400"
                placeholder="请输入密码"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                required
                class="w-full px-4 py-2.5 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[14px] text-gray-800 placeholder-gray-400"
                placeholder="请再次输入密码"
              >
            </div>
          </div>

          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-sm text-red-600">{{ error }}</p>
          </div>

          <button
            type="submit"
            :disabled="isLoading"
            class="w-full py-3 bg-red-500 text-white font-medium rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors mt-2"
          >
            {{ isLoading ? (activeTab === 'login' ? '登录中...' : '注册中...') : (activeTab === 'login' ? '登录' : '注册') }}
          </button>
        </div>
      </form>

      <div class="p-6 pt-0 text-center border-t border-gray-50 mt-2">
        <p class="text-sm text-gray-500 mt-4">
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

const emit = defineEmits<{ close: [] }>()
const authStore = useAuthStore()

const isLoading = ref(false)
const error = ref('')
const activeTab = ref<'login' | 'register'>('login')

// 登录表单
const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

// 注册表单 (新增了三个字段)
const registerForm = reactive<RegisterRequest & { confirmPassword: string; nickname: string; gender: number; avatarUrl: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  gender: 0,
  avatarUrl: ''
})

// =============== 🌟 头像上传逻辑 ===============
const fileInput = ref<HTMLInputElement | null>(null)

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 简单验证大小 (最大 10MB)
  if (file.size > 10 * 1024 * 1024) {
    error.value = '图片大小不能超过 10MB'
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    isLoading.value = true
    error.value = ''
    
    // 调用我们后端写好的 FileUploadController
    const response = await post<string>('/upload', formData)

    if (response.code === 1) {
      // 拿到后端返回的图片网络地址，赋值给表单，这样页面上就会显示预览图！
      registerForm.avatarUrl = response.data
      ElMessage.success('头像上传成功')
    } else {
      error.value = response.msg || '头像上传失败'
    }
  } catch (err: any) {
    error.value = '图片上传失败，请检查网络或后端配置'
  } finally {
    isLoading.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}
// ==============================================

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

    const response = await post('/user/login', params)

    if (response.code === 1) {
      authStore.setToken(response.data)

      // 🌟 核心 Bug 修复地！将那个旧的 /user/info/detail 彻底改为了 /user/me
      const userResponse = await get('/user/me')
      
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

const handleRegister = async () => {
  if (registerForm.password !== registerForm.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  try {
    isLoading.value = true
    error.value = ''

    // 将所有新加入的字段（性别、昵称、头像）一起打包发给后端
    const params = new URLSearchParams()
    params.append('username', registerForm.username)
    params.append('password', registerForm.password)
    params.append('gender', registerForm.gender.toString())
    if (registerForm.nickname) params.append('nickname', registerForm.nickname)
    if (registerForm.avatarUrl) params.append('avatarUrl', registerForm.avatarUrl)

    const response = await post('/user/register', params)

    if (response.code === 1) {
      ElMessage.success('注册成功！请登录')
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
    handleLogin()
  } else {
    handleRegister()
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