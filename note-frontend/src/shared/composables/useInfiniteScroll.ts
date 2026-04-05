import { computed, onBeforeUnmount, ref, unref, watch, type MaybeRefOrGetter } from 'vue'

interface UseInfiniteScrollOptions {
  enabled?: MaybeRefOrGetter<boolean>
  onIntersect: () => void | Promise<void>
  root?: MaybeRefOrGetter<Element | null | undefined>
  rootMargin?: string
  threshold?: number | number[]
}

export const useInfiniteScroll = ({
  enabled = true,
  onIntersect,
  root,
  rootMargin = '0px',
  threshold = 0,
}: UseInfiniteScrollOptions) => {
  const targetRef = ref<HTMLElement | null>(null)
  const isEnabled = computed(() => unref(enabled))
  let observer: IntersectionObserver | null = null

  const stop = () => {
    observer?.disconnect()
    observer = null
  }

  const observe = () => {
    stop()

    if (!targetRef.value || !isEnabled.value) return

    observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) {
          void onIntersect()
        }
      },
      {
        root: (unref(root) ?? null) as Element | null,
        rootMargin,
        threshold,
      },
    )

    observer.observe(targetRef.value)
  }

  watch(
    [targetRef, () => unref(root), isEnabled],
    () => {
      observe()
    },
    { flush: 'post' },
  )

  onBeforeUnmount(() => {
    stop()
  })

  return {
    targetRef,
    observe,
    stop,
  }
}
