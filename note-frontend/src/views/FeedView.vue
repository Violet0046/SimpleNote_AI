<template>
  <main class="flex h-screen flex-1 flex-col overflow-hidden bg-white">
    <AppSearchBar placeholder="Search Notes" z-class="z-10" />

    <div ref="scrollContainer" class="no-scrollbar flex-1 overflow-y-auto bg-white px-[6px] pb-[40px]">
      <div
        class="flex w-full items-center justify-center overflow-hidden transition-all duration-300"
        :class="isRefreshing ? 'h-[60px] opacity-100' : 'h-0 opacity-0'"
      >
        <svg class="-ml-1 mr-3 h-6 w-6 animate-spin text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
        <span class="text-sm font-medium text-gray-500">Refreshing recommendations...</span>
      </div>

      <PostWaterfall
        v-if="hasWaterfallPosts"
        :columns="waterfallColumns"
        :refresh-key="refreshKey"
        :set-column-ref="setColumnRef"
        :is-post-liked="likeStore.isPostLiked"
        animated
        @post-click="openPostDetail"
        @post-like="handleLike"
      />

      <div class="flex w-full flex-col items-center py-8">
        <div v-if="loading && !isRefreshing" class="mb-4 flex items-center gap-2 text-gray-400">
          <svg class="h-5 w-5 animate-spin text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
          </svg>
          <span class="text-sm font-medium">Loading...</span>
        </div>
        <div ref="loadMoreTrigger" class="h-10 w-full bg-transparent" style="pointer-events: none;"></div>
      </div>
    </div>

    <PostDetailModal
      :post="selectedPost"
      :visible="showModal"
      :trigger-rect="triggerRect"
      @close="closePostDetail"
      @like-toggle="handleLike"
    />
  </main>
</template>

<script setup lang="ts">
import {
  computed,
  inject,
  nextTick,
  onActivated,
  onBeforeUnmount,
  onMounted,
  ref,
  watch,
  type Ref,
} from 'vue'

import PostDetailModal from '@/components/PostDetailModal.vue'
import PostWaterfall from '@/modules/post/components/PostWaterfall.vue'
import { useInfiniteScroll } from '@/shared/composables/useInfiniteScroll'
import { useMasonryColumns } from '@/shared/composables/useMasonryColumns'
import { usePostDetailModal } from '@/shared/composables/usePostDetailModal'
import AppSearchBar from '@/shared/ui/AppSearchBar.vue'
import { useLikeStore } from '@/stores/like'
import type { Post, PostListResponse } from '@/types'
import { get } from '@/utils/request'

const scrollContainer = ref<HTMLElement | null>(null)
const savedScrollTop = ref(0)
let detachScrollListener: (() => void) | null = null
const SCROLL_STORAGE_KEY = 'feed-scroll-top'
const likeStore = useLikeStore()

const posts = ref<Post[]>([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(30)
const isRefreshing = ref(false)
const refreshKey = ref(0)

const refreshDiscoverTrigger = inject<Ref<number>>('refreshDiscoverTrigger', ref(0))

const {
  appendItems,
  columnCount,
  columns: waterfallColumns,
  relayout,
  setColumnRef,
  updateColumnCount,
} = useMasonryColumns<Post>({
  defaultColumns: 4,
  strategy: 'balanced',
  breakpoints: [
    { minWidth: 1280, columns: 4 },
    { minWidth: 768, columns: 3 },
    { minWidth: 0, columns: 2 },
  ],
})

const {
  selectedPost,
  visible: showModal,
  triggerRect,
  openPostDetail,
  closePostDetail,
} = usePostDetailModal<Post>()

const hasWaterfallPosts = computed(() => waterfallColumns.value.some((column) => column.length > 0))
const canLoadMore = computed(() => hasMore.value && !loading.value && !isRefreshing.value)

const { targetRef: loadMoreTrigger } = useInfiniteScroll({
  enabled: canLoadMore,
  onIntersect: async () => {
    await fetchPosts(true)
  },
  root: scrollContainer,
  rootMargin: '600px',
  threshold: 0.1,
})

watch(columnCount, async () => {
  if (posts.value.length > 0) {
    await relayout(posts.value)
  }
})

watch(refreshDiscoverTrigger, (newValue) => {
  if (newValue > 0) {
    void handleForceRefresh({ resetScroll: true })
  }
})

const fetchPosts = async (isLoadMore = false) => {
  if (loading.value) return

  loading.value = true

  try {
    const res = await get<PostListResponse>('/post/list/page', {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    })

    if (res.code !== 1) return

    const items = res.data.items || []
    likeStore.syncPostLikes(items)
    if (items.length === 0 && posts.value.length === 0) {
      hasMore.value = false
      return
    }

    const randomizedItems = [...items].sort(() => Math.random() - 0.5)

    if (isLoadMore) {
      posts.value.push(...randomizedItems)
      await appendItems(randomizedItems)
    } else {
      posts.value = randomizedItems
      await relayout(posts.value)
    }

    if (items.length < pageSize.value) {
      currentPage.value = 1
      hasMore.value = true
    } else {
      currentPage.value += 1
      hasMore.value = true
    }
  } finally {
    loading.value = false
  }
}

interface ForceRefreshOptions {
  resetScroll?: boolean
}

const handleForceRefresh = async ({ resetScroll = false }: ForceRefreshOptions = {}) => {
  if (resetScroll) {
    scrollContainer.value?.scrollTo({ top: 0, behavior: 'auto' })
    sessionStorage.setItem(SCROLL_STORAGE_KEY, '0')
    savedScrollTop.value = 0
  }

  isRefreshing.value = true
  currentPage.value = 1
  hasMore.value = true

  try {
    await fetchPosts(false)
    refreshKey.value += 1
  } finally {
    window.setTimeout(() => {
      isRefreshing.value = false
    }, 500)
  }
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)

  posts.value.forEach((post) => {
    if (post.id === postId) {
      post.likesCount = isLiked ? (post.likesCount || 0) + 1 : Math.max(0, (post.likesCount || 0) - 1)
    }
  })
}

const saveScroll = () => {
  if (!scrollContainer.value) return
  const top = scrollContainer.value.scrollTop
  savedScrollTop.value = top
  sessionStorage.setItem(SCROLL_STORAGE_KEY, String(top))
}

const restoreScroll = (immediate = false) => {
  const stored = Number(sessionStorage.getItem(SCROLL_STORAGE_KEY))
  const top = Number.isFinite(stored) ? stored : savedScrollTop.value
  savedScrollTop.value = top
  
  if (immediate) {
    nextTick(() => {
      if (scrollContainer.value) {
        scrollContainer.value.scrollTo({ top, behavior: 'auto' })
      }
    })
  } else {
    setTimeout(() => {
      if (scrollContainer.value) {
        scrollContainer.value.scrollTo({ top, behavior: 'auto' })
      }
    }, 50)
  }
}

onMounted(() => {
  updateColumnCount()
  void (async () => {
    await fetchPosts()
    await nextTick()
    restoreScroll(false)
  })()

  const handler = saveScroll

  nextTick(() => {
    if (scrollContainer.value) {
      scrollContainer.value.addEventListener('scroll', handler, { passive: true })
    }
  })

  detachScrollListener = () => {
    if (scrollContainer.value) {
      scrollContainer.value.removeEventListener('scroll', handler)
    }
  }
})

onBeforeUnmount(() => {
  detachScrollListener?.()
})

onActivated(() => {
  restoreScroll(true)
})
</script>
