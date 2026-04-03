<template>
  <div
    :data-post-id="post.id"
    class="group relative cursor-pointer transition-all duration-300 hover:scale-[1.02] hover:shadow-lg mb-4 break-inside-avoid"
    @click="handleCardClick"
  >
    <!-- 图片容器 -->
    <div class="relative aspect-[3/4] overflow-hidden rounded-lg">
      <!-- 图片 -->
      <img
        :src="post.images ? post.images.split(',')[0] : ''"
        :alt="post.title"
        class="w-full h-full object-cover transition-all duration-700 ease-out"
        :class="isImageLoaded ? 'blur-0 scale-100' : 'blur-xl scale-110'"
        @load="isImageLoaded = true"
        loading="lazy"
        v-image-error="{ type: 'image' }"
      />

      <!-- 视频角标 -->
      <div
        v-if="post.isVideo"
        class="absolute top-2 right-2 w-8 h-8 bg-black bg-opacity-50 rounded-full flex items-center justify-center"
      >
        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z"/>
        </svg>
      </div>

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

      <!-- 作者信息和互动 -->
      <div class="flex items-center justify-between">
        <!-- 作者头像和昵称 -->
        <router-link
          :to="`/user/${post.userId}`"
          class="flex items-center space-x-1 text-xs text-gray-500 hover:text-gray-700 transition-colors"
          @click.stop
        >
          <div
            class="w-6 h-6 rounded-full overflow-hidden bg-gray-300 flex-shrink-0"
            :title="post.authorName"
          >
            <img
              v-if="post.authorAvatar"
              :src="post.authorAvatar"
              :alt="post.authorName"
              class="w-full h-full object-cover"
              v-image-error="{ type: 'avatar' }"
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
          class="flex items-center space-x-1 text-gray-500 hover:text-[#FF2442] transition-colors group-hover:text-[#FF2442]"
        >
          <svg
            class="w-4 h-4"
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
          <span
            class="text-xs"
            :class="{ 'text-[#FF2442]': isLiked }"
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
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import { post as apiPost } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { Post } from '@/types'

interface Props {
  post: Post
  isLiked?: boolean
}

const props = defineProps<Props>()
const emit = defineEmits(['click', 'like'])
const likeStore = useLikeStore()
const isLoading = ref(false)
const isLiked = computed(() => props.isLiked || likeStore.isPostLiked(props.post.id))
// 增加一个响应式变量，记录图片是否已经加载完毕
const isImageLoaded = ref(false)
// 格式化点赞数
const formatLikeCount = (count?: number | null) => {
  if (count == null) return '0'
  const safeCount = Number(count) || 0
  if (safeCount >= 10000) return (safeCount / 10000).toFixed(1) + 'w'
  if (safeCount >= 1000) return (safeCount / 1000).toFixed(1) + 'k'
  return safeCount.toString()
}

// 获取卡片位置和尺寸
const getCardRect = (): DOMRect | null => {
  const card = document.querySelector(`[data-post-id="${props.post.id}"]`)
  if (card) {
    return card.getBoundingClientRect()
  }
  return null
}

// 处理卡片点击（跳转到帖子详情）
const handleCardClick = () => {
  const position = getCardRect()
  emit('click', props.post, position)
}

// 处理点赞
const handleLike = async (e: Event) => {
  e.preventDefault()
  e.stopPropagation()

  if (!useAuthStore().isLoggedIn) {
    useAuthStore().showLoginModal()
    return
  }

  try {
    isLoading.value = true

    // 切换点赞状态
    const newLikedState = !isLiked.value

    // 调用点赞 API
    await apiPost(`/post/${props.post.id}/like`)

    // 更新本地点赞数（通过emit通知父组件更新）
    if (newLikedState) {
      likeStore.addLikedPost(props.post.id)
      ElMessage.success('点赞成功！')
    } else {
      likeStore.removeLikedPost(props.post.id)
      ElMessage.success('取消点赞')
    }

    // 通知父组件
    emit('like', props.post.id, newLikedState)
  } catch (error: unknown) {
    ElMessage.error((error as Error).message || '操作失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 卡片阴影和过渡效果 */
.group {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.group:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

/* 瀑布流项目间距优化 */
.break-inside-avoid {
  break-inside: avoid;
  margin-bottom: 1rem;
}

/* 图片加载动画 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}
</style>