import axios, { type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

import type { ApiResponse } from '@/types'
import { API_BASE_URL } from '@/shared/api/env'
import { tokenStorage } from '@/shared/utils/storage'

export const loginEventBus = new EventTarget()

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenStorage.get()

    if (token) {
      config.headers = config.headers ?? {}

      if (typeof config.headers.set === 'function') {
        config.headers.set('Authorization', token)
      } else {
        config.headers.Authorization = token
      }
    }

    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => {
    const { data } = response

    if (data.code === 1) {
      return data
    }

    ElMessage.error(data.msg || 'Request failed')
    return Promise.reject(new Error(data.msg || 'Request failed'))
  },
  (error) => {
    if (error.response) {
      const { status } = error.response

      switch (status) {
        case 401:
          ElMessage.error('Login expired, please sign in again.')
          tokenStorage.clear()
          loginEventBus.dispatchEvent(new CustomEvent('showLoginModal'))
          break
        case 403:
          ElMessage.error('You do not have permission to access this resource.')
          break
        case 404:
          ElMessage.error('The requested resource was not found.')
          break
        case 500:
          ElMessage.error('Internal server error.')
          break
        default:
          ElMessage.error(`Request failed: ${error.message}`)
      }
    } else if (error.request) {
      ElMessage.error('Network error, please check your connection.')
    } else {
      ElMessage.error(`Request setup error: ${error.message}`)
    }

    return Promise.reject(error)
  },
)

const getBodyConfig = (data: unknown) => {
  if (data instanceof URLSearchParams) {
    return {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    }
  }

  if (data instanceof FormData) {
    return {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  }

  return undefined
}

export const get = <T = unknown>(url: string, params?: unknown): Promise<ApiResponse<T>> =>
  request.get(url, { params }) as Promise<ApiResponse<T>>

export const post = <T = unknown>(url: string, data?: unknown): Promise<ApiResponse<T>> =>
  request.post(url, data, getBodyConfig(data)) as Promise<ApiResponse<T>>

export const put = <T = unknown>(url: string, data?: unknown): Promise<ApiResponse<T>> =>
  request.put(url, data) as Promise<ApiResponse<T>>

export const del = <T = unknown>(url: string): Promise<ApiResponse<T>> =>
  request.delete(url) as Promise<ApiResponse<T>>

export const patch = <T = unknown>(url: string, data?: unknown): Promise<ApiResponse<T>> =>
  request.patch(url, data) as Promise<ApiResponse<T>>

export default request
