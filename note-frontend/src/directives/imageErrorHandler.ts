import type { Directive, DirectiveBinding } from 'vue'

// 默认头像 SVG
const defaultAvatarSVG = `
<svg width="100" height="100" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
  <rect width="100" height="100" fill="#f3f4f6" rx="50"/>
  <circle cx="50" cy="35" r="15" fill="#9ca3af"/>
  <path d="M 30 65 Q 50 55 70 65 L 70 85 L 30 85 Z" fill="#9ca3af"/>
</svg>
`

// 默认图片 SVG
const defaultImageSVG = `
<svg width="400" height="500" viewBox="0 0 400 500" xmlns="http://www.w3.org/2000/svg">
  <rect width="400" height="500" fill="#f3f4f6"/>
  <rect x="50" y="50" width="300" height="200" fill="#e5e7eb" rx="8"/>
  <circle cx="200" cy="150" r="40" fill="#d1d5db"/>
  <rect x="100" y="220" width="200" height="20" fill="#d1d5db" rx="10"/>
  <rect x="50" y="280" width="300" height="120" fill="#e5e7eb" rx="8"/>
  <rect x="70" y="300" width="260" height="20" fill="#d1d5db" rx="10"/>
  <rect x="70" y="340" width="180" height="20" fill="#d1d5db" rx="10"/>
</svg>
`

interface BindingValue {
  type?: 'avatar' | 'image'
  fallback?: string
  size?: string
}

// 将 SVG 转换为 Data URL
const svgToDataURL = (svg: string, size: string = '100%'): string => {
  const sizedSVG = svg
    .replace(/width="100"/, `width="${size}"`)
    .replace(/height="100"/, `height="${size}"`)
    .replace(/viewBox="0 0 100 100"/, `viewBox="0 0 100 100"`)
    .replace(/width="400"/, `width="${size}"`)
    .replace(/height="500"/, `height="${size}"`)
    .replace(/viewBox="0 0 400 500"/, `viewBox="0 0 400 500"`)

  return `data:image/svg+xml;base64,${btoa(unescape(encodeURIComponent(sizedSVG)))}`
}

const imageErrorHandler: Directive = {
  mounted(el: HTMLImageElement, binding: DirectiveBinding<BindingValue>) {
    const { type = 'image', size } = binding.value || {}

    // 创建默认图片
    const defaultImage = svgToDataURL(
      type === 'avatar' ? defaultAvatarSVG : defaultImageSVG,
      size || (type === 'avatar' ? '100%' : '100%')
    )

    // 设置默认图片
    el.src = defaultImage

    // 监听错误事件
    const handleError = () => {
      el.src = defaultImage
    }

    el.addEventListener('error', handleError)

    // 保存引用以便卸载时移除
    ;(el as any)._imageErrorHandler = { handleError }
  },

  updated(el: HTMLImageElement, binding: DirectiveBinding<BindingValue>) {
    // 如果 src 变化，重新绑定
    if (binding.value?.fallback !== (el as any)._lastFallback) {
      ;(el as any)._lastFallback = binding.value?.fallback
    }
  },

  unmounted(el: HTMLImageElement) {
    // 清理事件监听器
    if ((el as any)._imageErrorHandler) {
      el.removeEventListener('error', (el as any)._imageErrorHandler.handleError)
    }
  }
}

export default imageErrorHandler