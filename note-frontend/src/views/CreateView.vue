<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 创建笔记页面 -->
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="bg-white rounded-2xl shadow-sm p-8">
        <h1 class="text-2xl font-bold text-gray-900 mb-6">发布笔记</h1>

        <!-- 表单 -->
        <form @submit.prevent="handleSubmit">
          <!-- 标题 -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">标题</label>
            <input
              v-model="formData.title"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent"
              placeholder="输入笔记标题"
              maxlength="50"
            >
            <p class="mt-1 text-xs text-gray-500">{{ formData.title.length }}/50</p>
          </div>

          <!-- 内容 -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">内容</label>
            <textarea
              v-model="formData.content"
              required
              rows="8"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent"
              placeholder="分享你的想法..."
              maxlength="2000"
            ></textarea>
            <p class="mt-1 text-xs text-gray-500">{{ formData.content.length }}/2000</p>
          </div>

          <!-- 图片上传 -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">添加图片</label>
            <div class="grid grid-cols-3 gap-4">
              <div
                v-for="(image, index) in formData.images"
                :key="index"
                class="aspect-square relative group"
              >
                <img
                  :src="image.url"
                  :alt="`上传的图片 ${index + 1}`"
                  class="w-full h-full object-cover rounded-lg"
                >
                <button
                  type="button"
                  @click="removeImage(index)"
                  class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                </button>
              </div>
              <button
                type="button"
                @click="addImage"
                class="aspect-square border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center hover:border-gray-400 transition-colors"
              >
                <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
              </button>
            </div>
            <p class="mt-2 text-sm text-gray-500">最多可上传9张图片</p>
          </div>

          <!-- 标签 -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">标签</label>
            <div class="flex flex-wrap gap-2 mb-2">
              <span
                v-for="(tag, index) in formData.tags"
                :key="index"
                class="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm flex items-center"
              >
                #{{ tag }}
                <button
                  type="button"
                  @click="removeTag(index)"
                  class="ml-2 text-red-500 hover:text-red-700"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                </button>
              </span>
            </div>
            <div class="flex">
              <input
                v-model="newTag"
                @keyup.enter="addTag"
                type="text"
                class="flex-1 px-4 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent"
                placeholder="添加标签"
              >
              <button
                type="button"
                @click="addTag"
                class="px-4 py-2 bg-red-500 text-white rounded-r-md hover:bg-red-600 transition-colors"
              >
                添加
              </button>
            </div>
          </div>

          <!-- 发布按钮 -->
          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="cancel"
              class="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-6 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {{ isSubmitting ? '发布中...' : '发布' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const isSubmitting = ref(false)
const newTag = ref('')

const formData = reactive({
  title: '',
  content: '',
  images: [] as Array<{ url: string }>,
  tags: [] as string[]
})

// 添加图片
const addImage = () => {
  if (formData.images.length >= 9) {
    ElMessage.warning('最多只能上传9张图片')
    return
  }
  // TODO: 这里应该触发文件选择器
  formData.images.push({
    url: `https://picsum.photos/400/400?random=${Date.now()}`
  })
}

// 移除图片
const removeImage = (index: number) => {
  formData.images.splice(index, 1)
}

// 添加标签
const addTag = () => {
  if (!newTag.value.trim() || formData.tags.includes(newTag.value.trim())) {
    return
  }
  formData.tags.push(newTag.value.trim())
  newTag.value = ''
}

// 移除标签
const removeTag = (index: number) => {
  formData.tags.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  isSubmitting.value = true

  try {
    // TODO: 实际图片上传逻辑
    const imageData = formData.images.map(img => img.url)

    await post('/post/create', {
      title: formData.title,
      content: formData.content,
      images: imageData.join(','),
      tags: formData.tags.join(',')
    })

    ElMessage.success('发布成功')
    router.push('/')
  } catch (error: any) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    isSubmitting.value = false
  }
}

// 取消
const cancel = () => {
  router.push('/')
}
</script>