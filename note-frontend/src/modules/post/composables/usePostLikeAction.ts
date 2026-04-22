import { ElMessage } from 'element-plus'

import { setPostLike } from '@/modules/post/post-detail.api'
import type { LikeStateResponse } from '@/modules/post/post-detail.types'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'

interface SetPostLikeOptions {
  postId: number
  desiredLiked: boolean
  onToggled?: ((nextState: LikeStateResponse) => void) | null
  likeSuccessMessage?: string
  unlikeSuccessMessage?: string
  errorMessage?: string
}

export const usePostLikeAction = () => {
  const authStore = useAuthStore()
  const likeStore = useLikeStore()

  const setLike = async ({
    postId,
    desiredLiked,
    onToggled = null,
    likeSuccessMessage = 'Liked successfully',
    unlikeSuccessMessage = 'Removed like',
    errorMessage = 'Action failed, please try again later',
  }: SetPostLikeOptions) => {
    if (!authStore.isLoggedIn) {
      authStore.showLoginModal()
      return null
    }

    try {
      const nextState = await setPostLike(postId, desiredLiked)

      if (nextState.liked) {
        likeStore.addLikedPost(postId)
      } else {
        likeStore.removeLikedPost(postId)
      }

      if (nextState.changed) {
        ElMessage.success(nextState.liked ? likeSuccessMessage : unlikeSuccessMessage)
      }

      onToggled?.(nextState)
      return nextState
    } catch (error) {
      ElMessage.error(errorMessage)
      return null
    }
  }

  return {
    setLike,
  }
}
