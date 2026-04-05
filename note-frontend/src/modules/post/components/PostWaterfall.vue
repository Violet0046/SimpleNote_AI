<template>
  <div class="flex gap-[20px] items-start w-full" :class="minHeightClass">
    <div
      v-for="(colPosts, colIndex) in columns"
      :key="colIndex"
      :ref="(element) => bindColumnRef(element as Element | null, colIndex)"
      class="flex-1 flex flex-col gap-[20px]"
    >
      <PostCard
        v-for="(post, index) in colPosts"
        :key="resolvePostKey(post, index)"
        :post="post"
        :is-liked="resolveLikeState(post.id)"
        :class="animated ? 'w-full animate-fade-in-up' : 'w-full'"
        @click="handleClick"
        @like="handleLike"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import PostCard from '@/components/PostCard.vue'
import type { Post } from '@/types'

interface Props {
  columns: Post[][]
  refreshKey?: string | number
  minHeightClass?: string
  animated?: boolean
  setColumnRef?: ((element: Element | null, index: number) => void) | null
  isPostLiked?: ((postId: number) => boolean) | null
}

const props = withDefaults(defineProps<Props>(), {
  refreshKey: 0,
  minHeightClass: 'min-h-[101vh]',
  animated: false,
  setColumnRef: null,
  isPostLiked: null,
})

const emit = defineEmits<{
  (e: 'post-click', post: Post, rect: DOMRect | null): void
  (e: 'post-like', postId: number, isLiked: boolean): void
}>()

const bindColumnRef = (element: Element | null, index: number) => {
  props.setColumnRef?.(element, index)
}

const resolvePostKey = (post: Post, index: number) => `post_${post.id}_${index}_${props.refreshKey}`

const resolveLikeState = (postId: number) => props.isPostLiked?.(postId) ?? false

const handleClick = (post: Post, rect: DOMRect | null) => {
  emit('post-click', post, rect)
}

const handleLike = (postId: number, isLiked: boolean) => {
  emit('post-like', postId, isLiked)
}
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
