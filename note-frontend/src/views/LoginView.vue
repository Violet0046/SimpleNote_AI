<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <div class="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-red-100">
          <svg class="h-8 w-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
          </svg>
        </div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">登录账号</h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          还没有账号？
          <a href="#" class="font-medium text-red-500 hover:text-red-600">立即注册</a>
        </p>
      </div>

      <AuthModal @close="$router.back()" />
    </div>
  </div>
</template>

<script setup lang="ts">
import AuthModal from '@/components/AuthModal.vue'
</script>