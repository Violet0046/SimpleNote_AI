import type { UserInfo } from '@/types'

export interface RelationUser {
  id: number
  nickname: string
  avatar?: string
  intro?: string
  isFollowing?: boolean | number
  isFollower?: boolean | number
  following?: boolean | number
  follower?: boolean | number
}

export interface NormalizedRelationUser extends Omit<RelationUser, 'isFollowing' | 'isFollower'> {
  isFollowing: boolean
  isFollower: boolean
}

export interface FollowButtonInfo {
  text: string
  class: string
}

export interface FollowButtonLabels {
  mutual: string
  following: string
  followBack: string
  follow: string
}

export interface PaginationInput {
  pageNum: number
  pageSize: number
}

export type OwnProfileTabKey = 'posts' | 'liked' | 'following' | 'followers'
export type PublicProfileTabKey = 'posts' | 'following' | 'followers'

export const createEmptyUserInfo = (): UserInfo => ({
  id: 0,
  nickname: '',
  avatar: '',
  intro: '',
  gender: 0,
  ipLocation: '',
  followingCount: 0,
  followersCount: 0,
  likesCount: 0,
})
