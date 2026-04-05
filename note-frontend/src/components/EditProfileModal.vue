<template>
  <Transition name="fade">
    <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm transition-opacity" @click="handleClose"></div>

      <div class="relative w-full max-w-md overflow-hidden rounded-[24px] bg-white shadow-2xl transition-all">
        <div class="flex items-center justify-between border-b border-gray-100 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-900">编辑资料</h3>
          <button class="rounded-full p-2 text-gray-400 transition-colors hover:bg-gray-50 hover:text-gray-600" @click="handleClose">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <div class="space-y-6 p-6 pb-8">
          <div class="flex flex-col items-center justify-center">
            <div class="group relative cursor-pointer" @click="triggerFileInput">
              <div class="h-24 w-24 overflow-hidden rounded-full border-2 border-gray-100 shadow-sm transition-all group-hover:border-red-400 group-hover:shadow-md">
                <img :src="form.avatar || 'http://localhost:8080/1.jpg'" class="h-full w-full object-cover" />
              </div>
              <div class="absolute inset-0 flex items-center justify-center rounded-full bg-black/30 opacity-0 backdrop-blur-[2px] transition-opacity group-hover:opacity-100">
                <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"></path>
                </svg>
              </div>
              <input ref="fileInput" type="file" class="hidden" accept="image/jpeg,image/png,image/gif,image/webp" @change="handleFileUpload" />
            </div>
            <span class="mt-2 text-xs text-gray-400">点击更换头像</span>
          </div>

          <div>
            <label class="mb-1.5 ml-1 block text-[13px] font-medium text-gray-700">昵称</label>
            <input
              v-model="form.nickname"
              type="text"
              maxlength="20"
              class="w-full rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-2.5 text-[15px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
              placeholder="取一个好听的昵称"
            />
          </div>

          <div>
            <label class="mb-1.5 ml-1 block text-[13px] font-medium text-gray-700">性别</label>
            <div class="flex gap-3">
              <button
                type="button"
                class="flex-1 rounded-xl py-2.5 text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 1 ? 'bg-[#EBF3FF] text-[#1E90FF] ring-1 ring-[#1E90FF]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                @click="form.gender = 1"
              >
                男
              </button>
              <button
                type="button"
                class="flex-1 rounded-xl py-2.5 text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 0 ? 'bg-[#FFECF0] text-[#FF4D85] ring-1 ring-[#FF4D85]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                @click="form.gender = 0"
              >
                女
              </button>
              <button
                type="button"
                class="flex-1 rounded-xl py-2.5 text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 2 ? 'bg-gray-800 text-white ring-1 ring-gray-800' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
                @click="form.gender = 2"
              >
                保密
              </button>
            </div>
          </div>

          <div>
            <label class="mb-1.5 ml-1 block text-[13px] font-medium text-gray-700">简介</label>
            <div class="relative">
              <textarea
                v-model="form.intro"
                rows="3"
                maxlength="100"
                class="w-full resize-none rounded-xl border border-transparent bg-[#F7F7F7] px-4 py-3 text-[15px] text-gray-800 outline-none transition-all placeholder-gray-400 focus:border-red-400 focus:bg-white focus:ring-4 focus:ring-red-50"
                placeholder="介绍一下你自己..."
              ></textarea>
              <span class="absolute bottom-3 right-3 text-xs text-gray-400">
                {{ form.intro?.length || 0 }}/100
              </span>
            </div>
          </div>
        </div>

        <div class="flex gap-3 bg-gray-50 px-6 py-4">
          <button
            class="flex-1 rounded-full border border-gray-200 bg-white py-2.5 font-semibold text-gray-600 transition-colors hover:bg-gray-50"
            @click="handleClose"
          >
            取消
          </button>
          <button
            :disabled="isSubmitting"
            class="flex flex-1 items-center justify-center gap-2 rounded-full bg-[#FF2442] py-2.5 font-semibold text-white transition-colors hover:bg-red-600 disabled:opacity-50"
            @click="handleSubmit"
          >
            <svg v-if="isSubmitting" class="h-4 w-4 animate-spin text-white" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isSubmitting ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { uploadFile } from '@/modules/upload/upload.api'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { UserInfo } from '@/types'

const props = defineProps<{
  visible: boolean
  userInfo: UserInfo
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'success'): void
}>()

const isSubmitting = ref(false)

const form = reactive({
  nickname: '',
  gender: 0,
  intro: '',
  avatar: '',
})

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      form.nickname = props.userInfo.nickname || ''
      form.intro = props.userInfo.intro || ''
      form.avatar = props.userInfo.avatar || ''
      form.gender = Number(props.userInfo.gender ?? 0)
    }
  },
)

const handleClose = () => {
  if (!isSubmitting.value) {
    emit('close')
  }
}

const fileInput = ref<HTMLInputElement | null>(null)

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB')
    return
  }

  try {
    form.avatar = await uploadFile(file)
    ElMessage.success('头像上传成功')
  } catch (err: any) {
    ElMessage.error('图片上传失败')
  } finally {
    if (fileInput.value) fileInput.value.value = ''
  }
}

const handleSubmit = async () => {
  if (!form.nickname || !form.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }

  try {
    isSubmitting.value = true

    const payload = {
      nickname: form.nickname.trim(),
      gender: form.gender,
      intro: form.intro ? form.intro.trim() : '',
      avatar: form.avatar,
    }

    const response = await post('/user/update', payload)

    if (response.code === 1) {
      ElMessage.success('资料已更新')
      emit('success')
      emit('close')
    } else {
      ElMessage.error(response.msg || '更新失败，请重试')
    }
  } catch (err: any) {
    console.error('Profile update error:', err)
    ElMessage.error('更新失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
