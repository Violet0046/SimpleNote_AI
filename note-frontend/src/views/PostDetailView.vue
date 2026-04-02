<template>
  <div class="bg-gray-50 min-h-screen">
    <div class="max-w-4xl mx-auto px-4 py-8">
      <!-- 返回按钮 -->
      <button
        @click="router.go(-1)"
        class="mb-4 flex items-center text-gray-600 hover:text-gray-900 transition-colors"
      >
        <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
        </svg>
        返回
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2442]"></div>
          <span class="ml-3 text-gray-500">加载中...</span>
        </div>
      </div>

      <!-- 帖子内容 -->
      <div v-else-if="post" class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <!-- 图片区域 -->
        <div class="relative aspect-[3/4] overflow-hidden">
          <img
            :src="post.images"
            :alt="post.title"
            class="w-full h-full object-cover"
            v-image-error="{ type: 'image' }"
          />
        </div>

        <!-- 内容区域 -->
        <div class="p-6">
          <!-- 标题 -->
          <h1 class="text-2xl font-bold text-gray-900 mb-4">{{ post.title }}</h1>

          <!-- 作者信息 -->
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-3">
              <router-link
                :to="`/user/${post.userId}`"
                class="flex items-center space-x-2 hover:opacity-80 transition-opacity"
              >
                <div
                  class="w-10 h-10 rounded-full overflow-hidden bg-gray-300"
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
                    class="w-full h-full flex items-center justify-center text-gray-600"
                  >
                    {{ post.authorName?.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div>
                  <p class="font-medium text-gray-900">{{ post.authorName }}</p>
                  <p class="text-xs text-gray-500">{{ formatDate(post.createTime) }}</p>
                </div>
              </router-link>
            </div>

            <!-- 关注按钮 -->
            <button
              v-if="authStore.isLoggedIn && authStore.getUserInfo?.id !== post.userId"
              class="px-4 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors"
            >
              关注
            </button>
          </div>

          <!-- 正文内容 -->
          <div class="mb-6">
            <p class="text-gray-700 leading-relaxed whitespace-pre-line">{{ post.content }}</p>
          </div>

          <!-- 点赞和评论 -->
          <div class="flex items-center justify-between pt-6 border-t border-gray-100">
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
              <span>{{ formatLikeCount(post.likesCount ?? post.likeCount ?? 0) }}</span>
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

          <!-- 标签 -->
          <div v-if="post.tags && post.tags.length > 0" class="mt-6 flex flex-wrap gap-2">
            <span
              v-for="tag in post.tags?.split(',') || []"
              :key="tag"
              class="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm"
            >
              #{{ tag }}
            </span>
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="border-t border-gray-100" ref="commentsSection">
          <div class="p-6">
            <h2 class="text-xl font-bold text-gray-900 mb-6">评论 {{ comments.length }}</h2>

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
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent"
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
            <div class="space-y-6">
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
                          v-image-error="{ type: 'avatar', size: '40' }"
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

            <!-- 加载更多评论 -->
            <div v-if="hasMoreComments" class="mt-6 text-center">
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

      <!-- 404 -->
      <div v-else class="text-center py-12">
        <p class="text-gray-500">帖子不存在或已被删除</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { Post, Comment } from '@/types'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 帖子数据
const post = ref<Post | null>(null)
const loading = ref(true)
const isLiked = ref(false)
const isSubmitting = ref(false)

// 评论相关
const comments = ref<Comment[]>([])
const loadingComments = ref(false)
const hasMoreComments = ref(true)
const newComment = ref('')
const replyToComment = ref<Comment | null>(null)

// 获取帖子详情
const fetchPost = async () => {
  try {
    const response = await get<Post>(`/post/${route.params.id}`)
    if (response.code === 1) {
      post.value = response.data
      // 检查是否已点赞
      isLiked.value = false // 这里可以根据实际API返回的点赞状态更新
    } else {
      ElMessage.error(response.msg || '获取帖子详情失败')
    }
  } catch (error: any) {
    ElMessage.error('获取帖子详情失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    const response = await get<Comment[]>(`/comment/${route.params.id}`)
    if (response.code === 1) {
      comments.value = response.data
    } else {
      ElMessage.error(response.msg || '获取评论失败')
    }
  } catch (error: any) {
    ElMessage.error('获取评论失败，请检查网络连接')
  }
}

// 格式化点赞数
const formatLikeCount = (count: number | null | undefined) => {
  const value = count ?? 0
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + 'w'
  }
  if (value >= 1000) {
    return (value / 1000).toFixed(1) + 'k'
  }
  return value.toString()
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

// 处理点赞
const handleLike = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  try {
    isSubmitting.value = true
    await post(`/post/${route.params.id}/like`)

    // 更新点赞状态
    isLiked.value = !isLiked.value
    if (isLiked.value) {
      post.value!.likesCount = (post.value!.likesCount || 0) + 1
    } else {
      post.value!.likesCount = Math.max(0, (post.value!.likesCount || 0) - 1)
    }
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
      postId: route.params.id,
      content: newComment.value
    }

    // 如果是回复，添加回复信息
    if (replyToComment.value) {
      commentData.parentId = replyToComment.value.id
      commentData.replyToUserId = replyToComment.value.userId
    }

    const response = await post('/comment/add', commentData)

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

// 滚动到评论区域
const scrollToComments = () => {
  nextTick(() => {
    document.getElementById('comments-section')?.scrollIntoView({ behavior: 'smooth' })
  })
}

// 组件挂载
onMounted(() => {
  fetchPost()
  fetchComments()
})
</script>