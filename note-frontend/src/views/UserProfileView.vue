<template>
  <main ref="scrollContainer" class="no-scrollbar relative flex h-screen flex-1 flex-col overflow-y-auto scroll-smooth bg-[var(--sn-bg-app)]">
    <AppSearchBar placeholder="Search Notes" sticky z-class="z-50" />

    <ProfileHeader
      :user-info="userInfo"
      :display-name="userInfo.nickname || authStore.getUsername"
      :stats="profileStats"
      :show-action="true"
      :action-label="COPY.editProfile"
      :empty-bio-text="COPY.emptyBio"
      @action="showEditModal = true"
      @stat-click="handleHeaderTabSelect"
    />

    <ProfileTabs :items="profileTabs" :active-tab="activeTab" @select="switchTab" />

    <div class="w-full bg-[var(--sn-bg-app)] px-[6px] pb-[60px] pt-[20px]">
      <section v-if="isPostTab">
        <div v-if="loading && userPosts.length === 0" class="mt-10 flex justify-center">
          <svg class="h-8 w-8 animate-spin text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>

        <PostWaterfall
          v-else-if="hasWaterfallPosts"
          :columns="waterfallColumns"
          :is-post-liked="likeStore.isPostLiked"
          :set-column-ref="setColumnRef"
          @post-click="openPostDetail"
          @post-like="handleLike"
        />

        <div v-else class="mt-[100px] flex flex-col items-center justify-center opacity-70">
          <svg class="mb-4 h-[80px] w-[80px] text-gray-200" viewBox="0 0 1024 1024" fill="currentColor">
            <path d="M512 85.333333c-235.648 0-426.666667 191.018667-426.666667 426.666667s191.018667 426.666667 426.666667 426.666667 426.666667-191.018667 426.666667-426.666667S747.648 85.333333 512 85.333333z m0 768c-188.501333 0-341.333333-152.832-341.333333-341.333333 0-188.501333 152.832-341.333333 341.333333-341.333333s341.333333 152.832 341.333333 341.333333c0 188.501333-152.832 341.333333-341.333333 341.333333zM384 469.333333c-23.552 0-42.666667 19.114667-42.666667 42.666667s19.114667 42.666667 42.666667 42.666667 42.666667-19.114667 42.666667-42.666667-19.114667-42.666667-42.666667-42.666667z m256 0c-23.552 0-42.666667 19.114667-42.666667 42.666667s19.114667 42.666667 42.666667 42.666667 42.666667-19.114667 42.666667-42.666667-19.114667-42.666667-42.666667-42.666667z m-128 213.333334c-70.698667 0-128-57.301333-128-128h256c0 70.698667-57.301333 128-128 128z"></path>
          </svg>
          <span class="text-[14px] text-gray-500">{{ activeTab === 'posts' ? COPY.emptyPosts : COPY.emptyLikedPosts }}</span>
        </div>

        <div class="flex w-full flex-col items-center py-8">
          <div v-if="loading && userPosts.length > 0" class="mb-4 flex items-center gap-2 text-gray-400">
            <svg class="h-5 w-5 animate-spin text-[#FF2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span class="text-sm font-medium">{{ COPY.loadingMore }}</span>
          </div>
          <div ref="loadMoreTrigger" class="h-10 w-full bg-transparent" style="pointer-events: none;"></div>
        </div>
      </section>

      <RelationUserGrid
        v-else
        :users="userList"
        :auth-user-id="authStore.userInfo?.id"
        :has-more="hasMoreUsers"
        :loading="isLoadingUsers"
        :intro-fallback="COPY.relationIntro"
        :empty-text="COPY.emptyRelation"
        :load-more-text="COPY.loadMore"
        :loading-text="COPY.loadingMore"
        :no-more-text="COPY.noMore"
        :resolve-follow-button="resolveFollowButtonInfo"
        @follow="handleListFollow"
        @load-more="loadMoreUsers"
      />
    </div>

    <PostDetailModal
      :post="selectedPost"
      :visible="showModal"
      :trigger-rect="triggerRect"
      @close="closePostDetail"
    />

    <EditProfileModal
      :visible="showEditModal"
      :user-info="userInfo"
      @close="showEditModal = false"
      @success="handleProfileUpdated"
    />
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'

import { ElMessage } from 'element-plus'

