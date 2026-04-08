import { AI_API_BASE_URL } from '@/shared/api/env'

import type { GeneratePostDraftRequest, GeneratePostDraftResponse } from './ai.types'

const resolveErrorMessage = (payload: unknown, fallback: string) => {
  if (payload && typeof payload === 'object' && 'detail' in payload) {
    const { detail } = payload as { detail?: unknown }

    if (typeof detail === 'string' && detail.trim()) {
      return detail
    }
  }

  return fallback
}

export const generatePostDraft = async (
  payload: GeneratePostDraftRequest,
): Promise<GeneratePostDraftResponse> => {
  const response = await fetch(`${AI_API_BASE_URL}/api/content/generate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      media_type: payload.mediaType,
      keywords: payload.keywords,
      assets: payload.assets.map((asset) => ({
        type: asset.type,
        role: asset.role,
        data_url: asset.dataUrl,
        index: asset.index,
      })),
      draft: payload.draft,
    }),
  })

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    throw new Error(resolveErrorMessage(data, 'AI draft generation failed.'))
  }

  return data as GeneratePostDraftResponse
}
