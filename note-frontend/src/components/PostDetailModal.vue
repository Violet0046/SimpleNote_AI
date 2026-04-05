<template>
  <Teleport to="body">
    <Transition name="fade" appear>
      <div v-if="visible" class="fixed inset-0 z-[9998] bg-black/60 transition-opacity" @click="handleClose"></div>
    </Transition>

    <Transition name="fade" appear>
      <button 
        v-if="visible" 
        @click="handleClose" 
        class="fixed top-6 left-6 z-[10000] w-12 h-12 border border-white/20 bg-black/20 text-white hover:bg-white/20 rounded-full flex items-center justify-center backdrop-blur-md transition-all shadow-lg cursor-pointer"
      >
        <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </Transition>

    <div
      v-if="visible && postDetail"
      class="fixed z-[9999] overflow-hidden shadow-2xl transition-all duration-[400ms] ease-[cubic-bezier(0.32,0.72,0,1)] flex bg-white dark:bg-gray-900"
      :style="modalStyle"
      @click.stop
    >
<div class="flex-1 relative flex items-center justify-center overflow-hidden bg-[#F8F8F8] dark:bg-black">
      
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <video 
          v-if="Number(postDetail?.isVideo) === 1"
          :src="firstImage + '#t=0.1'"
          preload="auto"
          muted 
          playsinline
          class="absolute inset-0 w-full h-full object-cover blur-[50px] opacity-50 transform scale-110 dark:opacity-30"
        ></video>
        
        <div 
          v-else
          class="absolute inset-0 bg-cover bg-center blur-[50px] opacity-50 transform scale-110 dark:opacity-30" 
          :style="{ backgroundImage: `url(${firstImage})` }"
        ></div>
      </div>
      <div v-if="Number(postDetail?.isVideo) === 1" class="relative w-full h-full flex items-center justify-center z-10">
        <video 
          :src="firstImage" 
          controls 
          autoplay 
          loop 
          playsinline 
          class="max-w-full max-h-full object-contain drop-shadow-lg outline-none"
        ></video>
      </div>
      
      <div v-else-if="imageList && imageList.length > 0" class="relative w-full h-full overflow-hidden group z-10">
        <div class="w-full h-full transition-transform duration-300 ease-out flex" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
          <div v-for="(img, idx) in imageList" :key="idx" class="w-full h-full flex-shrink-0 flex items-center justify-center">
            <img :src="img" class="max-w-full max-h-full object-contain drop-shadow-lg" />
          </div>
        </div>
        
        <button v-if="imageList.length > 1" @click="prevImage" class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-black/20 text-white rounded-full flex items-center justify-center backdrop-blur-md opacity-0 group-hover:opacity-100 transition-opacity">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
        </button>
        <button v-if="imageList.length > 1" @click="nextImage" class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-black/20 text-white rounded-full flex items-center justify-center backdrop-blur-md opacity-0 group-hover:opacity-100 transition-opacity">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
        </button>
        
        <div v-if="imageList.length > 1" class="absolute bottom-6 left-1/2 -translate-x-1/2 flex space-x-2">
          <div v-for="(_, idx) in imageList" :key="idx" class="w-2 h-2 rounded-full transition-all" :class="currentImageIndex === idx ? 'bg-red-500 scale-110' : 'bg-gray-300'"></div>
        </div>
      </div>

    </div>

      <div class="w-[360px] lg:w-[400px] flex-shrink-0 flex flex-col h-full border-l border-gray-100 dark:border-gray-800 transition-opacity duration-300 bg-white dark:bg-gray-900" :class="isExpanded ? 'opacity-100' : 'opacity-0'">
        
        <div class="h-[76px] px-6 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between shrink-0">
          <div class="flex items-center space-x-3 cursor-pointer group" @click="goToUserProfile(postDetail?.userId)">
            <div class="w-10 h-10 rounded-full bg-gray-100 dark:bg-gray-700 overflow-hidden border border-gray-200 dark:border-gray-600 transition-transform group-hover:scale-105">
              <img v-if="postDetail?.authorAvatar" :src="postDetail.authorAvatar" class="w-full h-full object-cover" />
              <span v-else class="w-full h-full flex items-center justify-center text-gray-500 font-semibold">{{ postDetail?.authorName?.charAt(0).toUpperCase() }}</span>
            </div>
            <span class="font-medium text-[15px] text-gray-900 dark:text-gray-100 group-hover:text-blue-500 transition-colors">{{ postDetail?.authorName }}</span>
          </div>
          
          <button 
            v-if="isFollowing !== null && postDetail?.userId !== authStore.userInfo?.id"
            @click="toggleFollow" 
            class="px-5 py-1.5 rounded-full font-semibold text-[13px] transition-colors flex-shrink-0"
            :class="isFollowing ? 'bg-gray-100 dark:bg-gray-800 text-gray-500 hover:bg-gray-200 dark:hover:bg-gray-700' : 'bg-[#FF2442] text-white hover:bg-red-600'"
          >
            {{ isFollowing ? '已关注' : '关注' }}
          </button>
        </div>

        <div class="flex-1 overflow-y-auto no-scrollbar relative" id="comment-scroll-area">
          <div class="p-6 pb-4">
            <h2 class="text-[18px] font-semibold text-gray-900 dark:text-gray-100 mb-3 leading-snug">{{ postDetail?.title }}</h2>
            <p class="text-[15px] text-gray-800 dark:text-gray-300 leading-relaxed whitespace-pre-wrap mb-4">{{ postDetail?.content }}</p>
            <div class="flex flex-wrap gap-2 mb-4">
              <span v-for="tag in tagList" :key="tag" class="text-[#13386c] dark:text-blue-400 hover:text-blue-800 cursor-pointer text-[15px]">#{{ tag }}</span>
            </div>
            <p class="text-[12px] text-gray-400">{{ formatTime(postDetail?.createTime) }}</p>
          </div>

          <div class="h-px bg-gray-100 dark:bg-gray-800 mx-6 mb-4"></div>

          <div class="px-6 pb-6">
            
            <div class="flex items-center justify-between mb-5">
              <h3 class="text-[14px] text-gray-600 dark:text-gray-400 font-medium">共 {{ totalCommentsCount }} 条评论</h3>
              <div class="flex items-center gap-3 text-[13px] text-gray-400 dark:text-gray-500">
                <span class="cursor-pointer transition-colors" :class="currentSortType === 2 ? 'text-gray-900 dark:text-gray-100 font-semibold' : 'hover:text-gray-600'" @click="changeSort(2)">最热</span>
                <span class="w-px h-3 bg-gray-200 dark:bg-gray-700"></span>
                <span class="cursor-pointer transition-colors" :class="currentSortType === 1 ? 'text-gray-900 dark:text-gray-100 font-semibold' : 'hover:text-gray-600'" @click="changeSort(1)">最新</span>
              </div>
            </div>
            
            <div v-if="isLoadingComments" class="flex justify-center py-4">
              <span class="text-sm text-gray-400">加载中...</span>
            </div>
            
            <div v-else-if="comments.length === 0" class="flex flex-col items-center justify-center py-10 opacity-60">
              <span class="text-[13px] text-gray-400">还没有人评论，快来抢沙发~</span>
            </div>

            <div v-else class="space-y-6">
              <div v-for="comment in comments" :key="comment.id" class="group">
                
                <div class="flex gap-3">
                  <img :src="comment.authorAvatar || 'http://localhost:8080/1.jpg'" 
                       class="w-8 h-8 rounded-full border border-gray-100 dark:border-gray-700 shrink-0 cursor-pointer hover:opacity-80 transition-opacity" 
                       @click="goToUserProfile(comment.userId)" />
                  <div class="flex-1">
                    <div class="flex items-center gap-1.5">
                      <p class="text-[13px] text-gray-500 font-medium cursor-pointer hover:text-blue-500 transition-colors" @click="goToUserProfile(comment.userId)">
                        {{ comment.authorName }}
                      </p>
                      <span v-if="comment.userId === postDetail?.userId" class="px-1.5 py-[1px] bg-[#FF2442] text-white text-[10px] font-bold rounded-sm shadow-sm">作者</span>
                    </div>

                    <p class="text-[14px] text-gray-900 dark:text-gray-100 mt-0.5 leading-snug cursor-text" @click="setReply(comment, comment)">
                      {{ comment.content }}
                    </p>
                    <div class="flex items-center justify-between mt-1.5">
                      <div class="flex items-center gap-4">
                        <span class="text-[12px] text-gray-400">{{ formatTime(comment.createTime) }}</span>
                        <button @click="setReply(comment, comment)" class="text-[12px] text-gray-400 hover:text-gray-600 font-medium opacity-0 group-hover:opacity-100 transition-opacity">回复</button>
                      </div>
                      
                      <div class="flex items-center gap-1 cursor-pointer transition-colors" 
                           :class="comment.isLiked === 1 ? 'text-[#FF2442]' : 'text-gray-400 hover:text-[#FF2442]'"
                           @click="toggleCommentLike(comment)">
                        <svg class="w-4 h-4" :fill="comment.isLiked === 1 ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                        </svg>
                        <span class="text-[12px] font-medium" v-if="comment.likesCount">{{ comment.likesCount }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div v-if="comment.replies && comment.replies.length > 0" class="mt-3 ml-11 space-y-4">
                  <div v-for="sub in comment.replies" :key="sub.id" class="flex gap-2.5 group/sub">
                    <img :src="sub.authorAvatar || 'http://localhost:8080/1.jpg'" 
                         class="w-6 h-6 rounded-full border border-gray-100 dark:border-gray-700 shrink-0 cursor-pointer hover:opacity-80 transition-opacity" 
                         @click="goToUserProfile(sub.userId)" />
                    <div class="flex-1">
                      <div class="flex items-center gap-1.5">
                        <p class="text-[12px] text-gray-500 font-medium cursor-pointer hover:text-blue-500 transition-colors" @click="goToUserProfile(sub.userId)">
                          {{ sub.authorName }}
                        </p>
                        <span v-if="sub.userId === postDetail?.userId" class="px-1.5 py-[1px] bg-[#FF2442] text-white text-[9px] font-bold rounded-sm shadow-sm">作者</span>
                      </div>

                      <p class="text-[13px] text-gray-900 dark:text-gray-100 mt-0.5 leading-snug cursor-text" @click="setReply(comment, sub)">
                        <span v-if="sub.replyToUserName && sub.replyToUserName !== comment.authorName" class="text-blue-500 mr-1">回复 {{sub.replyToUserName}}:</span>
                        {{ sub.content }}
                      </p>
                      <div class="flex items-center justify-between mt-1.5">
                        <div class="flex items-center gap-4">
                          <span class="text-[11px] text-gray-400">{{ formatTime(sub.createTime) }}</span>
                          <button @click="setReply(comment, sub)" class="text-[11px] text-gray-400 hover:text-gray-600 font-medium opacity-0 group-hover/sub:opacity-100 transition-opacity">回复</button>
                        </div>
                        <div class="flex items-center gap-1 cursor-pointer transition-colors" 
                             :class="sub.isLiked === 1 ? 'text-[#FF2442]' : 'text-gray-400 hover:text-[#FF2442]'"
                             @click="toggleCommentLike(sub)">
                          <svg class="w-3.5 h-3.5" :fill="sub.isLiked === 1 ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                          </svg>
                          <span class="text-[11px] font-medium" v-if="sub.likesCount">{{ sub.likesCount }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="comment.childCount > (comment.replies?.length || 0)" class="mt-2 ml-11 text-[12px]">
                  <button @click="expandReplies(comment)" class="flex items-center text-blue-500 hover:text-blue-600 font-medium transition-colors">
                    <span class="w-4 h-px bg-gray-300 dark:bg-gray-600 mr-2"></span>
                    {{ comment.replies && comment.replies.length > 0 ? '展开更多回复' : '展开回复' }} 
                    ({{ comment.childCount - (comment.replies?.length || 0) }})
                    <svg class="w-3 h-3 ml-0.5 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" /></svg>
                  </button>
                </div>

              </div>
            </div>
          </div>
        </div>

        <div class="px-6 py-4 border-t border-gray-100 dark:border-gray-800 shrink-0 flex items-center gap-4 relative bg-white dark:bg-gray-900">
          
          <Transition name="fade">
            <div v-if="replyingTo" class="absolute -top-8 left-6 px-3 py-1 bg-white dark:bg-gray-800 border border-gray-100 dark:border-gray-700 rounded-t-lg shadow-sm flex items-center gap-2 text-[12px] z-10 text-gray-600 dark:text-gray-300">
              <span>回复 @{{ replyingTo.authorName }}</span>
              <button @click="clearReply" class="text-red-500 hover:text-red-600 font-bold ml-1">×</button>
            </div>
          </Transition>

          <div class="flex-1 bg-gray-100 dark:bg-gray-800 rounded-full px-4 py-2 flex items-center transition-all duration-200">
            <input 
              v-model="commentText"
              ref="commentInputRef"
              type="text" 
              :placeholder="replyingTo ? '写下你的回复...' : '说点什么...'" 
              class="w-full !bg-transparent outline-none text-[14px] text-gray-900 dark:text-gray-100 placeholder-gray-500 dark:placeholder-gray-400"
              @keyup.enter="handleSendComment"
            />
          </div>
          
          <div class="flex items-center gap-4 shrink-0">
            <button 
              @click="handleSendComment"
              :disabled="!commentText.trim() || isSending"
              class="px-4 py-1.5 rounded-full font-medium text-[13px] text-white transition-colors duration-200"
              :class="commentText.trim() ? 'bg-[#FF2442] hover:bg-red-600' : 'bg-[#FF91A0] cursor-not-allowed'"
            >
              {{ isSending ? '发送中' : '发送' }}
            </button>

            <div class="flex items-center gap-1 cursor-pointer transition-colors" 
                 :class="likeStore.isPostLiked(postDetail?.id || 0) ? 'text-[#FF2442]' : 'text-gray-500 hover:text-[#FF2442]'"
                 @click="$emit('like-toggle')">
              <svg class="w-6 h-6" :fill="likeStore.isPostLiked(postDetail?.id || 0) ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <span class="text-[13px] font-medium">{{ postDetail?.likesCount || '赞' }}</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

import { ref, computed, watch, nextTick } from 'vue'
import type { PropType } from 'vue'
import type { Post } from '@/types'
import { get, post } from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { ElMessage } from 'element-plus'
const router = useRouter()
const goToUserProfile = (userId?: number) => {
  if (!userId) return
  
  // 哪怕是点自己的头像，也新开一个标签页，保证当前页面的阅读状态不被破坏！
  const routeUrl = router.resolve(`/user/${userId}`)
  window.open(routeUrl.href, '_blank')
}
const props = defineProps({
  post: { type: Object as PropType<Post | null>, default: null },
  visible: { type: Boolean, default: false },
  triggerRect: { type: Object as PropType<DOMRect | null>, default: null }
})

const emit = defineEmits(['close', 'like-toggle'])
const authStore = useAuthStore()
const likeStore = useLikeStore()

const postDetail = ref<Post | null>(null)
const isExpanded = ref(false)
const modalStyle = ref({ top: '0px', left: '0px', width: '0px', height: '0px', opacity: '0', borderRadius: '16px' })
const currentImageIndex = ref(0)

const commentText = ref('')
const isSending = ref(false)
const isFollowing = ref<boolean | null>(null)

const replyingTo = ref<any>(null)
const rootCommentId = ref<number | null>(null)
const commentInputRef = ref<HTMLInputElement | null>(null)

const comments = ref<any[]>([])
const isLoadingComments = ref(false)
const currentSortType = ref(2) // 默认 2 (最热)

const firstImage = computed(() => props.post?.images?.split(',')[0]?.trim() || '')
const imageList = computed(() => props.post?.images ? String(props.post.images).split(',').map(i => i.trim()).filter(Boolean) : [])
const tagList = computed(() => props.post?.tags ? String(props.post.tags).split(',').map(t => t.trim()).filter(Boolean) : [])

// 🌟 精准总评论数计算
const totalCommentsCount = computed(() => {
  return comments.value.reduce((total, c) => total + 1 + (c.childCount || c.replies?.length || 0), 0)
})

const formatTime = (timeStr?: string) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16) // 截取到分钟，更清爽
}

