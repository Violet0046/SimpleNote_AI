<template>
  <div
    v-if="open"
    class="ai-search-panel-container pointer-events-none fixed z-[420]"
    :style="panelContainerStyle"
  >
    <Transition name="assistant-panel" appear>
      <section
        ref="panelRootRef"
        class="ai-search-panel-shell pointer-events-auto flex h-full flex-col overflow-hidden rounded-[30px] border backdrop-blur-xl"
      >
        <div class="ai-search-panel-header border-b px-5 pb-4 pt-5" @mousedown="handleHeaderMouseDown">
          <div class="flex items-start justify-between gap-3">
            <div>
              <div class="flex items-center gap-2">
                <div class="flex h-10 w-10 items-center justify-center rounded-2xl bg-[#ff2442] text-white shadow-[0_10px_25px_rgba(255,36,66,0.28)]">
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      d="M9.813 15.904L9 18l-.813-2.096a2 2 0 00-1.116-1.116L5 14l2.071-.788a2 2 0 001.116-1.116L9 10l.813 2.096a2 2 0 001.116 1.116L13 14l-2.071.788a2 2 0 00-1.116 1.116zM17.813 7.904L17 10l-.813-2.096a2 2 0 00-1.116-1.116L13 6l2.071-.788a2 2 0 001.116-1.116L17 2l.813 2.096a2 2 0 001.116 1.116L21 6l-2.071.788a2 2 0 00-1.116 1.116z"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="1.8"
                    />
                  </svg>
                </div>
                <div>
                  <p class="ai-search-title text-[18px] font-semibold tracking-tight">AI 搜推</p>
                  <p class="ai-search-subtitle text-sm">边刷边问，帮你从站内帖子里快速找线索</p>
                </div>
              </div>
            </div>

            <button
              type="button"
              class="ai-search-close-button ai-search-panel-ignore-close flex h-10 w-10 items-center justify-center rounded-2xl transition-colors"
              @click="$emit('close')"
            >
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
              </svg>
            </button>
          </div>
        </div>

        <div ref="scrollAreaRef" class="ai-search-scroll-area flex-1 overflow-y-auto px-4 pb-4 pt-4">
          <section class="ai-search-panel-intro rounded-[24px] border p-4">
            <div class="flex items-start gap-3">
              <div class="mt-0.5 flex h-9 w-9 items-center justify-center rounded-2xl bg-[#fff0f3] text-[#ff2442]">
                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-4l-3 3-3-3z"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                  />
                </svg>
              </div>
              <div class="min-w-0 flex-1">
                <p class="text-[15px] font-semibold text-[#251c20]">今天想从站内讨论里找什么？</p>
                <p class="mt-1 text-sm leading-6 text-[#75656a]">
                  我会先理解你的问题，再从站内帖子里做轻量检索，最后基于证据总结答案。
                </p>
              </div>
            </div>

            <div class="mt-4 flex flex-wrap gap-2">
              <button
                v-for="prompt in starterPrompts"
                :key="prompt"
                type="button"
                class="ai-search-prompt-chip ai-search-panel-ignore-close rounded-full border px-3 py-2 text-left text-[13px] font-medium leading-5 transition"
                @click="submitQuestion(prompt)"
              >
                {{ prompt }}
              </button>
            </div>
          </section>

          <div v-if="conversations.length === 0" class="px-2 py-10 text-center text-sm leading-6 text-[#8b7a80]">
            你的问题会以小对话的形式保留在这里，回答下面会附上相关帖子，方便继续点进去看详情。
          </div>

          <div v-for="conversation in conversations" :key="conversation.id" class="mt-4 space-y-3">
            <div class="flex justify-end">
              <div class="ai-search-question-bubble max-w-[88%] rounded-[22px] rounded-br-md px-4 py-3 text-sm leading-6">
                {{ conversation.question }}
              </div>
            </div>

            <div class="ai-search-response-card rounded-[26px] border p-4">
              <div v-if="conversation.status === 'loading'" class="flex items-center gap-3 py-6 text-sm text-[#76666b]">
                <svg class="h-5 w-5 animate-spin text-[#ff2442]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                <span>正在整理站内讨论和相关帖子...</span>
              </div>

              <div v-else-if="conversation.status === 'error'" class="space-y-3">
                <div class="rounded-2xl bg-[#fff5f6] px-4 py-3 text-sm leading-6 text-[#9a4a5a]">
                  {{ conversation.error }}
                </div>
                <button
                  type="button"
                  class="ai-search-panel-ignore-close rounded-full bg-[#ff2442] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#e91f3c]"
                  @click="submitQuestion(conversation.question)"
                >
                  再试一次
                </button>
              </div>

              <template v-else-if="conversation.response">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="rounded-full bg-[#fff1f4] px-3 py-1 text-xs font-semibold text-[#ff2442]">
                    {{ confidenceLabelMap[conversation.response.confidence] }}
                  </span>
                  <span class="rounded-full bg-[#f7f3f4] px-3 py-1 text-xs font-medium text-[#6e5c61]">
                    {{ intentLabelMap[conversation.response.analysis.intent] }}
                  </span>
                  <span
                    v-for="chip in buildAnalysisChips(conversation.response)"
                    :key="chip"
                    class="rounded-full bg-[#f7f3f4] px-3 py-1 text-xs font-medium text-[#78666b]"
                  >
                    {{ chip }}
                  </span>
                </div>

                <p class="mt-3 whitespace-pre-wrap text-[15px] leading-7 text-[#2a1f23]">
                  {{ conversation.response.answer }}
                </p>

                <ul v-if="conversation.response.summary_points.length > 0" class="mt-4 space-y-2">
                  <li
                    v-for="point in conversation.response.summary_points"
                    :key="point"
                    class="ai-search-summary-item flex items-start gap-2 rounded-2xl bg-[#fcf8f9] px-3 py-2 text-sm leading-6 text-[#5e4f53]"
                  >
                    <span class="mt-[7px] h-1.5 w-1.5 flex-shrink-0 rounded-full bg-[#ff2442]"></span>
                    <span>{{ point }}</span>
                  </li>
                </ul>

                <div v-if="conversation.response.retrieved_posts.length > 0" class="mt-5">
                  <div class="mb-3 flex items-center justify-between">
                    <div>
                      <p class="text-sm font-semibold text-[#2c2226]">相关帖子</p>
                      <p class="text-xs text-[#8b7a80]">按相关度排序，点开可直接看详情</p>
                    </div>
                    <span class="text-xs font-medium text-[#9a878d]">
                      {{ conversation.response.retrieved_posts.length }} 条证据
                    </span>
                  </div>

                  <div class="space-y-2.5">
                    <button
                      v-for="(post, index) in conversation.response.retrieved_posts"
                      :key="`${conversation.id}_${post.postId}`"
                      type="button"
                      class="ai-search-related-card ai-search-panel-ignore-close w-full rounded-[22px] border p-3 text-left transition"
                      :disabled="openingPostId === post.postId"
                      @click="handleOpenPost(post.postId)"
                    >
                      <div class="flex items-start justify-between gap-3">
                        <div class="min-w-0">
                          <p class="line-clamp-2 text-sm font-semibold leading-6 text-[#251b1f]">
                            {{ post.title }}
                          </p>
                          <div class="mt-2 flex flex-wrap items-center gap-2 text-xs text-[#857278]">
                            <span v-if="post.location" class="rounded-full bg-[#f7f2f4] px-2.5 py-1">
                              {{ post.location }}
                            </span>
                            <span v-if="post.createTime">{{ formatDate(post.createTime) }}</span>
                            <span>赞 {{ post.likesCount }}</span>
                            <span>评 {{ post.commentCount }}</span>
                          </div>
                        </div>

                        <div class="flex flex-shrink-0 items-center gap-2">
                          <span class="rounded-full bg-[#fff0f3] px-2.5 py-1 text-[11px] font-semibold text-[#ff2442]">
                            Top {{ index + 1 }}
                          </span>
                          <svg
                            v-if="openingPostId !== post.postId"
                            class="h-4 w-4 text-[#c2b3b8]"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path d="M9 5l7 7-7 7" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
                          </svg>
                          <svg
                            v-else
                            class="h-4 w-4 animate-spin text-[#ff2442]"
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                          >
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                            <path
                              class="opacity-75"
                              fill="currentColor"
                              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                            />
                          </svg>
                        </div>
                      </div>

                      <div v-if="post.matchReasons.length > 0" class="mt-3 flex flex-wrap gap-1.5">
                        <span
                          v-for="reason in post.matchReasons.slice(0, 3)"
                          :key="reason"
                          class="rounded-full bg-[#f7f3f4] px-2 py-1 text-[11px] text-[#7a686d]"
                        >
                          {{ matchReasonLabelMap[reason] || reason }}
                        </span>
                      </div>
                    </button>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>

        <div class="ai-search-input-area border-t px-4 pb-4 pt-3">
          <div class="ai-search-input-shell rounded-[26px] border p-2">
            <textarea
              v-model="draftQuestion"
              class="ai-search-panel-ignore-close ai-search-textarea h-[96px] w-full resize-none bg-transparent px-3 py-2 text-sm leading-6 outline-none"
              placeholder="问问最近大家在聊什么，或者某个城市 / 场景适合去哪..."
              @keydown="handleTextareaKeydown"
            ></textarea>

            <div class="flex items-center justify-between px-2 pb-1">
              <p class="text-xs text-[#8b7a80]">
                Enter 发送，Shift + Enter 换行
              </p>
              <button
                type="button"
                class="ai-search-panel-ignore-close inline-flex items-center gap-2 rounded-full bg-[#ff2442] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#e91f3c] disabled:cursor-not-allowed disabled:bg-[#ffc1cc]"
                :disabled="isSubmitting || !draftQuestion.trim()"
                @click="submitQuestion()"
              >
                <svg
                  v-if="isSubmitting"
                  class="h-4 w-4 animate-spin"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                <span>{{ isSubmitting ? '整理中' : '发送' }}</span>
              </button>
            </div>
          </div>
        </div>
      </section>
    </Transition>
  </div>

  <PostDetailModal
    :post="selectedPost"
    :visible="detailVisible"
    :trigger-rect="null"
    @close="closePostDetail"
  />
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'

