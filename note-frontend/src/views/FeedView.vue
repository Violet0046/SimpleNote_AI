<template>
  <main class="flex-1 ml-[35px] flex flex-col bg-white h-screen">
    <div class="flex justify-center mt-[20px] relative right-[177px] z-10 bg-white pb-[10px]">
      <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[54px] rounded-full bg-[#F7F7F7] border-none outline-none ring-0 shadow-none flex items-center px-4">
        <input type="text" placeholder="搜索小红书" class="flex-1 bg-transparent outline-none text-sm" />
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto bg-white pt-[20px] relative scroll-smooth no-scrollbar">
      
      <div 
        class="w-full flex justify-center items-center overflow-hidden transition-all duration-300 ease-out"
        :class="isRefreshing ? 'h-[60px] opacity-100 mb-4' : 'h-0 opacity-0 mb-0'"
      >
        <svg class="animate-spin -ml-1 mr-3 h-6 w-6 text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span class="text-sm text-gray-500 font-medium">正在刷新...</span>
      </div>

      <div class="columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-[24px] px-4 w-full">
        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
          :is-liked="likeStore.isPostLiked(post.id)"
          @click="(post, triggerRect) => openPostDetail(post, triggerRect)"
          @like="handleLike"
          class="break-inside-avoid inline-block w-full mb-6"
        />
      </div>

      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2442]"></div>
          <span class="ml-3 text-gray-500">加载中...</span>
        </div>
      </div>

      <div v-if="hasMore && !loading" ref="loadMoreTrigger" class="h-20"></div>
    </div>

    <PostDetailModal
      v-if="selectedPost"
      :post="selectedPost"
      :visible="showModal"
      :trigger-rect="triggerRect"
      @close="closePostDetail"
      @like-toggle="handleModalLike(selectedPost.id)"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useLikeStore } from '@/stores/like'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import type { Post, PostListResponse } from '@/types'

const likeStore = useLikeStore()

// 数据状态
const posts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)
const isRefreshing = ref(false)

// 弹窗状态
const selectedPost = ref<Post | null>(null)
const showModal = ref(false)
const triggerRect = ref<DOMRect | null>(null)

// 触底加载监听器
const loadMoreTrigger = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

// 核心拉取逻辑
const fetchPosts = async (isLoadMore = false, isForceRefresh = false) => {
  if (loading.value || (!isLoadMore && !isForceRefresh && posts.value.length > 0)) return
  loading.value = true

  try {
    const response = await get<PostListResponse>('/post/list/page', {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })

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
  } catch (error) {
    ElMessage.error('获取帖子列表失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 模拟“发现”按钮点击强制刷新（这个方法可以通过全局事件总线/Pinia抛给侧边栏，这里演示自动调用）
const handleForceRefresh = async () => {
  isRefreshing.value = true
  currentPage.value = 1
  try {
    await fetchPosts(false, true)
  } finally {
    setTimeout(() => { isRefreshing.value = false }, 500)
  }
}

// 无限滚动绑定
// 无限滚动绑定
const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return
  
  observer = new IntersectionObserver((entries) => {
    //  使用 ?. 语法：如果 entries[0] 是 undefined，就会直接返回 false，不会报错
    if (entries[0]?.isIntersecting && hasMore.value && !loading.value) {
      fetchPosts(true)
    }
  }, { rootMargin: '100px', threshold: 0.1 })
  
  observer.observe(loadMoreTrigger.value)
}

const cleanupInfiniteScroll = () => {
  if (observer) {
    observer.disconnect()
    observer = null
  }
}

onMounted(() => {
  fetchPosts()
  setupInfiniteScroll()
})

onUnmounted(() => {
  cleanupInfiniteScroll()
})

// 详情与点赞交互
const openPostDetail = (post: Post, triggerRectParam: DOMRect | null) => {
  selectedPost.value = post
  triggerRect.value = triggerRectParam
  showModal.value = true
}

const closePostDetail = () => {
  showModal.value = false
  selectedPost.value = null
  triggerRect.value = null
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)

  const targetPost = posts.value.find(p => p.id === postId)
  if (targetPost) {
    const currentCount = targetPost.likesCount ?? targetPost.likeCount ?? 0
    targetPost.likesCount = isLiked ? currentCount + 1 : Math.max(0, currentCount - 1)
    targetPost.likeCount = targetPost.likesCount
  }
}

const handleModalLike = (postId: number) => {
  handleLike(postId, !likeStore.isPostLiked(postId))
}
</script>

<style scoped>
/* 隐藏局部滚动条辅助类 */
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>