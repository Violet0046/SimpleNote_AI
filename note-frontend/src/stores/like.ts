import { defineStore } from 'pinia'
import { get } from '@/utils/request'

export const useLikeStore = defineStore('like', {
  state: () => ({
    likedPosts: new Set<number>() // 存储已点赞的帖子ID
  }),

  getters: {
    isPostLiked: (state) => (postId: number) => state.likedPosts.has(postId),
    likedPostCount: (state) => state.likedPosts.size
  },

  actions: {
    // 从后端拉取当前用户所有点赞过的 ID
    async fetchUserLikedIds() {
      try {
        // 注意这里根据你的实际后端返回结构可能需要微调，通常是 res.data
        const res = await get<any>('/post/liked/ids')
        if (res.code === 1) {
          // 将后端返回的数组 [3, 5, 12] 转换成 Set 集合
          this.likedPosts = new Set(res.data)
          // 顺便更新到本地缓存
          this.saveToLocalStorage()
        }
      } catch (error) {
        console.error('获取后端点赞列表失败:', error)
      }
    },

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

    // 清空数据 (退出登录时调用这个，或者用咱们之前教的直接刷新页面)
    clear() {
      this.likedPosts.clear()
      this.saveToLocalStorage()
    }
  }
})