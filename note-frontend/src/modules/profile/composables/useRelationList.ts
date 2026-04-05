import { ref, type Ref } from 'vue'

interface UseRelationListOptions<T> {
  loadPage: (pageNum: number, pageSize: number) => Promise<T[]>
  maxPages?: number | null
  onPageLimitReached?: (() => void) | null
  pageSize?: number
}

export const useRelationList = <T>({
  loadPage,
  maxPages = null,
  onPageLimitReached = null,
  pageSize = 20,
}: UseRelationListOptions<T>) => {
  const items = ref<T[]>([]) as Ref<T[]>
  const currentPage = ref(1)
  const hasMore = ref(true)
  const loading = ref(false)

  const reset = () => {
    currentPage.value = 1
    hasMore.value = true
    items.value = []
  }

  const fetchItems = async ({ resetList = false } = {}) => {
    if (resetList) {
      reset()
    }

    if (!hasMore.value || loading.value) {
      return [] as T[]
    }

    loading.value = true

    try {
      const nextItems = await loadPage(currentPage.value, pageSize)
      items.value = items.value.concat(nextItems)
      hasMore.value = nextItems.length === pageSize
      return nextItems
    } finally {
      loading.value = false
    }
  }

  const loadMore = async () => {
    if (maxPages !== null && currentPage.value >= maxPages) {
      hasMore.value = false
      onPageLimitReached?.()
      return [] as T[]
    }

    currentPage.value += 1
    return fetchItems()
  }

  return {
    items,
    currentPage,
    hasMore,
    loading,
    reset,
    fetchItems,
    loadMore,
  }
}