import PostDetailModal from '@/components/PostDetailModal.vue'
import { socialSearchPosts } from '@/modules/ai/ai.api'
import type { SocialSearchResponse } from '@/modules/ai/ai.types'
import { fetchPostDetail } from '@/modules/post/post-detail.api'
import { usePostDetailModal } from '@/shared/composables/usePostDetailModal'
import type { Post } from '@/types'

interface Props {
  open: boolean
}

type ConversationStatus = 'loading' | 'done' | 'error'

interface ConversationItem {
  id: number
  question: string
  status: ConversationStatus
  response: SocialSearchResponse | null
  error: string
}

interface PanelPosition {
  left: number
  top: number
}

const PANEL_WIDTH = 420
const DEFAULT_LEFT = 332
const DEFAULT_TOP = 24
const PANEL_HEIGHT_PADDING = 48
const STORAGE_KEY = 'ai-social-search-conversations'

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const starterPrompts = [
  '最近大家都在讨论什么好玩的去处？',
  '雨天适合去哪些室内地方？',
  '最近杭州适合周末去哪里？',
  '南京晚上大家更推荐哪些地方散步？',
]

const confidenceLabelMap: Record<string, string> = {
  high: '高把握回答',
  medium: '中等把握回答',
  low: '低把握回答',
}

