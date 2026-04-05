<template>
  <div class="px-6 pb-6">
    <div class="mb-5 flex items-center justify-between">
      <h3 class="text-[14px] font-medium text-gray-600 dark:text-gray-400">{{ commentsLabel }} {{ totalCommentsCount }}</h3>
      <div class="flex items-center gap-3 text-[13px] text-gray-400 dark:text-gray-500">
        <button
          type="button"
          class="transition-colors"
          :class="currentSortType === 2 ? 'font-semibold text-gray-900 dark:text-gray-100' : 'hover:text-gray-600'"
          @click="$emit('sort-change', 2)"
        >
          {{ hottestLabel }}
        </button>
        <span class="h-3 w-px bg-gray-200 dark:bg-gray-700"></span>
        <button
          type="button"
          class="transition-colors"
          :class="currentSortType === 1 ? 'font-semibold text-gray-900 dark:text-gray-100' : 'hover:text-gray-600'"
          @click="$emit('sort-change', 1)"
        >
          {{ newestLabel }}
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="flex justify-center py-4">
      <span class="text-sm text-gray-400">{{ loadingLabel }}</span>
    </div>

    <div v-else-if="comments.length === 0" class="flex flex-col items-center justify-center py-10 opacity-60">
      <span class="text-[13px] text-gray-400">{{ emptyLabel }}</span>
    </div>

    <div v-else class="space-y-6">
      <PostCommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :post-user-id="postUserId"
        :avatar-fallback="avatarFallback"
        :author-badge="authorBadge"
        :reply-label="replyLabel"
        :reply-to-label="replyToLabel"
        :expand-replies-label="expandRepliesLabel"
        :expand-more-replies-label="expandMoreRepliesLabel"
        :format-time="formatTime"
        :on-reply="onReply"
        :on-toggle-like="onToggleLike"
        :on-expand-replies="onExpandReplies"
        :on-open-user-page="onOpenUserPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CommentSortType, PostComment } from '@/modules/post/post-detail.types'
import PostCommentItem from '@/modules/post/components/PostCommentItem.vue'

defineProps<{
  comments: PostComment[]
  isLoading: boolean
  totalCommentsCount: number
  currentSortType: CommentSortType
  postUserId: number
  avatarFallback: string
  commentsLabel: string
  hottestLabel: string
  newestLabel: string
  loadingLabel: string
  emptyLabel: string
  authorBadge: string
  replyLabel: string
  replyToLabel: string
  expandRepliesLabel: string
  expandMoreRepliesLabel: string
  formatTime: (timeStr?: string) => string
  onReply: (rootComment: PostComment, targetComment: PostComment) => void
  onToggleLike: (comment: PostComment) => void | Promise<void>
  onExpandReplies: (comment: PostComment) => void | Promise<void>
  onOpenUserPage: (userId?: number | null) => void
}>()

defineEmits<{
  (e: 'sort-change', sortType: CommentSortType): void
}>()
</script>
