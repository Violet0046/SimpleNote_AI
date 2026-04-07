import { defineStore } from 'pinia'

import {
  LIKED_POSTS_STORAGE_KEY,
  readJsonStorage,
  writeJsonStorage,
} from '@/shared/utils/storage'
import type { Post } from '@/types'

export const useLikeStore = defineStore('like', {
  state: () => ({
    likedPosts: new Set<number>(readJsonStorage<number[]>(LIKED_POSTS_STORAGE_KEY, [])),
  }),

  getters: {
    isPostLiked: (state) => (postId: number) => state.likedPosts.has(postId),
    likedPostCount: (state) => state.likedPosts.size,
  },

  actions: {
    syncPostLikes(posts: Post[]) {
          posts.forEach(post => {
            if (post.isLiked) {
              this.likedPosts.add(post.id)
            } else {
              this.likedPosts.delete(post.id)
            }
          })
          this.saveToLocalStorage()
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