const intentLabelMap: Record<string, string> = {
  trend: '趋势总结',
  filter: '条件筛选',
  explore: '探索推荐',
}

const matchReasonLabelMap: Record<string, string> = {
  'location-match': '地点命中',
  'topic-match': '主题命中',
  'keyword-overlap': '关键词重合',
  engagement: '互动热度',
  recency: '时间新鲜',
  'trend-candidate': '趋势候选',
}

const draftQuestion = ref('')
const conversations = ref<ConversationItem[]>([])
const scrollAreaRef = ref<HTMLElement | null>(null)
const panelRootRef = ref<HTMLElement | null>(null)
const openingPostId = ref<number | null>(null)
const nextConversationId = ref(1)
const isSubmitting = ref(false)
const panelHeight = ref(Math.min(window.innerHeight - PANEL_HEIGHT_PADDING, 860))
const panelPosition = ref<PanelPosition>({
  left: DEFAULT_LEFT,
  top: DEFAULT_TOP,
})
const draggingState = ref<{
  startX: number
  startY: number
  originLeft: number
  originTop: number
} | null>(null)

const {
  selectedPost,
  visible: detailVisible,
  openPostDetail,
  closePostDetail,
} = usePostDetailModal<Post>()

const panelContainerStyle = computed(() => ({
  left: `${panelPosition.value.left}px`,
  top: `${panelPosition.value.top}px`,
  width: `${PANEL_WIDTH}px`,
  height: `${panelHeight.value}px`,
}))

