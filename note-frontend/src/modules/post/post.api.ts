import { post } from '@/utils/request'

import type { CreatePostPayload } from './post.types'

export const createPost = async (payload: CreatePostPayload) => {
  const response = await post<number | Record<string, unknown>>('/post/add', payload)
  return response.data
}
