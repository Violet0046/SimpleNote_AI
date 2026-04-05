import { computed, ref } from 'vue'

import { ElMessage } from 'element-plus'

import { useAuthStore } from '@/stores/auth'
import {
  addPostComment,
  fetchCommentReplies,
  fetchPostComments,
  toggleCommentLike as toggleCommentLikeRequest,
} from '@/modules/post/post-detail.api'
import type { CommentSortType, PostComment } from '@/modules/post/post-detail.types'

export const usePostComments = () => {
  const authStore = useAuthStore()

  const comments = ref<PostComment[]>([])
  const isLoadingComments = ref(false)
  const currentSortType = ref<CommentSortType>(2)
  const commentText = ref('')
  const isSending = ref(false)
  const replyingTo = ref<PostComment | null>(null)
  const rootCommentId = ref<number | null>(null)
  const commentInputRef = ref<HTMLInputElement | null>(null)

  const totalCommentsCount = computed(() => {
    return comments.value.reduce((total, comment) => {
      const replyCount = comment.childCount || comment.replies?.length || 0
      return total + 1 + replyCount
    }, 0)
  })

  const bindCommentInputRef = (element: HTMLInputElement | null) => {
    commentInputRef.value = element
  }

  const clearReply = () => {
    replyingTo.value = null
    rootCommentId.value = null
  }

  const resetComposer = () => {
    commentText.value = ''
    clearReply()
  }

  const setReply = (rootComment: PostComment, targetComment: PostComment) => {
    rootCommentId.value = rootComment.id
    replyingTo.value = targetComment
    commentInputRef.value?.focus()
  }

  const loadComments = async (postId: number) => {
    isLoadingComments.value = true
    comments.value = []

    try {
      comments.value = await fetchPostComments(postId, currentSortType.value)
    } finally {
      isLoadingComments.value = false
    }
  }

  const changeSort = async (postId: number, sortType: CommentSortType) => {
    if (currentSortType.value === sortType) return
    currentSortType.value = sortType
    await loadComments(postId)
  }

  const expandReplies = async (comment: PostComment) => {
    const nextPageSize = comment.replyPageSize ? comment.replyPageSize * 2 : 5
    comment.replyPageSize = nextPageSize
    comment.replies = await fetchCommentReplies(comment.id, nextPageSize)
  }

  const toggleCommentLike = async (comment: PostComment) => {
    if (!authStore.isLoggedIn) {
      authStore.showLoginModal()
      return
    }

    const isCurrentlyLiked = comment.isLiked === 1 || comment.isLiked === true
    comment.isLiked = isCurrentlyLiked ? 0 : 1
    comment.likesCount = Math.max(0, (comment.likesCount || 0) + (isCurrentlyLiked ? -1 : 1))

    try {
      await toggleCommentLikeRequest(comment.id)
    } catch {
      comment.isLiked = isCurrentlyLiked ? 1 : 0
      comment.likesCount = Math.max(0, (comment.likesCount || 0) + (isCurrentlyLiked ? 1 : -1))
      ElMessage.error('Comment like failed')
    }
  }

  const sendComment = async (postId: number) => {
    if (!authStore.isLoggedIn) {
      authStore.showLoginModal()
      return false
    }

    if (!commentText.value.trim() || isSending.value) {
      return false
    }

    isSending.value = true

    try {
      const payload = {
        postId,
        content: commentText.value.trim(),
        parentId: replyingTo.value ? rootCommentId.value : undefined,
        replyToUserId: replyingTo.value ? replyingTo.value.userId : undefined,
      }

      await addPostComment(payload)
      resetComposer()
      await loadComments(postId)
      return true
    } finally {
      isSending.value = false
    }
  }

  return {
    comments,
    isLoadingComments,
    currentSortType,
    totalCommentsCount,
    commentText,
    isSending,
    replyingTo,
    commentInputRef,
    bindCommentInputRef,
    loadComments,
    changeSort,
    expandReplies,
    toggleCommentLike,
    setReply,
    clearReply,
    resetComposer,
    sendComment,
  }
}
