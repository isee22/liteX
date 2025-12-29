import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  // 主题模式: dark | dim | light
  const mode = ref(uni.getStorageSync('theme-mode') || 'dark')
  
  // 主题色: blue | yellow | pink | purple | orange | green
  const color = ref(uni.getStorageSync('theme-color') || 'blue')
  
  // 字体大小: 0-4 (小到大)
  const fontSize = ref(uni.getStorageSync('theme-fontsize') || 2)

  // 应用主题
  const applyTheme = () => {
    // #ifdef H5
    document.documentElement.setAttribute('data-theme', mode.value)
    document.documentElement.setAttribute('data-color', color.value)
    document.documentElement.style.fontSize = (14 + fontSize.value) + 'px'
    // #endif
  }

  // 切换主题模式
  const setMode = (newMode) => {
    mode.value = newMode
    uni.setStorageSync('theme-mode', newMode)
    applyTheme()
  }

  // 切换主题色
  const setColor = (newColor) => {
    color.value = newColor
    uni.setStorageSync('theme-color', newColor)
    applyTheme()
  }

  // 设置字体大小
  const setFontSize = (size) => {
    fontSize.value = size
    uni.setStorageSync('theme-fontsize', size)
    applyTheme()
  }

  // 初始化时应用主题
  applyTheme()

  return { mode, color, fontSize, setMode, setColor, setFontSize, applyTheme }
})
