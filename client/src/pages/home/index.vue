<template>
  <view class="home-page">
    <!-- 手机端 -->
    <template v-if="isMobile">
      <!-- 顶部栏 -->
      <view class="mobile-header">
        <image class="header-avatar" :src="userStore.userInfo?.avatar || '/static/default-avatar.png'" @click="goProfile" />
        <text class="header-logo">𝕏</text>
        <text class="header-settings" @click="goSettings">⚙️</text>
      </view>
      
      <!-- 标签栏 -->
      <view class="tabs">
        <view class="tab" :class="{ active: tab === 'foryou' }" @click="switchTab('foryou')">为你推荐</view>
        <view class="tab" :class="{ active: tab === 'following' }" @click="switchTab('following')">正在关注</view>
      </view>
      
      <!-- 推文列表 -->
      <scroll-view scroll-y class="tweet-list" @scrolltolower="loadMore">
        <TweetItem v-for="item in tweets" :key="item.id" :tweet="item" />
        <view v-if="loading" class="loading">加载中...</view>
        <view v-if="!loading && tweets.length === 0" class="empty">暂无内容</view>
      </scroll-view>
      
      <!-- 浮动发帖按钮 -->
      <view class="fab" @click="openCompose">+</view>
    </template>
    
    <!-- 电脑端 -->
    <template v-else>
      <view class="twitter-layout">
        <LeftSidebar current="home" />
        <view class="main-content">
          <!-- 标签栏 -->
          <view class="tabs desktop-tabs">
            <view class="tab" :class="{ active: tab === 'foryou' }" @click="switchTab('foryou')">推荐</view>
            <view class="tab" :class="{ active: tab === 'following' }" @click="switchTab('following')">关注</view>
          </view>
          
          <!-- 发帖框 -->
          <view v-if="userStore.isLoggedIn" class="compose-box">
            <image class="compose-avatar" :src="userStore.userInfo?.avatar || '/static/default-avatar.png'" />
            <view class="compose-input" @click="openCompose">
              <text class="compose-placeholder">有什么新鲜事？</text>
            </view>
          </view>
          
          <!-- 推文列表 -->
          <scroll-view scroll-y class="tweet-scroll" @scrolltolower="loadMore">
            <TweetItem v-for="item in tweets" :key="item.id" :tweet="item" />
            <view v-if="loading" class="loading">加载中...</view>
          </scroll-view>
        </view>
        <RightSidebar />
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useTweetStore } from '@/stores/tweet'
import { useComposeStore } from '@/stores/compose'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'
import TweetItem from '@/components/TweetItem.vue'

const userStore = useUserStore()
const tweetStore = useTweetStore()
const composeStore = useComposeStore()
const tab = ref('foryou')
const tweets = ref([])
const loading = ref(false)
const page = ref(1)
const isMobile = ref(false)

const checkMobile = () => {
  const info = uni.getSystemInfoSync()
  isMobile.value = info.windowWidth <= 500
}

onMounted(async () => {
  checkMobile()
  // #ifdef H5
  window.addEventListener('resize', checkMobile)
  // #endif
  const token = uni.getStorageSync('token')
  if (token) {
    try { await userStore.fetchUserInfo() } catch (e) { uni.removeStorageSync('token') }
  }
  loadTweets()
})

onUnmounted(() => {
  // #ifdef H5
  window.removeEventListener('resize', checkMobile)
  // #endif
})

const switchTab = (t) => { if (tab.value !== t) { tab.value = t; loadTweets() } }

const loadTweets = async () => {
  loading.value = true
  page.value = 1
  try {
    const res = await tweetStore.fetchTimeline(1, tab.value)
    tweets.value = (res?.list || []).map(item => item.tweet ? { ...item.tweet, user: item.user, liked: item.liked, retweeted: item.retweeted } : item)
  } catch (e) {}
  loading.value = false
}

const loadMore = async () => {
  if (loading.value) return
  loading.value = true
  page.value++
  try {
    const res = await tweetStore.fetchTimeline(page.value, tab.value)
    const list = (res?.list || []).map(item => item.tweet ? { ...item.tweet, user: item.user, liked: item.liked, retweeted: item.retweeted } : item)
    tweets.value.push(...list)
  } catch (e) {}
  loading.value = false
}

const openCompose = () => {
  console.log('openCompose clicked, before:', composeStore.visible)
  composeStore.open()
  console.log('openCompose clicked, after:', composeStore.visible)
}
const goProfile = () => uni.navigateTo({ url: '/pages/profile/index' })
const goSettings = () => uni.navigateTo({ url: '/pages/settings/index' })
</script>

<style scoped>
.home-page { min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }

.mobile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  position: sticky;
  top: 0;
  background: var(--bg-modal);
  backdrop-filter: blur(12px);
  z-index: 100;
}
.header-avatar { width: 32px; height: 32px; border-radius: 50%; }
.header-logo { font-size: 24px; font-weight: bold; }
.header-settings { font-size: 20px; }

.tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 48px;
  background: var(--bg-modal);
  backdrop-filter: blur(12px);
  z-index: 99;
}
.desktop-tabs { top: 0; }
.tab {
  flex: 1;
  text-align: center;
  padding: 16px;
  color: var(--text-secondary);
  font-size: 15px;
  position: relative;
  cursor: pointer;
}
.tab:hover { background: var(--bg-hover); }
.tab.active { color: var(--text-primary); font-weight: bold; }
.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: var(--accent-primary);
  border-radius: 2px;
}

.fab {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  background: var(--accent-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(29,155,240,0.4);
  z-index: 100;
}

.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; background: var(--bg-primary); }
.main-content { flex: 1; max-width: 600px; min-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.compose-box { display: flex; padding: 16px; border-bottom: 1px solid var(--border-color); }
.compose-avatar { width: 40px; height: 40px; border-radius: 50%; }
.compose-input { flex: 1; margin-left: 12px; cursor: pointer; }
.compose-placeholder { color: var(--text-secondary); font-size: 20px; padding: 12px 0; display: block; }
.tweet-scroll { height: calc(100vh - 120px); }
.tweet-list { padding-bottom: 60px; }
.loading, .empty { text-align: center; padding: 20px; color: var(--text-secondary); }
</style>
