<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 用户信息头部 -->
    <div class="bg-white">
      <div class="max-w-4xl mx-auto px-4 py-8">
        <div class="flex items-center space-x-6">
          <!-- 用户头像 -->
          <div class="w-24 h-24 rounded-full overflow-hidden bg-gray-200">
            <img
              v-if="authStore.getUserAvatar"
              :src="authStore.getUserAvatar"
              alt="用户头像"
              class="w-full h-full object-cover"
            />
            <span
              v-else
              class="w-full h-full flex items-center justify-center text-gray-600 text-2xl font-bold"
            >
              {{ authStore.getUsername?.charAt(0).toUpperCase() }}
            </span>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1">
            <h1 class="text-2xl font-bold text-gray-900">{{ authStore.getUsername }}</h1>
            <p class="text-gray-500 mt-1">{{ userInfo.intro || '这个人很懒，什么都没写~' }}</p>

            <!-- 统计信息 -->
            <div class="flex items-center space-x-6 mt-4">
              <div class="text-center">
                <p class="text-2xl font-bold text-gray-900">{{ userInfo.followingCount || 0 }}</p>
                <p class="text-sm text-gray-500">关注</p>
              </div>
              <div class="text-center">
                <p class="text-2xl font-bold text-gray-900">{{ userInfo.followersCount || 0 }}</p>
                <p class="text-sm text-gray-500">粉丝</p>
              </div>
              <div class="text-center">
                <p class="text-2xl font-bold text-gray-900">{{ userInfo.likesCount || 0 }}</p>
                <p class="text-sm text-gray-500">获赞</p>
              </div>
            </div>
          </div>

          <!-- 编辑资料按钮 -->
          <button
            class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
          >
            编辑资料
          </button>
        </div>

        <!-- 位置信息 -->
        <div class="mt-4 text-sm text-gray-500">
          <span class="inline-flex items-center">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            {{ userInfo.ipLocation || '未知' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 用户内容 -->
    <div class="max-w-4xl mx-auto px-4 py-8">
      <!-- 标签切换 -->
      <div class="flex border-b border-gray-200 mb-6">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="activeTab = tab.key"
          :class="[
            'px-4 py-2 font-medium text-sm transition-colors relative',
            activeTab === tab.key
              ? 'text-red-500'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          {{ tab.name }}
          <div
            v-if="activeTab === tab.key"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-500"
          ></div>
        </button>
      </div>

      <!-- 内容区域 -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2442]"></div>
          <span class="ml-3 text-gray-500">加载中...</span>
        </div>
      </div>

      <div
        v-else-if="userPosts.length > 0"
        class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4"
      >
        <PostCard
          v-for="post in userPosts"
          :key="post.id"
          :post="post"
          class="mb-4"
        />
      </div>

      <div v-else class="text-center py-12">
        <p class="text-gray-500">暂无内容</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import type { Post, PostListResponse, PaginationParams, UserInfo } from '@/types'

const authStore = useAuthStore()

// 用户信息
const userInfo = ref<UserInfo>({
  id: 0,
  nickname: '',
  avatar: '',
  intro: '',
  gender: 0,
  ipLocation: '',
  followingCount: 0,
  followersCount: 0,
  likesCount: 0
})

// 标签页
const tabs = [
  { key: 'posts', name: '笔记' },
  { key: 'liked', name: '赞过' }
]
const activeTab = ref('posts')

// 用户帖子列表
const userPosts = ref<Post[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const hasMore = ref(true)

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await get<UserInfo>('/user/info/detail')
    if (response.code === 1) {
      userInfo.value = response.data
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error: any) {
    ElMessage.error('获取用户信息失败，请检查网络连接')
  }
}

// 获取用户帖子
const fetchUserPosts = async () => {
  if (loading.value || !hasMore.value) return

  loading.value = true

  try {
    const params: PaginationParams = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    let response

    if (activeTab.value === 'posts') {
      response = await get<PostListResponse>('/post/list/own', params)
    } else {
      response = await get<PostListResponse>('/post/list/liked', params)
    }

    if (response.code === 1) {
      const newPosts = response.data.items

      if (currentPage.value === 1) {
        userPosts.value = newPosts
      } else {
        userPosts.value.push(...newPosts)
      }

      hasMore.value = newPosts.length === pageSize.value
      currentPage.value++
    } else {
      ElMessage.error(response.msg || '获取帖子列表失败')
    }
  } catch (error: any) {
    console.error('获取帖子列表失败:', error)
    ElMessage.error('获取帖子列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 切换标签页
const switchTab = () => {
  currentPage.value = 1
  hasMore.value = true
  userPosts.value = []
  fetchUserPosts()
}

// 监听标签切换
const currentTab = computed(() => activeTab.value)
watch(currentTab, () => {
  switchTab()
})

// 组件挂载
onMounted(() => {
  fetchUserInfo()
  fetchUserPosts()
})
</script>