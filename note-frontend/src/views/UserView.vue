<template>
  <main class="flex-1 flex flex-col bg-white h-screen overflow-y-auto scroll-smooth no-scrollbar relative">
    
    <div class="sticky top-0 z-50 bg-white pt-[20px] pb-[10px] flex justify-center w-full">
      <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[54px] rounded-full bg-[#F7F7F7] border-none outline-none ring-0 shadow-none flex items-center px-4">
        <input type="text" placeholder="搜索小红书" class="flex-1 bg-transparent outline-none text-sm" />
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      </div>
    </div>

    <div class="flex flex-col items-center pt-[20px] pb-[40px]">
      <div class="flex items-center gap-[30px] w-full max-w-[800px] px-[40px]">
        <div class="w-[140px] h-[140px] rounded-full overflow-hidden flex-shrink-0 bg-gray-100 border border-gray-200">
          <img :src="userInfo.avatar || 'http://localhost:8080/1.jpg'" class="w-full h-full object-cover" />
        </div>
        <div class="flex flex-col gap-[10px]">
          <div class="flex items-center flex-wrap gap-x-20 gap-y-3 w-full">
            <h1 class="text-[28px] font-semibold text-gray-900 max-w-[280px] truncate" :title="userInfo.nickname">
              {{ userInfo.nickname }}
            </h1>
            
            <button 
              @click="toggleFollow" 
              class="px-6 py-1.5 rounded-full font-semibold text-[14px] transition-colors flex-shrink-0"
              :class="isFollowing ? 'bg-[#F2F2F2] text-gray-500 hover:bg-gray-200' : 'bg-[#FF2442] text-white hover:bg-red-600'"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </button>
          </div>
          
          <div class="flex items-center gap-4 text-[13px] text-gray-500">
            <span>小红书号：{{ userInfo.id }}</span>
            <span v-if="userInfo.ipLocation">IP属地：{{ userInfo.ipLocation }}</span>
            <div v-if="userInfo.gender === 1" class="flex items-center justify-center w-[22px] h-[22px] rounded-full bg-[#EBF3FF] text-[#1E90FF] text-[13px] font-bold">
              ♂
            </div>
            <div v-if="userInfo.gender === 0" class="flex items-center justify-center w-[22px] h-[22px] rounded-full bg-[#FFECF0] text-[#FF4D85] text-[13px] font-bold">
              ♀
            </div>
          </div>
          <p class="text-[14px] text-gray-700 mt-1">{{ userInfo.intro || '还没有简介哦~' }}</p>
          <div class="flex items-center gap-5 mt-2 text-[14px]">
            <div class="cursor-pointer hover:opacity-80"><span class="font-semibold text-gray-900 mr-1">{{ userInfo.followingCount || 0 }}</span><span class="text-gray-500">关注</span></div>
            <div class="cursor-pointer hover:opacity-80"><span class="font-semibold text-gray-900 mr-1">{{ userInfo.followersCount || 0 }}</span><span class="text-gray-500">粉丝</span></div>
            <div class="cursor-pointer hover:opacity-80"><span class="font-semibold text-gray-900 mr-1">{{ userInfo.likesCount || 0 }}</span><span class="text-gray-500">获赞</span></div>
          </div>
        </div>
      </div>
    </div>

    <div class="sticky top-[84px] z-40 bg-white w-full flex justify-center border-b border-gray-100 pt-[10px]">
      <div class="flex gap-12 px-4 h-[48px]">
        <button class="relative h-full px-2 text-[16px] font-semibold text-gray-900 transition-colors">
          笔记
          <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-[3px] bg-[#FF2442] rounded-full"></div>
        </button>
      </div>
    </div>

    <div class="w-full bg-white px-[6px] pt-[20px] pb-[60px]">
      <div v-if="loading" class="flex justify-center mt-10">
        <svg class="animate-spin h-8 w-8 text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
      </div>

      <div v-else-if="userPosts.length > 0" class="flex gap-[20px] items-start w-full">
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

      <div v-else class="flex flex-col items-center justify-center mt-[100px] opacity-70">
        <span class="text-[14px] text-gray-500">TA还没有发布任何内容哦</span>
      </div>
      
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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/PostCard.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import type { Post, UserInfo } from '@/types'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const likeStore = useLikeStore()

// 获取 URL 上的用户 ID
const targetUserId = computed(() => Number(route.params.id))

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

// 关注状态 (目前由前端接管逻辑，默认为 false)
const isFollowing = ref(false)

// 分页与数据状态
const userPosts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)
const refreshKey = ref(0) 

// 弹窗状态
const showModal = ref(false)
const selectedPost = ref<Post | null>(null)
const triggerRect = ref<DOMRect | null>(null)

// 触底加载监听器
const loadMoreTrigger = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

// 瀑布流逻辑
const colCount = ref(5)
const updateColCount = () => {
  const width = window.innerWidth
  if (width < 768) colCount.value = 2
  else if (width < 1024) colCount.value = 3
  else if (width < 1280) colCount.value = 4
  else colCount.value = 5
}

const waterfallColumns = computed(() => {
  const cols: Post[][] = Array.from({ length: colCount.value }, () => [])
  userPosts.value.forEach((post, index) => {
    const colIndex = index % colCount.value
    if (cols[colIndex]) {
      cols[colIndex].push(post)
    }
  })
  return cols
})

// 🌟 1. 获取他人信息
const fetchUserInfo = async () => {
  try {
    // 拦截：如果是自己看自己，跳回自己的主页
    if (authStore.userInfo?.id === targetUserId.value) {
      router.replace('/profile')
      return
    }

    const response = await get<any>(`/user/info/${targetUserId.value}`)
    if (response.code === 1) {
      userInfo.value = response.data
      // 注：因为后端 UserDetailVO 还没包含 isFollowing 字段，这里我们先默认 false。
      // 后续如果后端补上了，可以直接 isFollowing.value = response.data.isFollowing
    } else {
      ElMessage.error('用户不存在')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 🌟 2. 关注/取消关注逻辑
const toggleFollow = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }
  try {
    const res = await post(`/follow/${targetUserId.value}`)
    if (res.code === 1) {
      isFollowing.value = !isFollowing.value
      // 动态修改页面的粉丝数体验更棒
      if (isFollowing.value) {
        userInfo.value.followersCount! += 1
      } else {
        userInfo.value.followersCount! = Math.max(0, userInfo.value.followersCount! - 1)
      }
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 🌟 3. 获取他人的帖子 (请求 /post/list/user 接口)
const fetchUserPosts = async (isLoadMore = false) => {
  if (loading.value || (!isLoadMore && userPosts.value.length > 0)) return
  loading.value = true
  
  try {
    const response = await get<any>('/post/list/user', {
      userId: targetUserId.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.code === 1) {
      const newPosts = response.data.items || response.data || []
      if (isLoadMore) {
        userPosts.value.push(...newPosts)
      } else {
        userPosts.value = newPosts
      }
      hasMore.value = newPosts.length === pageSize.value
      currentPage.value++
    }
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 监听路由参数变化 (如果是从一个人的主页点到另一个人的主页)
watch(() => route.params.id, (newId) => {
  if (newId && route.name === 'user') {
    currentPage.value = 1
    userPosts.value = []
    hasMore.value = true
    isFollowing.value = false
    fetchUserInfo()
    fetchUserPosts()
  }
})

// 无限滚动绑定
const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return
  observer = new IntersectionObserver((entries) => {
    if (entries[0]?.isIntersecting && hasMore.value && !loading.value) {
      fetchUserPosts(true)
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

const openPostDetail = (post: Post, rect: DOMRect | null) => {
  selectedPost.value = post
  triggerRect.value = rect
  showModal.value = true
}

const closePostDetail = () => {
  showModal.value = false
  setTimeout(() => { selectedPost.value = null }, 300)
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)

  const targetPost = userPosts.value.find(p => p.id === postId)
  if (targetPost) {
    const currentCount = targetPost.likesCount ?? targetPost.likeCount ?? 0
    targetPost.likesCount = isLiked ? currentCount + 1 : Math.max(0, currentCount - 1)
    targetPost.likeCount = targetPost.likesCount
  }
}

const handleModalLike = (postId: number) => {
  handleLike(postId, !likeStore.isPostLiked(postId))
}

onMounted(() => {
  updateColCount()
  window.addEventListener('resize', updateColCount)
  fetchUserInfo()
  fetchUserPosts()
  setupInfiniteScroll()
})

onUnmounted(() => {
  window.removeEventListener('resize', updateColCount)
  cleanupInfiniteScroll()
})
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