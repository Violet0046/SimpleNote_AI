<template>
  <div :class="rootClasses">
    <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[54px] rounded-full bg-[#F7F7F7] flex items-center px-4">
      <input
        :value="modelValue"
        :placeholder="placeholder"
        :readonly="readonly"
        class="flex-1 bg-transparent outline-none text-sm text-gray-800 placeholder:text-gray-400"
        type="text"
        @input="handleInput"
      />

      <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
        />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue?: string
  placeholder?: string
  sticky?: boolean
  topClass?: string
  zClass?: string
  readonly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: 'Search',
  sticky: false,
  topClass: 'top-0',
  zClass: 'z-10',
  readonly: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const rootClasses = computed(() => [
  'w-full bg-white pt-[20px] pb-[10px] flex justify-center items-center',
  props.zClass,
  props.sticky ? ['sticky', props.topClass] : null,
])

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>