import { defineStore } from 'pinia'

import { get } from '@/utils/request'
import {
  LIKED_POSTS_STORAGE_KEY,
  readJsonStorage,
  writeJsonStorage,
} from '@/shared/utils/storage'

const normalizeLikedPostIds = (postIds: unknown): number[] => {
  if (!Array.isArray(postIds)) return []

  return postIds
    .map((postId) => Number(postId))
    .filter((postId) => Number.isInteger(postId) && postId > 0)
}

export const useLikeStore = defineStore('like', {
  state: () => ({
    likedPosts: new Set<number>(readJsonStorage<number[]>(LIKED_POSTS_STORAGE_KEY, [])),
  }),

  getters: {
    isPostLiked: (state) => (postId: number) => state.likedPosts.has(postId),
    likedPostCount: (state) => state.likedPosts.size,
  },

  actions: {
    async fetchUserLikedIds() {
      try {
        const res = await get<number[]>('/post/liked/ids')
        if (res.code === 1) {
          this.likedPosts = new Set(normalizeLikedPostIds(res.data))
          this.saveToLocalStorage()
        }
      } catch (error) {
        console.error('Failed to fetch liked post ids:', error)
      }
    },

    addLikedPost(postId: number) {
      this.likedPosts.add(postId)
      this.saveToLocalStorage()
    },

    removeLikedPost(postId: number) {
      this.likedPosts.delete(postId)
      this.saveToLocalStorage()
    },

    toggleLike(postId: number) {
      if (this.likedPosts.has(postId)) {
        this.likedPosts.delete(postId)
      } else {
        this.likedPosts.add(postId)
      }

      this.saveToLocalStorage()
    },

    setLikedPosts(postIds: number[]) {
      this.likedPosts = new Set(normalizeLikedPostIds(postIds))
      this.saveToLocalStorage()
    },

    loadFromLocalStorage() {
      try {
        this.likedPosts = new Set(readJsonStorage<number[]>(LIKED_POSTS_STORAGE_KEY, []))
      } catch (error) {
        console.error('Failed to load liked posts from storage:', error)
      }
    },

    saveToLocalStorage() {
      try {
        writeJsonStorage(LIKED_POSTS_STORAGE_KEY, Array.from(this.likedPosts))
      } catch (error) {
        console.error('Failed to save liked posts to storage:', error)
      }
    },

    clear() {
      this.likedPosts.clear()
      this.saveToLocalStorage()
    },
  },
})
