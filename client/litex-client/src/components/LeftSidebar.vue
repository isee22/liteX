<template>
  <view v-if="!isMobile" class="left-sidebar">
    <!-- Logo -->
    <view class="logo" @click="goHome">𝕏</view>
    
    <!-- 导航菜单 -->
    <nav class="nav-menu">
      <view class="nav-item" :class="{ active: current === 'home' }" @click="goHome">
        <text class="icon">🏠</text>
        <text class="nav-text">首页</text>
      </view>
      <view class="nav-item" :class="{ active: current === 'explore' }" @click="goExplore">
        <text class="icon">🔍</text>
        <text class="nav-text">探索</text>
      </view>
      <view class="nav-item" :class="{ active: current === 'notifications' }" @click="goNotifications">
        <text class="icon">🔔</text>
        <text class="nav-text">通知</text>
        <view v-if="unreadNotifications > 0" class="badge">{{ unreadNotifications }}</view>
      </view>
      <view class="nav-item" :class="{ active: current === 'messages' }" @click="goMessages">
        <text class="icon">✉️</text>
        <text class="nav-text">私信</text>
        <view v-if="unreadMessages > 0" class="badge">{{ unreadMessages }}</view>
      </view>
      <view class="nav-item" :class="{ active: current === 'bookmarks' }" @click="goBookmarks">
        <text class="icon">⭐</text>
        <text class="nav-text">收藏</text>
      </view>
      <view class="nav-item" :class="{ active: current === 'profile' }" @click="goProfile">
        <text class="icon">👤</text>
        <text class="nav-text">个人资料</text>
      </view>
      <view class="nav-item" @click="toggleMoreMenu">
        <text class="icon">⚪</text>
        <text class="nav-text">更多</text>
      </view>
    </nav>
    
    <!-- 发帖按钮 -->
    <button v-if="isLoggedIn" class="btn-post" @click="openCompose">发帖</button>
    <button v-else class="btn-login" @click="goLogin">登录</button>
    
    <view class="spacer"></view>
    
    <!-- 用户菜单 -->
    <view v-if="isLoggedIn" class="user-menu" @click="showUserMenu">
      <image class="user-avatar" :src="userInfo?.avatar || '/static/default-avatar.png'" />
      <view class="user-info">
        <text class="user-name">{{ userInfo?.nickname }}</text>
        <text class="user-handle">@{{ userInfo?.username }}</text>
      </view>
      <text class="more-icon">···</text>
    </view>

    <!-- 更多菜单弹窗 -->
    <view v-if="showMore" class="more-menu">
      <view class="menu-item" @click="goSettings">
        <text>⚙️</text>
        <text>设置和隐私</text>
      </view>
      <view class="menu-item" @click="goDisplay">
        <text>🎨</text>
        <text>显示</text>
      </view>
    </view>
    <view v-if="showMore" class="overlay" @click="showMore = false"></view>
  </view>
  
  <!-- 发帖弹窗 - 使用 Teleport 渲染到 body 避免 z-index 问题 -->
  <Teleport to="body">
    <ComposeModal />
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import { useMessageStore } from '@/stores/message'
import { useComposeStore } from '@/stores/compose'
import ComposeModal from '@/components/ComposeModal.vue'

const props = defineProps({ current: String })

const isMobile = ref(false)
const checkMobile = () => {
  const info = uni.getSystemInfoSync()
  isMobile.value = info.windowWidth <= 500
}
onMounted(() => {
  checkMobile()
  // #ifdef H5
  window.addEventListener('resize', checkMobile)
  // #endif
})
onUnmounted(() => {
  // #ifdef H5
  window.removeEventListener('resize', checkMobile)
  // #endif
})

const userStore = useUserStore()
const notificationStore = useNotificationStore()
const messageStore = useMessageStore()
const composeStore = useComposeStore()
const showMore = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const unreadNotifications = computed(() => notificationStore.unreadCount)
const unreadMessages = computed(() => messageStore.unreadCount)

const goHome = () => uni.switchTab({ url: '/pages/home/index' })
const goExplore = () => uni.switchTab({ url: '/pages/explore/index' })
const goNotifications = () => uni.switchTab({ url: '/pages/notifications/index' })
const goMessages = () => uni.switchTab({ url: '/pages/messages/index' })
const goBookmarks = () => isLoggedIn.value ? uni.navigateTo({ url: '/pages/bookmarks/index' }) : goLogin()
const goProfile = () => isLoggedIn.value ? uni.navigateTo({ url: '/pages/profile/index' }) : goLogin()
const goSettings = () => { showMore.value = false; uni.navigateTo({ url: '/pages/settings/index' }) }
const goLogin = () => uni.navigateTo({ url: '/pages/login/index' })
const openCompose = () => composeStore.open()
const toggleMoreMenu = () => { showMore.value = !showMore.value }
const showUserMenu = () => {
  uni.showActionSheet({
    itemList: ['个人资料', '退出登录'],
    success: (res) => {
      if (res.tapIndex === 0) goProfile()
      else if (res.tapIndex === 1) { userStore.logout(); uni.reLaunch({ url: '/pages/home/index' }) }
    }
  })
}
const goDisplay = () => { showMore.value = false; uni.navigateTo({ url: '/pages/settings/index' }) }
</script>

<style scoped>
.left-sidebar {
  width: 275px;
  padding: 0 12px;
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.logo {
  font-size: 32px;
  font-weight: bold;
  padding: 12px;
  cursor: pointer;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px;
  border-radius: 9999px;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-item:hover {
  background: var(--bg-hover);
}

.nav-item.active {
  font-weight: bold;
}

.icon {
  font-size: 24px;
  width: 28px;
  text-align: center;
}

.nav-text {
  font-size: 20px;
}

.badge {
  background: var(--accent-primary);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 9999px;
  margin-left: 8px;
}

.btn-post {
  width: 90%;
  background: var(--accent-primary);
  color: #fff;
  font-weight: bold;
  padding: 16px;
  border-radius: 9999px;
  border: none;
  font-size: 17px;
  margin-top: 16px;
  cursor: pointer;
}

.btn-post:hover {
  background: var(--accent-hover);
}

.btn-login {
  width: 90%;
  background: var(--btn-primary-bg);
  color: var(--btn-primary-text);
  font-weight: bold;
  padding: 16px;
  border-radius: 9999px;
  border: none;
  font-size: 17px;
  margin-top: 16px;
  cursor: pointer;
}

.spacer {
  flex: 1;
}

.user-menu {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 9999px;
  cursor: pointer;
  margin-bottom: 12px;
}

.user-menu:hover {
  background: var(--bg-hover);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.user-info {
  flex: 1;
  margin-left: 12px;
}

.user-name {
  font-weight: bold;
  display: block;
}

.user-handle {
  font-size: 14px;
  color: var(--text-secondary);
}

.more-icon {
  color: var(--text-secondary);
}

.more-menu {
  position: absolute;
  bottom: 80px;
  left: 12px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
  z-index: 100;
  min-width: 200px;
  box-shadow: var(--shadow);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
}

.menu-item:hover {
  background: var(--bg-hover);
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 50;
  background: transparent;
}

@media (max-width: 1280px) {
  .left-sidebar { width: 88px; }
  .nav-text, .user-info { display: none; }
  .btn-post, .btn-login { width: 50px; height: 50px; padding: 0; font-size: 24px; }
}

@media (max-width: 500px) {
  .left-sidebar { display: none; }
}
</style>
