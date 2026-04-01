// API 响应格式
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  // 其他用户信息字段
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
  title: string
  content: string
  images?: string[]
  userId: number
  createTime: string
  updateTime: string
}