<template>
  <main class="flex h-screen flex-1 flex-col overflow-hidden bg-white">
    <AppSearchBar placeholder="Search Notifications" z-class="z-10" />

    <div class="no-scrollbar flex-1 overflow-y-auto bg-white">
      <div class="mx-auto w-full max-w-[800px] px-4 py-8">
        
        <section class="grid grid-cols-3 gap-6 mb-10">
          <button
            v-for="item in quickActions"
            :key="item.label"
            type="button"
            class="group flex flex-col items-center gap-3"
          >
            <div
              class="flex h-16 w-16 items-center justify-center rounded-2xl text-white shadow-md transition duration-300 group-hover:scale-110 group-active:scale-95"
              :class="item.bg"
            >
              <svg class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="item.iconPath" />
              </svg>
            </div>
            <span class="text-[14px] font-bold text-gray-800">{{ item.label }}</span>
          </button>
        </section>

        <div class="h-[1px] w-full bg-gray-100 mb-8"></div>

        <div class="mb-4 px-2 text-[16px] font-bold text-gray-900">最近消息</div>

        <section class="space-y-1">
          <article
            v-for="notice in notices"
            :key="notice.id"
            class="flex items-center gap-4 rounded-2xl px-4 py-5 transition-colors duration-200 hover:bg-gray-50 cursor-pointer"
          >
            <div class="relative h-14 w-14 flex-shrink-0">
              <template v-if="notice.avatars.length === 1">
                <div :class="['flex h-full w-full items-center justify-center rounded-full text-lg font-bold border-2 border-white shadow-sm', getAvatarBg(notice.avatars[0]!)]">
                  {{ notice.avatars[0] }}
                </div>
              </template>
              <template v-else>
                <div :class="['absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-full text-xs font-bold border-2 border-white z-10 shadow-sm', getAvatarBg(notice.avatars[0]!)]">
                  {{ notice.avatars[0] }}
                </div>
                <div :class="['absolute right-0 bottom-0 flex h-10 w-10 items-center justify-center rounded-full text-xs font-bold border-2 border-white shadow-sm', getAvatarBg(notice.avatars[1]!)]">
                  {{ notice.avatars[1] }}
                </div>
              </template>
            </div>

            <div class="flex-1 min-w-0">
              <div class="text-[15px] leading-snug">
                <span class="font-bold text-gray-900 hover:text-blue-600 transition-colors">{{ notice.user }}</span>
                <span class="ml-2 text-gray-600">{{ notice.action }}</span>
              </div>
              <p class="mt-2 text-[13px] text-gray-400 font-medium">{{ notice.time }}</p>
            </div>

            <div v-if="notice.thumbnail" class="h-14 w-14 flex-shrink-0 overflow-hidden rounded-xl border border-gray-100 bg-gray-50">
              <div
                class="flex h-full w-full items-center justify-center text-[11px] font-bold tracking-wider"
                :class="notice.thumbnailColor"
              >
                {{ notice.thumbnail }}
              </div>
            </div>
            
            <button
              v-else
              type="button"
              class="flex-shrink-0 rounded-full bg-[#FF2442] px-6 py-1.5 text-[13px] font-bold text-white transition duration-200 hover:bg-red-600 active:scale-95"
            >
              {{ notice.followLabel }}
            </button>
          </article>
        </section>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppSearchBar from '@/shared/ui/AppSearchBar.vue'

// --- 类型定义 ---
type NoticeType = 'like' | 'follow' | 'comment'

interface Notice {
  id: number
  type: NoticeType
  user: string
  action: string
  time: string
  avatars: string[]
  thumbnail?: string
  thumbnailColor?: string
  followLabel?: string
}

// --- 数据 Mock ---
const notices = computed<Notice[]>(() => [
  {
    id: 1,
    type: 'like',
    user: 'Alice',
    action: '\u8d5e\u4e86\u4f60\u7684\u7b14\u8bb0', 
    time: '2 \u5206\u949f\u524d',
    avatars: ['A'],
    thumbnail: 'NOTE',
    thumbnailColor: 'bg-rose-50 text-rose-500',
  },
  {
    id: 2,
    type: 'follow',
    user: 'Ben',
    action: '\u5f00\u59cb\u5173\u6ce8\u4e86\u4f60',
    time: '2 \u5c0f\u65f6\u524d',
    avatars: ['B'],
    followLabel: '\u56de\u7c89',
  },
  {
    id: 3,
    type: 'comment',
    user: 'Cindy',
    action: '\u8bc4\u8bba\u4e86\uff1a\u201c\u592a\u68d2\u4e86\uff01\u201d',
    time: '\u6628\u5929',
    avatars: ['C'],
    thumbnail: 'IMG',
    thumbnailColor: 'bg-sky-50 text-sky-500',
  },
  {
    id: 4,
    type: 'like',
    user: 'Dylan \u7b49 2 \u4eba',
    action: '\u8d5e\u4e86\u4f60\u7684\u7b14\u8bb0',
    time: '2 \u5929\u524d',
    avatars: ['D', 'E'],
    thumbnail: 'POST',
    thumbnailColor: 'bg-emerald-50 text-emerald-500',
  },
  {
    id: 5,
    type: 'follow',
    user: 'Fiona',
    action: '\u5f00\u59cb\u5173\u6ce8\u4e86\u4f60',
    time: '3 \u5929\u524d',
    avatars: ['F'],
    followLabel: '\u5173\u6ce8',
  },
])

interface QuickAction {
  label: string
  bg: string
  iconPath: string
}

const quickActions: QuickAction[] = [
  {
    label: '\u8d5e', 
    bg: 'bg-[#FF2442]', 
    iconPath: 'M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z',
  },
  {
    label: '\u65b0\u589e\u5173\u6ce8', 
    bg: 'bg-amber-400',
    iconPath: 'M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z',
  },
  {
    label: '\u8bc4\u8bba\u548c @', 
    bg: 'bg-sky-400',
    iconPath: 'M8 10h.01M12 10h.01M16 10h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z',
  },
]

const getAvatarBg = (name: string) => {
  const colors = [
    'bg-rose-100 text-rose-500',
    'bg-sky-100 text-sky-500',
    'bg-emerald-100 text-emerald-500',
    'bg-amber-100 text-amber-500',
    'bg-violet-100 text-violet-500'
  ]
  return colors[name.charCodeAt(0) % colors.length]
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>