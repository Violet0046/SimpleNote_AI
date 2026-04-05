import { useRouter } from 'vue-router'

export const useOpenUserPage = () => {
  const router = useRouter()

  const openUserPage = (userId?: number | string | null) => {
    if (userId === null || userId === undefined || userId === '') return

    const routeUrl = router.resolve(`/user/${userId}`)
    window.open(routeUrl.href, '_blank', 'noopener')
  }

  return {
    openUserPage,
  }
}
