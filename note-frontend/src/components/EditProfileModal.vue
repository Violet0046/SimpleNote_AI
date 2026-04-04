<template>
  <Transition name="fade">
    <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm transition-opacity" @click="handleClose"></div>
      
      <div class="relative bg-white rounded-[24px] shadow-2xl w-full max-w-md overflow-hidden transform transition-all">
        
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 class="text-lg font-semibold text-gray-900">编辑资料</h3>
          <button @click="handleClose" class="p-2 text-gray-400 hover:text-gray-600 transition-colors rounded-full hover:bg-gray-50">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <div class="p-6 pb-8 space-y-6">
          
          <div class="flex flex-col items-center justify-center">
            <div class="relative group cursor-pointer" @click="triggerFileInput">
              <div class="w-24 h-24 rounded-full overflow-hidden border-2 border-gray-100 shadow-sm transition-all group-hover:border-red-400 group-hover:shadow-md">
                <img :src="form.avatar || 'http://localhost:8080/1.jpg'" class="w-full h-full object-cover" />
              </div>
              <div class="absolute inset-0 bg-black/30 backdrop-blur-[2px] flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity rounded-full">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
              </div>
              <input type="file" ref="fileInput" class="hidden" accept="image/jpeg,image/png,image/gif,image/webp" @change="handleFileUpload" />
            </div>
            <span class="text-xs text-gray-400 mt-2">点击更换头像</span>
          </div>

          <div>
            <label class="block text-[13px] font-medium text-gray-700 mb-1.5 ml-1">昵称</label>
            <input 
              v-model="form.nickname" 
              type="text" 
              maxlength="20"
              class="w-full px-4 py-2.5 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[15px] text-gray-800 placeholder-gray-400"
              placeholder="起个好听的名字吧"
            >
          </div>

          <div>
            <label class="block text-[13px] font-medium text-gray-700 mb-1.5 ml-1">性别</label>
            <div class="flex gap-3">
              <button 
                type="button"
                @click="form.gender = 1"
                class="flex-1 py-2.5 rounded-xl text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 1 ? 'bg-[#EBF3FF] text-[#1E90FF] ring-1 ring-[#1E90FF]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
              >
                ♂ 男生
              </button>
              <button 
                type="button"
                @click="form.gender = 2"
                class="flex-1 py-2.5 rounded-xl text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 2 ? 'bg-[#FFECF0] text-[#FF4D85] ring-1 ring-[#FF4D85]' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
              >
                ♀ 女生
              </button>
              <button 
                type="button"
                @click="form.gender = 0"
                class="flex-1 py-2.5 rounded-xl text-[14px] font-medium transition-all duration-200"
                :class="form.gender === 0 ? 'bg-gray-800 text-white ring-1 ring-gray-800' : 'bg-[#F7F7F7] text-gray-500 hover:bg-gray-100'"
              >
                保密
              </button>
            </div>
          </div>

          <div>
            <label class="block text-[13px] font-medium text-gray-700 mb-1.5 ml-1">简介</label>
            <div class="relative">
              <textarea 
                v-model="form.intro" 
                rows="3"
                maxlength="100"
                class="w-full px-4 py-3 bg-[#F7F7F7] border border-transparent rounded-xl focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-50 transition-all outline-none text-[15px] text-gray-800 placeholder-gray-400 resize-none"
                placeholder="介绍一下你自己吧..."
              ></textarea>
              <span class="absolute bottom-3 right-3 text-xs text-gray-400">
                {{ form.intro?.length || 0 }}/100
              </span>
            </div>
          </div>
        </div>

        <div class="px-6 py-4 bg-gray-50 flex gap-3">
          <button 
            @click="handleClose" 
            class="flex-1 py-2.5 rounded-full font-semibold text-gray-600 bg-white border border-gray-200 hover:bg-gray-50 transition-colors"
          >
            取消
          </button>
          <button 
            @click="handleSubmit" 
            :disabled="isSubmitting"
            class="flex-1 py-2.5 rounded-full font-semibold text-white bg-[#FF2442] hover:bg-red-600 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
          >
            <svg v-if="isSubmitting" class="animate-spin h-4 w-4 text-white" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            {{ isSubmitting ? '保存中...' : '保存修改' }}
          </button>
        </div>

      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { UserInfo } from '@/types'

const props = defineProps<{
  visible: boolean
  userInfo: UserInfo
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'success'): void // 更新成功后通知父组件刷新
}>()

const isSubmitting = ref(false)

// 表单数据
const form = reactive({
  nickname: '',
  gender: 0,
  intro: '',
  avatar: ''
})

// 当弹窗打开时，把当前的用户信息回显到表单里
watch(() => props.visible, (newVal) => {
  if (newVal) {
    form.nickname = props.userInfo.nickname || ''
    form.gender = props.userInfo.gender || 0
    form.intro = props.userInfo.intro || ''
    form.avatar = props.userInfo.avatar || ''
  }
})

const handleClose = () => {
  if (!isSubmitting.value) {
    emit('close')
  }
}

// ============== 头像上传逻辑 ==============
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

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await post<string>('/upload', formData)
    if (response.code === 1) {
      form.avatar = response.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(response.msg || '头像上传失败')
    }
  } catch (err: any) {
    ElMessage.error('图片上传失败，请检查网络')
  } finally {
    if (fileInput.value) fileInput.value.value = ''
  }
}

// ============== 提交修改逻辑 ==============
const handleSubmit = async () => {
  // 1. 防御性校验，防止 form.nickname 为 undefined 导致代码崩溃
  if (!form.nickname || !form.nickname.trim()) {
    ElMessage.warning('昵称不能为空哦')
    return
  }

  try {
    isSubmitting.value = true
    
    // 2. 组装参数：严格对照后端 User 实体类的字段！
    const payload = {
      nickname: form.nickname.trim(),
      gender: form.gender,
      intro: form.intro ? form.intro.trim() : '',
      avatar: form.avatar // ⚠️ 注意：这里必须是 avatar，和后端的数据库字段对应
    }

    // 3. 发送请求给咱们写好的后端接口
    const response = await post('/user/update', payload)
    
    if (response.code === 1) {
      ElMessage.success('资料修改成功！')
      emit('success') // 通知父组件去重新拉取最新数据
      emit('close')   // 优雅地关掉弹窗
    } else {
      ElMessage.error(response.msg || '修改失败，请重试')
    }
  } catch (err: any) {
    console.error("提交资料报错:", err)
    ElMessage.error('网络开了个小差，请检查后端是否启动')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* 弹窗渐隐渐显动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>