<script setup lang="ts">
import { RouterView } from 'vue-router'
import { useUserStore } from './stores/user'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()
const showLoginDialog = ref(false)
const showPostDialog = ref(false)

// 监听登录事件
onMounted(() => {
  window.addEventListener('show-login', () => {
    showLoginDialog.value = true
  })
})

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loginFormRef = ref()

// 发布表单
const postForm = reactive({
  title: '',
  content: ''
})

const postRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const postFormRef = ref()

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()

    const formData = new URLSearchParams()
    formData.append('username', loginForm.username)
    formData.append('password', loginForm.password)

    const response = await axios.post('http://localhost:8080/user/login', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })

    if (response.data.code === 1) {
      userStore.setToken(response.data.data)
      ElMessage.success('登录成功！')
      showLoginDialog.value = false
      loginForm.username = ''
      loginForm.password = ''
    } else {
      ElMessage.error(response.data.msg || '登录失败')
    }
  } catch (error: any) {
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('登录失败，请检查网络连接')
    }
  }
}

// 发布处理
const handlePublish = async () => {
  if (!postFormRef.value) return

  try {
    await postFormRef.value.validate()

    const response = await axios.post('http://localhost:8080/post/add', {
      title: postForm.title,
      content: postForm.content,
      images: ''
    }, {
      headers: {
        'Authorization': userStore.token,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.code === 1) {
      ElMessage.success('发布成功！')
      showPostDialog.value = false
      postForm.title = ''
      postForm.content = ''
    } else {
      ElMessage.error(response.data.msg || '发布失败')
    }
  } catch (error: any) {
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('发布失败，请检查网络连接')
    }
  }
}
</script>

<template>
  <!-- 顶部导航栏 -->
  <nav class="fixed top-0 left-0 right-0 h-16 bg-white shadow-sm/50 backdrop-blur-sm z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
      <div class="flex items-center justify-between h-full">
        <!-- 左侧 Logo -->
        <div class="flex-shrink-0">
          <h1 class="text-2xl font-bold text-gray-900">SimpleNote</h1>
        </div>

        <!-- 中间搜索框 -->
        <div class="flex-1 max-w-2xl mx-8">
          <div class="relative">
            <input
              type="text"
              placeholder="搜索笔记..."
              class="w-full px-4 py-2.5 bg-gray-100 text-gray-900 placeholder-gray-500 rounded-full focus:outline-none focus:ring-2 focus:ring-[#ff2442] focus:bg-white transition-all duration-300"
            />
            <svg
              class="absolute right-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
        </div>

        <!-- 右侧按钮 -->
        <div class="flex items-center space-x-4">
          <template v-if="!userStore.isLoggedIn">
            <button
              @click="showLoginDialog = true"
              class="px-6 py-2.5 text-gray-900 hover:bg-gray-100 rounded-full transition-all duration-300"
            >
              登录
            </button>
          </template>

          <template v-else>
            <button
              @click="showPostDialog = true"
              class="px-6 py-2.5 bg-[#ff2442] text-white hover:bg-red-500 rounded-full transition-all duration-300 shadow-sm"
            >
              发布笔记
            </button>
            <button
              @click="userStore.logout()"
              class="px-6 py-2.5 text-gray-900 hover:bg-gray-100 rounded-full transition-all duration-300"
            >
              退出登录
            </button>
            <div class="w-8 h-8 bg-gray-300 rounded-full"></div>
          </template>
        </div>
      </div>
    </div>
  </nav>

  <!-- 登录弹窗 -->
  <el-dialog
    v-model="showLoginDialog"
    title="用户登录"
    width="400px"
    center
    :close-on-click-modal="false"
    class="rounded-2xl"
  >
    <div class="space-y-4">
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            class="rounded-lg"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            class="rounded-lg"
            show-password
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button
          @click="handleLogin"
          type="primary"
          class="w-full bg-[#ff2442] hover:bg-red-500 rounded-lg transition-all duration-300"
        >
          登录
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 发布弹窗 -->
  <el-dialog
    v-model="showPostDialog"
    title="发布笔记"
    width="500px"
    center
    :close-on-click-modal="false"
    class="rounded-2xl"
  >
    <div class="space-y-4">
      <el-form :model="postForm" :rules="postRules" ref="postFormRef" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="postForm.title"
            placeholder="请输入笔记标题"
            class="rounded-lg"
          />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入笔记内容"
            class="rounded-lg"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button
          @click="handlePublish"
          type="primary"
          class="w-full bg-[#ff2442] hover:bg-red-500 rounded-lg transition-all duration-300"
        >
          发布
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 主内容区域 -->
  <main class="pt-16 min-h-screen">
    <RouterView />
  </main>
</template>

<style scoped>
/* 全局样式已移至 main.css */
</style>
