<template>
  <main class="flex-1 flex flex-col bg-white h-screen overflow-hidden">
    
    <div class="flex justify-center items-center py-[15px] w-full z-10 bg-white">
      <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[50px] rounded-full bg-[#F7F7F7] border-none outline-none flex items-center px-4">
        <input type="text" placeholder="搜索小红书" class="flex-1 bg-transparent outline-none text-sm" />
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto bg-white px-[6px] scroll-smooth no-scrollbar pb-[40px]">
      
      <div class="w-full flex justify-center items-center overflow-hidden transition-all duration-300" :class="isRefreshing ? 'h-[60px] opacity-100' : 'h-0 opacity-0'">
        <svg class="animate-spin -ml-1 mr-3 h-6 w-6 text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span class="text-sm text-gray-500 font-medium">正在刷新...</span>
      </div>

      <div v-if="posts.length > 0" class="flex gap-[20px] items-start w-full">
        
        <div 
          v-for="(colPosts, colIndex) in waterfallColumns" 
          :key="colIndex"
          class="flex-1 flex flex-col gap-[20px]"
        >
          <PostCard
            v-for="post in colPosts"
            :key="`${post.id}-${refreshKey}`"
            :post="post"
            :is-liked="likeStore.isPostLiked(post.id)"
            @click="(postObj, rect) => openPostDetail(postObj, rect)"
            @like="handleLike"
            class="w-full"
          />
        </div>

      </div>

      <div v-if="loading" class="text-center py-10"><span class="text-gray-500">加载中...</span></div>
      <div v-if="hasMore && !loading" ref="loadMoreTrigger" class="h-20"></div>
    </div>

    <PostDetailModal
      :post="selectedPost"
      :visible="showModal"
      :trigger-rect="triggerRect"
      @close="closePostDetail"
      @like-toggle="selectedPost ? handleModalLike(selectedPost.id) : null"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, inject, watch } from 'vue'
import { useLikeStore } from '@/stores/like'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import type { Post, PostListResponse } from '@/types'
const colCount = ref(5)
const updateColCount = () => {
  const width = window.innerWidth
  if (width < 768) colCount.value = 2       // 手机
  else if (width < 1024) colCount.value = 3 // 平板
  else if (width < 1280) colCount.value = 4 // 窄屏电脑
  else colCount.value = 5                   // 宽屏电脑
}
// 我们把 posts 按顺序依次发牌给 5 个列，绝对不会出现中间空洞
const waterfallColumns = computed(() => {
  const cols: Post[][] = Array.from({ length: colCount.value }, () => [])
  posts.value.forEach((post, index) => {
    // 轮询发牌算法：0号帖子给0列，1号给1列，5号又给0列...
    cols[index % colCount.value].push(post)
  })
  return cols
})
const likeStore = useLikeStore()

const posts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)
const isRefreshing = ref(false)
const refreshKey = ref(0)

const selectedPost = ref<Post | null>(null)
const showModal = ref(false)
const triggerRect = ref<DOMRect | null>(null)
const loadMoreTrigger = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

const refreshDiscoverTrigger = inject('refreshDiscoverTrigger', ref(0))
watch(refreshDiscoverTrigger, (newVal) => {
  if (newVal > 0) handleForceRefresh()
})

const fetchPosts = async (isLoadMore = false, isForceRefresh = false) => {
  if (loading.value || (!isLoadMore && !isForceRefresh && posts.value.length > 0)) return
  loading.value = true
  try {
    const res = await get<PostListResponse>('/post/list/page', { pageNum: currentPage.value, pageSize: pageSize.value })
    if (res.code === 1) {
      if (isLoadMore) posts.value.push(...res.data.items)
      else posts.value = res.data.items
      hasMore.value = res.data.items.length === pageSize.value
      currentPage.value++
    }
  } finally { loading.value = false }
}

const handleForceRefresh = async () => {
  isRefreshing.value = true
  currentPage.value = 1
  try {
    await fetchPosts(false, true)
    refreshKey.value++
  } finally {
    setTimeout(() => { isRefreshing.value = false }, 500)
  }
}

const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return
  observer = new IntersectionObserver((entries) => {
    if (entries[0]?.isIntersecting && hasMore.value && !loading.value) fetchPosts(true)
  }, { rootMargin: '100px', threshold: 0.1 })
  observer.observe(loadMoreTrigger.value)
}

onMounted(() => {
  updateColCount()
  window.addEventListener('resize', updateColCount)
  fetchPosts()
  setupInfiniteScroll()
})
onUnmounted(() => {
  window.removeEventListener('resize', updateColCount)
  if (observer) observer.disconnect()
})

// 🌟 弹窗开关核心逻辑 (加入了安全校验和延迟卸载)
const openPostDetail = (postObj: Post, rect: DOMRect | null) => {
  selectedPost.value = postObj
  triggerRect.value = rect instanceof DOMRect ? rect : null
  showModal.value = true
}

const closePostDetail = () => {
  showModal.value = false
  // 🌟 等待 400ms，让弹窗的缩小动画播完，再彻底清空数据销毁组件！
  setTimeout(() => {
    selectedPost.value = null
    triggerRect.value = null
  }, 400)
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)
  const targetPost = posts.value.find(p => p.id === postId)
  if (targetPost) targetPost.likesCount = isLiked ? (targetPost.likesCount || 0) + 1 : Math.max(0, (targetPost.likesCount || 0) - 1)
}

const handleModalLike = (postId: number) => {
  handleLike(postId, !likeStore.isPostLiked(postId))
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
</style>