<template>
  <Teleport to="body">
    <!-- 毛玻璃背景遮罩 -->
    <Transition
      name="modal-overlay"
      appear
    >
      <div
        v-if="visible"
        class="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm"
        @click="handleClose"
      ></div>
    </Transition>

    <!-- 弹窗内容 -->
    <Transition
      name="modal-content"
      appear
    >
      <div
        v-if="visible"
        ref="modalRef"
        class="fixed z-50 bg-white rounded-2xl shadow-2xl overflow-hidden"
        :style="modalStyle"
        @click.stop
      >
        <div class="flex h-full" style="height: 100%;">
          <!-- 左侧图片区域 -->
          <div class="flex-1 relative bg-gray-100" style="min-height: 100%;">
            <!-- 使用传入的缩略图作为背景，避免黑屏 -->
            <div
              v-if="initialRect && firstImage"
              class="absolute inset-0 bg-cover bg-center"
              :style="{ backgroundImage: 'url(' + firstImage + ')', opacity: 0 }"
            ></div>

            <!-- 单图显示 -->
            <div v-if="!postDetail.images.includes(',')" class="relative aspect-[3/4] overflow-hidden">
              <img
                :src="postDetail.images"
                :alt="postDetail.title"
                class="w-full h-full object-cover transition-opacity duration-300"
                :class="{ 'opacity-100': !initialRect, 'opacity-0': initialRect }"
                v-image-error="{ type: 'image' }"
                loading="eager"
              />
            </div>

            <!-- 多图轮播 -->
            <div v-else class="relative aspect-[3/4] overflow-hidden">
              <div
                class="w-full h-full transition-transform duration-300"
                :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }"
              >
                <img
                  v-for="(image, index) in imageList"
                  :key="index"
                  :src="image"
                  :alt="`${postDetail.title} ${index + 1}`"
                  class="w-full h-full object-cover flex-shrink-0 transition-opacity duration-300"
                  :class="{ 'opacity-100': !initialRect, 'opacity-0': initialRect }"
                  v-image-error="{ type: 'image' }"
                  loading="eager"
                />
              </div>

              <!-- 左右切换按钮 -->
              <button
                v-if="imageList.length > 1"
                @click="prevImage"
                class="absolute left-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-black bg-opacity-30 text-white rounded-full flex items-center justify-center hover:bg-opacity-50 transition-all"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <button
                v-if="imageList.length > 1"
                @click="nextImage"
                class="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-black bg-opacity-30 text-white rounded-full flex items-center justify-center hover:bg-opacity-50 transition-all"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>

              <!-- 图片指示器 -->
              <div v-if="imageList.length > 1" class="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
                <div
                  v-for="(_, index) in imageList"
                  :key="index"
                  class="w-2 h-2 rounded-full transition-all"
                  :class="currentImageIndex === index ? 'bg-white' : 'bg-white bg-opacity-50'"
                ></div>
              </div>

              <!-- 视频角标 -->
              <div
                v-if="postDetail.isVideo"
                class="absolute top-2 right-2 w-8 h-8 bg-black bg-opacity-50 rounded-full flex items-center justify-center"
              >
                <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M8 5v14l11-7z"/>
                </svg>
              </div>

              <!-- 关闭按钮 -->
              <button
                @click="handleClose"
                class="absolute top-4 right-4 w-10 h-10 bg-black bg-opacity-30 text-white rounded-full flex items-center justify-center hover:bg-opacity-50 transition-all"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- 右侧内容区域 -->
          <div class="w-96 border-l border-gray-100 flex flex-col" style="min-height: 0;">
            <!-- 作者信息 -->
            <div class="p-6 border-b border-gray-100 flex items-center space-x-3">
              <router-link
                :to="`/user/${postDetail.userId}`"
                class="flex items-center space-x-3 hover:opacity-80 transition-opacity"
                @click.stop
              >
                <div
                  class="w-12 h-12 rounded-full overflow-hidden bg-gray-300 flex-shrink-0"
                  :title="postDetail.authorName"
                >
                  <img
                    v-if="postDetail.authorAvatar"
                    :src="postDetail.authorAvatar"
                    :alt="postDetail.authorName"
                    class="w-full h-full object-cover"
                    v-image-error="{ type: 'avatar' }"
                  />
                  <span v-else class="w-full h-full flex items-center justify-center text-gray-600 text-sm">
                    {{ postDetail.authorName?.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-medium text-gray-900 truncate">{{ postDetail.authorName }}</p>
                  <p class="text-sm text-gray-500">{{ postDetail.createTime }}</p>
                </div>
              </router-link>
            </div>

            <!-- 标题和标签 -->
            <div class="p-6 border-b border-gray-100">
              <h2 class="text-xl font-bold text-gray-900 mb-3">{{ postDetail.title }}</h2>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="tag in tagList"
                  :key="tag"
                  class="px-3 py-1 bg-[#ff2442] bg-opacity-10 text-[#ff2442] text-sm rounded-full"
                >
                  #{{ tag }}
                </span>
              </div>
            </div>

            <!-- 点赞和评论按钮 -->
            <div class="px-6 py-4 border-b border-gray-100">
              <div class="flex items-center justify-between">
                <button
                  @click="handleLike"
                  class="flex items-center space-x-2 text-gray-500 hover:text-[#FF2442] transition-colors"
                >
                  <svg
                    class="w-6 h-6"
                    :class="{ 'fill-current text-[#FF2442]': isLiked }"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                    />
                  </svg>
                  <span>{{ formatLikeCount(postDetail.likesCount) }}</span>
                </button>
                <button class="flex items-center space-x-2 text-gray-500 hover:text-gray-700 transition-colors">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                    />
                  </svg>
                  <span>{{ postDetail.commentCount || 0 }}</span>
                </button>
                <button class="flex items-center space-x-2 text-gray-500 hover:text-gray-700 transition-colors">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"
                    />
                  </svg>
                </button>
              </div>
            </div>

            <!-- 评论区域 -->
            <div class="flex-1 overflow-hidden flex flex-col transition-opacity duration-300"
                 :class="initialRect ? 'opacity-0' : 'opacity-100'">
              <!-- 评论列表 -->
              <div class="flex-1 overflow-y-auto px-6 py-4 space-y-4">
                <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="flex space-x-3 pb-4 border-b border-gray-100 last:border-0"
                >
                  <div
                    class="w-10 h-10 rounded-full overflow-hidden bg-gray-300 flex-shrink-0"
                    :title="comment.userName"
                  >
                    <img
                      v-if="comment.userAvatar"
                      :src="comment.userAvatar"
                      :alt="comment.userName"
                      class="w-full h-full object-cover"
                      v-image-error="{ type: 'avatar' }"
                    />
                    <span v-else class="w-full h-full flex items-center justify-center text-gray-600 text-xs">
                      {{ comment.userName?.charAt(0).toUpperCase() }}
                    </span>
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center space-x-2 mb-1">
                      <p class="font-medium text-gray-900">{{ comment.userName }}</p>
                      <p class="text-sm text-gray-500">{{ comment.createTime }}</p>
                    </div>
                    <p class="text-gray-700">{{ comment.content }}</p>
                  </div>
                </div>

                <!-- 加载更多 -->
                <div v-if="loadingComments" class="text-center py-4">
                  <div class="inline-flex items-center">
                    <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-[#ff2442]"></div>
                    <span class="ml-2 text-gray-500">加载中...</span>
                  </div>
                </div>
              </div>

              <!-- 评论输入框 -->
              <div class="border-t border-gray-100 p-4">
                <div class="flex space-x-3">
                  <input
                    v-model="newComment"
                    type="text"
                    placeholder="写下你的评论..."
                    class="flex-1 px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:border-[#FF2442] focus:bg-white"
                    @keyup.enter="handleComment"
                  />
                  <button
                    @click="handleComment"
                    :disabled="!newComment.trim() || isSubmitting"
                    class="px-6 py-2 bg-[#FF2442] text-white rounded-full hover:bg-opacity-90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    发送
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { get, post as postRequest } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { Post, Comment } from '@/types'

interface Props {
  post: Post
  visible: boolean
  triggerRect?: DOMRect | null
}

const props = defineProps<Props>()
const emit = defineEmits(['close', 'like-toggle'])

const authStore = useAuthStore()
const likeStore = useLikeStore()

// 模态框引用
const modalRef = ref<HTMLElement>()
const modalStyle = ref({
  top: '0px',
  left: '0px',
  width: '0px',
  height: '0px',
  transform: 'none',
  opacity: '0'
})

// 初始矩形（用于动画）
const initialRect = ref<DOMRect | null>(null)
const isAnimating = ref(false)

// 帖子详情数据
const postDetail = ref<Post>(props.post)

// 图片相关
const firstImage = computed(() => {
  if (!postDetail.value.images) return ''
  const images = postDetail.value.images.split(',').map(img => img.trim()).filter(img => img)
  return images[0] || ''
})

const currentImageIndex = ref(0)
const imageList = computed(() => postDetail.value.images.split(',').map(img => img.trim()).filter(img => img))

// 标签相关
const tagList = computed(() => {
  const tags = (postDetail.value as Post).tags
  if (!tags || typeof tags !== 'string') return []
  return tags
    .split(',')
    .map((t: string) => t.trim())
    .filter((t: string) => t)
})

// 评论相关
const comments = ref<Comment[]>([])
const loadingComments = ref(false)
const hasMoreComments = ref(true)
const newComment = ref('')
const replyToComment = ref<Comment | null>(null)
const isSubmitting = ref(false)

// 计算属性
const isLiked = computed(() => likeStore.isPostLiked(postDetail.value.id))

// 格式化点赞数
const formatLikeCount = (count: number | null | undefined) => {
  const safeCount = count ?? 0
  if (safeCount >= 10000) {
    return (safeCount / 10000).toFixed(1) + 'w'
  }
  if (safeCount >= 1000) {
    return (safeCount / 1000).toFixed(1) + 'k'
  }
  return safeCount.toString()
}

// 处理动画
const animateModal = () => {
  if (!initialRect.value || !modalRef.value) return

  // 计算最终尺寸
  const finalWidth = Math.min(window.innerWidth * 0.7, 1200)
  const finalHeight = Math.min(window.innerHeight * 0.9, 800)
  const finalLeft = (window.innerWidth - finalWidth) / 2
  const finalTop = (window.innerHeight - finalHeight) / 2

  // 更新样式到最终状态
  modalStyle.value = {
    top: `${finalTop}px`,
    left: `${finalLeft}px`,
    width: `${finalWidth}px`,
    height: `${finalHeight}px`,
    transform: 'translate(0, 0)',
    opacity: '1'
  }

  // 动画结束后显示评论区
  setTimeout(() => {
    isAnimating.value = false
  }, 300)
}

// 监听 visible 变化
watch(() => props.visible, (visible) => {
  if (visible) {
    // 设置 body overflow hidden
    document.body.style.overflow = 'hidden'

    // 设置初始矩形
    if (props.triggerRect) {
      initialRect.value = props.triggerRect
      modalStyle.value = {
        top: `${props.triggerRect.top}px`,
        left: `${props.triggerRect.left}px`,
        width: `${props.triggerRect.width}px`,
        height: `${props.triggerRect.height}px`,
        transform: 'none',
        opacity: '0'
      }
    }

    // 下一帧开始动画
    nextTick(() => {
      setTimeout(() => {
        animateModal()
      }, 50)
    })

    // 重置数据
    comments.value = []
    newComment.value = ''
    replyToComment.value = null
    postDetail.value = props.post
  } else {
    // 恢复 body overflow
    document.body.style.overflow = ''
    initialRect.value = null
    isAnimating.value = true
  }
}, { immediate: true })

// 获取帖子详情
const fetchPostDetail = async () => {
  try {
    const response = await get<Post>('/post/detail', { id: props.post.id })
    if (response.code === 1) {
      postDetail.value = response.data
    } else {
      ElMessage.error(response.msg || '获取帖子详情失败')
    }
  } catch (_error: unknown) {
    ElMessage.error('获取帖子详情失败，请检查网络连接')
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    loadingComments.value = true
    const response = await get<Comment[]>('/comment/list', { postId: postDetail.value.id })
    if (response.code === 1) {
      comments.value = response.data
    } else {
      ElMessage.error(response.msg || '获取评论失败')
    }
  } catch (_error: unknown) {
    ElMessage.error('获取评论失败，请检查网络连接')
  } finally {
    loadingComments.value = false
  }
}

// 处理点赞
const handleLike = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  try {
    isSubmitting.value = true

    // 切换点赞状态
    const newLikedState = !isLiked.value

    // 调用点赞 API
    await postRequest(`/post/${postDetail.value.id}/like`)

    // 更新本地状态
    if (newLikedState) {
      likeStore.addLikedPost(postDetail.value.id)
      ElMessage.success('点赞成功！')
    } else {
      likeStore.removeLikedPost(postDetail.value.id)
      ElMessage.success('取消点赞')
    }

    // 通知父组件
    emit('like-toggle')
  } catch (error: unknown) {
    ElMessage.error((error as Error).message || '操作失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 处理评论
const handleComment = async () => {
  if (!newComment.value.trim()) return

  try {
    isSubmitting.value = true

    const commentData: {
      postId: number
      content: string
      commentId?: number
      parentId?: number
      replyToUserId?: number
    } = {
      postId: postDetail.value.id,
      content: newComment.value
    }

    // 如果是回复，添加回复信息
    if (replyToComment.value) {
      commentData.parentId = replyToComment.value.id
      commentData.replyToUserId = replyToComment.value.userId
    }

    const response = await postRequest('/comment/add', commentData)

    if (response.code === 1) {
      ElMessage.success('评论成功！')
      newComment.value = ''
      replyToComment.value = null
      // 重新获取评论列表
      await fetchComments()
    } else {
      ElMessage.error(response.msg || '评论失败')
    }
  } catch (error: unknown) {
    ElMessage.error((error as Error).message || '评论失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 切换回复
const toggleReply = (comment: Comment) => {
  if (replyToComment.value?.id === comment.id) {
    replyToComment.value = null
    newComment.value = ''
  } else {
    replyToComment.value = comment
    newComment.value = `@${comment.userName} `
  }
}

// 图片切换
const prevImage = () => {
  currentImageIndex.value = currentImageIndex.value > 0 ? currentImageIndex.value - 1 : imageList.value.length - 1
}

const nextImage = () => {
  currentImageIndex.value = currentImageIndex.value < imageList.value.length - 1 ? currentImageIndex.value + 1 : 0
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
}

// 监听 post 变化
watch(() => props.post, (newPost) => {
  if (newPost) {
    postDetail.value = newPost
    currentImageIndex.value = 0
  }
}, { immediate: true })
</script>

<style scoped>
/* 背景动画 */
.modal-overlay-enter-active,
.modal-overlay-leave-active {
  transition: opacity 0.3s ease;
}

.modal-overlay-enter-from,
.modal-overlay-leave-to {
  opacity: 0;
}

/* 弹窗内容动画 */
.modal-content-enter-active,
.modal-content-leave-active {
  transition: none;
}

/* 弹窗主容器需要添加 transition-all 类 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.2, 0, 0.2, 1);
  transition-duration: 300ms;
}
</style>