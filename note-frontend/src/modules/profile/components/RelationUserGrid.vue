<template>
  <div class="mx-auto w-full max-w-[1100px] px-4 py-4">
    <div v-if="users.length > 0" class="grid grid-cols-2 gap-5 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      <div
        v-for="user in users"
        :key="user.id"
        class="flex flex-col items-center rounded-2xl border border-gray-100 bg-white p-5 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md"
      >
        <img
          :src="user.avatar || avatarFallback"
          class="mb-3 h-16 w-16 cursor-pointer rounded-full border border-gray-100 object-cover hover:opacity-90"
          @click="openUserPage(user.id)"
        />

        <span
          class="w-full cursor-pointer truncate text-center text-[15px] font-bold text-gray-900 transition-colors hover:text-blue-600"
          @click="openUserPage(user.id)"
        >
          {{ user.nickname }}
        </span>

        <span class="mb-4 mt-1 w-full truncate text-center text-[12px] text-gray-400">
          {{ user.intro || introFallback }}
        </span>

        <button
          v-if="user.id !== authUserId"
          type="button"
          class="w-full rounded-full py-1.5 text-[13px] font-bold transition-colors"
          :class="getFollowButtonInfo(user).class"
          @click="$emit('follow', user)"
        >
          {{ getFollowButtonInfo(user).text }}
        </button>
      </div>
    </div>

    <div class="py-10 text-center">
      <button
        v-if="hasMore"
        type="button"
        class="rounded-full border border-gray-200 px-8 py-2 text-sm text-gray-500 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
        :disabled="loading"
        @click="$emit('load-more')"
      >
        {{ loading ? loadingText : loadMoreText }}
      </button>
      <span v-else-if="users.length > 0" class="text-sm text-gray-400">{{ noMoreText }}</span>
      <span v-else class="text-sm text-gray-400">{{ emptyText }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useOpenUserPage } from '@/shared/composables/useOpenUserPage'

interface RelationUser {
  id: number
  nickname: string
  avatar?: string
  intro?: string
  isFollowing?: boolean
  isFollower?: boolean
}

interface FollowButtonInfo {
  text: string
  class: string
}

interface Props {
  users: RelationUser[]
  authUserId?: number | null
  hasMore: boolean
  loading: boolean
  avatarFallback?: string
  introFallback?: string
  emptyText?: string
  loadMoreText?: string
  loadingText?: string
  noMoreText?: string
  resolveFollowButton?: ((user: RelationUser) => FollowButtonInfo) | null
}

const props = withDefaults(defineProps<Props>(), {
  authUserId: null,
  avatarFallback: 'http://localhost:8080/1.jpg',
  introFallback: 'No intro yet',
  emptyText: 'Nothing here yet',
  loadMoreText: 'Load more',
  loadingText: 'Loading...',
  noMoreText: 'No more users',
  resolveFollowButton: null,
})

defineEmits<{
  (e: 'follow', user: RelationUser): void
  (e: 'load-more'): void
}>()

const { openUserPage } = useOpenUserPage()

const getFollowButtonInfo = (user: RelationUser) => {
  return props.resolveFollowButton?.(user) ?? {
    text: 'Follow',
    class: 'bg-[#FF2442] text-white hover:bg-red-600',
  }
}
</script>
