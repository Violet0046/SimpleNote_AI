<template>
  <Teleport to="body">
    <Transition name="fade" appear>
      <div v-if="visible" class="fixed inset-0 z-[9998] bg-black/60 transition-opacity" @click="handleClose"></div>
    </Transition>

    <Transition name="fade" appear>
      <button
        v-if="visible"
        type="button"
        class="fixed left-6 top-6 z-[10000] flex h-12 w-12 items-center justify-center rounded-full border border-white/20 bg-black/20 text-white shadow-lg backdrop-blur-md transition-all hover:bg-white/20"
        @click="handleClose"
      >
        <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </Transition>

    <div
      v-if="visible && postDetail"
      class="fixed z-[9999] flex overflow-hidden bg-white shadow-2xl transition-all duration-[400ms] ease-[cubic-bezier(0.32,0.72,0,1)] dark:bg-gray-900"
      :style="modalStyle"
      @click.stop
    >
      <div class="relative flex flex-1 items-center justify-center overflow-hidden bg-[#F8F8F8] dark:bg-black">
        <div class="pointer-events-none absolute inset-0 overflow-hidden">
          <video
            v-if="isVideoPost"
            :src="`${firstImage}#t=0.1`"
            preload="auto"
            muted
            playsinline
            class="absolute inset-0 h-full w-full scale-110 object-cover opacity-50 blur-[50px] dark:opacity-30"
          ></video>

          <div
            v-else
            class="absolute inset-0 scale-110 bg-cover bg-center opacity-50 blur-[50px] dark:opacity-30"
            :style="{ backgroundImage: `url(${firstImage})` }"
          ></div>
        </div>

        <div v-if="isVideoPost" class="relative z-10 flex h-full w-full items-center justify-center">
          <video ref="detailVideoRef" :src="firstImage" controls autoplay loop playsinline class="max-h-full max-w-full object-contain outline-none drop-shadow-lg"></video>
        </div>

        <div v-else-if="imageList.length > 0" class="group relative z-10 h-full w-full overflow-hidden">
          <div class="flex h-full w-full transition-transform duration-300 ease-out" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
            <div v-for="(image, index) in imageList" :key="`${image}_${index}`" class="flex h-full w-full flex-shrink-0 items-center justify-center">
              <img :src="image" class="max-h-full max-w-full object-contain drop-shadow-lg" />
            </div>
          </div>

          <button
            v-if="imageList.length > 1"
            type="button"
            class="absolute left-4 top-1/2 flex h-10 w-10 -translate-y-1/2 items-center justify-center rounded-full bg-black/20 text-white opacity-0 backdrop-blur-md transition-opacity group-hover:opacity-100"
            @click="prevImage"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <button
            v-if="imageList.length > 1"
            type="button"
            class="absolute right-4 top-1/2 flex h-10 w-10 -translate-y-1/2 items-center justify-center rounded-full bg-black/20 text-white opacity-0 backdrop-blur-md transition-opacity group-hover:opacity-100"
            @click="nextImage"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>

          <div v-if="imageList.length > 1" class="absolute bottom-6 left-1/2 flex -translate-x-1/2 space-x-2">
            <div
              v-for="(_, index) in imageList"
              :key="index"
              class="h-2 w-2 rounded-full transition-all"
              :class="currentImageIndex === index ? 'scale-110 bg-red-500' : 'bg-gray-300'"
            ></div>
          </div>
        </div>
      </div>

      <div
        class="flex h-full w-[360px] flex-shrink-0 flex-col border-l border-gray-100 bg-white transition-opacity duration-300 dark:border-gray-800 dark:bg-gray-900 lg:w-[400px]"
        :class="isExpanded ? 'opacity-100' : 'opacity-0'"
      >
        <div class="flex h-[76px] shrink-0 items-center justify-between border-b border-gray-100 px-6 dark:border-gray-800">
          <div class="group flex cursor-pointer items-center space-x-3" @click="goToUserProfile(postDetail.userId)">
            <div class="h-10 w-10 overflow-hidden rounded-full border border-gray-200 bg-gray-100 transition-transform group-hover:scale-105 dark:border-gray-600 dark:bg-gray-700">
              <img v-if="postDetail.authorAvatar" :src="postDetail.authorAvatar" class="h-full w-full object-cover" />
              <span v-else class="flex h-full w-full items-center justify-center font-semibold text-gray-500">
                {{ postDetail.authorName?.charAt(0).toUpperCase() }}
              </span>
            </div>
            <span class="text-[15px] font-medium text-gray-900 transition-colors group-hover:text-blue-500 dark:text-gray-100">
              {{ postDetail.authorName }}
            </span>
          </div>

          <button
            v-if="isFollowing !== null && postDetail.userId !== authStore.userInfo?.id"
            type="button"
            class="flex-shrink-0 rounded-full px-5 py-1.5 text-[13px] font-semibold transition-colors"
            :class="isFollowing ? 'bg-gray-100 text-gray-500 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700' : 'bg-[#FF2442] text-white hover:bg-red-600'"
            @click="toggleFollow"
          >
            {{ isFollowing ? COPY.following : COPY.follow }}
          </button>
        </div>

        <div ref="scrollAreaRef" class="no-scrollbar relative flex-1 overflow-y-auto">
          <div class="p-6 pb-4">
            <h2 class="mb-3 text-[18px] font-semibold leading-snug text-gray-900 dark:text-gray-100">{{ postDetail.title }}</h2>
            <p class="mb-4 whitespace-pre-wrap text-[15px] leading-relaxed text-gray-800 dark:text-gray-300">{{ postDetail.content }}</p>
            <div v-if="tagList.length > 0" class="mb-4 flex flex-wrap gap-2">
              <span v-for="tag in tagList" :key="tag" class="cursor-pointer text-[15px] text-[#13386c] hover:text-blue-800 dark:text-blue-400">
                #{{ tag }}
              </span>
            </div>
            <p class="text-[12px] text-gray-400">{{ formatDetailTime(postDetail.createTime) }}</p>
          </div>

          <div class="mx-6 mb-4 h-px bg-gray-100 dark:bg-gray-800"></div>

          <PostCommentsPanel
            :comments="comments"
            :is-loading="isLoadingComments"
            :total-comments-count="totalCommentsCount"
            :current-sort-type="currentSortType"
            :post-user-id="postDetail.userId"
            :avatar-fallback="avatarFallback"
            :comments-label="COPY.CommentsCount(totalCommentsCount)"
            :empty-comments-title="COPY.emptyCommentsTitle"
            :hottest-label="COPY.hottest"
            :newest-label="COPY.newest"
            :loading-label="COPY.loading"
            :empty-label="COPY.emptyComments"
            :author-badge="COPY.authorBadge"
            :reply-label="COPY.reply"
            :reply-to-label="COPY.replyTo"
            :expand-replies-label="COPY.expandReplies"
            :expand-more-replies-label="COPY.expandMoreReplies"
            :format-time="formatDetailTime"
            :on-reply="setReply"
            :on-toggle-like="toggleCommentLike"
            :on-expand-replies="expandReplies"
            :on-open-user-page="goToUserProfile"
            @sort-change="handleSortChange"
          />
        </div>

        <PostCommentComposer
          :model-value="commentText"
          :readonly="!authStore.isLoggedIn"
          :placeholder="composerPlaceholder"
          :can-submit="canSubmitComment"
          :sending="isSending"
          :send-label="COPY.send"
          :sending-label="COPY.sending"
          :is-liked="isPostLiked"
          :like-count-label="likeCountLabel"
          :replying-to="replyingTo"
          :replying-to-label="COPY.replyingTo"
          :bind-input-ref="bindCommentInputRef"
          @update:model-value="updateCommentText"
          @submit="handleSendComment"
          @toggle-like="handlePostLike"
          @clear-reply="clearReply"
          @input-click="handleInputClick"
        />
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'