const fetchComments = async (postId: number) => {
  isLoadingComments.value = true
  comments.value = []
  try {
    const res = await get<any>('/comment/list', { postId, sortType: currentSortType.value })
    if (res.code === 1) comments.value = res.data.items || res.data || []
  } catch (e) {
    console.error('获取评论失败')
  } finally {
    isLoadingComments.value = false
  }
}

const changeSort = (type: number) => {
  if (currentSortType.value === type) return
  currentSortType.value = type
  if (postDetail.value?.id) fetchComments(postDetail.value.id)
}

// 🌟 倍增式展开评论引擎
const expandReplies = async (comment: any) => {
  if (!comment.replyPageSize) comment.replyPageSize = 5 // 第一次点，加载 5 条
  else comment.replyPageSize *= 2 // 之后倍增 10, 20, 40...

  try {
    // 拉取第一页，但 size 是倍增后的，直接覆盖旧的 preview，防止数据重复
    const res = await get<any>('/comment/replies', {
      parentId: comment.id,
      pageNum: 1,
      pageSize: comment.replyPageSize
    })
    if (res.code === 1) {
      comment.replies = res.data.items || res.data || []
    }
  } catch (e) {
    ElMessage.error('展开失败')
  }
}

// 🌟 评论点赞交互
const toggleCommentLike = async (commentItem: any) => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录哦')
    return
  }
  
  // 乐观更新：先在前端变红，不用等后端
  const isLikedNow = commentItem.isLiked === 1
  commentItem.isLiked = isLikedNow ? 0 : 1
  commentItem.likesCount = (commentItem.likesCount || 0) + (isLikedNow ? -1 : 1)

  try {
    const res = await post(`/comment/like/${commentItem.id}`)
    if (res.code !== 1) throw new Error()
  } catch (e) {
    // 失败了再回滚
    commentItem.isLiked = isLikedNow ? 1 : 0
    commentItem.likesCount += (isLikedNow ? 1 : -1)
    ElMessage.error('点赞失败，请重试')
  }
}