const scrollToBottom = async (behavior: ScrollBehavior = 'smooth') => {
  await nextTick()
  if (!scrollAreaRef.value) return
  scrollAreaRef.value.scrollTo({
    top: scrollAreaRef.value.scrollHeight,
    behavior,
  })
}

const buildAnalysisChips = (response: SocialSearchResponse) => {
  const chips: string[] = []

  if (response.analysis.recency) {
    chips.push('最近')
  }

  chips.push(...response.analysis.locations)
  chips.push(...response.analysis.topics)

  return chips.slice(0, 4)
}

const formatDate = (value?: string | null) => {
  if (!value) return ''
  return value.slice(0, 10)
}

const persistConversations = () => {
  const persisted = conversations.value.filter((item) => item.status !== 'loading')
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(persisted))
}

const restoreConversations = () => {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return

  try {
    const parsed = JSON.parse(raw) as ConversationItem[]
    if (!Array.isArray(parsed)) return
    conversations.value = parsed
    nextConversationId.value = parsed.reduce((maxId, item) => Math.max(maxId, item.id), 0) + 1
  } catch {
    sessionStorage.removeItem(STORAGE_KEY)
  }
}

const submitQuestion = async (presetQuestion?: string) => {
  const question = (presetQuestion ?? draftQuestion.value).trim()
  if (!question || isSubmitting.value) return

  draftQuestion.value = ''
  isSubmitting.value = true

  const item: ConversationItem = {
    id: nextConversationId.value++,
    question,
    status: 'loading',
    response: null,
    error: '',
  }

  conversations.value.push(item)
  await scrollToBottom('smooth')

  try {
    const response = await socialSearchPosts({ question })
    item.response = response
    item.status = 'done'
  } catch (error) {
    item.status = 'error'
    item.error = error instanceof Error ? error.message : 'AI 搜推暂时不可用，请稍后重试。'
  } finally {
    isSubmitting.value = false
    persistConversations()
    await scrollToBottom('smooth')
  }
}

const handleTextareaKeydown = (event: KeyboardEvent) => {
  if (event.key !== 'Enter' || event.shiftKey) return
  event.preventDefault()
  void submitQuestion()
}

const handleOpenPost = async (postId: number) => {
  if (openingPostId.value) return

  openingPostId.value = postId

  try {
    const detail = await fetchPostDetail(postId)
    openPostDetail(detail, null)
  } finally {
    openingPostId.value = null
  }
}

const clampPanelPosition = (left: number, top: number) => {
  const maxLeft = Math.max(24, window.innerWidth - PANEL_WIDTH - 24)
  const maxTop = Math.max(16, window.innerHeight - panelHeight.value - 16)

  panelPosition.value = {
    left: Math.min(Math.max(16, left), maxLeft),
    top: Math.min(Math.max(16, top), maxTop),
  }
}

const updatePanelHeight = () => {
  panelHeight.value = Math.min(window.innerHeight - PANEL_HEIGHT_PADDING, 860)
  clampPanelPosition(panelPosition.value.left, panelPosition.value.top)
}

