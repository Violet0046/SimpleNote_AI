import { get, post } from '@/utils/request'

import type { AddCommentPayload, CommentSortType, PostComment } from './post-detail.types'

const resolveItems = <T>(data: { items?: T[] } | T[] | null | undefined): T[] => {
  if (Array.isArray(data)) {
    return data
  }

  if (Array.isArray(data?.items)) {
    return data.items
  }

  return []
}

export const fetchPostComments = async (postId: number, sortType: CommentSortType) => {
  const response = await get<{ items?: PostComment[] } | PostComment[]>('/comment/list', { postId, sortType })
  return resolveItems(response.data)
}

export const fetchCommentReplies = async (parentId: number, pageSize: number) => {
  const response = await get<{ items?: PostComment[] } | PostComment[]>('/comment/replies', {
    parentId,
    pageNum: 1,
    pageSize,
  })

  return resolveItems(response.data)
}

export const addPostComment = async (payload: AddCommentPayload) => {
  const response = await post<unknown>('/comment/add', payload)
  return response.data
}

export const toggleCommentLike = async (commentId: number) => {
  const response = await post<unknown>(`/comment/like/${commentId}`)
  return response.data
}

export const togglePostLike = async (postId: number) => {
  const response = await post<unknown>(`/post/${postId}/like`)
  return response.data
}
