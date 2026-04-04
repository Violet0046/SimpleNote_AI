<template>
  <div
    :data-post-id="post.id"
    class="group relative cursor-pointer mb-5 break-inside-avoid w-full transition-transform duration-300 hover:scale-[1.02]"
    @click="handleCardClick"
  >
    <div class="relative w-full overflow-hidden rounded-2xl bg-gray-100">
      
      <img
        :src="post.images ? post.images.split(',')[0] : ''"
        :alt="post.title"
        class="w-full h-auto block object-cover transition-all duration-700 ease-out"
        :class="isImageLoaded ? 'blur-0 scale-100' : 'blur-xl scale-110'"
        @load="isImageLoaded = true"
        loading="lazy"
        v-image-error="{ type: 'image' }"
      />

      <div
        v-if="post.isVideo"
        class="absolute top-3 right-3 w-7 h-7 bg-black/40 backdrop-blur-sm rounded-full flex items-center justify-center"
      >
        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z"/>
        </svg>
      </div>

      <div v-if="isLoading" class="absolute inset-0 bg-gray-200 animate-pulse">
        <div class="w-full h-full bg-gradient-to-br from-[#ff2442] to-[#ff6b6b] flex items-center justify-center">
          <span class="text-white text-lg">图片</span>
        </div>
      </div>
    </div>

    <div class="mt-3 px-1 pb-1">
      <h3
        class="text-[15px] font-medium text-gray-900 mb-2 line-clamp-2 leading-snug"
        :title="post.title"
      >
        {{ post.title }}
      </h3>

      <div class="flex items-center justify-between">
        
        <div
          class="flex items-center space-x-1.5 text-xs text-gray-500 hover:text-gray-800 transition-colors cursor-pointer group/author"
          @click.stop="goToUserProfile(post.userId)"
        >
          <div
            class="w-[22px] h-[22px] rounded-full overflow-hidden bg-gray-200 flex-shrink-0 border border-black/5 transition-transform group-hover/author:scale-110"
            :title="post.authorName"
          >
            <img
              v-if="post.authorAvatar"
              :src="post.authorAvatar"
              :alt="post.authorName"
              class="w-full h-full object-cover"
              v-image-error="{ type: 'avatar' }"
            />
            <span v-else class="w-full h-full flex items-center justify-center text-gray-500 text-[10px]">
              {{ post.authorName?.charAt(0).toUpperCase() }}
            </span>
          </div>
          <span class="truncate max-w-[100px] group-hover/author:text-blue-500 transition-colors">{{ post.authorName }}</span>
        </div>

        <button
          @click.stop="handleLike"
          class="flex items-center space-x-1 text-gray-500 hover:text-[#FF2442] transition-colors group-hover:text-[#FF2442]"
        >
          <svg
            class="w-[15px] h-[15px]"
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
            class="text-[13px] font-medium"
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
import { useRouter } from 'vue-router' // 🌟 引入 useRouter
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
const router = useRouter() // 🌟 获取 router 实例

const isLoading = ref(false)
const isLiked = computed(() => props.isLiked || likeStore.isPostLiked(props.post.id))
const isImageLoaded = ref(false)

// 🌟 新增：新开标签页跳转到主页的方法
const goToUserProfile = (userId?: number) => {
  if (!userId) return
  // 使用 resolve 解析出完整 URL，然后用 window.open 在新标签页打开
  const routeUrl = router.resolve(`/user/${userId}`)
  window.open(routeUrl.href, '_blank')
}

// 格式化点赞数
const formatLikeCount = (count?: number | null) => {
  if (count == null) return '0'
  const safeCount = Number(count) || 0
  if (safeCount >= 10000) return (safeCount / 10000).toFixed(1) + 'w'
  if (safeCount >= 1000) return (safeCount / 1000).toFixed(1) + 'k'
  return safeCount.toString()
}

const getCardRect = (): DOMRect | null => {
  const card = document.querySelector(`[data-post-id="${props.post.id}"]`)
  if (card) {
    return card.getBoundingClientRect()
  }
  return null
}

const handleCardClick = () => {
  const position = getCardRect()
  emit('click', props.post, position)
}

const handleLike = async (e: Event) => {
  e.preventDefault()
  e.stopPropagation()

  if (!useAuthStore().isLoggedIn) {
    useAuthStore().showLoginModal()
    return
  }

  try {
    isLoading.value = true
    const newLikedState = !isLiked.value
    await apiPost(`/post/${props.post.id}/like`)

    if (newLikedState) {
      likeStore.addLikedPost(props.post.id)
      ElMessage.success('点赞成功！')
    } else {
      likeStore.removeLikedPost(props.post.id)
      ElMessage.success('取消点赞')
    }

    emit('like', props.post.id, newLikedState)
  } catch (error: unknown) {
    ElMessage.error((error as Error).message || '操作失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.break-inside-avoid {
  break-inside: avoid;
  margin-bottom: 1rem;
}

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