const handleHeaderMouseDown = (event: MouseEvent) => {
  const target = event.target as HTMLElement | null
  if (target?.closest('button')) return

  draggingState.value = {
    startX: event.clientX,
    startY: event.clientY,
    originLeft: panelPosition.value.left,
    originTop: panelPosition.value.top,
  }
  event.preventDefault()
}

const handleDocumentMouseMove = (event: MouseEvent) => {
  if (!draggingState.value) return

  const deltaX = event.clientX - draggingState.value.startX
  const deltaY = event.clientY - draggingState.value.startY

  clampPanelPosition(
    draggingState.value.originLeft + deltaX,
    draggingState.value.originTop + deltaY,
  )
}

const handleDocumentMouseUp = () => {
  draggingState.value = null
}

const hasAnyOpenPostDetail = () =>
  Boolean(
    document.querySelector(
      '[data-post-detail-overlay="true"], [data-post-detail-dialog="true"], [data-post-detail-close="true"]',
    ),
  )

const isBlankAreaClick = (target: HTMLElement) => {
  if (panelRootRef.value?.contains(target)) return false
  if (target.closest('.ai-search-panel-ignore-close')) return false
  if (target.closest('[data-post-detail-overlay="true"], [data-post-detail-dialog="true"], [data-post-detail-close="true"]')) return false
  if (target.closest('.el-message, .el-message-box, .el-popper, .el-overlay, [role="dialog"]')) return false
  if (target.closest('[data-post-id], button, a, input, textarea, video, img, svg, path')) return false

  return ['MAIN', 'ASIDE', 'DIV', 'SECTION'].includes(target.tagName)
}

const handleDocumentClick = (event: MouseEvent) => {
  if (!props.open || detailVisible.value || hasAnyOpenPostDetail()) return

  const target = event.target
  if (!(target instanceof HTMLElement)) return
  if (isBlankAreaClick(target)) {
    emit('close')
  }
}

watch(
  conversations,
  () => {
    persistConversations()
  },
  { deep: true },
)

watch(
  () => props.open,
  async (open) => {
    if (open) {
      await scrollToBottom('auto')
    }
  },
)

onMounted(() => {
  restoreConversations()
  updatePanelHeight()
  document.addEventListener('click', handleDocumentClick, true)
  document.addEventListener('mousemove', handleDocumentMouseMove)
  document.addEventListener('mouseup', handleDocumentMouseUp)
  window.addEventListener('resize', updatePanelHeight)
})

onUnmounted(() => {
  document.removeEventListener('click', handleDocumentClick, true)
  document.removeEventListener('mousemove', handleDocumentMouseMove)
  document.removeEventListener('mouseup', handleDocumentMouseUp)
  window.removeEventListener('resize', updatePanelHeight)
})
</script>

<style scoped>
.assistant-panel-enter-active,
.assistant-panel-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.assistant-panel-enter-from,
.assistant-panel-leave-to {
  opacity: 0;
  transform: translateX(-18px) scale(0.985);
}

.ai-search-panel-shell {
  border-color: var(--sn-ai-shell-border);
  background: var(--sn-ai-shell-bg);
  box-shadow: var(--sn-ai-shell-shadow);
  color: var(--sn-ai-text-primary);
}

.ai-search-panel-header {
  border-color: var(--sn-ai-header-border);
  background: var(--sn-ai-header-bg);
  cursor: grab;
  user-select: none;
}

.ai-search-panel-header:active {
  cursor: grabbing;
}

.ai-search-input-area {
  border-color: var(--sn-ai-header-border);
  background: var(--sn-ai-header-bg);
}

