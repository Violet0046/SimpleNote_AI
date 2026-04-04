import axios, { type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

// 定义事件总线用于触发全局登录弹窗
const loginEventBus = new EventTarget()

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('userToken')
    if (token && config.headers) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response

    // 根据后端约定的格式处理响应
    if (data.code === 1) {
      // 成功，返回data
      return data
    } else {
      // 业务错误，直接弹出错误提示
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg))
    }
  },
  (error) => {
    // 处理HTTP错误
    if (error.response) {
      const { status } = error.response

      switch (status) {
        case 401:
          // Token过期或无效
          ElMessage.error('登录已过期，请重新登录')
          // 清除本地存储
          localStorage.removeItem('userToken')
          // 触发唤起全局登录弹窗的事件
          loginEventBus.dispatchEvent(new CustomEvent('showLoginModal'))
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求失败: ${error.message}`)
      }
    } else if (error.request) {
      // 请求已发出但没有响应
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      // 请求配置错误
      ElMessage.error(`请求配置错误: ${error.message}`)
    }

    return Promise.reject(error)
  }
)

// GET请求
export const get = <T = any>(url: string, params?: any): Promise<ApiResponse<T>> => {
  return request.get(url, { params }) as Promise<ApiResponse<T>>
}

// POST请求
export const post = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  // 如果是 URLSearchParams，设置正确的 Content-Type
  if (data instanceof URLSearchParams) {
    return request.post(url, data, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }) as Promise<ApiResponse<T>>
  }
  // 如果是 FormData (文件流上传)，必须换成 multipart/form-data！
  if (data instanceof FormData) {
    return request.post(url, data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }) as Promise<ApiResponse<T>>
  }
  return request.post(url, data) as Promise<ApiResponse<T>>
}

// PUT请求
export const put = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request.put(url, data) as Promise<ApiResponse<T>>
}

// DELETE请求
export const del = <T = any>(url: string): Promise<ApiResponse<T>> => {
  return request.delete(url) as Promise<ApiResponse<T>>
}

// PATCH请求
export const patch = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request.patch(url, data) as Promise<ApiResponse<T>>
}

export default request

// 导出事件总线供其他组件使用
export { loginEventBus }