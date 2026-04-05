<template>
  <div class="sticky top-[84px] z-40 flex w-full justify-center border-b border-gray-100 bg-white pt-[10px]">
    <div class="flex h-[48px] gap-12 px-4">
      <button
        v-for="item in visibleItems"
        :key="item.key"
        type="button"
        class="relative h-full px-2 text-[16px] transition-colors"
        :class="activeTab === item.key ? 'font-semibold text-gray-900' : 'text-gray-500 hover:text-gray-700'"
        @click="$emit('select', item.key)"
      >
        {{ item.label }}
        <div
          v-show="activeTab === item.key"
          class="absolute bottom-0 left-1/2 h-[3px] w-4 -translate-x-1/2 rounded-full bg-[#FF2442]"
        ></div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface ProfileTabItem {
  key: string
  label: string
  visible?: boolean
}

const props = defineProps<{
  items: ProfileTabItem[]
  activeTab: string
}>()

defineEmits<{
  (e: 'select', key: string): void
}>()

const visibleItems = computed(() => props.items.filter((item) => item.visible !== false))
</script>
