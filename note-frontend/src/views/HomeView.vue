<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

interface Post {
  id: number
  userId: number
  title: string
  content: string
  images: string
  createTime: string
}

const posts = ref<Post[]>([])
const loading = ref(false)
const router = useRouter()

// 获取帖子列表
const fetchPosts = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/post/list')
    if (response.data.code === 1) {
      posts.value = response.data.data
    } else {
      ElMessage.error(response.data.msg || '获取帖子列表失败')
    }
  } catch (error: any) {
    ElMessage.error('获取帖子列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 处理互动
const handleLike = () => {
  const token = localStorage.getItem('userToken')
  if (!token) {
    ElMessage.warning('请先登录后再进行互动')
    // 获取全局变量触发登录弹窗
    const event = new CustomEvent('show-login')
    window.dispatchEvent(event)
    return
  }

  ElMessage.success('互动成功')
}

// 页面加载时获取帖子列表
onMounted(() => {
  fetchPosts()
})
</script>

<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 首页内容 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 欢迎横幅 -->
      <div class="bg-white rounded-2xl shadow-sm p-8 mb-8 transition-all duration-300 hover:shadow-md">
        <h2 class="text-3xl font-bold text-gray-900 mb-4">欢迎来到 SimpleNote</h2>
        <p class="text-gray-600">发现、分享、记录你的精彩笔记</p>
      </div>

      <!-- 瀑布流布局 -->
      <div class="columns-1 sm:columns-2 lg:columns-3 gap-6 space-y-6">
        <div
          v-for="post in posts"
          :key="post.id"
          class="bg-white rounded-2xl shadow-sm p-6 mb-6 break-inside-avoid transition-all duration-300 hover:shadow-lg hover:-translate-y-1 cursor-pointer"
        >
          <!-- 图片区域 -->
          <div class="w-full h-48 bg-gray-200 rounded-xl mb-4 overflow-hidden">
            <img
              v-if="post.images"
              :src="post.images"
              :alt="post.title"
              class="w-full h-full object-cover"
            />
            <div
              v-else
              class="w-full h-full bg-gradient-to-br from-[#ff2442] to-[#ff6b6b] flex items-center justify-center text-white"
            >
              <span class="text-2xl font-bold">图片</span>
            </div>
          </div>

          <!-- 标题 -->
          <h3 class="text-lg font-semibold text-gray-900 mb-2 line-clamp-1">{{ post.title }}</h3>

          <!-- 内容 -->
          <p class="text-gray-600 text-sm mb-4 line-clamp-2">{{ post.content }}</p>

          <!-- 时间和互动 -->
          <div class="flex items-center justify-between">
            <span class="text-xs text-gray-400">{{ new Date(post.createTime).toLocaleDateString() }}</span>
            <div class="flex items-center space-x-3">
              <button
                @click="handleLike"
                class="flex items-center space-x-1 text-gray-500 hover:text-[#ff2442] transition-colors duration-300"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" />
                </svg>
                <span class="text-xs">赞</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="text-center py-8">
          <el-icon class="animate-spin h-8 w-8 text-[#ff2442] mx-auto">
            <svg fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </el-icon>
          <p class="mt-2 text-gray-500">加载中...</p>
        </div>
      </div>
    </div>
  </div>
</template>
