<template>
  <view class="twitter-layout">
    <LeftSidebar />
    <view class="main-content">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <view class="search-box">
          <text class="search-icon">🔍</text>
          <input class="search-input" v-model="keyword" @confirm="search" placeholder="搜索" />
        </view>
        <text class="settings-icon" @click="showSettingsTip">⚙️</text>
      </view>
      
      <view class="tabs">
        <view class="tab" :class="{ active: tab === 'top' }" @click="switchTab('top')">热门</view>
        <view class="tab" :class="{ active: tab === 'latest' }" @click="switchTab('latest')">最新</view>
        <view class="tab" :class="{ active: tab === 'people' }" @click="switchTab('people')">用户</view>
        <view class="tab" :class="{ active: tab === 'media' }" @click="switchTab('media')">媒体</view>
        <view class="tab" :class="{ active: tab === 'lists' }" @click="switchTab('lists')">列表</view>
      </view>
      
      <!-- 推文列表 -->
      <view v-if="tab !== 'people'" class="tweet-list">
        <TweetItem v-for="item in results" :key="item.id" :tweet="item" />
        <view v-if="!results.length && !loading" class="empty">
          <text class="empty-title">没有找到"{{ keyword }}"的结果</text>
          <text class="empty-desc">尝试搜索其他内容，或检查拼写是否正确。</text>
        </view>
      </view>
      
      <!-- 用户列表 -->
      <view v-else class="user-list">
        <view v-for="user in users" :key="user.id" class="user-item" @click="goProfile(user.id)">
          <image class="user-avatar" :src="user.avatar || '/static/default-avatar.png'" />
          <view class="user-info">
            <view class="user-header">
              <text class="user-name">{{ user.nickname }}</text>
              <text v-if="user.verified" class="verified">✓</text>
            </view>
            <text class="user-handle">@{{ user.username }}</text>
            <text class="user-bio">{{ user.bio }}</text>
          </view>
          <button 
            v-if="user.id != userStore.userInfo?.id"
            class="follow-btn" 
            :class="{ following: user.isfollowing }"
            @click.stop="toggleFollow(user)"
          >
            {{ user.isfollowing ? '已关注' : '关注' }}
          </button>
        </view>
        <view v-if="!users.length && !loading" class="empty">
          <text class="empty-title">没有找到"{{ keyword }}"的用户</text>
          <text class="empty-desc">尝试搜索其他内容。</text>
        </view>
      </view>
      
      <view v-if="loading" class="loading">
        <view class="spinner"></view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/stores/user'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'
import TweetItem from '@/components/TweetItem.vue'

const userStore = useUserStore()
const keyword = ref('')
const tab = ref('top')
const results = ref([])
const users = ref([])
const loading = ref(false)

onLoad((options) => {
  keyword.value = decodeURIComponent(options?.q || '')
  if (keyword.value) search()
})

const search = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    if (tab.value === 'people') {
      const res = await get('/search/users', { q: keyword.value })
      const list = res.data?.list || res.data || []
      users.value = list.map(item => item.user || item)
    } else {
      const res = await get('/search', { q: keyword.value, sort: tab.value })
      const list = res.data?.list || res.data || []
      results.value = list.map(item => item.tweet ? { ...item.tweet, user: item.user, liked: item.liked } : item)
    }
  } catch (e) {}
  loading.value = false
}

const switchTab = (t) => { 
  tab.value = t
  search() 
}

const toggleFollow = async (user) => {
  try {
    await post(`/user/${user.id}/follow`)
    user.isfollowing = !user.isfollowing
  } catch (e) {}
}

const goBack = () => uni.navigateBack()
const goProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })
const showSettingsTip = () => uni.showToast({ title: '搜索设置功能开发中', icon: 'none' })
</script>

<style scoped>
.twitter-layout {
  display: flex;
  max-width: 1280px;
  margin: 0 auto;
}
.main-content {
  flex: 1;
  max-width: 600px;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  min-height: 100vh;
  background: var(--bg-primary);
}
.header {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  gap: 16px;
  position: sticky;
  top: 0;
  background: var(--bg-primary-alpha);
  backdrop-filter: blur(12px);
  z-index: 10;
}
.back-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-primary);
}
.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background: var(--bg-tertiary);
  border-radius: 9999px;
  padding: 12px 16px;
  border: 1px solid transparent;
}
.search-box:focus-within {
  background: transparent;
  border-color: var(--accent-primary);
}
.search-icon {
  margin-right: 12px;
  color: var(--text-secondary);
}
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 15px;
}
.settings-icon {
  font-size: 20px;
  color: var(--text-primary);
  cursor: pointer;
}
.tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  overflow-x: auto;
}
.tab {
  flex: 1;
  min-width: fit-content;
  text-align: center;
  padding: 16px 16px;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  white-space: nowrap;
}
.tab:hover {
  background: var(--bg-hover);
}
.tab.active {
  color: var(--text-primary);
  font-weight: bold;
}
.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 56px;
  height: 4px;
  background: var(--accent-primary);
  border-radius: 2px;
}
.user-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
}
.user-item:hover {
  background: var(--bg-hover);
}
.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--bg-tertiary);
}
.user-info {
  flex: 1;
  margin-left: 12px;
}
.user-header {
  display: flex;
  align-items: center;
  gap: 4px;
}
.user-name {
  font-weight: bold;
  font-size: 15px;
  color: var(--text-primary);
}
.verified {
  color: var(--accent-primary);
  font-size: 14px;
}
.user-handle {
  color: var(--text-secondary);
  font-size: 15px;
  display: block;
}
.user-bio {
  font-size: 15px;
  display: block;
  margin-top: 4px;
  line-height: 1.4;
  color: var(--text-primary);
}
.follow-btn {
  background: var(--btn-primary-bg);
  color: var(--btn-primary-text);
  border: none;
  border-radius: 9999px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}
.follow-btn.following {
  background: transparent;
  border: 1px solid var(--text-secondary);
  color: var(--text-primary);
}
.empty {
  text-align: center;
  padding: 48px 32px;
}
.empty-title {
  font-size: 20px;
  font-weight: bold;
  display: block;
  margin-bottom: 8px;
  color: var(--text-primary);
}
.empty-desc {
  color: var(--text-secondary);
  font-size: 15px;
}
.loading {
  display: flex;
  justify-content: center;
  padding: 32px;
}
.spinner {
  width: 24px;
  height: 24px;
  border: 2px solid var(--border-color);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
@media (max-width: 768px) {
  .main-content {
    max-width: 100%;
    border: none;
  }
}
</style>
