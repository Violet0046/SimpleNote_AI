import { defineStore } from 'pinia'

export const useLikeStore = defineStore('like', {
  state: () => ({
    likedPosts: new Set<number>() // 存储已点赞的帖子ID
  }),

  getters: {
    isPostLiked: (state) => (postId: number) => state.likedPosts.has(postId),
    likedPostCount: (state) => state.likedPosts.size
  },

  actions: {
    // 添加点赞
    addLikedPost(postId: number) {
      this.likedPosts.add(postId)
      // 持久化到 localStorage
      this.saveToLocalStorage()
    },

    // 移除点赞
    removeLikedPost(postId: number) {
      this.likedPosts.delete(postId)
      // 持久化到 localStorage
      this.saveToLocalStorage()
    },

    // 切换点赞状态
    toggleLike(postId: number) {
      if (this.likedPosts.has(postId)) {
        this.likedPosts.delete(postId)
      } else {
        this.likedPosts.add(postId)
      }
      // 持久化到 localStorage
      this.saveToLocalStorage()
    },

    // 批量设置点赞状态
    setLikedPosts(postIds: number[]) {
      this.likedPosts = new Set(postIds)
      this.saveToLocalStorage()
    },

    // 从 localStorage 加载
    loadFromLocalStorage() {
      try {
        const saved = localStorage.getItem('likedPosts')
        if (saved) {
          this.likedPosts = new Set(JSON.parse(saved))
        }
      } catch (error) {
        console.error('加载点赞数据失败:', error)
      }
    },

    // 保存到 localStorage
    saveToLocalStorage() {
      try {
        localStorage.setItem('likedPosts', JSON.stringify(Array.from(this.likedPosts)))
      } catch (error) {
        console.error('保存点赞数据失败:', error)
      }
    },

    // 清空数据
    clear() {
      this.likedPosts.clear()
      this.saveToLocalStorage()
    }
  }
})