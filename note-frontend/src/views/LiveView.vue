<template>
  <main class="flex-1 flex flex-col bg-white h-screen overflow-y-auto scroll-smooth no-scrollbar relative">
    <AppSearchBar placeholder="Search Live" sticky z-class="z-50" />

    <div class="flex-1 flex flex-col items-center justify-center pb-[20vh]">
      <svg class="w-[100px] h-[100px] text-gray-200 mb-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
      </svg>
      <h2 class="text-[18px] font-bold text-gray-800 mb-2">直播功能暂未开放</h2>
      <p class="text-[14px] text-gray-400">主播们正在快马加鞭赶来的路上，敬请期待哦~</p>
    </div>
  </main>
</template>

<script setup lang="ts">
import AppSearchBar from '@/shared/ui/AppSearchBar.vue'
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