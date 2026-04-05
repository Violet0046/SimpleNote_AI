import type { FollowButtonInfo, FollowButtonLabels, NormalizedRelationUser, RelationUser } from './profile.types'

export const normalizeRelationUser = (user: RelationUser): NormalizedRelationUser => {
  const isFollowing = user.isFollowing === 1 || user.following === 1 || user.isFollowing === true || user.following === true
  const isFollower = user.isFollower === 1 || user.follower === 1 || user.isFollower === true || user.follower === true

  return {
    ...user,
    isFollowing,
    isFollower,
  }
}

export const getFollowButtonInfo = (
  user: RelationUser,
  labels: FollowButtonLabels,
): FollowButtonInfo => {
  if (user.isFollowing && user.isFollower) {
    return { text: labels.mutual, class: 'bg-gray-100 text-gray-500 hover:bg-gray-200' }
  }

  if (user.isFollowing) {
    return { text: labels.following, class: 'bg-gray-100 text-gray-500 hover:bg-gray-200' }
  }

  if (user.isFollower) {
    return { text: labels.followBack, class: 'bg-[#FF2442] text-white hover:bg-red-600' }
  }

  return { text: labels.follow, class: 'bg-[#FF2442] text-white hover:bg-red-600' }
}

export const getErrorMessage = (error: unknown, fallback: string) => {
  if (error instanceof Error && error.message) {
    return error.message
  }

  return fallback
}
