import type { PublishType } from '@/modules/post/post.types'

import type { AiMediaAsset } from './ai.types'

const MAX_IMAGE_ASSET_COUNT = 6
const MAX_VIDEO_FRAME_COUNT = 4
const TARGET_MAX_SIDE = 1280
const JPEG_QUALITY = 0.82

const pickRepresentativeItems = <T>(items: T[], maxCount: number): T[] => {
  if (items.length === 0) {
    return []
  }

  if (items.length <= maxCount) {
    return [...items]
  }

  if (maxCount <= 1) {
    return [items[0]].filter((item): item is T => item !== undefined)
  }

  const lastIndex = items.length - 1

  return Array.from({ length: maxCount }, (_, index) => {
    const mappedIndex = Math.round((index * lastIndex) / (maxCount - 1))
    return items[mappedIndex]
  }).filter((item): item is T => item !== undefined)
}

const calculateTargetSize = (width: number, height: number) => {
  if (width <= TARGET_MAX_SIDE && height <= TARGET_MAX_SIDE) {
    return { width, height }
  }

  const ratio = width >= height ? TARGET_MAX_SIDE / width : TARGET_MAX_SIDE / height

  return {
    width: Math.max(1, Math.round(width * ratio)),
    height: Math.max(1, Math.round(height * ratio)),
  }
}

const drawToCompressedDataUrl = (
  drawable: CanvasImageSource,
  width: number,
  height: number,
) => {
  const canvas = document.createElement('canvas')
  canvas.width = width
  canvas.height = height

  const context = canvas.getContext('2d')
  if (!context) {
    throw new Error('Canvas is not available for AI asset generation.')
  }

  context.drawImage(drawable, 0, 0, width, height)
  return canvas.toDataURL('image/jpeg', JPEG_QUALITY)
}

const imageFileToDataUrl = async (file: File) => {
  const bitmap = await createImageBitmap(file)

  try {
    const size = calculateTargetSize(bitmap.width, bitmap.height)
    return drawToCompressedDataUrl(bitmap, size.width, size.height)
  } finally {
    bitmap.close()
  }
}

const loadVideoElement = (file: File): Promise<{ url: string; video: HTMLVideoElement }> => (
  new Promise((resolve, reject) => {
    const url = URL.createObjectURL(file)
    const video = document.createElement('video')

    const cleanup = () => {
      video.onloadedmetadata = null
      video.onerror = null
    }

    video.preload = 'metadata'
    video.muted = true
    video.playsInline = true
    video.src = url

    video.onloadedmetadata = () => {
      cleanup()
      resolve({ url, video })
    }

    video.onerror = () => {
      cleanup()
      URL.revokeObjectURL(url)
      reject(new Error('Unable to read the uploaded video for AI generation.'))
    }
  })
)

const seekVideo = (video: HTMLVideoElement, time: number) => (
  new Promise<void>((resolve, reject) => {
    const cleanup = () => {
      video.onseeked = null
      video.onerror = null
    }

    video.onseeked = () => {
      cleanup()
      resolve()
    }

    video.onerror = () => {
      cleanup()
      reject(new Error('Unable to extract video frames for AI generation.'))
    }

    video.currentTime = Math.min(Math.max(time, 0), Math.max(video.duration - 0.05, 0))
  })
)

const buildVideoFrameTimes = (duration: number, maxFrames: number) => {
  if (!Number.isFinite(duration) || duration <= 0) {
    return [0]
  }

  if (maxFrames === 1) {
    return [Math.max(0, duration / 2)]
  }

  const startRatio = duration > 3 ? 0.15 : 0.05
  const endRatio = duration > 3 ? 0.85 : 0.95

  return Array.from({ length: maxFrames }, (_, index) => {
    const progress = index / (maxFrames - 1)
    return duration * (startRatio + (endRatio - startRatio) * progress)
  })
}

const extractVideoFrameAssets = async (file: File): Promise<AiMediaAsset[]> => {
  const { url, video } = await loadVideoElement(file)

  try {
    const times = buildVideoFrameTimes(video.duration, MAX_VIDEO_FRAME_COUNT)
    const rawWidth = video.videoWidth || 1280
    const rawHeight = video.videoHeight || 720
    const size = calculateTargetSize(rawWidth, rawHeight)
    const assets: AiMediaAsset[] = []

    for (let index = 0; index < times.length; index += 1) {
      const frameTime = times[index]
      if (frameTime === undefined) {
        continue
      }

      await seekVideo(video, frameTime)
      assets.push({
        type: 'image',
        role: 'video_frame',
        dataUrl: drawToCompressedDataUrl(video, size.width, size.height),
        index,
      })
    }

    return assets
  } finally {
    video.pause()
    video.removeAttribute('src')
    video.load()
    URL.revokeObjectURL(url)
  }
}

export const buildAiMediaAssets = async (
  publishType: PublishType,
  files: File[],
): Promise<AiMediaAsset[]> => {
  if (files.length === 0) {
    throw new Error('Please upload image(s) or video before using AI generation.')
  }

  if (publishType === 'video') {
    const [firstFile] = files
    if (!firstFile) {
      throw new Error('Please upload a video before using AI generation.')
    }

    return extractVideoFrameAssets(firstFile)
  }

  const selectedFiles = pickRepresentativeItems(files, MAX_IMAGE_ASSET_COUNT)
  const assets: AiMediaAsset[] = []

  for (let index = 0; index < selectedFiles.length; index += 1) {
    const file = selectedFiles[index]
    if (!file) {
      continue
    }

    assets.push({
      type: 'image',
      role: 'image',
      dataUrl: await imageFileToDataUrl(file),
      index,
    })
  }

  return assets
}