import EditProfileModal from '@/components/EditProfileModal.vue'
import PostDetailModal from '@/components/PostDetailModal.vue'
import PostWaterfall from '@/modules/post/components/PostWaterfall.vue'
import {
  fetchCurrentUserProfile,
  fetchLikedPosts,
  fetchOwnProfilePosts,
  fetchProfileRelations,
  setFollowUser,
} from '@/modules/profile/profile.api'
import { useProfilePostFeed } from '@/modules/profile/composables/useProfilePostFeed'
import { useRelationList } from '@/modules/profile/composables/useRelationList'
import ProfileHeader from '@/modules/profile/components/ProfileHeader.vue'
import ProfileTabs from '@/modules/profile/components/ProfileTabs.vue'
import RelationUserGrid from '@/modules/profile/components/RelationUserGrid.vue'
import type { NormalizedRelationUser, OwnProfileTabKey, RelationUser } from '@/modules/profile/profile.types'
import { createEmptyUserInfo } from '@/modules/profile/profile.types'
import { getErrorMessage, getFollowButtonInfo, normalizeRelationUser } from '@/modules/profile/profile.utils'
import { useInfiniteScroll } from '@/shared/composables/useInfiniteScroll'
import { useMasonryColumns } from '@/shared/composables/useMasonryColumns'
import { usePostDetailModal } from '@/shared/composables/usePostDetailModal'
import AppSearchBar from '@/shared/ui/AppSearchBar.vue'
import { useAuthStore } from '@/stores/auth'
import { useLikeStore } from '@/stores/like'
import type { Post } from '@/types'

const COPY = {
  editProfile: '\u7f16\u8f91\u8d44\u6599',
  emptyBio: '\u8fd8\u6ca1\u6709\u7b80\u4ecb\u54e6~',
  emptyPosts: '\u4f60\u8fd8\u6ca1\u6709\u53d1\u5e03\u4efb\u4f55\u5185\u5bb9\u54e6',
  emptyLikedPosts: '\u4f60\u8fd8\u6ca1\u6709\u70b9\u8d5e\u8fc7\u4efb\u4f55\u5185\u5bb9\u54e6',
  relationIntro: '\u8fd9\u4e2a\u4eba\u5f88\u61d2\uff0c\u4ec0\u4e48\u90fd\u6ca1\u5199~',
  loadMore: '\u52a0\u8f7d\u66f4\u591a',
  loadingMore: '\u52a0\u8f7d\u4e2d...',
  noMore: '\u6ca1\u6709\u66f4\u591a\u4e86',
  emptyRelation: '\u5217\u8868\u6682\u65f6\u4e3a\u7a7a',
  loginFirst: '\u8bf7\u5148\u767b\u5f55',
  actionFailed: '\u64cd\u4f5c\u5931\u8d25',
  fetchListFailed: '\u83b7\u53d6\u5217\u8868\u5931\u8d25',
  fetchUserFailed: '\u83b7\u53d6\u7528\u6237\u4fe1\u606f\u5931\u8d25',
  tabs: {
    posts: '\u7b14\u8bb0',
    liked: '\u8d5e\u8fc7',
    following: '\u5173\u6ce8',
    followers: '\u7c89\u4e1d',
    likes: '\u83b7\u8d5e',
  },
  followButton: {
    mutual: '\u4e92\u7c89',
    following: '\u5df2\u5173\u6ce8',
    followBack: '\u56de\u5173',
    follow: '\u5173\u6ce8',
  },
} as const

const authStore = useAuthStore()
const likeStore = useLikeStore()

const scrollContainer = ref<HTMLElement | null>(null)
const showEditModal = ref(false)
const userInfo = ref(createEmptyUserInfo())
const activeTab = ref<OwnProfileTabKey>('posts')

const {
  appendItems,
  columnCount,
  columns: waterfallColumns,
  relayout,
  setColumnRef,
  updateColumnCount,
} = useMasonryColumns<Post>({
  defaultColumns: 4,
  strategy: 'sequential',
  breakpoints: [
    { minWidth: 1280, columns: 4 },
    { minWidth: 768, columns: 3 },
    { minWidth: 0, columns: 2 },
  ],
})

const {
  posts: userPosts,
  loading,
  hasMore,
  fetchPosts,
  reset: resetPostState,
  applyLike,
} = useProfilePostFeed({
  loadPage: async (pageNum, pageSize) => {
    if (activeTab.value === 'liked') {
      return fetchLikedPosts({ pageNum, pageSize })
    }

    return fetchOwnProfilePosts({ pageNum, pageSize })
  },
  onAppend: appendItems,
  onReplace: relayout,
  pageSize: 15,
})

const {
  items: userList,
  hasMore: hasMoreUsers,
  loading: isLoadingUsers,
  fetchItems: fetchRelationUsers,
  loadMore: loadMoreRelationUsers,
} = useRelationList<NormalizedRelationUser>({
  loadPage: async (pageNum, pageSize) => {
    const currentUserId = authStore.userInfo?.id ?? userInfo.value.id
    if (!currentUserId) {
      return []
    }

    const relationType = activeTab.value === 'following' ? 'following' : 'followers'
    const relations = await fetchProfileRelations({
      userId: currentUserId,
      relationType,
      pageNum,
      pageSize,
    })

    return relations.map(normalizeRelationUser)
  },
  pageSize: 20,
})

