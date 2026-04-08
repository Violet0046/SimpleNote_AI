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