import { ElMessage } from 'element-plus'

import { fetchFollowStatus, toggleFollowUser } from '@/modules/profile/profile.api'
import PostCommentComposer from '@/modules/post/components/PostCommentComposer.vue'
import PostCommentsPanel from '@/modules/post/components/PostCommentsPanel.vue'
import { usePostComments } from '@/modules/post/composables/usePostComments'
import { usePostLikeAction } from '@/modules/post/composables/usePostLikeAction'
import { formatDetailCount, formatDetailTime, splitPostMedia, splitPostTags } from '@/modules/post/post-detail.utils'
import { useOpenUserPage } from '@/shared/composables/useOpenUserPage'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import type { Post } from '@/types'

const COPY = {
  follow: '\u5173\u6ce8',
  following: '\u5df2\u5173\u6ce8',
  emptyCommentsTitle: '\u8fd9\u662f\u4e00\u7247\u8352\u5730',
  hottest: '\u6700\u70ed',
  newest: '\u6700\u65b0',
  loading: '\u52a0\u8f7d\u4e2d...',
  emptyComments: '\u8fd8\u6ca1\u6709\u4eba\u8bc4\u8bba\uff0c\u5feb\u6765\u62a2\u6c99\u53d1~',
  authorBadge: '\u4f5c\u8005',
  reply: '\u56de\u590d',
  replyTo: '\u56de\u590d',
  expandReplies: '\u5c55\u5f00\u56de\u590d',
  expandMoreReplies: '\u5c55\u5f00\u66f4\u591a\u56de\u590d',
  replyingTo: '\u56de\u590d',
  loginToComment: '\u767b\u5f55\u540e\u8bc4\u8bba',
  saySomething: '\u8bf4\u70b9\u4ec0\u4e48...',
  replyPlaceholder: '\u56de\u590d @',
  sending: '\u53d1\u9001\u4e2d',
  send: '\u53d1\u9001',
  likeSuccess: '\u70b9\u8d5e\u6210\u529f',
  unlikeSuccess: '\u53d6\u6d88\u70b9\u8d5e',
  likeFailed: '\u70b9\u8d5e\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5',
  followFailed: '\u64cd\u4f5c\u5931\u8d25',
  commentSent: '\u8bc4\u8bba\u53d1\u9001\u6210\u529f',
  commentFailed: '\u8bc4\u8bba\u53d1\u9001\u5931\u8d25',
  CommentsCount: (count: number | string) => `\u5171 ${count} \u6761\u8bc4\u8bba`,
} as const

