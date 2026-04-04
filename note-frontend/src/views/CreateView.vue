<template>
  <div class="min-h-screen bg-[#FAFAFA] flex flex-col">
    <header class="bg-white h-16 px-6 flex items-center justify-between border-b border-gray-100 shadow-sm shrink-0">
      <div class="flex items-center gap-2 cursor-pointer" @click="router.push('/')">
        <img src="@/assets/logo.svg" class="w-8 h-8" alt="Logo" />
        <span class="text-lg font-bold text-gray-900">创作者中心</span>
      </div>
      <div class="flex items-center gap-4">
        <img :src="authStore.userInfo?.avatar || 'http://localhost:8080/1.jpg'" class="w-8 h-8 rounded-full object-cover border border-gray-200" />
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden">
      <aside class="w-[200px] bg-white border-r border-gray-100 flex flex-col py-6 shrink-0">
        <div class="px-6 mb-2 text-xs font-semibold text-gray-400 tracking-wider">创作</div>
        <div class="mx-3 px-4 py-2.5 bg-[#FFE8EB] text-[#FF2442] rounded-lg font-medium cursor-pointer flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
          发布笔记
        </div>
      </aside>

      <main class="flex-1 overflow-y-auto p-6 md:p-10 flex justify-center">
        
        <div class="w-full max-w-[1000px] bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden flex flex-col min-h-[600px]">
          
          <div v-if="step === 1" class="flex-1 flex flex-col">
            <div class="flex justify-center border-b border-gray-100 pt-6">
              <div class="flex gap-12">
                <button 
                  class="relative pb-4 text-[16px] font-semibold transition-colors"
                  :class="publishType === 'video' ? 'text-gray-900' : 'text-gray-500 hover:text-gray-700'"
                  @click="publishType = 'video'"
                >
                  上传视频
                  <div v-if="publishType === 'video'" class="absolute bottom-0 left-1/2 -translate-x-1/2 w-8 h-[3px] bg-[#FF2442] rounded-full"></div>
                </button>
                <button 
                  class="relative pb-4 text-[16px] font-semibold transition-colors"
                  :class="publishType === 'image' ? 'text-gray-900' : 'text-gray-500 hover:text-gray-700'"
                  @click="publishType = 'image'"
                >
                  上传图文
                  <div v-if="publishType === 'image'" class="absolute bottom-0 left-1/2 -translate-x-1/2 w-8 h-[3px] bg-[#FF2442] rounded-full"></div>
                </button>
              </div>
            </div>

            <div class="flex-1 flex items-center justify-center p-10">
              <label class="w-[400px] h-[300px] border-2 border-dashed border-gray-300 rounded-xl hover:border-[#FF2442] hover:bg-[#FFF5F6] transition-all flex flex-col items-center justify-center cursor-pointer group">
                <svg class="w-16 h-16 text-gray-300 group-hover:text-[#FF2442] mb-4 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <span class="text-[16px] font-medium text-gray-700 group-hover:text-[#FF2442]">
                  点击或拖拽上传{{ publishType === 'video' ? '视频' : '图片' }}
                </span>
                <span class="text-[13px] text-gray-400 mt-2">
                  {{ publishType === 'video' ? '支持 mp4、mov 格式，最多 1 个' : '支持 jpg、png、webp 格式，最多 18 张' }}
                </span>
                <input type="file" class="hidden" :accept="publishType === 'video' ? 'video/*' : 'image/*'" :multiple="publishType === 'image'" @change="handleFileSelect" />
              </label>
            </div>
          </div>

          <div v-else class="flex-1 flex flex-col md:flex-row h-full relative">
            
            <div v-if="isUploading || isPublishing" class="absolute inset-0 z-50 bg-white/80 backdrop-blur-sm flex flex-col items-center justify-center">
              <svg class="animate-spin h-10 w-10 text-[#FF2442] mb-3" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
              <span class="text-gray-700 font-medium">{{ isUploading ? '正在努力上传文件...' : '笔记发布中...' }}</span>
            </div>

            <div class="w-full md:w-[40%] border-r border-gray-100 bg-[#F8F8F8] p-6 flex flex-col h-[600px] overflow-y-auto">
              <div class="flex justify-between items-center mb-4">
                <span class="text-[15px] font-bold text-gray-800">媒体预览</span>
                <button @click="resetUpload" class="text-[13px] text-gray-500 hover:text-[#FF2442]">重新上传</button>
              </div>

              <div v-if="publishType === 'video' && uploadedMedia.length > 0" class="w-full rounded-xl overflow-hidden shadow-sm bg-black aspect-[3/4] flex items-center justify-center">
                <video :src="uploadedMedia[0]" controls class="w-full h-full object-contain"></video>
              </div>

              <div v-if="publishType === 'image'" class="grid grid-cols-3 gap-2">
                <div v-for="(img, idx) in uploadedMedia" :key="idx" class="relative aspect-square rounded-lg overflow-hidden group border border-gray-200 bg-white">
                  <img :src="img" class="w-full h-full object-cover" />
                  <button @click="removeImage(idx)" class="absolute top-1 right-1 w-5 h-5 bg-black/50 text-white rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    ×
                  </button>
                </div>
                <label v-if="uploadedMedia.length < 18" class="aspect-square rounded-lg border-2 border-dashed border-gray-300 hover:border-gray-400 bg-white flex items-center justify-center cursor-pointer transition-colors">
                  <svg class="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
                  <input type="file" class="hidden" accept="image/*" multiple @change="handleFileSelect" />
                </label>
              </div>
            </div>

            <div class="flex-1 p-6 lg:p-10 flex flex-col h-[600px] bg-white">
              <div class="flex-1">
                <input 
                  v-model="postForm.title" 
                  type="text" 
                  placeholder="填写标题会有更多赞哦~" 
                  class="w-full text-[20px] font-bold text-gray-900 placeholder-gray-300 border-b border-gray-100 pb-4 mb-6 outline-none focus:border-gray-300 transition-colors"
                  maxlength="20"
                />
                
                <textarea 
                  v-model="postForm.content" 
                  placeholder="添加正文" 
                  class="w-full h-[200px] text-[15px] text-gray-700 placeholder-gray-400 outline-none resize-none leading-relaxed"
                  maxlength="1000"
                ></textarea>
                <div class="text-right text-[12px] text-gray-400">{{ postForm.content.length }}/1000</div>
              </div>

              <div class="pt-6 border-t border-gray-50 flex justify-end gap-4 mt-auto">
                <button @click="resetUpload" class="px-6 py-2.5 rounded-full text-[14px] font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 transition-colors">取消</button>
                <button 
                  @click="handlePublish" 
                  :disabled="isPublishing || !postForm.title.trim() || !postForm.content.trim() || uploadedMedia.length === 0"
                  class="px-8 py-2.5 rounded-full text-[14px] font-medium text-white transition-all shadow-md"
                  :class="(postForm.title.trim() && postForm.content.trim() && uploadedMedia.length > 0) ? 'bg-[#FF2442] hover:bg-red-600 hover:shadow-lg hover:-translate-y-0.5' : 'bg-[#FF91A0] cursor-not-allowed shadow-none'"
                >
                  发布
                </button>
              </div>
            </div>

          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

// 状态机: 1=选择文件, 2=编辑发布
const step = ref(1)
const publishType = ref<'video' | 'image'>('video')

// 数据状态
const uploadedMedia = ref<string[]>([])
const isUploading = ref(false)
const isPublishing = ref(false)

const postForm = ref({
  title: '',
  content: ''
})

// 🌟 原生 Fetch 极速上传引擎 (完美适配多文件，自带 Token)
const uploadFileToServer = async (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)

  const token = localStorage.getItem('userToken') // 从本地拿 token
  const res = await fetch('http://localhost:8080/upload', {
    method: 'POST',
    headers: {
      'Authorization': token || '' 
    },
    body: formData
  })
  
  const data = await res.json()
  if (data.code === 1) return data.data
  throw new Error(data.msg || '上传失败')
}

