<template>
  <div class="flex min-h-screen flex-col bg-[#FAFAFA]">
    <header class="flex h-16 shrink-0 items-center justify-between border-b border-gray-100 bg-white px-6 shadow-sm">
      <button class="flex items-center gap-2" type="button" @click="goToHome">
        <span class="text-lg font-bold text-[#FF2442]">{{ COPY.creatorCenter }}</span>
      </button>

      <div class="flex items-center">
        <div class="flex flex-col items-end">
          <div class="text-lg font-bold text-gray-900">
            <span>{{ authStore.userInfo?.nickname || authStore.getUsername }}</span>
            <span class="ml-2 text-[#FF2442]">{{ COPY.capturingIdeas }}</span>
          </div>
          <span class="mt-0.5 text-[10px] font-medium uppercase tracking-widest text-[#FF2442] opacity-80">
            Creative Mode
          </span>
        </div>
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden">
      <aside class="flex w-[200px] shrink-0 flex-col border-r border-gray-100 bg-white py-6">
        <div class="mb-2 px-6 text-xs font-semibold tracking-wider text-gray-400">{{ COPY.createSection }}</div>
        <div class="mx-3 flex cursor-pointer items-center gap-2 rounded-lg bg-[#FFE8EB] px-4 py-2.5 font-medium text-[#FF2442]">
          <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          {{ COPY.publishNote }}
        </div>
      </aside>

      <main class="flex flex-1 justify-center overflow-y-auto p-6 md:p-10">
        <div class="flex min-h-[600px] w-full max-w-[1000px] flex-col overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
          <section v-if="step === 1" class="flex flex-1 flex-col">
            <div class="flex justify-center border-b border-gray-100 pt-6">
              <div class="flex gap-12">
                <button
                  type="button"
                  class="relative pb-4 text-[16px] font-semibold transition-colors"
                  :class="publishType === 'video' ? 'text-gray-900' : 'text-gray-500 hover:text-gray-700'"
                  @click="setPublishType('video')"
                >
                  {{ COPY.videoTab }}
                  <div v-if="publishType === 'video'" class="absolute bottom-0 left-1/2 h-[3px] w-8 -translate-x-1/2 rounded-full bg-[#FF2442]"></div>
                </button>
                <button
                  type="button"
                  class="relative pb-4 text-[16px] font-semibold transition-colors"
                  :class="publishType === 'image' ? 'text-gray-900' : 'text-gray-500 hover:text-gray-700'"
                  @click="setPublishType('image')"
                >
                  {{ COPY.imageTab }}
                  <div v-if="publishType === 'image'" class="absolute bottom-0 left-1/2 h-[3px] w-8 -translate-x-1/2 rounded-full bg-[#FF2442]"></div>
                </button>
              </div>
            </div>

            <div class="flex flex-1 items-center justify-center p-10">
              <label class="flex h-[300px] w-[400px] cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-dashed border-gray-300 transition-all hover:border-[#FF2442] hover:bg-[#FFF5F6]">
                <svg class="mb-4 h-16 w-16 text-gray-300 transition-colors group-hover:text-[#FF2442]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <span class="text-[16px] font-medium text-gray-700">{{ uploadPrompt }}</span>
                <span class="mt-2 text-[13px] text-gray-400">{{ uploadHint }}</span>
                <input
                  class="hidden"
                  type="file"
                  :accept="inputAccept"
                  :multiple="publishType === 'image'"
                  @change="handleFileSelect"
                />
              </label>
            </div>
          </section>

          <section v-else-if="step === 2" class="relative flex h-full flex-1 flex-col md:flex-row">
            <div v-if="isUploading || isPublishing" class="absolute inset-0 z-50 flex flex-col items-center justify-center bg-white/80 backdrop-blur-sm">
              <svg class="mb-3 h-10 w-10 animate-spin text-[#FF2442]" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span class="font-medium text-gray-700">{{ busyLabel }}</span>
            </div>

            <div class="flex h-[600px] w-full flex-col overflow-y-auto border-r border-gray-100 bg-[#F8F8F8] p-6 md:w-[40%]">
              <div class="mb-4 flex items-center justify-between">
                <span class="text-[15px] font-bold text-gray-800">{{ COPY.mediaPreview }}</span>
                <button class="text-[13px] text-gray-500 hover:text-[#FF2442]" type="button" @click="resetUpload">
                  {{ COPY.resetUpload }}
                </button>
              </div>

              <div v-if="publishType === 'video' && uploadedMedia.length > 0" class="flex aspect-[3/4] w-full items-center justify-center overflow-hidden rounded-xl bg-black shadow-sm">
                <video :src="uploadedMedia[0]" controls class="h-full w-full object-contain"></video>
              </div>

              <div v-else-if="publishType === 'image'" class="grid grid-cols-3 gap-2">
                <div
                  v-for="(mediaUrl, index) in uploadedMedia"
                  :key="`${mediaUrl}_${index}`"
                  class="group relative aspect-square overflow-hidden rounded-lg border border-gray-200 bg-white"
                >
                  <img :src="mediaUrl" class="h-full w-full object-cover" />
                  <button
                    class="absolute right-1 top-1 flex h-5 w-5 items-center justify-center rounded-full bg-black/50 text-white opacity-0 transition-opacity group-hover:opacity-100"
                    type="button"
                    @click="removeImage(index)"
                  >
                    x
                  </button>
                </div>
                <label
                  v-if="uploadedMedia.length < 18"
                  class="flex aspect-square cursor-pointer items-center justify-center rounded-lg border-2 border-dashed border-gray-300 bg-white transition-colors hover:border-gray-400"
                >
                  <svg class="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                  <input class="hidden" type="file" accept="image/*" multiple @change="handleFileSelect" />
                </label>
              </div>
            </div>

            <div class="flex h-[600px] flex-1 flex-col bg-white p-6 lg:p-10">
              <div class="flex-1">
                <div class="mb-6 rounded-2xl border border-[#FFD9DF] bg-[linear-gradient(135deg,#FFF7F8_0%,#FFFFFF_100%)] p-4 shadow-[0_10px_30px_rgba(255,36,66,0.06)]">
                  <div class="flex flex-col gap-4 lg:flex-row lg:items-center">
                    <div class="flex-1">
                      <div class="flex items-start gap-3">
                        <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-[#FF2442] text-white shadow-sm shadow-red-200">
                          <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path
                              stroke-linecap="round"
                              stroke-linejoin="round"
                              stroke-width="1.8"
                              d="M13 3l-1.743 4.358a1 1 0 01-.55.55L6.35 9.65l4.358 1.743a1 1 0 01.55.55L13 16.3l1.743-4.358a1 1 0 01.55-.55L19.65 9.65l-4.358-1.742a1 1 0 01-.55-.55L13 3z"
                            />
                          </svg>
                        </div>
                        <div class="min-w-0">
                          <div class="text-[15px] font-semibold text-gray-900">{{ COPY.aiMagicTitle }}</div>
                          <p class="mt-1 text-[12px] leading-5 text-gray-500">{{ COPY.aiMagicHint }}</p>
                        </div>
                      </div>

                      <div class="mt-4 flex flex-col gap-3 sm:flex-row">
                        <input
                          v-model="magicKeywords"
                          type="text"
                          :placeholder="COPY.aiMagicPlaceholder"
                          class="h-11 flex-1 rounded-xl border border-[#FFD0D7] bg-white px-4 text-[14px] text-gray-800 outline-none transition-all placeholder:text-gray-400 focus:border-[#FF2442] focus:ring-4 focus:ring-red-50"
                        />
                        <button
                          class="inline-flex h-11 items-center justify-center rounded-xl px-5 text-[14px] font-semibold text-white transition-all"
                          :class="canUseAiMagic ? 'bg-[#FF2442] shadow-sm shadow-red-200 hover:-translate-y-0.5 hover:bg-red-600' : 'cursor-not-allowed bg-[#FF9AA8]'"
                          :disabled="!canUseAiMagic"
                          type="button"
                          @click="handleGenerateWithAi"
                        >
                          <svg v-if="isAiGenerating" class="mr-2 h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                          </svg>
                          {{ aiActionText }}
                        </button>
                      </div>

                      <p class="mt-2 text-[12px] text-gray-400">{{ COPY.aiMagicExample }}</p>
                    </div>
                  </div>
                </div>

                <input
                  v-model="postForm.title"
                  type="text"
                  :placeholder="COPY.titlePlaceholder"
                  class="mb-6 w-full border-b border-gray-100 pb-4 text-[20px] font-bold text-gray-900 outline-none transition-colors placeholder:text-gray-300 focus:border-gray-300"
                  maxlength="20"
                />

                <textarea
                  v-model="postForm.content"
                  :placeholder="COPY.contentPlaceholder"
                  class="h-[200px] w-full resize-none text-[15px] leading-relaxed text-gray-700 outline-none placeholder:text-gray-400"
                  maxlength="1000"
                ></textarea>
                <div class="text-right text-[12px] text-gray-400">{{ postForm.content.length }}/1000</div>
              </div>

              <div class="mt-auto flex justify-end gap-4 border-t border-gray-50 pt-6">
                <button
                  class="rounded-full bg-gray-100 px-6 py-2.5 text-[14px] font-medium text-gray-600 transition-colors hover:bg-gray-200"
                  type="button"
                  @click="resetUpload"
                >
                  {{ COPY.cancel }}
                </button>
                <button
                  class="rounded-full px-8 py-2.5 text-[14px] font-medium text-white transition-all"
                  :class="canPublish ? 'bg-[#FF2442] shadow-md hover:-translate-y-0.5 hover:bg-red-600 hover:shadow-lg' : 'cursor-not-allowed bg-[#FF91A0] shadow-none'"
                  :disabled="!canPublish"
                  type="button"
                  @click="handlePublish"
                >
                  {{ COPY.publish }}
                </button>
              </div>
            </div>
          </section>

          <section v-else class="relative flex flex-1 items-center justify-center bg-white p-6 md:p-10">
            <div class="animate-fade-in relative flex h-[300px] w-[400px] flex-col items-center justify-center rounded-xl border border-gray-100 bg-white p-6 shadow-inner">
              <div class="mb-5 flex h-16 w-16 shrink-0 items-center justify-center rounded-full bg-red-50">
                <svg class="h-8 w-8 text-[#FF2442]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
              </div>

              <h2 class="mb-2 text-xl font-bold text-gray-900">{{ COPY.publishSuccess }}</h2>
              <p class="mb-6 text-[14px] text-gray-500">
                {{ COPY.publishSuccessTip }}
                <span class="mx-1 font-bold text-[#FF2442]">{{ countdown }}</span>
                {{ COPY.secondsSuffix }}
              </p>

              <div class="flex gap-4">
                <button
                  class="rounded-full border border-gray-200 px-5 py-2.5 text-[13px] font-medium text-gray-700 transition-colors hover:bg-gray-50"
                  type="button"
                  @click="goToHome"
                >
                  {{ COPY.backHome }}
                </button>
                <button
                  class="rounded-full bg-[#FF2442] px-5 py-2.5 text-[13px] font-medium text-white shadow-sm shadow-red-200 transition-colors hover:bg-red-600"
                  type="button"
                  @click="resetUpload"
                >
                  {{ COPY.createAnother }}
                </button>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import { ElMessage } from 'element-plus'

import { useAiPostAssistant } from '@/modules/ai/composables/useAiPostAssistant'
import { useCreatePostFlow } from '@/modules/post/composables/useCreatePostFlow'
import { useAuthStore } from '@/stores/auth'

const COPY = {
  creatorCenter: '\u521b\u4f5c\u8005\u4e2d\u5fc3',
  capturingIdeas: '\u6b63\u5728\u6355\u6349\u7075\u611f...',
  createSection: '\u521b\u4f5c',
  publishNote: '\u53d1\u5e03\u7b14\u8bb0',
  videoTab: '\u4e0a\u4f20\u89c6\u9891',
  imageTab: '\u4e0a\u4f20\u56fe\u6587',
  videoPrompt: '\u70b9\u51fb\u6216\u62d6\u62fd\u4e0a\u4f20\u89c6\u9891',
  imagePrompt: '\u70b9\u51fb\u6216\u62d6\u62fd\u4e0a\u4f20\u56fe\u7247',
  videoHint: '\u652f\u6301 mp4\u3001mov \u683c\u5f0f\uff0c\u6700\u591a 1 \u4e2a',
  imageHint: '\u652f\u6301 jpg\u3001png\u3001webp \u683c\u5f0f\uff0c\u6700\u591a 18 \u5f20',
  mediaPreview: '\u5a92\u4f53\u9884\u89c8',
  resetUpload: '\u91cd\u65b0\u4e0a\u4f20',
  titlePlaceholder: '\u586b\u5199\u6807\u9898\u4f1a\u6709\u66f4\u591a\u8d5e\u54e6~',
  contentPlaceholder: '\u6dfb\u52a0\u6b63\u6587',
  cancel: '\u53d6\u6d88',
  publish: '\u53d1\u5e03',
  publishSuccess: '\u53d1\u5e03\u6210\u529f',
  publishSuccessTip: '\u4f60\u7684\u7b14\u8bb0\u5df2\u6210\u529f\u53d1\u5e03\uff0c',
  secondsSuffix: '\u79d2\u540e\u81ea\u52a8\u91cd\u7f6e...',
  backHome: '\u56de\u9996\u9875',
  createAnother: '\u7ee7\u7eed\u521b\u4f5c',
  uploading: '\u6b63\u5728\u52aa\u529b\u4e0a\u4f20\u6587\u4ef6...',
  publishing: '\u7b14\u8bb0\u53d1\u5e03\u4e2d...',
  uploadFailed: '\u6587\u4ef6\u4e0a\u4f20\u4e2d\u65ad',
  publishFailed: '\u53d1\u5e03\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5',
  aiMagicTitle: '\u9b54\u6cd5\u751f\u6210',
  aiMagicHint: '\u8f93\u5165\u51e0\u4e2a\u7b80\u6d01\u5173\u952e\u8bcd\uff0cAI \u4f1a\u7ed3\u5408\u4f60\u5f53\u524d\u4e0a\u4f20\u7684\u56fe\u7247\u6216\u89c6\u9891\u5173\u952e\u5e27\uff0c\u5e2e\u4f60\u751f\u6210\u6807\u9898\u548c\u6b63\u6587\u8349\u7a3f\u3002',
  aiMagicPlaceholder: '\u4f8b\u5982\uff1a\u7a7f\u642d \u3001 \u5973\u5b69 \u3001 \u6625\u65e5',
  aiMagicExample: '\u5efa\u8bae\u8f93\u5165 2-5 \u4e2a\u5173\u952e\u8bcd\uff0cAI \u4f1a\u76f4\u63a5\u56de\u586b\u5230\u6807\u9898\u548c\u6b63\u6587\u533a\u57df\u3002',
  aiGenerate: '\u9b54\u6cd5\u751f\u6210',
  aiRegenerate: '\u91cd\u65b0\u751f\u6210',
  aiGenerating: '\u751f\u6210\u4e2d...',
  aiGenerateFailed: 'AI \u751f\u6210\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5',
  aiGenerateSuccess: 'AI \u5df2\u4e3a\u4f60\u751f\u6210\u8349\u7a3f',
} as const

const router = useRouter()
const authStore = useAuthStore()

const {
  step,
  publishType,
  uploadedMedia,
  selectedMediaFiles,
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
} = useCreatePostFlow()

const {
  keywords: magicKeywords,
  isGenerating: isAiGenerating,
  canGenerate: canGenerateMagic,
  generate: generateWithAi,
  reset: resetAiAssistant,
} = useAiPostAssistant({
  publishType,
  mediaFiles: selectedMediaFiles,
  postForm,
})

const inputAccept = computed(() => (publishType.value === 'video' ? 'video/*' : 'image/*'))
const uploadPrompt = computed(() => (publishType.value === 'video' ? COPY.videoPrompt : COPY.imagePrompt))
const uploadHint = computed(() => (publishType.value === 'video' ? COPY.videoHint : COPY.imageHint))
const busyLabel = computed(() => (isUploading.value ? COPY.uploading : COPY.publishing))
const aiActionText = computed(() => {
  if (isAiGenerating.value) {
    return COPY.aiGenerating
  }

  return postForm.title.trim() || postForm.content.trim()
    ? COPY.aiRegenerate
    : COPY.aiGenerate
})
const canUseAiMagic = computed(() => (
  canGenerateMagic.value
  && !isUploading.value
  && !isPublishing.value
))

const toMessage = (error: unknown, fallback: string) => {
  if (error instanceof Error && error.message) {
    return error.message
  }

  return fallback
}

const handleFileSelect = async (event: Event) => {
  const input = event.target as HTMLInputElement

  try {
    await selectFiles(input.files)
  } catch (error) {
    ElMessage.error(toMessage(error, COPY.uploadFailed))
  } finally {
    input.value = ''
  }
}

const removeImage = (index: number) => {
  removeMediaAt(index)
}

const resetUpload = () => {
  reset()
  resetAiAssistant()
}

const goToHome = () => {
  stopCountdown()
  void router.push('/')
}

const handlePublish = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  try {
    await publish()
  } catch (error) {
    ElMessage.error(toMessage(error, COPY.publishFailed))
  }
}

const handleGenerateWithAi = async () => {
  try {
    await generateWithAi()
    ElMessage.success(COPY.aiGenerateSuccess)
  } catch (error) {
    ElMessage.error(toMessage(error, COPY.aiGenerateFailed))
  }
}
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
