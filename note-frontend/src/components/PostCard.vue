<template>
  <div
    :data-post-id="post.id"
    class="group relative mb-5 w-full cursor-pointer break-inside-avoid transition-transform duration-300 hover:scale-[1.02]"
    @click="handleCardClick"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <div class="relative w-full overflow-hidden rounded-2xl bg-gray-100">
      <video
        v-if="isVideoPost"
        ref="videoRef"
        :src="previewMedia"
        class="block h-auto w-full object-cover transition-all duration-700 ease-out pointer-events-none"
        :class="isImageLoaded ? 'blur-0 scale-100' : 'blur-xl scale-110'"
        muted
        loop
        playsinline
        preload="metadata"
        @loadeddata="isImageLoaded = true"
        @timeupdate="handleTimeUpdate"
      ></video>

      <img
        v-else
        :src="previewMedia"
        :alt="post.title"
        class="block h-auto w-full object-cover transition-all duration-700 ease-out"
        :class="isImageLoaded ? 'blur-0 scale-100' : 'blur-xl scale-110'"
        loading="lazy"
        @load="isImageLoaded = true"
        v-image-error="{ type: 'image' }"
      />

      <div
        v-if="isVideoPost"
        class="absolute right-3 top-3 z-10 flex h-7 w-7 items-center justify-center rounded-full bg-black/40 backdrop-blur-sm"
      >
        <svg class="ml-0.5 h-4 w-4 text-white" fill="currentColor" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z" />
        </svg>
      </div>

      <div v-if="isPending" class="absolute inset-0 z-0 bg-gray-200/70 backdrop-blur-[1px]">
        <div class="flex h-full w-full items-center justify-center bg-gradient-to-br from-[#ff2442]/85 to-[#ff6b6b]/85">
          <span class="text-sm font-medium text-white">Loading</span>
        </div>
      </div>
    </div>

    <div class="mt-3 px-1 pb-1">
      <h3 class="mb-2 line-clamp-2 text-[15px] font-medium leading-snug text-gray-900" :title="post.title">
        {{ post.title }}
      </h3>

      <div class="flex items-center justify-between">
        <div
          class="group/author flex cursor-pointer items-center space-x-1.5 text-xs text-gray-500 transition-colors hover:text-gray-800"
          @click.stop="goToUserProfile(post.userId)"
        >
          <div
            class="h-[22px] w-[22px] flex-shrink-0 overflow-hidden rounded-full border border-black/5 bg-gray-200 transition-transform group-hover/author:scale-110"
            :title="post.authorName"
          >
            <img
              v-if="post.authorAvatar"
              :src="post.authorAvatar"
              :alt="post.authorName"
              class="h-full w-full object-cover"
              v-image-error="{ type: 'avatar' }"
            />
            <span v-else class="flex h-full w-full items-center justify-center text-[10px] text-gray-500">
              {{ post.authorName?.charAt(0).toUpperCase() }}
            </span>
          </div>
          <span class="max-w-[100px] truncate transition-colors group-hover/author:text-blue-500">{{ post.authorName }}</span>
        </div>

        <button
          type="button"
          class="flex items-center space-x-1 text-gray-500 transition-colors hover:text-[#FF2442] group-hover:text-[#FF2442]"
          @click.stop="handleLike"
        >
          <svg
            class="h-[15px] w-[15px]"
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
          <span class="text-[13px] font-medium" :class="{ 'text-[#FF2442]': isLiked }">
            {{ likeCountLabel }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import { usePostLikeAction } from '@/modules/post/composables/usePostLikeAction'
import { formatDetailCount } from '@/modules/post/post-detail.utils'
import { useOpenUserPage } from '@/shared/composables/useOpenUserPage'
import { useLikeStore } from '@/stores/like'
import type { Post } from '@/types'

interface Props {
  post: Post
  isLiked?: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'click', post: Post, position: DOMRect | null): void
  (e: 'like', postId: number, isLiked: boolean): void
}>()

const likeStore = useLikeStore()
const { openUserPage } = useOpenUserPage()
const { setLike } = usePostLikeAction()

const videoRef = ref<HTMLVideoElement | null>(null)
const currentPlayTime = ref(0)

const isPending = ref(false)
const isImageLoaded = ref(false)
const previewMedia = computed(() => (props.post.images?.split(',')[0] || '').trim())
const isVideoPost = computed(() => Number(props.post.isVideo) === 1)
const isLiked = computed(() => props.isLiked ?? likeStore.isPostLiked(props.post.id))
const likeCountLabel = computed(() => formatDetailCount(props.post.likesCount ?? props.post.likeCount))

const goToUserProfile = (userId?: number) => {
  openUserPage(userId)
}

const getCardRect = () => {
  const card = document.querySelector(`[data-post-id="${props.post.id}"]`)
  return card instanceof HTMLElement ? card.getBoundingClientRect() : null
}

const handleMouseEnter = async () => {
  if (isVideoPost.value && videoRef.value) {
    try {
      await videoRef.value.play()
    } catch (e) {
      console.warn('Play interrupted or blocked by browser', e)
    }
  }
}

const handleMouseLeave = () => {
  if (isVideoPost.value && videoRef.value) {
    videoRef.value.pause()
  }
}

const handleTimeUpdate = () => {
  if (videoRef.value) {
    currentPlayTime.value = videoRef.value.currentTime
  }
}

const handleCardClick = () => {
  const postWithTime = {
    ...props.post,
    _initialVideoTime: currentPlayTime.value
  } as Post & { _initialVideoTime?: number }

  emit('click', postWithTime, getCardRect())
}

const handleLike = async () => {
  isPending.value = true

  try {
    const nextState = await setLike({
      postId: props.post.id,
      desiredLiked: !isLiked.value,
    })

    if (nextState !== null) {
      emit('like', props.post.id, nextState.liked)
    }
  } finally {
    isPending.value = false
  }
}
</script>

<style scoped>
.break-inside-avoid {
  break-inside: avoid;
  margin-bottom: 1rem;
}
</style>