const {
  selectedPost,
  visible: showModal,
  triggerRect,
  openPostDetail,
  closePostDetail,
} = usePostDetailModal<Post>({ closeDelay: 300 })

const isPostTab = computed(() => activeTab.value === 'posts' || activeTab.value === 'liked')
const canLoadMorePosts = computed(() => hasMore.value && !loading.value && isPostTab.value)
const hasWaterfallPosts = computed(() => waterfallColumns.value.some((column) => column.length > 0))

const profileStats = computed(() => [
  {
    key: 'following',
    label: COPY.tabs.following,
    count: userInfo.value.followingCount || 0,
    active: activeTab.value === 'following',
    clickable: true,
  },
  {
    key: 'followers',
    label: COPY.tabs.followers,
    count: userInfo.value.followersCount || 0,
    active: activeTab.value === 'followers',
    clickable: true,
  },
  {
    key: 'likes',
    label: COPY.tabs.likes,
    count: userInfo.value.likesCount || 0,
    active: false,
    clickable: false,
  },
])

const profileTabs = computed(() => [
  { key: 'posts', label: COPY.tabs.posts },
  { key: 'liked', label: COPY.tabs.liked },
  { key: 'following', label: COPY.tabs.following, visible: activeTab.value === 'following' },
  { key: 'followers', label: COPY.tabs.followers, visible: activeTab.value === 'followers' },
])

const resolveFollowButtonInfo = (user: RelationUser) => getFollowButtonInfo(user, COPY.followButton)

const { targetRef: loadMoreTrigger } = useInfiniteScroll({
  enabled: canLoadMorePosts,
  onIntersect: async () => {
    await loadUserPosts(true)
  },
  root: scrollContainer,
  rootMargin: '600px',
  threshold: 0.1,
})

watch(columnCount, async () => {
  await relayout(userPosts.value)
})

const loadCurrentUserInfo = async () => {
  try {
    const profile = await fetchCurrentUserProfile()
    userInfo.value = profile
    authStore.setUserInfo(profile)
  } catch (error) {
    ElMessage.error(getErrorMessage(error, COPY.fetchUserFailed))
  }
}

const loadUserPosts = async (isLoadMore = false) => {
  try {
    await fetchPosts(isLoadMore)
  } catch (error) {
    ElMessage.error(getErrorMessage(error, COPY.fetchListFailed))
  }
}

const resetPostFeed = async () => {
  await resetPostState()
  await loadUserPosts()
}

const loadRelationList = async (resetList = false) => {
  try {
    await fetchRelationUsers({ resetList })
  } catch (error) {
    ElMessage.error(getErrorMessage(error, COPY.fetchListFailed))
  }
}

const loadMoreUsers = async () => {
  try {
    await loadMoreRelationUsers()
  } catch (error) {
    ElMessage.error(getErrorMessage(error, COPY.fetchListFailed))
  }
}

const handleListFollow = async (user: RelationUser) => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning(COPY.loginFirst)
    authStore.showLoginModal()
    return
  }

  try {
    const relationUser = user as NormalizedRelationUser
    const nextState = await setFollowUser(relationUser.id, !relationUser.isFollowing)
    if (nextState.changed && nextState.following) {
      userInfo.value.followingCount += 1
    } else if (nextState.changed) {
      userInfo.value.followingCount = Math.max(0, userInfo.value.followingCount - 1)
    }

    relationUser.isFollowing = nextState.following
  } catch (error) {
    ElMessage.error(getErrorMessage(error, COPY.actionFailed))
  }
}

const switchTab = (tabKey: string) => {
  if (activeTab.value === tabKey) return

  activeTab.value = tabKey as OwnProfileTabKey
  scrollContainer.value?.scrollTo({ top: 0, behavior: 'auto' })

  if (isPostTab.value) {
    void resetPostFeed()
    return
  }

  void loadRelationList(true)
}

const handleHeaderTabSelect = (tabKey: string) => {
  if (tabKey === 'following' || tabKey === 'followers') {
    switchTab(tabKey)
  }
}

const handleLike = (postId: number, isLiked: boolean) => {
  if (isLiked) likeStore.addLikedPost(postId)
  else likeStore.removeLikedPost(postId)

  applyLike(postId, isLiked)
}

const handleProfileUpdated = async () => {
  await loadCurrentUserInfo()
}

onMounted(() => {
  updateColumnCount()
  void loadCurrentUserInfo()
  void loadUserPosts()
})
</script>
