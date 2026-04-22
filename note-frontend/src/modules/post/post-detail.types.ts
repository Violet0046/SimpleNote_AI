export type CommentSortType = 1 | 2

export interface LikeStateResponse {
  liked: boolean
  changed: boolean
  likeCount: number | null
}

export interface PostComment {
  id: number
  userId: number
  authorName: string
  authorAvatar: string
  content: string
  createTime: string
  parentId: number
  replyToUserId: number | null
  replyToUserName: string | null
  replies: PostComment[] | null
  likesCount?: number
  isLiked?: boolean | number
  childCount?: number
  replyPageSize?: number
}

export interface AddCommentPayload {
  postId: number
  content: string
  parentId?: number | null
  replyToUserId?: number | null
}
