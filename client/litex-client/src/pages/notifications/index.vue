<template>
  <view class="twitter-layout">
    <LeftSidebar current="notifications" />
    <view class="main-content">
      <view class="header">
        <text class="title">通知</text>
        <text class="settings-icon" @click="showSettingsTip">⚙️</text>
      </view>
      <view class="tabs">
        <view class="tab" :class="{ active: tab === 'all' }" @click="switchTab('all')">全部</view>
        <view class="tab" :class="{ active: tab === 'verified' }" @click="switchTab('verified')">已验证</view>
        <view class="tab" :class="{ active: tab === 'mentions' }" @click="switchTab('mentions')">提及</view>
      </view>

      <view class="notify-list">
        <view v-for="item in notifications" :key="item.id" class="notify-item" @click="goDetail(item)">
          <text class="notify-icon">{{ getIcon(item.type) }}</text>
          <view class="notify-content">
            <view class="notify-avatars">
              <image v-for="(u, i) in getUsers(item)" :key="i" class="notify-avatar" :src="u.avatar || '/static/default-avatar.png'" />
            </view>
            <text class="notify-text">
              <text class="notify-user">{{ item.fromUser?.nickname }}</text>
              {{ getText(item.type) }}
            </text>
            <text v-if="item.tweet?.content" class="notify-tweet">{{ item.tweet?.content }}</text>
          </view>
        </view>
        <view v-if="!notifications.length && !loading" class="empty">暂无通知</view>
        <view v-if="loading" class="loading">加载中...</view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'
import { useNotificationStore } from '@/stores/notification'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'

const notificationStore = useNotificationStore()
const tab = ref('all')
const notifications = ref([])
const loading = ref(false)

onMounted(() => {
  fetchNotifications()
  // 标记全部已读
  notificationStore.markAllAsRead().catch(() => {})
})

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await get('/notifications', { type: tab.value === 'all' ? '' : tab.value })
    notifications.value = res.data || []
  } catch (e) {}
  loading.value = false
}

const switchTab = (t) => { tab.value = t; fetchNotifications() }

const getIcon = (type) => {
  const icons = { like: '👍', retweet: '🔁', follow: '👤', mention: '@', comment: '💬', quote: '📝' }
  return icons[type] || '🔔'
}

const getText = (type) => {
  const texts = { like: ' 赞了你的帖子', retweet: ' 转推了你的帖子', follow: ' 关注了你', mention: ' 在帖子中提到了你', comment: ' 回复了你', quote: ' 引用了你的帖子' }
  return texts[type] || ''
}

const getUsers = (item) => {
  if (item.users) return item.users.slice(0, 3)
  return item.fromUser ? [item.fromUser] : []
}

const goDetail = (item) => {
  if (item.tweetid) uni.navigateTo({ url: `/pages/tweet/detail?id=${item.tweetid}` })
  else if (item.fromUser?.id) uni.navigateTo({ url: `/pages/profile/index?id=${item.fromUser.id}` })
}

const showSettingsTip = () => uni.showToast({ title: '通知设置功能开发中', icon: 'none' })
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; justify-content: space-between; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.title { font-size: 20px; font-weight: bold; color: var(--text-primary); }
.settings-icon { font-size: 20px; cursor: pointer; color: var(--text-primary); }
.tabs { display: flex; border-bottom: 1px solid var(--border-color); }
.tab { flex: 1; text-align: center; padding: 16px; color: var(--text-secondary); cursor: pointer; position: relative; }
.tab:hover { background: var(--bg-hover); }
.tab.active { color: var(--text-primary); font-weight: bold; }
.tab.active::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 56px; height: 4px; background: var(--accent-primary); border-radius: 2px; }
.notify-item { display: flex; padding: 16px; border-bottom: 1px solid var(--border-color); cursor: pointer; }
.notify-item:hover { background: var(--bg-hover); }
.notify-icon { font-size: 24px; width: 40px; text-align: center; }
.notify-content { flex: 1; margin-left: 12px; }
.notify-avatars { display: flex; margin-bottom: 4px; }
.notify-avatar { width: 32px; height: 32px; border-radius: 50%; margin-right: -8px; border: 2px solid var(--bg-primary); }
.notify-user { font-weight: bold; }
.notify-text { font-size: 15px; display: block; color: var(--text-primary); }
.notify-tweet { display: block; color: var(--text-secondary); font-size: 15px; margin-top: 8px; }
.empty, .loading { text-align: center; padding: 32px; color: var(--text-secondary); }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