// 处理文件选择
const handleFileSelect = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const files = input.files
  if (!files || files.length === 0) return

  // 校验限制
  if (publishType.value === 'video') {
    if (files.length > 1 || uploadedMedia.value.length > 0) {
      return ElMessage.warning('视频最多只能上传 1 个哦')
    }
  } else {
    if (uploadedMedia.value.length + files.length > 18) {
      return ElMessage.warning('图片最多只能上传 18 张哦')
    }
  }

  isUploading.value = true
  step.value = 2 // 瞬间切到编辑页面

  try {
    // 🌟 修复方案：使用 Array.from 将 FileList 转换为纯 File 数组进行循环
    const fileList = Array.from(files)
    for (const file of fileList) {
      const url = await uploadFileToServer(file)
      uploadedMedia.value.push(url)
    }
    
  } catch (error: any) {
    ElMessage.error(error.message || '文件上传中断')
    if (uploadedMedia.value.length === 0) step.value = 1 
  } finally {
    isUploading.value = false
    input.value = '' 
  }
}

const removeImage = (index: number) => {
  uploadedMedia.value.splice(index, 1)
  if (uploadedMedia.value.length === 0) {
    step.value = 1 // 删光了自动退回第一步
  }
}

const resetUpload = () => {
  step.value = 1
  uploadedMedia.value = []
  postForm.value.title = ''
  postForm.value.content = ''
}

// 终极发布提交
const handlePublish = async () => {
  if (!authStore.isLoggedIn) return authStore.showLoginModal()
  
  isPublishing.value = true
  try {
    const payload = {
      title: postForm.value.title,
      content: postForm.value.content,
      images: uploadedMedia.value.join(','), // 逗号拼接
      isVideo: publishType.value === 'video' ? 1 : 0
    }

    const res = await post('/post/add', payload)
    if (res.code === 1) {
      ElMessage.success('笔记发布成功！')
      // 发布成功后，关闭当前新开的创作者网页，或者跳回主页
      setTimeout(() => {
        window.close() // 尝试关闭当前新开的标签页
        // 如果浏览器不让关，就降级回退到个人主页
        router.push('/profile') 
      }, 1000)
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后再试')
  } finally {
    isPublishing.value = false
  }
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
</style>