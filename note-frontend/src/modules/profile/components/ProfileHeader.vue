<template>
  <div class="flex flex-col items-center pt-[20px] pb-[40px]">
    <div class="flex w-full max-w-[800px] items-center gap-[30px] px-[40px]">
      <div class="h-[140px] w-[140px] flex-shrink-0 overflow-hidden rounded-full border border-gray-200 bg-gray-100">
        <img :src="userInfo.avatar || avatarFallback" class="h-full w-full object-cover" />
      </div>

      <div class="flex flex-1 flex-col gap-[10px]">
        <div class="flex w-full flex-wrap items-center gap-x-20 gap-y-3">
          <h1 class="max-w-[280px] truncate text-[28px] font-semibold text-gray-900" :title="displayName">
            {{ displayName }}
          </h1>

          <button
            v-if="showAction"
            type="button"
            class="flex-shrink-0 rounded-full border px-5 py-1.5 text-[14px] font-semibold transition-colors"
            :class="actionClass"
            @click="$emit('action')"
          >
            {{ actionLabel }}
          </button>
        </div>

        <div class="flex items-center gap-4 text-[13px] text-gray-500">
          <span>User ID: {{ userInfo.id }}</span>
          <span v-if="userInfo.ipLocation">IP: {{ userInfo.ipLocation }}</span>
          <div
            v-if="genderBadge"
            class="flex h-[22px] w-[22px] items-center justify-center rounded-full text-[11px] font-bold"
            :class="genderBadge.className"
          >
            {{ genderBadge.label }}
          </div>
        </div>

        <p class="mt-1 text-[14px] text-gray-700">{{ userInfo.intro || emptyBioText }}</p>

        <div class="mt-2 flex gap-6 text-[14px]">
          <button
            v-for="stat in stats"
            :key="stat.key"
            type="button"
            class="transition-colors duration-200 disabled:cursor-default"
            :class="stat.active ? 'font-bold text-[#FF2442]' : stat.clickable ? 'text-gray-600 hover:text-black' : 'text-gray-600'"
            :disabled="!stat.clickable"
            @click="handleStatClick(stat)"
          >
            <span class="mr-1 font-semibold" :class="stat.active ? 'text-[#FF2442]' : 'text-gray-900'">
              {{ stat.count ?? 0 }}
            </span>
            {{ stat.label }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { UserInfo } from '@/types'

interface ProfileStat {
  key: string
  label: string
  count: number
  active?: boolean
  clickable?: boolean
}

interface Props {
  userInfo: UserInfo
  displayName: string
  stats: ProfileStat[]
  showAction?: boolean
  actionLabel?: string
  actionClass?: string
  avatarFallback?: string
  emptyBioText?: string
}

const props = withDefaults(defineProps<Props>(), {
  showAction: false,
  actionLabel: '',
  actionClass: 'border-gray-300 text-gray-700 hover:bg-gray-50',
  avatarFallback: 'http://localhost:8080/1.jpg',
  emptyBioText: 'No bio yet',
})

const genderBadge = computed(() => {
  if (props.userInfo.gender === 1) {
    return {
      label: 'M',
      className: 'bg-[#EBF3FF] text-[#1E90FF]',
    }
  }

  if (props.userInfo.gender === 0) {
    return {
      label: 'F',
      className: 'bg-[#FFECF0] text-[#FF4D85]',
    }
  }

  return null
})

const emit = defineEmits<{
  (e: 'action'): void
  (e: 'stat-click', key: string): void
}>()

const handleStatClick = (stat: ProfileStat) => {
  if (!stat.clickable) return
  emit('stat-click', stat.key)
}
</script>
