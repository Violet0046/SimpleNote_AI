// API 响应格式
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 用户信息
export interface UserInfo {
  id: number
  nickname: string
  avatar: string
  intro: string
  gender: number
  ipLocation: string
  followingCount: number
  followersCount: number
  likesCount: number
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 注册请求
export interface RegisterRequest {
  username: string
  password: string
}

// 帖子信息
export interface Post {
  id: number
  userId: number
  title: string
  content: string
  images: string
  likesCount: number
  createTime: string
  authorName: string
  authorAvatar: string
  likeCount?: number | null
  tags?: string
}

// 评论信息
export interface Comment {
  id: number
  userId: number
  authorName: string
  authorAvatar: string
  content: string
  ipLocation: string
  parentId: number
  isDeleted: number
  replyToUserId: number | null
  replyToUserName: string | null
  createTime: string
  replies: Comment[] | null
}

// 帖子列表响应
export interface PostListResponse {
  total: number
  items: Post[]
}

// 评论列表响应
export interface CommentListResponse {
  code: number
  msg: string
  data: Comment[]
}

// 分页参数
export interface PaginationParams {
  pageNum: number
  pageSize: number
}