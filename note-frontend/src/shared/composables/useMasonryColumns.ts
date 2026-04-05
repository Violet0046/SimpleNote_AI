import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'

interface MasonryBreakpoint {
  minWidth: number
  columns: number
}

interface UseMasonryColumnsOptions<T> {
  breakpoints?: MasonryBreakpoint[]
  defaultColumns?: number
  strategy?: 'balanced' | 'sequential'
}

const DEFAULT_BREAKPOINTS: MasonryBreakpoint[] = [
  { minWidth: 1280, columns: 4 },
  { minWidth: 768, columns: 3 },
  { minWidth: 0, columns: 2 },
]

export const useMasonryColumns = <T>({
  breakpoints = DEFAULT_BREAKPOINTS,
  defaultColumns = 4,
  strategy = 'sequential',
}: UseMasonryColumnsOptions<T> = {}) => {
  const columnCount = ref(defaultColumns)
  const columns = ref<T[][]>(Array.from({ length: defaultColumns }, () => []))
  const columnRefs = ref<HTMLElement[]>([])

  const setColumnRef = (element: Element | null, index: number) => {
    if (!element) return
    columnRefs.value[index] = element as HTMLElement
  }

  const getColumnCount = (width: number) => {
    const matched = [...breakpoints]
      .sort((left, right) => right.minWidth - left.minWidth)
      .find((item) => width >= item.minWidth)

    return matched?.columns ?? defaultColumns
  }

  const resetColumns = () => {
    columns.value = Array.from({ length: columnCount.value }, () => [])
    columnRefs.value = []
  }

  const appendItems = async (items: T[]) => {
    for (const item of items) {
      let targetIndex = 0

      if (strategy === 'balanced') {
        await nextTick()

        let minHeight = columnRefs.value[0]?.offsetHeight ?? 0

        for (let index = 1; index < columnCount.value; index += 1) {
          const height = columnRefs.value[index]?.offsetHeight ?? 0
          if (height < minHeight) {
            minHeight = height
            targetIndex = index
          }
        }
      } else {
        const lengths = columns.value.map((column) => column.length)
        const minLength = Math.min(...lengths)
        targetIndex = lengths.findIndex((length) => length === minLength)
      }

      columns.value[targetIndex]?.push(item as never)
    }
  }

  const relayout = async (items: T[]) => {
    resetColumns()
    await nextTick()
    await appendItems(items)
  }

  const updateColumnCount = () => {
    const nextColumnCount = getColumnCount(window.innerWidth)
    if (nextColumnCount === columnCount.value) return false

    columnCount.value = nextColumnCount
    resetColumns()
    return true
  }

  onMounted(() => {
    window.addEventListener('resize', updateColumnCount)
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', updateColumnCount)
  })

  return {
    appendItems,
    columnCount,
    columnRefs,
    columns,
    relayout,
    resetColumns,
    setColumnRef,
    updateColumnCount,
  }
}
