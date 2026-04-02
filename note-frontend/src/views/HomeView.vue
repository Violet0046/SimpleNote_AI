<template>
  <div class="flex h-screen bg-white">
    <!-- 左侧导航栏 -->
    <div class="w-52 bg-white border-r border-gray-100 flex flex-col">
      <div class="flex-1">
        <nav class="flex flex-col">
          <router-link
            to="/discover"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
            active-class="bg-gray-50 text-black"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            发现
          </router-link>
          <router-link
            to="/live"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
            </svg>
            直播
          </router-link>
          <router-link
            to="/publish"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            发布
          </router-link>
          <router-link
            to="/notifications"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            通知
          </router-link>
          <router-link
            to="/profile"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            我
          </router-link>
          <router-link
            to="/more"
            class="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
            更多
          </router-link>
        </nav>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col">
      <!-- 顶部导航栏 -->
      <header class="bg-white border-b border-gray-100 px-6 py-3">
        <div class="flex items-center justify-between">
          <!-- 左侧logo -->
          <div class="flex items-center">
            <svg class="w-8 h-8 text-[#FF2442]" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
            </svg>
            <span class="ml-2 text-lg font-medium text-[#FF2442]">小红书</span>
          </div>

          <!-- 中间搜索框 -->
          <div class="flex-1 max-w-md mx-8">
            <div class="relative">
              <input
                type="text"
                placeholder="搜索小红书"
                class="w-full px-4 py-2 pl-10 pr-4 bg-gray-50 border border-gray-200 rounded-full text-sm focus:outline-none focus:border-[#FF2442] focus:bg-white"
              />
              <svg class="absolute left-3 top-2.5 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- 右侧链接 -->
          <div class="flex items-center space-x-6">
            <router-link
              to="/creator"
              class="text-sm text-gray-600 hover:text-[#FF2442] transition-colors"
            >
              创作中心
            </router-link>
            <router-link
              to="/business"
              class="text-sm text-gray-600 hover:text-[#FF2442] transition-colors"
            >
              业务合作
            </router-link>
          </div>
        </div>
      </header>

      <!-- 分类标签栏 -->
      <div class="bg-white border-b border-gray-100 px-6 py-3">
        <div class="flex items-center space-x-6 overflow-x-auto">
          <router-link
            to="/discover"
            class="text-sm font-medium text-black whitespace-nowrap"
            active-class="text-black font-medium"
          >
            推荐
          </router-link>
          <router-link
            to="/fashion"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            穿搭
          </router-link>
          <router-link
            to="/food"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            美食
          </router-link>
          <router-link
            to="/makeup"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            彩妆
          </router-link>
          <router-link
            to="/movie"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            影视
          </router-link>
          <router-link
            to="/career"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            职场
          </router-link>
          <router-link
            to="/emotion"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            情感
          </router-link>
          <router-link
            to="/home"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            家居
          </router-link>
          <router-link
            to="/game"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            游戏
          </router-link>
          <router-link
            to="/travel"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            旅行
          </router-link>
          <router-link
            to="/fitness"
            class="text-sm text-gray-600 whitespace-nowrap hover:text-gray-800 transition-colors"
          >
            健身
          </router-link>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="flex-1 flex overflow-hidden">
        <!-- 瀑布流内容区 -->
        <div class="flex-1 overflow-y-auto px-4 py-4">
          <div class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4">
            <PostCard
              v-for="post in posts"
              :key="post.id"
              :post="post"
              :is-liked="likeStore.isPostLiked(post.id)"
              @click="(post, triggerRect) => openPostDetail(post, triggerRect)"
              @like="handleLike"
              class="mb-4 break-inside-avoid"
            ></PostCard>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="text-center py-12">
            <div class="inline-flex items-center">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2442]"></div>
              <span class="ml-3 text-gray-500">加载中...</span>
            </div>
          </div>

          <!-- 加载更多触发器 -->
          <div
            v-if="hasMore && !loading"
            ref="loadMoreTrigger"
            class="h-20"
          ></div>
        </div>

        <!-- 右侧推荐栏 -->
        <div class="w-80 bg-white border-l border-gray-100 p-4 overflow-y-auto">
          <h3 class="text-sm font-medium text-gray-900 mb-4">推荐关注</h3>
          <div class="space-y-4">
            <div
              v-for="recommend in recommendations"
              :key="recommend.id"
              class="bg-white rounded-lg border border-gray-100 overflow-hidden hover:shadow-md transition-shadow cursor-pointer"
              @click="openPostDetail(recommend, null)"
            >
              <div class="relative aspect-[3/4] overflow-hidden">
                <img
                  :src="recommend.images"
                  :alt="recommend.title"
                  class="w-full h-full object-cover"
                  loading="lazy"
                />
                <div
                  v-if="recommend.isVideo"
                  class="absolute top-2 right-2 w-8 h-8 bg-black bg-opacity-50 rounded-full flex items-center justify-center"
                >
                  <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M8 5v14l11-7z"/>
                  </svg>
                </div>
              </div>
              <div class="p-3">
                <h4 class="text-sm font-medium text-gray-900 mb-1 line-clamp-2">{{ recommend.title }}</h4>
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
                    <div class="w-6 h-6 rounded-full overflow-hidden bg-gray-300">
                      <img
                        v-if="recommend.authorAvatar"
                        :src="recommend.authorAvatar"
                        :alt="recommend.authorName"
                        class="w-full h-full object-cover"
                      />
                    </div>
                    <span class="text-xs text-gray-500">{{ recommend.authorName }}</span>
                  </div>
                  <div class="flex items-center space-x-1 text-xs text-gray-500">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                    <span>{{ formatLikeCount(recommend.likesCount) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 帖子详情弹窗 -->
    <PostDetailModal
      v-if="selectedPost"
      :post="selectedPost"
      :visible="showModal"
      :trigger-rect="triggerRect"
      @close="closePostDetail"
      @like-toggle="handleModalLike(selectedPost.id)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import type { Post, PostListResponse, PaginationParams } from '@/types'

const likeStore = useLikeStore()

// 数据相关
const posts = ref<Post[]>([])
const recommendations = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)

// 弹窗相关
const selectedPost = ref<Post | null>(null)
const showModal = ref(false)
const triggerRect = ref<DOMRect | null>(null)

// Intersection Observer 用于无限滚动
const loadMoreTrigger = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

// 获取帖子列表
const fetchPosts = async (isLoadMore = false) => {
  if (loading.value || (!isLoadMore && posts.value.length > 0)) return

  loading.value = true

  try {
    const params: PaginationParams = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    const response = await get<PostListResponse>('/post/list/page', params)

    if (response.code === 1) {
      const newPosts = response.data.items

      if (isLoadMore) {
        posts.value.push(...newPosts)
      } else {
        posts.value = newPosts
      }

      hasMore.value = newPosts.length === pageSize.value
      currentPage.value++
    } else {
      ElMessage.error(response.msg || '获取帖子列表失败')
    }
  } catch (error: unknown) {
    console.error('获取帖子列表失败:', error)
    ElMessage.error('获取帖子列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 获取推荐内容
const fetchRecommendations = async () => {
  try {
    const response = await get<Post[]>('/post/recommend')
    if (response.code === 1) {
      recommendations.value = response.data
    }
  } catch (error: unknown) {
    console.error('获取推荐内容失败:', error)
  }
}

// 设置无限滚动
const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return

  observer = new IntersectionObserver(
    (entries) => {
      if (entries.length > 0 && entries[0] && entries[0].isIntersecting && hasMore.value && !loading.value) {
        fetchPosts(true)
      }
    },
    {
      rootMargin: '100px',
      threshold: 0.1
    }
  )

  observer.observe(loadMoreTrigger.value)
}

// 清理无限滚动
const cleanupInfiniteScroll = () => {
  if (observer) {
    observer.disconnect()
    observer = null
  }
}

// 组件挂载
onMounted(() => {
  fetchPosts()
  fetchRecommendations()
  setupInfiniteScroll()
})

// 组件卸载
onUnmounted(() => {
  cleanupInfiniteScroll()
})

// 打开帖子详情
const openPostDetail = (post: Post, triggerRectParam: DOMRect | null) => {
  selectedPost.value = post
  triggerRect.value = triggerRectParam
  showModal.value = true
}

// 关闭弹窗
const closePostDetail = () => {
  showModal.value = false
  selectedPost.value = null
  triggerRect.value = null
}

// 处理点赞
const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) {
    likeStore.addLikedPost(postId)
  } else {
    likeStore.removeLikedPost(postId)
  }
}

// 处理弹窗内的点赞
const handleModalLike = (postId: number) => {
  handleLike(postId, !likeStore.isPostLiked(postId))
}

// 格式化点赞数
const formatLikeCount = (count?: number | null) => {
  if (count == null) return '0'
  const safeCount = Number(count) || 0
  if (safeCount >= 10000) return (safeCount / 10000).toFixed(1) + 'w'
  if (safeCount >= 1000) return (safeCount / 1000).toFixed(1) + 'k'
  return safeCount.toString()
}
</script>

<style>
/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 瀑布流项目间距优化 */
.break-inside-avoid {
  break-inside: avoid;
  margin-bottom: 1rem;
}

/* 图片加载动画 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}
</style>