interface Props {
  post: Post | null
  visible: boolean
  triggerRect: DOMRect | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'like-toggle', postId: number, isLiked: boolean): void
}>()

const authStore = useAuthStore()
const likeStore = useLikeStore()
const { openUserPage } = useOpenUserPage()
const { toggleLike } = usePostLikeAction()

const postDetail = ref<Post | null>(null)
const detailVideoRef = ref<HTMLVideoElement | null>(null)
const isExpanded = ref(false)
const isFollowing = ref<boolean | null>(null)
const currentImageIndex = ref(0)
const scrollAreaRef = ref<HTMLElement | null>(null)
const modalStyle = ref<Record<string, string>>({
  top: '0px',
  left: '0px',
  width: '0px',
  height: '0px',
  opacity: '0',
  borderRadius: '16px',
})

const {
  comments,
  isLoadingComments,
  currentSortType,
  totalCommentsCount,
  commentText,
  isSending,
  replyingTo,
  commentInputRef,
  bindCommentInputRef,
  loadComments,
  changeSort,
  expandReplies,
  toggleCommentLike,
  setReply,
  clearReply,
  resetComposer,
  sendComment,
} = usePostComments()

const imageList = computed(() => splitPostMedia(postDetail.value))
const tagList = computed(() => splitPostTags(postDetail.value))
const firstImage = computed(() => imageList.value[0] || '')
const isVideoPost = computed(() => Number(postDetail.value?.isVideo) === 1)
const composerPlaceholder = computed(() => {
  if (!authStore.isLoggedIn) {
    return COPY.loginToComment
  }

  if (replyingTo.value) {
    return `${COPY.replyPlaceholder}${replyingTo.value.authorName}`
  }

  return COPY.saySomething
})
const canSubmitComment = computed(() => Boolean(commentText.value.trim()) && !isSending.value)
const isPostLiked = computed(() => Boolean(postDetail.value && likeStore.isPostLiked(postDetail.value.id)))
const likeCountLabel = computed(() => {
  const count = postDetail.value?.likesCount ?? postDetail.value?.likeCount
  return count ? formatDetailCount(count) : '0'
})

const avatarFallback = 'http://localhost:8080/1.jpg'

const goToUserProfile = (userId?: number | null) => {
  openUserPage(userId)
}

const updateCommentText = (value: string) => {
  commentText.value = value
}

const handleClose = () => {
  emit('close')
}

const applyInitialModalStyle = () => {
  if (props.triggerRect) {
    modalStyle.value = {
      top: `${props.triggerRect.top}px`,
      left: `${props.triggerRect.left}px`,
      width: `${props.triggerRect.width}px`,
      height: `${props.triggerRect.height}px`,
      opacity: '1',
      borderRadius: '16px',
    }
    return
  }

  modalStyle.value = {
    top: '50%',
    left: '50%',
    width: '420px',
    height: '560px',
    opacity: '1',
    borderRadius: '20px',
    transform: 'translate(-50%, -50%)',
  }
}

const resolveMediaRatio = async () => {
  if (isVideoPost.value || !firstImage.value) {
    return 0.75
  }

  return await new Promise<number>((resolve) => {
    const image = new Image()
    image.src = firstImage.value

    const finish = () => {
      if (image.naturalWidth > 0 && image.naturalHeight > 0) {
        resolve(image.naturalWidth / image.naturalHeight)
        return
      }

      resolve(1)
    }

    if (image.complete) {
      finish()
      return
    }

    image.onload = finish
    image.onerror = () => resolve(1)
  })
}

