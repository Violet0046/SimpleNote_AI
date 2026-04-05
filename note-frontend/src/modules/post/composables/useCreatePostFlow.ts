import { computed, onBeforeUnmount, reactive, ref } from 'vue'

import { createPost } from '@/modules/post/post.api'
import type { CreatePostStep, PublishType } from '@/modules/post/post.types'
import { uploadFile } from '@/modules/upload/upload.api'

const IMAGE_LIMIT = 18
const VIDEO_LIMIT = 1

const ensureMediaSelectionIsValid = (
  publishType: PublishType,
  files: File[],
  currentMediaCount: number,
) => {
  if (publishType === 'video') {
    if (files.length > VIDEO_LIMIT || currentMediaCount > 0) {
      throw new Error('\u89c6\u9891\u6700\u591a\u53ea\u80fd\u4e0a\u4f20 1 \u4e2a')
    }

    return
  }

  if (currentMediaCount + files.length > IMAGE_LIMIT) {
    throw new Error('\u56fe\u7247\u6700\u591a\u53ea\u80fd\u4e0a\u4f20 18 \u5f20')
  }
}

export const useCreatePostFlow = () => {
  const step = ref<CreatePostStep>(1)
  const publishType = ref<PublishType>('video')
  const uploadedMedia = ref<string[]>([])
  const isUploading = ref(false)
  const isPublishing = ref(false)
  const countdown = ref(5)
  const postForm = reactive({
    title: '',
    content: '',
  })

  let timer: number | null = null

  const stopCountdown = () => {
    if (timer !== null) {
      window.clearInterval(timer)
      timer = null
    }
  }

  const reset = () => {
    stopCountdown()
    step.value = 1
    uploadedMedia.value = []
    postForm.title = ''
    postForm.content = ''
    countdown.value = 5
    isUploading.value = false
    isPublishing.value = false
  }

  const setPublishType = (nextType: PublishType) => {
    if (step.value !== 1) {
      return
    }

    publishType.value = nextType
  }

  const selectFiles = async (selection: FileList | File[] | null | undefined) => {
    const files = Array.from(selection ?? [])
    if (files.length === 0) {
      return [] as string[]
    }

    ensureMediaSelectionIsValid(publishType.value, files, uploadedMedia.value.length)

    isUploading.value = true
    step.value = 2

    try {
      const uploadedUrls: string[] = []

      for (const file of files) {
        const mediaUrl = await uploadFile(file)
        uploadedUrls.push(mediaUrl)
        uploadedMedia.value.push(mediaUrl)
      }

      return uploadedUrls
    } catch (error) {
      if (uploadedMedia.value.length === 0) {
        step.value = 1
      }

      throw error
    } finally {
      isUploading.value = false
    }
  }

  const removeMediaAt = (index: number) => {
    uploadedMedia.value.splice(index, 1)

    if (uploadedMedia.value.length === 0) {
      step.value = 1
    }
  }

  const canPublish = computed(() => (
    uploadedMedia.value.length > 0
    && postForm.title.trim().length > 0
    && postForm.content.trim().length > 0
    && !isPublishing.value
  ))

  const publish = async () => {
    if (!canPublish.value) {
      throw new Error('\u8bf7\u5148\u8865\u5168\u6807\u9898\u3001\u6b63\u6587\u548c\u5a92\u4f53\u5185\u5bb9')
    }

    isPublishing.value = true

    try {
      await createPost({
        title: postForm.title.trim(),
        content: postForm.content.trim(),
        images: uploadedMedia.value.join(','),
        isVideo: publishType.value === 'video' ? 1 : 0,
      })

      step.value = 3
      countdown.value = 5
      stopCountdown()

      timer = window.setInterval(() => {
        countdown.value -= 1

        if (countdown.value <= 0) {
          reset()
        }
      }, 1000)
    } finally {
      isPublishing.value = false
    }
  }

  onBeforeUnmount(() => {
    stopCountdown()
  })

  return {
    step,
    publishType,
    uploadedMedia,
    isUploading,
    isPublishing,
    countdown,
    postForm,
    canPublish,
    selectFiles,
    removeMediaAt,
    reset,
    publish,
    setPublishType,
    stopCountdown,
  }
}
