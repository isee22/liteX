<template>
  <view class="twitter-layout">
    <LeftSidebar />
    
    <!-- 设置列表 -->
    <view class="settings-sidebar">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <text class="header-title">设置</text>
      </view>

      <!-- 搜索 -->
      <view class="search-wrap">
        <view class="search-box">
          <text class="search-icon">🔍</text>
          <input class="search-input" placeholder="搜索设置" />
        </view>
      </view>

      <!-- 设置项 -->
      <view class="settings-list">
        <view class="settings-item" @click="goAccount">
          <text class="item-icon">👤</text>
          <view class="item-info">
            <text class="item-title">你的账号</text>
            <text class="item-desc">查看账号信息、下载数据存档或了解账号停用选项</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goSecurity">
          <text class="item-icon">🔐</text>
          <view class="item-info">
            <text class="item-title">安全和账号访问</text>
            <text class="item-desc">管理账号安全和跟踪账号使用情况</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goPrivacy">
          <text class="item-icon">🔒</text>
          <view class="item-info">
            <text class="item-title">隐私和安全</text>
            <text class="item-desc">管理你在 X 上看到和分享的内容</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goNotifications">
          <text class="item-icon">🔔</text>
          <view class="item-info">
            <text class="item-title">通知</text>
            <text class="item-desc">选择你想收到的通知类型</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" :class="{ active: currentSection === 'display' }" @click="showDisplay">
          <text class="item-icon">🎨</text>
          <view class="item-info">
            <text class="item-title">显示</text>
            <text class="item-desc">管理字体大小、颜色和背景</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goBlocked">
          <text class="item-icon">🚫</text>
          <view class="item-info">
            <text class="item-title">已屏蔽的账号</text>
            <text class="item-desc">管理你屏蔽的用户</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goMuted">
          <text class="item-icon">🔇</text>
          <view class="item-info">
            <text class="item-title">已静音的账号</text>
            <text class="item-desc">管理你静音的用户</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item logout" @click="handleLogout">
          <text class="item-icon">🚪</text>
          <text class="item-title">退出登录</text>
        </view>
      </view>
    </view>

    <!-- 设置详情 -->
    <view class="settings-detail">
      <view v-if="currentSection === 'display'" class="display-settings">
        <view class="header">
          <text class="header-title">显示</text>
        </view>

        <view class="detail-content">
          <text class="detail-desc">管理字体大小、颜色和背景。这些设置会影响此浏览器上的所有 X 账号。</text>

          <!-- 字体大小 -->
          <view class="setting-section">
            <text class="section-title">字体大小</text>
            <view class="font-size-slider">
              <text class="font-small">Aa</text>
              <view class="slider-track" @click="onSliderClick">
                <view class="slider-dots">
                  <view v-for="i in 5" :key="i" class="slider-dot" :class="{ active: fontSize >= i - 1 }" @click.stop="fontSize = i - 1"></view>
                </view>
                <view class="slider-thumb" :style="{ left: fontSize * 25 + '%' }"></view>
              </view>
              <text class="font-large">Aa</text>
            </view>
          </view>

          <!-- 颜色 -->
          <view class="setting-section">
            <text class="section-title">颜色</text>
            <view class="color-options">
              <view 
                v-for="c in colors" 
                :key="c.name" 
                class="color-option" 
                :style="{ background: c.value }"
                :class="{ active: themeColor === c.name }"
                @click="themeColor = c.name"
              >
                <text v-if="themeColor === c.name">✓</text>
              </view>
            </view>
          </view>

          <!-- 背景 -->
          <view class="setting-section">
            <text class="section-title">背景</text>
            <view class="bg-options">
              <view class="bg-option light" :class="{ active: theme === 'light' }" @click="theme = 'light'">
                <text>默认</text>
              </view>
              <view class="bg-option dim" :class="{ active: theme === 'dim' }" @click="theme = 'dim'">
                <text>暗淡</text>
              </view>
              <view class="bg-option dark" :class="{ active: theme === 'dark' }" @click="theme = 'dark'">
                <text>深色</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-detail">
        <text class="empty-text">选择一个设置项查看详情</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import LeftSidebar from '@/components/LeftSidebar.vue'

const userStore = useUserStore()
const themeStore = useThemeStore()
const currentSection = ref('display')

const fontSize = computed({
  get: () => themeStore.fontSize,
  set: (val) => themeStore.setFontSize(val)
})

const themeColor = computed({
  get: () => themeStore.color,
  set: (val) => themeStore.setColor(val)
})

const theme = computed({
  get: () => themeStore.mode,
  set: (val) => themeStore.setMode(val)
})