const animateModalOpen = async () => {
  const mediaRatio = await resolveMediaRatio()

  requestAnimationFrame(() => {
    const finalHeight = window.innerHeight * 0.9
    const sidePanelWidth = window.innerWidth < 1024 ? 360 : 400

    let mediaWidth = mediaRatio <= 1.2 ? finalHeight * mediaRatio : Math.min(850, window.innerWidth * 0.55)
    mediaWidth = Math.max(mediaWidth, 420)

    const finalWidth = mediaWidth + sidePanelWidth
    const finalTop = (window.innerHeight - finalHeight) / 2
    const finalLeft = (window.innerWidth - finalWidth) / 2

    modalStyle.value = {
      top: `${finalTop}px`,
      left: `${finalLeft}px`,
      width: `${finalWidth}px`,
      height: `${finalHeight}px`,
      opacity: '1',
      borderRadius: '20px',
    }

    window.setTimeout(() => {
      isExpanded.value = true
    }, 280)
  })
}

const loadFollowState = async (authorId: number) => {
  if (!authStore.isLoggedIn) {
    isFollowing.value = false
    return
  }

  try {
    isFollowing.value = await fetchFollowStatus(authorId)
  } catch {
    isFollowing.value = false
  }
}

const openPostDetail = async () => {
  if (!props.post) return

  document.body.style.overflow = 'hidden'
  postDetail.value = { ...props.post }
  currentImageIndex.value = 0
  isExpanded.value = false
  isFollowing.value = null
  resetComposer()
  applyInitialModalStyle()

  await Promise.all([
    loadComments(props.post.id),
    props.post.userId ? loadFollowState(props.post.userId) : Promise.resolve(),
  ])

  await nextTick()

  const initialTime = (props.post as any)._initialVideoTime
  if (isVideoPost.value && detailVideoRef.value && typeof initialTime === 'number') {
    detailVideoRef.value.currentTime = initialTime
  }
  await animateModalOpen()
}

const resetBodyState = () => {
  document.body.style.overflow = ''
  isExpanded.value = false
}

const handleInputClick = (event: Event) => {
  if (authStore.isLoggedIn) return

  event.preventDefault()
  commentInputRef.value?.blur()
  authStore.showLoginModal()
}

const handleSortChange = async (sortType: 1 | 2) => {
  if (!postDetail.value) return
  await changeSort(postDetail.value.id, sortType)
}

const handleSendComment = async () => {
  if (!postDetail.value) return

  try {
    const didSend = await sendComment(postDetail.value.id)
    if (!didSend) return

    ElMessage.success(COPY.commentSent)
    window.setTimeout(() => {
      if (scrollAreaRef.value) {
        scrollAreaRef.value.scrollTop = scrollAreaRef.value.scrollHeight
      }
    }, 120)
  } catch {
    ElMessage.error(COPY.commentFailed)
  }
}

const handlePostLike = async () => {
  if (!postDetail.value) return

  const currentPost = postDetail.value
  const currentCount = Number(currentPost.likesCount || currentPost.likeCount || 0)
  const nextLiked = await toggleLike({
    postId: currentPost.id,
    isLiked: isPostLiked.value,
    likeSuccessMessage: COPY.likeSuccess,
    unlikeSuccessMessage: COPY.unlikeSuccess,
    errorMessage: COPY.likeFailed,
    onToggled: (liked) => {
      const nextCount = Math.max(0, currentCount + (liked ? 1 : -1))
      currentPost.likesCount = nextCount
      currentPost.likeCount = nextCount
    },
  })

  if (nextLiked !== null) {
    emit('like-toggle', currentPost.id, nextLiked)
  }
}

const toggleFollow = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  if (!postDetail.value?.userId) return

  try {
    await toggleFollowUser(postDetail.value.userId)
    isFollowing.value = !isFollowing.value
  } catch {
    ElMessage.error(COPY.followFailed)
  }
}

const prevImage = () => {
  currentImageIndex.value = currentImageIndex.value > 0 ? currentImageIndex.value - 1 : imageList.value.length - 1
}

const nextImage = () => {
  currentImageIndex.value = currentImageIndex.value < imageList.value.length - 1 ? currentImageIndex.value + 1 : 0
}

watch(
  () => [props.visible, props.post?.id] as const,
  ([visible]) => {
    if (visible && props.post) {
      void openPostDetail()
      return
    }

    resetBodyState()
  },
  { immediate: true },
)

watch(
  () => authStore.isLoggedIn,
  () => {
    if (props.visible && postDetail.value?.userId) {
      void loadFollowState(postDetail.value.userId)
    }
  },
)

onBeforeUnmount(() => {
  resetBodyState()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}

.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
