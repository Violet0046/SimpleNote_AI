<template>
  <div
    class="group relative break-ins-avoid cursor-pointer transition-all duration-300 hover:scale-[1.02] hover:shadow-lg mb-4"
    @click="handleCardClick"
  >
    <!-- 图片容器 -->
    <div class="relative aspect-[3/4] overflow-hidden rounded-xl">
      <!-- 图片 -->
      <img
        :src="post.images || 'https://via.placeholder.com/400'"
        :alt="post.title"
        class="w-full h-full object-cover"
        loading="lazy"
      />

      <!-- 图片加载状态 -->
      <div v-if="isLoading" class="absolute inset-0 bg-gray-200 animate-pulse">
        <div class="w-full h-full bg-gradient-to-br from-[#ff2442] to-[#ff6b6b] flex items-center justify-center">
          <span class="text-white text-lg">图片</span>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="mt-2 px-1">
      <!-- 标题（最多两行） -->
      <h3
        class="text-sm font-medium text-gray-900 mb-1 line-clamp-2 leading-tight"
        :title="post.title"
      >
        {{ post.title }}
      </h3>

      <!-- 作者信息和点赞 -->
      <div class="flex items-center justify-between">
        <!-- 作者头像和昵称 -->
        <router-link
          :to="`/user/${post.userId}`"
          class="flex items-center space-x-1 text-xs text-gray-500 hover:text-gray-700 transition-colors"
          @click.stop
        >
          <div
            class="w-5 h-5 rounded-full overflow-hidden bg-gray-300 flex-shrink-0"
            :title="post.authorName"
          >
            <img
              v-if="post.authorAvatar"
              :src="post.authorAvatar"
              :alt="post.authorName"
              class="w-full h-full object-cover"
            />
            <span v-else class="w-full h-full flex items-center justify-center text-gray-600 text-[10px]">
              {{ post.authorName?.charAt(0).toUpperCase() }}
            </span>
          </div>
          <span class="truncate max-w-16">{{ post.authorName }}</span>
        </router-link>

        <!-- 点赞 -->
        <button
          @click.stop="handleLike"
          class="flex items-center space-x-1 text-gray-500 hover:text-red-500 transition-colors group-hover:text-red-500"
        >
          <svg
            class="w-4 h-4"
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
          <span
            class="text-xs"
            :class="{ 'text-red-500': isLiked }"
          >
            {{ formatLikeCount(post.likesCount ?? post.likeCount) }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { Post } from '@/types'

interface Props {
  post: Post
}

const props = defineProps<Props>()
const router = useRouter()
const authStore = useAuthStore()
const isLoading = ref(false)
const isLiked = ref(false)

// 格式化点赞数
const formatLikeCount = (count?: number | null) => {
  if (count == null) return '0'
  const safeCount = Number(count) || 0
  if (safeCount >= 10000) return (safeCount / 10000).toFixed(1) + 'w'
  if (safeCount >= 1000) return (safeCount / 1000).toFixed(1) + 'k'
  return safeCount.toString()
}

// 处理卡片点击（跳转到帖子详情）
const handleCardClick = () => {
  router.push(`/post/${props.post.id}`)
}

// 处理点赞
const handleLike = async (e: Event) => {
  e.preventDefault()
  e.stopPropagation()

  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  try {
    isLoading.value = true

    // 切换点赞状态
    isLiked.value = !isLiked.value

    // 调用点赞 API
    await post(`/post/${props.post.id}/like`)

    // 更新本地点赞数
    if (isLiked.value) {
      props.post.likesCount = (props.post.likesCount || 0) + 1
      props.post.likeCount = (props.post.likeCount || 0) + 1
      ElMessage.success('点赞成功！')
    } else {
      props.post.likesCount = Math.max(0, (props.post.likesCount || 0) - 1)
      props.post.likeCount = Math.max(0, (props.post.likeCount || 0) - 1)
      ElMessage.success('取消点赞')
    }
  } catch (error: any) {
    // 恢复点赞状态
    isLiked.value = !isLiked.value
    ElMessage.error(error.message || '操作失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>