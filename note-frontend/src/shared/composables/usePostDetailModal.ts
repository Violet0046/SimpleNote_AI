import { ref } from 'vue'

interface UsePostDetailModalOptions {
  closeDelay?: number
}

export const usePostDetailModal = <T>({
  closeDelay = 400,
}: UsePostDetailModalOptions = {}) => {
  const selectedPost = ref<T | null>(null)
  const visible = ref(false)
  const triggerRect = ref<DOMRect | null>(null)

  const openPostDetail = (post: T, rect: DOMRect | null) => {
    selectedPost.value = post
    triggerRect.value = rect instanceof DOMRect ? rect : null
    visible.value = true
  }

  const closePostDetail = () => {
    visible.value = false

    window.setTimeout(() => {
      selectedPost.value = null
      triggerRect.value = null
    }, closeDelay)
  }

  return {
    selectedPost,
    visible,
    triggerRect,
    openPostDetail,
    closePostDetail,
  }
}
