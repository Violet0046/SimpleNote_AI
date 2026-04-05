import type { Post } from '@/types'

export const splitPostMedia = (post: Post | null | undefined) =>
  post?.images
    ? String(post.images)
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean)
    : []

export const splitPostTags = (post: Post | null | undefined) =>
  post?.tags
    ? String(post.tags)
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean)
    : []

export const formatDetailTime = (timeStr?: string) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

export const formatDetailCount = (count?: number | string | null) => {
  const numericCount = Number(count)
  if (!numericCount || Number.isNaN(numericCount)) return '0'
  if (numericCount >= 10000) {
    return `${(numericCount / 10000).toFixed(1)}w`
  }

  return numericCount.toString()
}
