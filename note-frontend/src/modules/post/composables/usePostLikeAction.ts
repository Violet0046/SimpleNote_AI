import { ElMessage } from 'element-plus'

import { togglePostLike } from '@/modules/post/post-detail.api'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'

interface TogglePostLikeOptions {
  postId: number
  isLiked: boolean
  onToggled?: ((nextLiked: boolean) => void) | null
  likeSuccessMessage?: string
  unlikeSuccessMessage?: string
  errorMessage?: string
}

export const usePostLikeAction = () => {
  const authStore = useAuthStore()
  const likeStore = useLikeStore()

  const toggleLike = async ({
    postId,
    isLiked,
    onToggled = null,
    likeSuccessMessage = 'Liked successfully',
    unlikeSuccessMessage = 'Removed like',
    errorMessage = 'Action failed, please try again later',
  }: TogglePostLikeOptions) => {
    if (!authStore.isLoggedIn) {
      authStore.showLoginModal()
      return null
    }

    try {
      await togglePostLike(postId)
      const nextLiked = !isLiked

      if (nextLiked) {
        likeStore.addLikedPost(postId)
        ElMessage.success(likeSuccessMessage)
      } else {
        likeStore.removeLikedPost(postId)
        ElMessage.success(unlikeSuccessMessage)
      }

      onToggled?.(nextLiked)
      return nextLiked
    } catch (error) {
      ElMessage.error(errorMessage)
      return null
    }
  }

  return {
    toggleLike,
  }
}
