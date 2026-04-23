import type { PublishType } from '@/modules/post/post.types'

export type AiAssetRole = 'image' | 'video_frame'

export interface AiMediaAsset {
  type: 'image'
  role: AiAssetRole
  dataUrl: string
  index: number
}

export interface GeneratePostDraftRequest {
  mediaType: PublishType
  keywords: string
  assets: AiMediaAsset[]
  draft: {
    title: string
    content: string
  }
}

export interface GeneratePostDraftResponse {
  title: string
  content: string
  keywords: string[]
  provider: string
  model: string
}

export interface SocialSearchRequest {
  question: string
  minPostId?: number
  maxPostId?: number
  knowledgeLimit?: number
  topK?: number
}

export interface SocialSearchAnalysis {
  intent: 'trend' | 'filter' | 'explore'
  locations: string[]
  topics: string[]
  recency: boolean
  originalQuestion: string
}

export interface SocialSearchRetrievedPost {
  postId: number
  title: string
  location: string
  createTime?: string | null
  likesCount: number
  commentCount: number
  score: number
  matchReasons: string[]
}

export interface SocialSearchResponse {
  answer: string
  summary_points: string[]
  confidence: 'high' | 'medium' | 'low'
  analysis: SocialSearchAnalysis
  retrieved_posts: SocialSearchRetrievedPost[]
}
