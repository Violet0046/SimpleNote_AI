import { get, post } from '@/utils/request'

import { del, put } from '@/utils/request'
import type { Post } from '@/types'
import type { AddCommentPayload, CommentSortType, LikeStateResponse, PostComment } from './post-detail.types'

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

export const setCommentLike = async (commentId: number, desiredLiked: boolean) => {
  const response = desiredLiked
    ? await put<LikeStateResponse>(`/comment/like/${commentId}`)
    : await del<LikeStateResponse>(`/comment/like/${commentId}`)

  return response.data
}

export const setPostLike = async (postId: number, desiredLiked: boolean) => {
  const response = desiredLiked
    ? await put<LikeStateResponse>(`/post/${postId}/like`)
    : await del<LikeStateResponse>(`/post/${postId}/like`)

  return response.data
}

export const fetchPostDetail = async (postId: number) => {
  const response = await get<Post>(`/post/detail/${postId}`)
  return response.data
}
