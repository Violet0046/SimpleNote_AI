<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 首页内容 -->
    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- 标题 -->
      <h1 class="text-3xl font-bold text-gray-900 mb-8">推荐</h1>

      <!-- 瀑布流容器 -->
      <div
        class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4"
      >
        <!-- PostCard 列表 -->
        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
          :is-liked="likeStore.isPostLiked(post.id)"
          @click="(event, post) => openPostDetail(post, event)"
          @like="handleLike"
          class="mb-4"
        ></PostCard>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2442]"></div>
          <span class="ml-3 text-gray-500">加载中...</span>
        </div>
      </div>

      <!-- 没有更多数据 -->
      <div v-if="!loading && hasMore && posts.length === 0" class="text-center py-12">
        <p class="text-gray-500">暂无内容</p>
      </div>

      <!-- 加载更多触发器 -->
      <div
        v-if="hasMore && !loading"
        ref="loadMoreTrigger"
        class="h-20"
      ></div>
    </div>

    <!-- 帖子详情弹窗 -->
    <PostDetailModal
      v-if="selectedPost"
      :post="selectedPost"
      :visible="showModal"
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

// const authStore = useAuthStore()
const likeStore = useLikeStore()

// 数据相关
const posts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)

// 弹窗相关
const selectedPost = ref<Post | null>(null)
const showModal = ref(false)

// 初始化点赞数据
onMounted(() => {
  likeStore.loadFromLocalStorage()
})

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
        // 追加到现有列表
        posts.value.push(...newPosts)
      } else {
        // 替换列表
        posts.value = newPosts
      }

      // 检查是否还有更多数据
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
  setupInfiniteScroll()
})

// 组件卸载
onUnmounted(() => {
  cleanupInfiniteScroll()
})

// 打开帖子详情
const openPostDetail = (post: Post, _event?: unknown) => {
  selectedPost.value = post
  showModal.value = true
  // 暂时不使用位置信息，因为需要更复杂的动画实现
}

// 关闭弹窗
const closePostDetail = () => {
  showModal.value = false
  selectedPost.value = null
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

// 手动刷新（可以下拉刷新）
const refresh = () => {
  currentPage.value = 1
  hasMore.value = true
  fetchPosts()
}

// 暴露给下拉刷新组件
defineExpose({
  refresh
})
</script>

<style scoped>
/* 瀑布流项目间距优化 */
:deep(.break-ins-avoid) {
  break-inside: avoid;
  margin-bottom: 1rem;
}

/* 图片加载动画 */
:deep(.animate-pulse) {
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