import type { ApiResponse, Post, PostListResponse, UserInfo } from '@/types'
import { get, post } from '@/utils/request'

import type { PaginationInput, RelationUser } from './profile.types'

interface RelationRequest extends PaginationInput {
  userId: number
  relationType: 'following' | 'followers'
}

interface UserPostsRequest extends PaginationInput {
  userId: number
}

const ensureSuccess = <T>(response: ApiResponse<T>, fallback: string): T => {
  if (response.code !== 1) {
    throw new Error(response.msg || fallback)
  }

  return response.data
}

const resolvePageItems = <T>(data: { items?: T[] } | T[] | null | undefined): T[] => {
  if (Array.isArray(data)) {
    return data
  }

  if (Array.isArray(data?.items)) {
    return data.items
  }

  return []
}

export const fetchCurrentUserProfile = async () => {
  const response = await get<UserInfo>('/user/me')
  return ensureSuccess(response, 'Failed to fetch current user')
}

export const fetchUserProfileById = async (userId: number) => {
  const response = await get<UserInfo>(`/user/info/${userId}`)
  return ensureSuccess(response, 'Failed to fetch user profile')
}

export const fetchOwnProfilePosts = async (params: PaginationInput) => {
  const response = await get<PostListResponse | Post[]>('/post/list/own', params)
  return resolvePageItems(ensureSuccess(response, 'Failed to fetch own posts'))
}

export const fetchLikedPosts = async (params: PaginationInput) => {
  const response = await get<PostListResponse | Post[]>('/post/list/liked', params)
  return resolvePageItems(ensureSuccess(response, 'Failed to fetch liked posts'))
}

export const fetchUserPosts = async (params: UserPostsRequest) => {
  const response = await get<PostListResponse | Post[]>('/post/list/user', params)
  return resolvePageItems(ensureSuccess(response, 'Failed to fetch user posts'))
}

export const fetchProfileRelations = async ({ userId, relationType, pageNum, pageSize }: RelationRequest) => {
  const response = await get<{ items?: RelationUser[] } | RelationUser[]>(`/follow/${relationType}/${userId}`, {
    pageNum,
    pageSize,
  })

  return resolvePageItems(ensureSuccess(response, 'Failed to fetch relation list'))
}

export const fetchFollowStatus = async (userId: number) => {
  const response = await get<boolean>(`/follow/status/${userId}`)
  return ensureSuccess(response, 'Failed to fetch follow status')
}

export const toggleFollowUser = async (userId: number) => {
  const response = await post<unknown>(`/follow/${userId}`)
  return ensureSuccess(response, 'Failed to update follow status')
}
