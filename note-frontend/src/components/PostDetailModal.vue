<template>
  <Teleport to="body">
    <Transition name="fade" appear>
      <div v-if="visible" class="fixed inset-0 z-[9998] bg-black/60 backdrop-blur-sm" @click="handleClose"></div>
    </Transition>

    <div
      v-if="visible && postDetail"
      class="fixed z-[9999] bg-white overflow-hidden shadow-2xl transition-all duration-[400ms] ease-[cubic-bezier(0.32,0.72,0,1)]"
      :style="modalStyle"
      @click.stop
    >
      <div v-show="!isExpanded" class="absolute inset-0 bg-cover bg-center" :style="{ backgroundImage: `url(${firstImage})` }"></div>

      <div class="flex h-full w-full transition-opacity duration-300" :class="isExpanded ? 'opacity-100' : 'opacity-0'" style="height: 100%;">
        
        <div class="flex-[6] relative bg-black flex items-center justify-center">
          
          <div v-if="postDetail?.images && !String(postDetail.images).includes(',')" class="relative w-full h-full flex items-center justify-center overflow-hidden">
            <img :src="postDetail.images" class="max-w-full max-h-full object-contain" />
          </div>

          <div v-else-if="postDetail?.images && String(postDetail.images).includes(',')" class="relative w-full h-full overflow-hidden">
            <div class="w-full h-full transition-transform duration-300 flex" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
              <img v-for="(img, idx) in imageList" :key="idx" :src="img" class="w-full h-full object-contain flex-shrink-0 bg-black" />
            </div>
            <button v-if="imageList.length > 1" @click="prevImage" class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-black/30 text-white rounded-full flex items-center justify-center backdrop-blur-md">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
            </button>
            <button v-if="imageList.length > 1" @click="nextImage" class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-black/30 text-white rounded-full flex items-center justify-center backdrop-blur-md">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
            </button>
            <div v-if="imageList.length > 1" class="absolute bottom-6 left-1/2 -translate-x-1/2 flex space-x-2">
              <div v-for="(_, idx) in imageList" :key="idx" class="w-2 h-2 rounded-full transition-all" :class="currentImageIndex === idx ? 'bg-white scale-110' : 'bg-white/50'"></div>
            </div>
          </div>

          <button @click="handleClose" class="absolute top-5 left-5 w-10 h-10 bg-black/30 text-white rounded-full flex items-center justify-center backdrop-blur-md">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <div class="flex-[4] min-w-[320px] max-w-[450px] bg-white flex flex-col">
          <div class="p-6 border-b border-gray-100 flex items-center space-x-3">
            <div class="w-12 h-12 rounded-full bg-gray-100 overflow-hidden border border-gray-200">
              <img v-if="postDetail?.authorAvatar" :src="postDetail.authorAvatar" class="w-full h-full object-cover" />
              <span v-else class="w-full h-full flex items-center justify-center text-gray-500">{{ postDetail?.authorName?.charAt(0).toUpperCase() }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900">{{ postDetail?.authorName }}</p>
              <p class="text-sm text-gray-500">{{ postDetail?.createTime }}</p>
            </div>
          </div>

          <div class="px-6 pt-5 pb-3">
            <h2 class="text-xl font-bold text-gray-900 mb-3">{{ postDetail?.title }}</h2>
            <div class="flex flex-wrap gap-2 mb-2">
              <span v-for="tag in tagList" :key="tag" class="px-3 py-1 bg-[#ff2442]/10 text-[#ff2442] text-sm rounded-full">#{{ tag }}</span>
            </div>
            <p class="text-gray-700 leading-relaxed whitespace-pre-wrap">{{ postDetail?.content }}</p>
          </div>

          <div class="flex-1 overflow-y-auto px-6 py-4 bg-gray-50/30 text-center">
            <span class="text-gray-400 text-sm mt-10 block">评论区功能一切正常加载中...</span>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import type { PropType } from 'vue'
import type { Post } from '@/types'

const props = defineProps({
  post: { type: Object as PropType<Post | null>, default: null },
  visible: { type: Boolean, default: false },
  triggerRect: { type: Object as PropType<DOMRect | null>, default: null }
})

const emit = defineEmits(['close'])

const postDetail = ref<Post | null>(null)
const isExpanded = ref(false)
const modalStyle = ref({ top: '0px', left: '0px', width: '0px', height: '0px', opacity: '0', borderRadius: '16px' })
const currentImageIndex = ref(0)

const firstImage = computed(() => props.post?.images ? String(props.post.images).split(',')[0].trim() : '')
const imageList = computed(() => props.post?.images ? String(props.post.images).split(',').map(i => i.trim()).filter(Boolean) : [])
const tagList = computed(() => props.post?.tags ? String(props.post.tags).split(',').map(t => t.trim()).filter(Boolean) : [])

// 🌟 最强动画引擎
watch(() => props.visible, async (isVisible) => {
  if (isVisible && props.post) {
    document.body.style.overflow = 'hidden'
    postDetail.value = props.post
    currentImageIndex.value = 0
    isExpanded.value = false

    // 1. 设置起点贴合卡片
    if (props.triggerRect) {
      modalStyle.value = {
        top: `${props.triggerRect.top}px`, left: `${props.triggerRect.left}px`,
        width: `${props.triggerRect.width}px`, height: `${props.triggerRect.height}px`,
        opacity: '1', borderRadius: '16px'
      }
    }

    // 2. 等待 Vue 真正把这个小框渲染到屏幕上
    await nextTick()
    
    // 3. 触发形变飞翔
    requestAnimationFrame(() => {
      const finalTop = 35
      const finalLeft = 350
      const finalWidth = window.innerWidth - finalLeft - 30
      const finalHeight = window.innerHeight - 70

      modalStyle.value = {
        top: `${finalTop}px`, left: `${finalLeft}px`,
        width: `${finalWidth}px`, height: `${finalHeight}px`,
        opacity: '1', borderRadius: '24px'
      }
      setTimeout(() => { isExpanded.value = true }, 300)
    })
  } else {
    // 关闭退场
    document.body.style.overflow = ''
    isExpanded.value = false
    if (props.triggerRect) {
      modalStyle.value = {
        top: `${props.triggerRect.top}px`, left: `${props.triggerRect.left}px`,
        width: `${props.triggerRect.width}px`, height: `${props.triggerRect.height}px`,
        opacity: '0', borderRadius: '16px'
      }
    }
  }
}, { immediate: true })

const prevImage = () => { currentImageIndex.value = currentImageIndex.value > 0 ? currentImageIndex.value - 1 : imageList.value.length - 1 }
const nextImage = () => { currentImageIndex.value = currentImageIndex.value < imageList.value.length - 1 ? currentImageIndex.value + 1 : 0 }
const handleClose = () => emit('close')
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>