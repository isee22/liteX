<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { useThemeStore } from '@/stores/theme'
import LoginModal from '@/components/LoginModal.vue'

onLaunch(() => {
  console.log('App Launch')
  const themeStore = useThemeStore()
  themeStore.applyTheme()
})
</script>

<template>
  <LoginModal />
</template>

<style>
@import './styles/tailwind.css';
@import './styles/themes.css';

/* 全局基础样式 - 使用 CSS 变量 */
page, uni-page-body, body, html {
  background: var(--bg-primary, #000) !important;
  color: var(--text-primary, #e7e9ea) !important;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  min-height: 100vh;
}

/* 三栏布局 - 电脑端 */
.twitter-layout {
  display: flex;
  max-width: 1280px;
  margin: 0 auto;
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.left-sidebar {
  width: 275px;
  padding: 0 12px;
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

.main-content {
  flex: 1;
  max-width: 600px;
  min-height: 100vh;
  border-left: 1px solid var(--border-color, #2f3336);
  border-right: 1px solid var(--border-color, #2f3336);
  background: var(--bg-primary);
}

.right-sidebar {
  width: 350px;
  padding: 0 16px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  background: var(--bg-primary);
}

/* 响应式 */
@media (max-width: 1280px) {
  .right-sidebar { width: 290px; }
}
@media (max-width: 1000px) {
  .right-sidebar { display: none !important; }
}
@media (max-width: 768px) {
  .left-sidebar { width: 68px; }
  .left-sidebar .nav-text { display: none; }
}

/* 手机端 */
@media screen and (max-width: 500px) {
  .left-sidebar, .right-sidebar {
    display: none !important;
    width: 0 !important;
  }
  .twitter-layout { flex-direction: column !important; }
  .main-content { 
    border: none !important;
    max-width: 100% !important;
    width: 100% !important;
    padding-bottom: 60px;
  }
}

/* 电脑端隐藏 tabBar */
@media (min-width: 501px) {
  uni-tabbar, .uni-tabbar { display: none !important; }
}

/* 玻璃效果 */
.glass {
  background: var(--bg-modal, rgba(0,0,0,0.65)) !important;
  backdrop-filter: blur(12px);
}

/* 修复 input 无法输入和选择文字问题 */
input, textarea, [contenteditable] {
  -webkit-user-select: text !important;
  user-select: text !important;
  pointer-events: auto !important;
}

uni-input, uni-textarea {
  pointer-events: auto !important;
}

uni-input input, uni-textarea textarea {
  -webkit-user-select: text !important;
  user-select: text !important;
  pointer-events: auto !important;
}
</style>