.ai-search-title,
.ai-search-panel-shell .text-\[\#251c20\],
.ai-search-panel-shell .text-\[\#251b1f\],
.ai-search-panel-shell .text-\[\#2a1f23\],
.ai-search-panel-shell .text-\[\#2c2226\] {
  color: var(--sn-ai-text-primary);
}

.ai-search-subtitle,
.ai-search-panel-shell .text-\[\#5d4d52\],
.ai-search-panel-shell .text-\[\#5e4f53\],
.ai-search-panel-shell .text-\[\#6e5c61\],
.ai-search-panel-shell .text-\[\#75656a\],
.ai-search-panel-shell .text-\[\#76666b\],
.ai-search-panel-shell .text-\[\#78666b\],
.ai-search-panel-shell .text-\[\#857278\],
.ai-search-panel-shell .text-\[\#8b7a80\],
.ai-search-panel-shell .text-\[\#9a878d\],
.ai-search-panel-shell .text-\[\#9b8b90\],
.ai-search-panel-shell .text-\[\#c2b3b8\] {
  color: var(--sn-ai-text-muted);
}

.ai-search-panel-intro {
  border-color: var(--sn-ai-intro-border);
  background: var(--sn-ai-intro-bg);
  box-shadow: 0 12px 28px rgba(255, 36, 66, 0.06);
}

.ai-search-response-card,
.ai-search-related-card,
.ai-search-input-shell {
  border-color: var(--sn-ai-card-border);
  background: var(--sn-ai-card-bg);
}

.ai-search-response-card {
  box-shadow: 0 14px 32px rgba(17, 17, 17, 0.05);
}

.ai-search-prompt-chip {
  border-color: var(--sn-ai-chip-border);
  background: var(--sn-ai-card-bg);
  color: var(--sn-ai-text-secondary);
}

.ai-search-prompt-chip:hover {
  border-color: var(--sn-ai-chip-hover-border);
  background: var(--sn-ai-chip-hover-bg);
  color: var(--sn-ai-chip-text);
}

.ai-search-related-card:hover {
  transform: translateY(-1px);
  border-color: var(--sn-ai-card-hover-border);
  background: var(--sn-ai-card-hover-bg);
  box-shadow: 0 10px 24px rgba(255, 36, 66, 0.08);
}

.ai-search-summary-item,
.ai-search-panel-shell .bg-\[\#fcf8f9\],
.ai-search-panel-shell .bg-\[\#f7f3f4\],
.ai-search-panel-shell .bg-\[\#f7f2f4\] {
  background: var(--sn-ai-muted-bg);
  color: var(--sn-ai-muted-text);
}

.ai-search-panel-shell .bg-\[\#fff1f4\],
.ai-search-panel-shell .bg-\[\#fff0f3\] {
  background: var(--sn-ai-chip-bg);
  color: var(--sn-ai-chip-text);
}

.ai-search-question-bubble {
  border: 1px solid var(--sn-ai-card-border);
  background: var(--sn-ai-card-bg);
  color: var(--sn-ai-text-primary);
  box-shadow: 0 14px 30px rgba(255, 36, 66, 0.18);
}

.ai-search-panel-shell .bg-\[\#fff5f6\] {
  background: var(--sn-ai-error-bg);
  color: var(--sn-ai-error-text);
}

.ai-search-textarea {
  color: var(--sn-ai-text-primary);
}

.ai-search-textarea::placeholder {
  color: var(--sn-ai-text-muted);
}

.ai-search-close-button {
  color: var(--sn-ai-text-muted);
}

.ai-search-close-button:hover {
  background: var(--sn-ai-muted-bg);
  color: var(--sn-ai-text-primary);
}

.ai-search-scroll-area {
  -ms-overflow-style: auto;
  scrollbar-width: thin;
  scrollbar-color: color-mix(in srgb, var(--sn-ai-text-muted) 65%, transparent) transparent;
}

.ai-search-scroll-area::-webkit-scrollbar {
  display: block;
  width: 8px;
}

.ai-search-scroll-area::-webkit-scrollbar-track {
  background: transparent;
}

.ai-search-scroll-area::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: color-mix(in srgb, var(--sn-ai-text-muted) 58%, transparent);
}

.ai-search-scroll-area::-webkit-scrollbar-thumb:hover {
  background: color-mix(in srgb, var(--sn-ai-text-secondary) 70%, transparent);
}
</style>
