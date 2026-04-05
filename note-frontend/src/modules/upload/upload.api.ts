import { post } from '@/utils/request'

export const uploadFile = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await post<string>('/upload', formData)
  return response.data
}
