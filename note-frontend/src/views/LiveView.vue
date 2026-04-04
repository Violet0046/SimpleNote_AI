<template>
  <main class="flex-1 flex flex-col bg-white h-screen overflow-y-auto scroll-smooth no-scrollbar relative">
    
    <div class="sticky top-0 z-50 bg-white pt-[20px] pb-[10px] flex justify-center w-full">
      <div class="w-[480px] xl:w-[520px] 2xl:w-[580px] h-[54px] rounded-full bg-[#F7F7F7] border-none outline-none ring-0 shadow-none flex items-center px-4">
        <input type="text" placeholder="搜索小红书直播" class="flex-1 bg-transparent outline-none text-sm" />
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      </div>
    </div>

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
// 这是一个纯展示页面，暂时不需要复杂的逻辑
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