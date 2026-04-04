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
        <div class="flex flex-col gap-[10px] flex-1">
          <div class="flex items-center flex-wrap gap-x-20 gap-y-3 w-full">
            <h1 class="text-[28px] font-semibold text-gray-900 max-w-[280px] truncate" :title="userInfo.nickname">
              {{ userInfo.nickname }}
            </h1>
            
            <button 
              v-if="isFollowing !== null && targetUserId !== authStore.userInfo?.id"
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
          
          <div class="flex gap-6 mt-2 text-[14px]">
            <div class="cursor-pointer transition-colors duration-200" 
                 :class="activeTab === 'following' ? 'text-[#FF2442] font-bold' : 'text-gray-600 hover:text-black'"
                 @click="switchTab('following')">
              <span class="font-semibold mr-1" :class="activeTab === 'following' ? 'text-[#FF2442]' : 'text-gray-900'">{{ userInfo.followingCount || 0 }}</span>关注
            </div>
            
            <div class="cursor-pointer transition-colors duration-200"
                 :class="activeTab === 'followers' ? 'text-[#FF2442] font-bold' : 'text-gray-600 hover:text-black'"
                 @click="switchTab('followers')">
              <span class="font-semibold mr-1" :class="activeTab === 'followers' ? 'text-[#FF2442]' : 'text-gray-900'">{{ userInfo.followersCount || 0 }}</span>粉丝
            </div>
            
            <div class="text-gray-600">
              <span class="font-semibold text-gray-900 mr-1">{{ userInfo.likesCount || 0 }}</span>获赞
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="sticky top-[84px] z-40 bg-white w-full flex justify-center border-b border-gray-100 pt-[10px]">
      <div class="flex gap-12 px-4 h-[48px]">
        <button @click="switchTab('posts')" class="relative h-full px-2 text-[16px] font-semibold transition-colors" :class="activeTab === 'posts' ? 'text-gray-900' : 'text-gray-500'">
          笔记
          <div v-show="activeTab === 'posts'" class="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-[3px] bg-[#FF2442] rounded-full"></div>
        </button>

        <button v-show="activeTab === 'following'" class="relative h-full px-2 text-[16px] font-semibold text-gray-900 transition-colors">
          关注
          <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-[3px] bg-[#FF2442] rounded-full"></div>
        </button>

        <button v-show="activeTab === 'followers'" class="relative h-full px-2 text-[16px] font-semibold text-gray-900 transition-colors">
          粉丝
          <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-4 h-[3px] bg-[#FF2442] rounded-full"></div>
        </button>
      </div>
    </div>

    <div class="w-full bg-white px-[6px] pt-[20px] pb-[60px]">
      
      <div v-if="activeTab === 'posts'">
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

      <div v-else-if="activeTab === 'following' || activeTab === 'followers'" class="max-w-2xl mx-auto py-2">
        <div class="space-y-4">
          <div v-for="user in userList" :key="user.id" class="flex items-center justify-between p-4 bg-white rounded-2xl border border-gray-100 shadow-sm transition-all hover:shadow-md">
            
            <div class="flex items-center gap-4 cursor-pointer" @click="router.push(`/user/${user.id}`)">
              <img :src="user.avatar || 'http://localhost:8080/1.jpg'" class="w-12 h-12 rounded-full object-cover border border-gray-100" />
              <div class="flex flex-col">
                <span class="text-[15px] font-bold text-gray-900">{{ user.nickname }}</span>
                <span class="text-[13px] text-gray-400 mt-0.5 truncate w-[180px] sm:w-[250px]">
                  {{ user.intro || '这个人很懒，什么都没写~' }}
                </span>
              </div>
            </div>

            <button 
              v-if="user.id !== authStore.userInfo?.id"
              @click="handleListFollow(user)"
              class="px-5 py-1.5 rounded-full text-[13px] font-medium transition-colors"
              :class="getFollowBtnInfo(user).class"
            >
              {{ getFollowBtnInfo(user).text }}
            </button>
          </div>
        </div>

        <div class="py-10 text-center">
          <button v-if="hasMoreUsers" @click="loadMoreUsers" class="px-6 py-2 rounded-full border border-gray-200 text-gray-500 text-sm hover:bg-gray-50 transition-colors">
            {{ isLoadingUsers ? '加载中...' : '加载更多' }}
          </button>
          <span v-else-if="userList.length > 0" class="text-sm text-gray-400">没有更多了~</span>
          <span v-else class="text-sm text-gray-400">列表空空如也</span>
        </div>
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

// 关注状态
const isFollowing = ref<boolean | null>(null)

// 🌟 核心新增：当前选中的标签页 ('posts', 'following', 'followers')
const activeTab = ref('posts')

// =========== 关系链列表逻辑 ===========
const userList = ref<any[]>([])
const userListPage = ref(1)
const hasMoreUsers = ref(true)
const isLoadingUsers = ref(false)

const fetchUserList = async (reset = false) => {
  if (reset) {
    userListPage.value = 1
    userList.value = []
    hasMoreUsers.value = true
  }
  
  if (!hasMoreUsers.value || isLoadingUsers.value) return
  isLoadingUsers.value = true

  try {
    const endpoint = activeTab.value === 'following' ? 'following' : 'followers'
    // 获取列表
    const res = await get<any>(`/follow/${endpoint}/${authStore.userInfo?.id}`, {
      pageNum: userListPage.value,
      pageSize: 20
    })
    
    if (res.code === 1) {
      let newItems = []
      if (res.data && Array.isArray(res.data.items)) {
        newItems = res.data.items 
      } else if (Array.isArray(res.data)) {
        newItems = res.data       
      }
      
      // 🌟 终极防爆层：暴力抹平前后端数据差异
      // 识别后端传来的 1 统统转为 true，0 转为 false
      newItems = newItems.map(u => {
        const following = u.isFollowing === 1 || u.isFollowing === true || u.following === 1 || u.following === true
        const follower = u.isFollower === 1 || u.isFollower === true || u.follower === 1 || u.follower === true
        return { ...u, isFollowing: following, isFollower: follower }
      })
      
      userList.value.push(...newItems)
      hasMoreUsers.value = newItems.length === 20
    }
  } catch (e) {
    console.error('获取列表失败')
  } finally {
    isLoadingUsers.value = false
  }
}

const loadMoreUsers = () => {
  const isSelf = authStore.userInfo?.id === Number(targetUserId.value)
  if (!isSelf && userListPage.value >= 5) {
    ElMessage.info('保护用户隐私，仅展示部分列表哦')
    hasMoreUsers.value = false
    return
  }
  userListPage.value++
  fetchUserList()
}

const switchTab = (tab: string) => {
  activeTab.value = tab
  if (tab === 'following' || tab === 'followers') {
    fetchUserList(true)
  }
}

const getFollowBtnInfo = (user: any) => {
  if (user.isFollowing && user.isFollower) return { text: '互粉', class: 'bg-gray-100 text-gray-500' }
  if (user.isFollowing && !user.isFollower) return { text: '已关注', class: 'bg-gray-100 text-gray-500' }
  if (!user.isFollowing && user.isFollower) return { text: '回关', class: 'bg-[#FF2442] text-white' }
  return { text: '关注', class: 'bg-[#FF2442] text-white' }
}

const handleListFollow = async (user: any) => {
  if (!authStore.isLoggedIn) return ElMessage.warning('请先登录哦')
  try {
    const res = await post(`/follow/${user.id}`)
    if (res.code === 1) {
      user.isFollowing = !user.isFollowing
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const checkFollowStatus = async () => {
  if (!authStore.isLoggedIn) {
    isFollowing.value = false
    return
  }
  try {
    const response = await get<boolean>(`/follow/status/${targetUserId.value}`)
    if (response.code === 1) {
      isFollowing.value = response.data 
    }
  } catch (error) {
    console.error('获取关注状态失败')
  }
}

// =========== 笔记列表逻辑 ===========
const userPosts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(15)
const refreshKey = ref(0) 

const showModal = ref(false)
const selectedPost = ref<Post | null>(null)
const triggerRect = ref<DOMRect | null>(null)

const loadMoreTrigger = ref<HTMLElement>()
let observer: IntersectionObserver | null = null

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

const fetchUserInfo = async () => {
  try {
    if (authStore.userInfo?.id === targetUserId.value) {
      router.replace('/profile')
      return
    }
    const response = await get<any>(`/user/info/${targetUserId.value}`)
    if (response.code === 1) {
      userInfo.value = response.data
    } else {
      ElMessage.error('用户不存在')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const toggleFollow = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }
  try {
    const res = await post(`/follow/${targetUserId.value}`)
    if (res.code === 1) {
      isFollowing.value = !isFollowing.value
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

watch(() => route.params.id, (newId) => {
  if (newId && route.name === 'user') {
    currentPage.value = 1
    userPosts.value = []
    hasMore.value = true
    isFollowing.value = null 
    activeTab.value = 'posts' // 切人时重置回笔记
    
    fetchUserInfo()
    fetchUserPosts()
    checkFollowStatus()
  }
})

const setupInfiniteScroll = () => {
  if (!loadMoreTrigger.value) return
  observer = new IntersectionObserver((entries) => {
    // 只有在处于“笔记”标签页时，才触发无限滚动
    if (entries[0]?.isIntersecting && hasMore.value && !loading.value && activeTab.value === 'posts') {
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
  checkFollowStatus() 
  
  // 必须延迟一下挂载监听器，确保 DOM 已渲染
  setTimeout(() => {
    setupInfiniteScroll()
  }, 500)
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