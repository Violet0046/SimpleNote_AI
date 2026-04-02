<template>
  <div class="flex h-screen overflow-hidden bg-white">
    <!-- 全局布局容器 -->
    <div class="mx-auto flex w-full max-w-[2130px] px-[30px] 2xl:px-[210px] h-full">
      <!-- 左侧边栏 -->
      <aside class="w-[320px] flex-shrink-0 h-full flex flex-col bg-white">
        <!-- Logo -->
        <div class="flex items-center justify-center mt-[8px]">
          <svg class="w-8 h-8 text-[#FF2442]" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8-8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
          </svg>
          <span class="ml-2 text-lg font-medium text-[#FF2442]">这不是小红书</span>
        </div>

        <!-- 导航区 -->
        <div class="flex-1 flex flex-col mt-[45px]">
          <!-- 导航项 -->
          <nav class="flex flex-col space-y-[8px]">
            <router-link
              to="/discover"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full bg-[#F7F7F7] text-lg font-medium text-gray-800"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <span>发现</span>
            </router-link>
            <router-link
              to="/live"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
              </svg>
              <span>直播</span>
            </router-link>
            <router-link
              to="/publish"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>发布</span>
            </router-link>
            <router-link
              to="/notifications"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
              <span>通知</span>
            </router-link>
            <router-link
              to="/profile"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span>我</span>
            </router-link>
          </nav>

          <!-- 更多按钮 -->
          <div class="mt-auto mb-[30px]">
            <router-link
              to="/more"
              class="flex items-center justify-center h-[56px] rounded-xl hover:bg-[#F7F7F7] text-gray-700 transition-colors"
            >
              <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
              <span>更多</span>
            </router-link>
          </div>
        </div>
      </aside>

      <!-- 右侧核心区 -->
      <main class="flex-1 ml-[35px] flex flex-col bg-white">
        <!-- 搜索栏 -->
        <div class="flex justify-center mt-[20px]">
          <div class="w-[669px] h-[54px] rounded-full bg-[#F7F7F7] border-none outline-none ring-0 shadow-none focus:ring-0 focus:outline-none flex items-center px-4">
            <input
              type="text"
              placeholder="搜索小红书"
              class="flex-1 bg-transparent outline-none text-sm"
            />
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>

        <!-- 瀑布流内容区 -->
        <div class="flex-1 overflow-y-auto bg-white pt-[56px]">
          <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-[24px] p-4">
            <PostCard
              v-for="post in posts"
              :key="post.id"
              :post="post"
              :is-liked="likeStore.isPostLiked(post.id)"
              @click="(post, triggerRect) => openPostDetail(post, triggerRect)"
              @like="handleLike"
              class="break-inside-avoid"
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
      </main>
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