const setReply = (rootComment: any, targetComment: any) => {
  rootCommentId.value = rootComment.id
  replyingTo.value = targetComment
  commentInputRef.value?.focus()
}

const clearReply = () => {
  replyingTo.value = null
  rootCommentId.value = null
}

const checkFollowStatus = async (authorId: number) => {
  if (!authStore.isLoggedIn) {
    isFollowing.value = false
    return
  }
  try {
    const res = await get<boolean>(`/follow/status/${authorId}`)
    if (res.code === 1) isFollowing.value = res.data
  } catch (error) {
    console.error('获取关注状态失败')
  }
}

const toggleFollow = async () => {
  if (!authStore.isLoggedIn) return ElMessage.warning('请先登录哦')
  try {
    const res = await post(`/follow/${postDetail.value?.userId}`)
    if (res.code === 1) isFollowing.value = !isFollowing.value
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleSendComment = async () => {
  if (!commentText.value.trim() || !authStore.isLoggedIn || isSending.value) {
    if (!authStore.isLoggedIn) ElMessage.warning('请先登录哦')
    return
  }
  isSending.value = true
  try {
    const payload: any = {
      postId: postDetail.value?.id,
      content: commentText.value.trim()
    }
    
    if (replyingTo.value) {
      payload.parentId = rootCommentId.value
      payload.replyToUserId = replyingTo.value.userId
    }

    const res = await post('/comment/add', payload)
    
    if (res.code === 1) {
      ElMessage.success('评论发送成功')
      commentText.value = ''
      clearReply()
      if (postDetail.value?.id) fetchComments(postDetail.value.id)
      setTimeout(() => {
        const scrollArea = document.getElementById('comment-scroll-area')
        if(scrollArea) scrollArea.scrollTop = scrollArea.scrollHeight
      }, 500)
    } else {
      ElMessage.error(res.msg || '发送失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    isSending.value = false
  }
}

watch(() => props.visible, async (isVisible) => {
  if (isVisible && props.post) {
    document.body.style.overflow = 'hidden'
    postDetail.value = props.post
    currentImageIndex.value = 0
    isExpanded.value = false
    isFollowing.value = null
    commentText.value = ''
    clearReply()
    
    fetchComments(props.post.id)
    if (props.post.userId) checkFollowStatus(props.post.userId)

    if (props.triggerRect) {
      modalStyle.value = {
        top: `${props.triggerRect.top}px`, left: `${props.triggerRect.left}px`,
        width: `${props.triggerRect.width}px`, height: `${props.triggerRect.height}px`,
        opacity: '1', borderRadius: '16px'
      }
    }

    await nextTick()
    
    let isAnimated = false
    const animateModal = (imgRatio: number) => {
      if (isAnimated) return
      isAnimated = true
      requestAnimationFrame(() => {
        const finalHeight = window.innerHeight * 0.90 
        const rightPanelWidth = window.innerWidth < 1024 ? 360 : 400
        
        let imgDisplayWidth
        if (imgRatio <= 1.2) {
          imgDisplayWidth = finalHeight * imgRatio
          // 🎛️ 遥控器：竖屏最小宽度，想要更宽可以把 420 改成 460、500 等等
          imgDisplayWidth = Math.max(imgDisplayWidth, 420) 
        } else {
          // 🎛️ 遥控器：横屏最大宽度，不超过 850px 或屏幕的 55%
          imgDisplayWidth = Math.min(850, window.innerWidth * 0.55)
        }
        
        const finalWidth = imgDisplayWidth + rightPanelWidth
        const finalTop = (window.innerHeight - finalHeight) / 2
        const finalLeft = (window.innerWidth - finalWidth) / 2

        modalStyle.value = {
          top: `${finalTop}px`, left: `${finalLeft}px`,
          width: `${finalWidth}px`, height: `${finalHeight}px`,
          opacity: '1', borderRadius: '20px'
        }
        setTimeout(() => { isExpanded.value = true }, 300)
      })
    }

//如果是视频，不用去量了，直接给一个标准的竖屏比例 (比如 0.75 也就是 3:4) 展开弹窗
    if (props.post.isVideo) {
      setTimeout(() => animateModal(0.75), 50)
    } else {
      const img = new Image()
      img.src = firstImage.value
      if (img.complete && img.naturalHeight) {
        animateModal(img.naturalWidth / img.naturalHeight)
      } else {
        img.onload = () => animateModal(img.naturalWidth / img.naturalHeight)
        img.onerror = () => animateModal(1) // 兜底为 1:1 方形
        setTimeout(() => animateModal(1), 150)
      }
    }

  } else {
    document.body.style.overflow = ''
    isExpanded.value = false
    if (props.triggerRect) {
      modalStyle.value = {
        top: `${props.triggerRect.top}px`, left: `${props.triggerRect.left}px`,
        width: `${props.triggerRect.width}px`, height: `${props.triggerRect.height}px`,
        opacity: '0', borderRadius: '16px'
      }
    }
  }
}, { immediate: true })

const prevImage = () => { currentImageIndex.value = currentImageIndex.value > 0 ? currentImageIndex.value - 1 : imageList.value.length - 1 }
const nextImage = () => { currentImageIndex.value = currentImageIndex.value < imageList.value.length - 1 ? currentImageIndex.value + 1 : 0 }
const handleClose = () => emit('close')
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
</style>