const colors = [
  { name: 'blue', value: '#1d9bf0' },
  { name: 'yellow', value: '#ffd400' },
  { name: 'pink', value: '#f91880' },
  { name: 'purple', value: '#7856ff' },
  { name: 'orange', value: '#ff7a00' },
  { name: 'green', value: '#00ba7c' }
]

const showDisplay = () => { currentSection.value = 'display' }
const goBack = () => uni.navigateBack()
const goAccount = () => uni.navigateTo({ url: '/pages/settings/account' })
const goSecurity = () => uni.showToast({ title: '功能开发中', icon: 'none' })
const goPrivacy = () => uni.showToast({ title: '功能开发中', icon: 'none' })
const goNotifications = () => uni.showToast({ title: '功能开发中', icon: 'none' })
const goBlocked = () => uni.navigateTo({ url: '/pages/settings/blocked' })
const goMuted = () => uni.navigateTo({ url: '/pages/settings/muted' })

const onSliderClick = (e) => {
  // 点击滑块轨道时计算位置
  const rect = e.currentTarget.getBoundingClientRect?.() || { width: 200 }
  const x = e.detail?.x || e.clientX || 0
  const left = rect.left || 0
  const width = rect.width || 200
  const percent = Math.max(0, Math.min(1, (x - left) / width))
  fontSize.value = Math.round(percent * 4)
}

const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/home/index' })
      }
    }
  })
}
</script>

<style scoped>
.twitter-layout {
  display: flex;
  max-width: 1280px;
  margin: 0 auto;
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
}
.settings-sidebar {
  width: 400px;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  min-height: 100vh;
  background: var(--bg-primary);
}
.settings-detail {
  flex: 1;
  border-right: 1px solid var(--border-color);
  min-height: 100vh;
  background: var(--bg-primary);
}
.header {
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 53px;
  position: sticky;
  top: 0;
  background: var(--bg-modal);
  backdrop-filter: blur(12px);
  z-index: 10;
}
.back-btn { font-size: 20px; margin-right: 32px; cursor: pointer; }
.header-title { font-size: 20px; font-weight: bold; }
.search-wrap { padding: 0 16px 16px; }
.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-tertiary);
  border-radius: 9999px;
  padding: 12px 16px;
}
.search-icon { margin-right: 12px; }
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 15px;
}
.settings-item {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--border-color);
}
.settings-item:hover { background: var(--bg-hover); }
.settings-item.active {
  background: var(--bg-hover);
  border-right: 2px solid var(--accent-primary);
}
.item-icon { font-size: 20px; margin-right: 16px; }
.item-info { flex: 1; }
.item-title { font-size: 15px; display: block; }
.item-desc { font-size: 13px; color: var(--text-secondary); display: block; margin-top: 2px; }
.item-arrow { color: var(--text-secondary); font-size: 20px; }
.logout .item-title { color: var(--error); }
.display-settings .header { border-bottom: 1px solid var(--border-color); }
.detail-content { padding: 16px; }
.detail-desc { color: var(--text-secondary); font-size: 15px; display: block; margin-bottom: 24px; line-height: 1.4; }
.setting-section { margin-bottom: 32px; }
.section-title { font-size: 15px; font-weight: bold; display: block; margin-bottom: 16px; }
.font-size-slider { display: flex; align-items: center; gap: 16px; }
.font-small { font-size: 13px; }
.font-large { font-size: 20px; }
.slider-track { flex: 1; height: 4px; background: var(--border-color); border-radius: 2px; position: relative; cursor: pointer; }
.slider-dots { display: flex; justify-content: space-between; position: absolute; width: 100%; top: 50%; transform: translateY(-50%); }
.slider-dot { width: 8px; height: 8px; background: var(--border-color); border-radius: 50%; cursor: pointer; }
.slider-dot.active { background: var(--accent-primary); }
.slider-thumb { position: absolute; top: 50%; transform: translate(-50%, -50%); width: 16px; height: 16px; background: var(--accent-primary); border-radius: 50%; cursor: pointer; }
.color-options { display: flex; gap: 16px; }
.color-option { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #fff; font-weight: bold; }
.bg-options { display: flex; gap: 16px; }
.bg-option { flex: 1; padding: 16px; border-radius: 8px; text-align: center; cursor: pointer; font-size: 15px; font-weight: bold; }
.bg-option.light { background: #fff; color: #0f1419; }
.bg-option.dim { background: #15202b; color: #e7e9ea; }
.bg-option.dark { background: #000; color: #e7e9ea; border: 1px solid #2f3336; }
.bg-option.active { border: 2px solid var(--accent-primary); }
.empty-detail { display: flex; align-items: center; justify-content: center; height: 100%; min-height: 300px; }
.empty-text { color: var(--text-secondary); font-size: 15px; }
@media (max-width: 1000px) {
  .settings-detail { display: none; }
  .settings-sidebar { flex: 1; width: auto; }
}
</style>
