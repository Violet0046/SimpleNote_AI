export type PublishType = 'video' | 'image'
export type CreatePostStep = 1 | 2 | 3

export interface CreatePostPayload {
  title: string
  content: string
  images: string
  isVideo: 0 | 1
}
