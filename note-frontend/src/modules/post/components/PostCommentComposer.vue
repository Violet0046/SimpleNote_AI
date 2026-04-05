<template>
  <div class="relative flex shrink-0 items-center gap-4 border-t border-gray-100 bg-white px-6 py-4 dark:border-gray-800 dark:bg-gray-900">
    <Transition name="fade">
      <div v-if="replyingTo" class="absolute -top-8 left-6 z-10 flex items-center gap-2 rounded-t-lg border border-gray-100 bg-white px-3 py-1 text-[12px] text-gray-600 shadow-sm dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300">
        <span>{{ replyingToLabel }} @{{ replyingTo.authorName }}</span>
        <button type="button" class="ml-1 font-bold text-red-500 hover:text-red-600" @click="$emit('clear-reply')">x</button>
      </div>
    </Transition>

    <div class="flex flex-1 items-center rounded-full bg-gray-100 px-4 py-2 transition-all duration-200 dark:bg-gray-800">
      <input
        :ref="setInputRef"
        :value="modelValue"
        type="text"
        :readonly="readonly"
        :placeholder="placeholder"
        class="w-full !bg-transparent text-[14px] text-gray-900 outline-none placeholder:text-gray-500 dark:text-gray-100 dark:placeholder:text-gray-400"
        :class="{ 'cursor-pointer': readonly }"
        @input="handleInput"
        @click="$emit('input-click', $event)"
        @keyup.enter="$emit('submit')"
      />
    </div>

    <div class="flex shrink-0 items-center gap-4">
      <button
        type="button"
        class="rounded-full px-4 py-1.5 text-[13px] font-medium text-white transition-colors duration-200"
        :class="canSubmit ? 'bg-[#FF2442] hover:bg-red-600' : 'cursor-not-allowed bg-[#FF91A0]'"
        :disabled="!canSubmit"
        @click="$emit('submit')"
      >
        {{ sending ? sendingLabel : sendLabel }}
      </button>

      <button
        type="button"
        class="flex items-center gap-1 transition-colors"
        :class="isLiked ? 'text-[#FF2442]' : 'text-gray-500 hover:text-[#FF2442]'"
        @click="$emit('toggle-like')"
      >
        <svg class="h-6 w-6" :fill="isLiked ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
        </svg>
        <span class="text-[13px] font-medium">{{ likeCountLabel }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ComponentPublicInstance } from 'vue'

import type { PostComment } from '@/modules/post/post-detail.types'

interface Props {
  modelValue: string
  readonly: boolean
  placeholder: string
  canSubmit: boolean
  sending: boolean
  sendLabel: string
  sendingLabel: string
  isLiked: boolean
  likeCountLabel: string
  replyingTo: PostComment | null
  replyingToLabel: string
  bindInputRef?: ((element: HTMLInputElement | null) => void) | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'submit'): void
  (e: 'toggle-like'): void
  (e: 'clear-reply'): void
  (e: 'input-click', event: Event): void
}>()

const handleInput = (event: Event) => {
  emit('update:modelValue', (event.target as HTMLInputElement).value)
}

const setInputRef = (element: Element | ComponentPublicInstance | null) => {
  props.bindInputRef?.(element instanceof HTMLInputElement ? element : null)
}
</script>
