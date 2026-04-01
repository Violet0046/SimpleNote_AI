import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
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
  (response: AxiosResponse<ApiResponse>) => {
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
          // 可以在这里跳转到登录页面
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
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
  return request.get(url, { params })
}

// POST请求
export const post = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request.post(url, data)
}

// PUT请求
export const put = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request.put(url, data)
}

// DELETE请求
export const del = <T = any>(url: string): Promise<ApiResponse<T>> => {
  return request.delete(url)
}

// PATCH请求
export const patch = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return request.patch(url, data)
}

export default request