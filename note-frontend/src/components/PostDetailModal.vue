<template>
  <Teleport to="body">
    <Transition name="modal" appear>
      <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <!-- 遮罩背景 -->
        <div
          class="absolute inset-0 bg-black bg-opacity-75 transition-opacity"
          @click="handleClose"
        ></div>

        <!-- 弹窗内容 -->
        <div
          class="relative bg-white rounded-2xl max-w-7xl w-full max-h-[90vh] overflow-hidden"
          :class="{ 'max-w-5xl': isMobile }"
        >
          <div class="flex h-full">
            <!-- 左侧图片区域 -->
            <div class="flex-1 relative">
              <!-- 单图显示 -->
              <div v-if="!post.images.includes(',')" class="relative aspect-[3/4] overflow-hidden">
                <img
                  :src="post.images"
                  :alt="post.title"
                  class="w-full h-full object-cover"
                  v-image-error="{ type: 'image' }"
                />
              </div>

              <!-- 多图轮播 -->
              <div v-else class="relative aspect-[3/4] overflow-hidden">
                <!-- 主图 -->
                <div
                  class="w-full h-full transition-transform duration-300"
                  :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }"
                >
                  <img
                    v-for="(image, index) in imageList"
                    :key="index"
                    :src="image"
                    :alt="`${post.title} ${index + 1}`"
                    class="w-full h-full object-cover flex-shrink-0"
                    v-image-error="{ type: 'image' }"
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

            <!-- 右侧内容区域（PC端显示） -->
            <div v-if="!isMobile" class="w-96 flex-shrink-0 border-l border-gray-100 flex flex-col">
              <!-- 作者信息 -->
              <div class="p-6 border-b border-gray-100">
                <router-link
                  :to="`/user/${post.userId}`"
                  class="flex items-center space-x-3 hover:opacity-80 transition-opacity"
                  @click.stop
                >
                  <div
                    class="w-12 h-12 rounded-full overflow-hidden bg-gray-300"
                    :title="post.authorName"
                  >
                    <img
                      v-if="post.authorAvatar"
                      :src="post.authorAvatar"
                      :alt="post.authorName"
                      class="w-full h-full object-cover"
                      v-image-error="{ type: 'avatar' }"
                    />
                    <span
                      v-else
                      class="w-full h-full flex items-center justify-center text-gray-600 text-lg"
                    >
                      {{ post.authorName?.charAt(0).toUpperCase() }}
                    </span>
                  </div>
                  <div class="flex-1">
                    <p class="font-medium text-gray-900">{{ post.authorName }}</p>
                    <p class="text-sm text-gray-500">{{ formatDate(post.createTime) }}</p>
                  </div>
                </router-link>

                <!-- 关注按钮 -->
                <button
                  v-if="authStore.isLoggedIn && authStore.getUserInfo?.id !== post.userId"
                  class="mt-4 w-full px-4 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors"
                >
                  关注
                </button>
              </div>

              <!-- 内容区域 -->
              <div class="flex-1 overflow-y-auto p-6">
                <!-- 标题 -->
                <h1 class="text-xl font-bold text-gray-900 mb-4">{{ post.title }}</h1>

                <!-- 正文 -->
                <div class="mb-6">
                  <p class="text-gray-700 leading-relaxed whitespace-pre-line">{{ post.content }}</p>
                </div>

                <!-- 标签 -->
                <div v-if="tagList.length > 0" class="mb-6 flex flex-wrap gap-2">
                  <span
                    v-for="tag in tagList"
                    :key="tag"
                    class="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm"
                  >
                    #{{ tag }}
                  </span>
                </div>

                <!-- 点赞和评论数 -->
                <div class="flex items-center justify-between mb-6 pt-6 border-t border-gray-100">
                  <button
                    @click="handleLike"
                    class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors"
                  >
                    <svg
                      class="w-5 h-5"
                      :class="{ 'fill-current text-red-500': isLiked }"
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
                    <span>{{ formatLikeCount(post.likesCount || post.likeCount) }}</span>
                  </button>

                  <button
                    @click="scrollToComments"
                    class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                      />
                    </svg>
                    <span>{{ comments.length }} 评论</span>
                  </button>
                </div>

                <!-- 评论列表 -->
                <div class="space-y-4">
                  <h2 class="text-lg font-bold text-gray-900">评论 {{ comments.length }}</h2>

                  <!-- 评论输入框 -->
                  <div v-if="authStore.isLoggedIn" class="mb-6">
                    <div class="flex space-x-3">
                      <div class="w-10 h-10 rounded-full bg-gray-300 overflow-hidden flex-shrink-0">
                        <img
                          v-if="authStore.getUserAvatar"
                          :src="authStore.getUserAvatar"
                          alt="用户头像"
                          class="w-full h-full object-cover"
                          v-image-error="{ type: 'avatar' }"
                        />
                        <span
                          v-else
                          class="w-full h-full flex items-center justify-center text-gray-600"
                        >
                          {{ authStore.getUsername?.charAt(0).toUpperCase() }}
                        </span>
                      </div>
                      <div class="flex-1">
                        <textarea
                          v-model="newComment"
                          placeholder="写下你的评论..."
                          rows="3"
                          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent resize-none"
                        ></textarea>
                        <div class="mt-2 flex justify-end">
                          <button
                            @click="handleComment"
                            :disabled="!newComment.trim() || isSubmitting"
                            class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                          >
                            {{ isSubmitting ? '发送中...' : '发送' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 未登录提示 -->
                  <div v-else class="mb-6 text-center py-4">
                    <p class="text-gray-500">
                      <a
                        href="#"
                        @click="authStore.showLoginModal()"
                        class="text-red-500 hover:text-red-600 font-medium"
                      >登录</a>
                      后参与评论
                    </p>
                  </div>

                  <!-- 评论列表 -->
                  <div class="space-y-4">
                    <div
                      v-for="comment in comments"
                      :key="comment.id"
                      class="flex space-x-3"
                    >
                      <div class="w-10 h-10 rounded-full bg-gray-300 overflow-hidden flex-shrink-0">
                        <img
                          :src="comment.authorAvatar"
                          :alt="comment.authorName"
                          class="w-full h-full object-cover"
                          v-image-error="{ type: 'avatar', size: '40' }"
                        />
                      </div>
                      <div class="flex-1">
                        <div class="bg-gray-50 rounded-lg p-4">
                          <div class="flex items-center justify-between mb-1">
                            <p class="font-medium text-gray-900">{{ comment.authorName }}</p>
                            <span class="text-xs text-gray-500">{{ formatDate(comment.createTime) }}</span>
                          </div>
                          <p class="text-gray-700">{{ comment.content }}</p>
                        </div>

                        <!-- 回复列表 -->
                        <div v-if="comment.replies && comment.replies.length > 0" class="mt-3 space-y-3 ml-4">
                          <div
                            v-for="reply in comment.replies"
                            :key="reply.id"
                            class="flex space-x-2"
                          >
                            <div class="w-8 h-8 rounded-full bg-gray-300 overflow-hidden flex-shrink-0">
                              <img
                                :src="reply.authorAvatar"
                                :alt="reply.authorName"
                                class="w-full h-full object-cover"
                                v-image-error="{ type: 'avatar', size: '32' }"
                              />
                            </div>
                            <div class="flex-1">
                              <div class="bg-gray-50 rounded-lg p-3">
                                <div class="flex items-center space-x-2 mb-1">
                                  <span class="font-medium text-gray-900 text-sm">{{ reply.authorName }}</span>
                                  <span
                                    v-if="reply.replyToUserName"
                                    class="text-sm text-gray-500"
                                  >@{{ reply.replyToUserName }}</span>
                                  <span class="text-xs text-gray-500">{{ formatDate(reply.createTime) }}</span>
                                </div>
                                <p class="text-gray-700 text-sm">{{ reply.content }}</p>
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 回复按钮 -->
                        <button
                          v-if="authStore.isLoggedIn"
                          @click="toggleReply(comment)"
                          class="mt-2 text-sm text-gray-500 hover:text-gray-700 transition-colors"
                        >
                          回复
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- 加载更多 -->
                  <div v-if="hasMoreComments" class="text-center mt-6">
                    <button
                      @click="loadMoreComments"
                      :disabled="loadingComments"
                      class="px-4 py-2 text-gray-600 hover:text-gray-800 disabled:opacity-50 transition-colors"
                    >
                      {{ loadingComments ? '加载中...' : '加载更多评论' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 移动端底部操作栏 -->
          <div v-if="isMobile" class="absolute bottom-0 left-0 right-0 bg-white border-t border-gray-100 p-4">
            <div class="flex items-center justify-between">
              <button
                @click="handleLike"
                class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors"
              >
                <svg
                  class="w-6 h-6"
                  :class="{ 'fill-current text-red-500': isLiked }"
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
                <span>{{ formatLikeCount(post.likesCount || post.likeCount) }}</span>
              </button>

              <button
                @click="scrollToComments"
                class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors"
              >
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                  />
                </svg>
                <span>{{ comments.length }} 评论</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { get, post as postRequest } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { Post, Comment } from '@/types'

interface Props {
  post: Post
  visible: boolean
}

const props = defineProps<Props>()
const emit = defineEmits(['close', 'like-toggle'])

const authStore = useAuthStore()
const likeStore = useLikeStore()

// 响应式判断
const isMobile = computed(() => window.innerWidth < 1024)

// 图片相关
const currentImageIndex = ref(0)
const imageList = computed(() => props.post.images.split(',').map(img => img.trim()))
const tagList = computed(() => {
  const tags = (props.post as any).tags
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

// 点赞状态
const isLiked = computed(() => likeStore.isPostLiked(props.post.id))

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

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString()
}

// 图片切换
const prevImage = () => {
  currentImageIndex.value = (currentImageIndex.value - 1 + imageList.value.length) % imageList.value.length
}

const nextImage = () => {
  currentImageIndex.value = (currentImageIndex.value + 1) % imageList.value.length
}

// 获取评论
const fetchComments = async () => {
  try {
    const response = await get<Comment[]>(`/comment/${props.post.id}`)
    if (response.code === 1) {
      comments.value = response.data
    } else {
      ElMessage.error(response.msg || '获取评论失败')
    }
  } catch (error: any) {
    ElMessage.error('获取评论失败，请检查网络连接')
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
    const newLikedState = !isLiked.value
    await postRequest(`/post/${props.post.id}/like`)

    // 更新点赞状态
    if (newLikedState) {
      props.post.likesCount = (props.post.likesCount || 0) + 1
      props.post.likeCount = (props.post.likeCount || 0) + 1
      likeStore.addLikedPost(props.post.id)
    } else {
      props.post.likesCount = Math.max(0, (props.post.likesCount || 0) - 1)
      props.post.likeCount = Math.max(0, (props.post.likeCount || 0) - 1)
      likeStore.removeLikedPost(props.post.id)
    }

    emit('like-toggle')
    ElMessage.success(newLikedState ? '点赞成功！' : '取消点赞')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 处理评论
const handleComment = async () => {
  if (!newComment.value.trim()) return

  try {
    isSubmitting.value = true

    const commentData: any = {
      postId: props.post.id,
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
  } catch (error: any) {
    ElMessage.error(error.message || '评论失败，请稍后重试')
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
    newComment.value = `@${comment.authorName} `
  }
}

// 滚动到评论
const scrollToComments = () => {
  nextTick(() => {
    const commentsSection = document.querySelector('.space-y-4')
    if (commentsSection) {
      commentsSection.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  })
}

// 加载更多评论
const loadMoreComments = async () => {
  // 这里可以实现分页加载评论的逻辑
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
}

// 组件挂载时获取评论
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
/* 动画 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from {
  opacity: 0;
  transform: scale(0.9);
}

.modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* 移动端适配 */
@media (max-width: 1023px) {
  :deep(.bg-white.rounded-2xl) {
    max-height: 100vh;
    border-radius: 1rem;
  }

  .flex > div:first-child {
    max-height: 60vh;
  }

  .w-96 {
    display: none;
  }
}
</style>