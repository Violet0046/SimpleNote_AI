<template>
  <div class="group">
    <div :class="itemContainerClass">
      <img
        :src="comment.authorAvatar || avatarFallback"
        :class="avatarClass"
        @click="onOpenUserPage(comment.userId)"
      />
      <div class="flex-1">
        <div class="flex items-center gap-1.5">
          <p :class="authorNameClass" @click="onOpenUserPage(comment.userId)">
            {{ comment.authorName }}
          </p>
          <span v-if="comment.userId === postUserId" :class="authorBadgeClass">
            {{ authorBadge }}
          </span>
        </div>

        <p :class="contentClass" @click="onReply(rootComment, comment)">
          <span v-if="showReplyPrefix" class="mr-1 text-blue-500">
            {{ replyToLabel }} {{ comment.replyToUserName }}:
          </span>
          {{ comment.content }}
        </p>

        <div class="mt-1.5 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <span :class="timeClass">{{ formatTime(comment.createTime) }}</span>
            <button type="button" :class="replyButtonClass" @click="onReply(rootComment, comment)">
              {{ replyLabel }}
            </button>
          </div>

          <button
            type="button"
            class="flex items-center gap-1 transition-colors"
            :class="isLiked ? 'text-[#FF2442]' : 'text-gray-400 hover:text-[#FF2442]'"
            @click="onToggleLike(comment)"
          >
            <svg :class="likeIconClass" :fill="isLiked ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span v-if="comment.likesCount" :class="likeCountClass">{{ comment.likesCount }}</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="comment.replies && comment.replies.length > 0" class="ml-11 mt-3 space-y-4">
      <PostCommentItem
        v-for="reply in comment.replies"
        :key="reply.id"
        :comment="reply"
        :root-comment="rootComment"
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

    <div v-if="canExpandReplies" class="ml-11 mt-2 text-[12px]">
      <button type="button" class="flex items-center font-medium text-blue-500 transition-colors hover:text-blue-600" @click="onExpandReplies(comment)">
        <span class="mr-2 h-px w-4 bg-gray-300 dark:bg-gray-600"></span>
        {{ comment.replies && comment.replies.length > 0 ? expandMoreRepliesLabel : expandRepliesLabel }}
        ({{ hiddenReplyCount }})
        <svg class="ml-0.5 mt-0.5 h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { PostComment } from '@/modules/post/post-detail.types'

defineOptions({
  name: 'PostCommentItem',
})

interface Props {
  comment: PostComment
  rootComment?: PostComment
  postUserId: number
  avatarFallback: string
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
}

const props = defineProps<Props>()

const rootComment = computed(() => props.rootComment ?? props.comment)
const isNested = computed(() => Boolean(props.rootComment))
const isLiked = computed(() => props.comment.isLiked === 1 || props.comment.isLiked === true)
const canExpandReplies = computed(() => {
  return Boolean(props.comment.childCount && props.comment.childCount > (props.comment.replies?.length || 0))
})
const hiddenReplyCount = computed(() => {
  return Math.max(0, (props.comment.childCount || 0) - (props.comment.replies?.length || 0))
})
const showReplyPrefix = computed(() => {
  return Boolean(props.rootComment && props.comment.replyToUserName && props.comment.replyToUserName !== rootComment.value.authorName)
})

const itemContainerClass = computed(() => (isNested.value ? 'flex gap-2.5' : 'flex gap-3'))
const avatarClass = computed(() => {
  return isNested.value
    ? 'h-6 w-6 shrink-0 cursor-pointer rounded-full border border-gray-100 transition-opacity hover:opacity-80 dark:border-gray-700'
    : 'h-8 w-8 shrink-0 cursor-pointer rounded-full border border-gray-100 transition-opacity hover:opacity-80 dark:border-gray-700'
})
const authorNameClass = computed(() => {
  return isNested.value
    ? 'cursor-pointer text-[12px] font-medium text-gray-500 transition-colors hover:text-blue-500'
    : 'cursor-pointer text-[13px] font-medium text-gray-500 transition-colors hover:text-blue-500'
})
const authorBadgeClass = computed(() => {
  return isNested.value
    ? 'rounded-sm bg-[#FF2442] px-1.5 py-[1px] text-[9px] font-bold text-white shadow-sm'
    : 'rounded-sm bg-[#FF2442] px-1.5 py-[1px] text-[10px] font-bold text-white shadow-sm'
})
const contentClass = computed(() => {
  return isNested.value
    ? 'mt-0.5 cursor-text text-[13px] leading-snug text-gray-900 dark:text-gray-100'
    : 'mt-0.5 cursor-text text-[14px] leading-snug text-gray-900 dark:text-gray-100'
})
const timeClass = computed(() => (isNested.value ? 'text-[11px] text-gray-400' : 'text-[12px] text-gray-400'))
const replyButtonClass = computed(() => {
  return isNested.value
    ? 'text-[11px] font-medium text-gray-400 opacity-0 transition-opacity group-hover:opacity-100 hover:text-gray-600'
    : 'text-[12px] font-medium text-gray-400 opacity-0 transition-opacity group-hover:opacity-100 hover:text-gray-600'
})
const likeIconClass = computed(() => (isNested.value ? 'h-3.5 w-3.5' : 'h-4 w-4'))
const likeCountClass = computed(() => (isNested.value ? 'text-[11px] font-medium' : 'text-[12px] font-medium'))
</script>
