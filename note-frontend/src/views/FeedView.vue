<template>
  <main class="flex-1 flex flex-col bg-white h-screen overflow-hidden">
    
    <div class="flex justify-center items-center pt-[20px] pb-[10px] w-full z-10 bg-white">
      <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[54px] rounded-full bg-[#F7F7F7] border-none outline-none flex items-center px-4">
        <input type="text" placeholder="搜索小红书" class="flex-1 bg-transparent outline-none text-sm" />
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      </div>
    </div>

    <div ref="scrollContainer" class="flex-1 overflow-y-auto bg-white px-[6px] no-scrollbar pb-[40px]">
      
      <div class="w-full flex justify-center items-center overflow-hidden transition-all duration-300" :class="isRefreshing ? 'h-[60px] opacity-100' : 'h-0 opacity-0'">
        <svg class="animate-spin -ml-1 mr-3 h-6 w-6 text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        <span class="text-sm text-gray-500 font-medium">正在为你推荐...</span>
      </div>

      <div v-if="waterfallColumns.length > 0" class="flex gap-[20px] items-start w-full min-h-[101vh]">
        <div 
          v-for="(colPosts, colIndex) in waterfallColumns" 
          :key="colIndex"
          :ref="(el) => setColumnRef(el, colIndex)"
          class="flex-1 flex flex-col gap-[20px]"
        >
          <PostCard
            v-for="(post, index) in colPosts"
            :key="`post_${post.id}_${index}_${refreshKey}`"
            :post="post"
            :is-liked="likeStore.isPostLiked(post.id)"
            @click="(postObj, rect) => openPostDetail(postObj, rect)"
            @like="handleLike"
            class="w-full animate-fade-in-up"
          />
        </div>
      </div>

      <div class="w-full flex flex-col items-center py-8">
        <div v-if="loading && !isRefreshing" class="flex items-center gap-2 text-gray-400">
          <svg class="animate-spin h-5 w-5 text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
          <span class="text-sm font-medium">加载中...</span>
        </div>
        <div ref="loadMoreTrigger" class="h-10 w-full bg-transparent" style="pointer-events: none;"></div>
      </div>

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
import { ref, onMounted, onUnmounted, inject, watch, nextTick } from 'vue'
import { useLikeStore } from '@/stores/like'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import type { Post, PostListResponse } from '@/types'

// 🌟 修复：获取滚动容器的 DOM 引用
const scrollContainer = ref<HTMLElement | null>(null)

// 列数与容器引用控制
const colCount = ref(4)
const waterfallColumns = ref<Post[][]>([[], [], [], []])
const columnsRef = ref<HTMLElement[]>([]) 

const setColumnRef = (el: any, index: number) => {
  if (el) columnsRef.value[index] = el as HTMLElement
}

// 响应式重排布局
const updateColCount = () => {
  const width = window.innerWidth
  let newColCount = 4
  if (width < 768) newColCount = 2
  else if (width < 1280) newColCount = 3
  else newColCount = 4

  if (waterfallColumns.value.length !== newColCount) {
    colCount.value = newColCount
    reflowWaterfall()
  }
}

// 动态高度贪心分配算法
const layoutPosts = async (items: Post[]) => {
  for (const post of items) {
    await nextTick() 
    
    let minIndex = 0
    let minHeight = columnsRef.value[0]?.offsetHeight || 0
    
    for (let i = 1; i < colCount.value; i++) {
      const h = columnsRef.value[i]?.offsetHeight || 0
      if (h < minHeight) {
        minHeight = h
        minIndex = i
      }
    }
    
    const targetColumn = waterfallColumns.value[minIndex]
    if (targetColumn) {
      targetColumn.push(post)
    }
  }
}

// 重新排版
const reflowWaterfall = async () => {
  waterfallColumns.value = Array.from({ length: colCount.value }, () => [])
  await nextTick()
  if (posts.value.length > 0) {
    await layoutPosts(posts.value)
  }
}

const likeStore = useLikeStore()
const posts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(30)
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
  if (loading.value) return
  loading.value = true
  try {
    const res = await get<PostListResponse>('/post/list/page', { pageNum: currentPage.value, pageSize: pageSize.value })
    if (res.code === 1) {
      const items = res.data.items || []
      
      if (items.length === 0 && posts.value.length === 0) {
        hasMore.value = false
        return
      }

      const randomizedItems = [...items].sort(() => Math.random() - 0.5)

      if (isLoadMore) {
        posts.value.push(...randomizedItems)
        await layoutPosts(randomizedItems)
      } else {
        posts.value = randomizedItems
        waterfallColumns.value = Array.from({ length: colCount.value }, () => [])
        await nextTick()
        await layoutPosts(randomizedItems)
      }
      
      if (items.length < pageSize.value) {
        currentPage.value = 1
        hasMore.value = true 
      } else {
        currentPage.value++
        hasMore.value = true
      }
    }
  } finally { 
    loading.value = false 
  }
}

const handleForceRefresh = async () => {
  // 1. 瞬间秒跳回顶部！(将 behavior 从 'smooth' 改为默认的 'auto')
  if (scrollContainer.value) {
    scrollContainer.value.scrollTo({ top: 0, behavior: 'auto' })
  }

  isRefreshing.value = true
  currentPage.value = 1
  hasMore.value = true

  // 2. 删掉之前为了等滚动条而加的 setTimeout，直接瞬间同步拉取数据！
  try {
    await fetchPosts(false, true)
    refreshKey.value++ // 触发新卡片的渐显动画，掩盖瞬间替换的生硬感
  } finally {
    // 稍微保留 500ms 的动画收尾，防止因为网速太快导致 Loading 圈闪烁一下就没了
    setTimeout(() => { isRefreshing.value = false }, 500)
  }
}

const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return
  observer = new IntersectionObserver((entries) => {
    if (entries[0]?.isIntersecting && hasMore.value && !loading.value) {
      fetchPosts(true)
    }
  }, { 
    rootMargin: '600px',
    threshold: 0.1 
  })
  observer.observe(loadMoreTrigger.value)
}

onMounted(() => {
  updateColCount()
  window.addEventListener('resize', updateColCount)
  fetchPosts()
  setTimeout(() => { setupInfiniteScroll() }, 300)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateColCount)
  if (observer) observer.disconnect()
})

const openPostDetail = (postObj: Post, rect: DOMRect | null) => {
  selectedPost.value = postObj
  triggerRect.value = rect instanceof DOMRect ? rect : null
  showModal.value = true
}

const closePostDetail = () => {
  showModal.value = false
  setTimeout(() => {
    selectedPost.value = null
    triggerRect.value = null
  }, 400)
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)
  
  posts.value.forEach(p => {
    if (p.id === postId) p.likesCount = isLiked ? (p.likesCount || 0) + 1 : Math.max(0, (p.likesCount || 0) - 1)
  })
}

const handleModalLike = (postId: number) => {
  handleLike(postId, !likeStore.isPostLiked(postId))
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

.animate-fade-in-up {
  animation: fadeInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>