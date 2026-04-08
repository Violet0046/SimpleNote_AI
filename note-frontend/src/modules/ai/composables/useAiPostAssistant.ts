import { computed, ref, type Ref } from 'vue'

import type { PublishType } from '@/modules/post/post.types'

import { generatePostDraft } from '../ai.api'
import { buildAiMediaAssets } from '../ai-media'

interface DraftFormLike {
  title: string
  content: string
}

interface UseAiPostAssistantOptions {
  publishType: Ref<PublishType>
  mediaFiles: Ref<File[]>
  postForm: DraftFormLike
}

export const useAiPostAssistant = ({
  publishType,
  mediaFiles,
  postForm,
}: UseAiPostAssistantOptions) => {
  const keywords = ref('')
  const isGenerating = ref(false)

  const normalizedKeywords = computed(() => keywords.value.trim())
  const canGenerate = computed(() => (
    mediaFiles.value.length > 0
    && normalizedKeywords.value.length > 0
    && !isGenerating.value
  ))

  const generate = async () => {
    if (mediaFiles.value.length === 0) {
      throw new Error('请先上传图片或视频')
    }

    if (!normalizedKeywords.value) {
      throw new Error('请输入关键词后再使用 AI 生成')
    }

    isGenerating.value = true

    try {
      const assets = await buildAiMediaAssets(publishType.value, mediaFiles.value)
      const result = await generatePostDraft({
        mediaType: publishType.value,
        keywords: normalizedKeywords.value,
        assets,
        draft: {
          title: postForm.title,
          content: postForm.content,
        },
      })

      postForm.title = result.title
      postForm.content = result.content

      return result
    } finally {
      isGenerating.value = false
    }
  }

  const reset = () => {
    keywords.value = ''
    isGenerating.value = false
  }

  return {
    keywords,
    isGenerating,
    canGenerate,
    generate,
    reset,
  }
}
