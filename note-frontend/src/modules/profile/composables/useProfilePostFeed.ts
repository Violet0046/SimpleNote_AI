import { ref } from 'vue'

import type { Post } from '@/types'

interface UseProfilePostFeedOptions {
  loadPage: (pageNum: number, pageSize: number) => Promise<Post[]>
  onAppend?: ((items: Post[]) => void | Promise<void>) | null
  onReplace?: ((items: Post[]) => void | Promise<void>) | null
  pageSize?: number
}

export const useProfilePostFeed = ({
  loadPage,
  onAppend = null,
  onReplace = null,
  pageSize = 15,
}: UseProfilePostFeedOptions) => {
  const posts = ref<Post[]>([])
  const loading = ref(false)
  const hasMore = ref(true)
  const currentPage = ref(1)

  const fetchPosts = async (isLoadMore = false) => {
    if (loading.value) {
      return [] as Post[]
    }

    loading.value = true

    try {
      const nextPosts = await loadPage(currentPage.value, pageSize)

      if (isLoadMore) {
        posts.value.push(...nextPosts)
        await onAppend?.(nextPosts)
      } else {
        posts.value = nextPosts
        await onReplace?.(posts.value)
      }

      hasMore.value = nextPosts.length === pageSize
      if (nextPosts.length > 0) {
        currentPage.value += 1
      }

      return nextPosts
    } finally {
      loading.value = false
    }
  }

  const reset = async () => {
    currentPage.value = 1
    hasMore.value = true
    posts.value = []
    await onReplace?.([])
  }

  const applyLike = (postId: number, isLiked: boolean) => {
    const targetPost = posts.value.find((post) => post.id === postId)
    if (!targetPost) return

    const currentCount = targetPost.likesCount ?? targetPost.likeCount ?? 0
    targetPost.likesCount = isLiked ? currentCount + 1 : Math.max(0, currentCount - 1)
    targetPost.likeCount = targetPost.likesCount
  }

  return {
    posts,
    loading,
    hasMore,
    currentPage,
    pageSize,
    fetchPosts,
    reset,
    applyLike,
  }
}
