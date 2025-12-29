<template>
  <view class="twitter-layout">
    <LeftSidebar current="messages" />
    <view class="main-content">
      <view class="header">
        <text class="title">私信</text>
        <view class="header-actions">
          <text class="action-icon" @click="showSettingsTip">⚙️</text>
          <text class="action-icon" @click="newMessage">✉️</text>
        </view>
      </view>
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <input class="search-input" placeholder="搜索私信" v-model="searchText" />
      </view>
      <view class="chat-list">
        <view v-for="chat in filteredChats" :key="chat.id" class="chat-item" :class="{ active: chat.unread }" @click="goChat(chat)">
          <image class="chat-avatar" :src="chat.user?.avatar || '/static/default-avatar.png'" />
          <view class="chat-info">
            <view class="chat-header">
              <text class="chat-name">{{ chat.user?.nickname }}</text>
              <text class="chat-handle">@{{ chat.user?.username }}</text>
              <text class="chat-time">· {{ formatTime(chat.lasttime) }}</text>
            </view>
            <text class="chat-preview">{{ chat.lastmessage }}</text>
          </view>
          <view v-if="chat.unread" class="unread-dot"></view>
        </view>
        <view v-if="!chats.length" class="empty">
          <text class="empty-title">欢迎使用私信功能</text>
          <text class="empty-desc">与好友私密聊天，分享推文和更多内容</text>
          <button class="btn-primary" @click="newMessage">发起私信</button>
        </view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get } from '@/utils/request'
import { useMessageStore } from '@/stores/message'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'

const messageStore = useMessageStore()
const chats = ref([])
const searchText = ref('')

const filteredChats = computed(() => {
  if (!searchText.value) return chats.value
  return chats.value.filter(c => 
    c.user?.nickname?.includes(searchText.value) || 
    c.user?.username?.includes(searchText.value)
  )
})

onMounted(async () => {
  try {
    const res = await get('/messages/chats')
    chats.value = res.data || []
    messageStore.markAsRead()
  } catch (e) {}
})

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 86400) return `${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
  if (diff < 604800) return ['周日','周一','周二','周三','周四','周五','周六'][d.getDay()]
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const goChat = (chat) => uni.navigateTo({ url: `/pages/messages/chat?userId=${chat.user?.id}` })
const newMessage = () => uni.navigateTo({ url: '/pages/messages/new' })
const showSettingsTip = () => uni.showToast({ title: '私信设置功能开发中', icon: 'none' })
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; justify-content: space-between; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.title { font-size: 20px; font-weight: bold; color: var(--text-primary); }
.header-actions { display: flex; gap: 16px; }
.action-icon { font-size: 20px; cursor: pointer; color: var(--text-primary); }
.search-box { display: flex; align-items: center; background: var(--bg-tertiary); border-radius: 9999px; padding: 12px 16px; margin: 0 16px 16px; }
.search-icon { margin-right: 12px; color: var(--text-secondary); }
.search-input { flex: 1; background: transparent; border: none; color: var(--text-primary); font-size: 15px; }
.chat-item { display: flex; align-items: center; padding: 16px; cursor: pointer; }
.chat-item:hover { background: var(--bg-hover); }
.chat-item.active { background: var(--bg-secondary); }
.chat-avatar { width: 48px; height: 48px; border-radius: 50%; }
.chat-info { flex: 1; margin-left: 12px; min-width: 0; }
.chat-header { display: flex; align-items: center; gap: 4px; }
.chat-name { font-weight: bold; font-size: 15px; color: var(--text-primary); }
.chat-handle, .chat-time { color: var(--text-secondary); font-size: 15px; }
.chat-preview { color: var(--text-secondary); font-size: 15px; display: block; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.unread-dot { width: 10px; height: 10px; background: var(--accent-primary); border-radius: 50%; }
.empty { text-align: center; padding: 48px 32px; }
.empty-title { font-size: 31px; font-weight: bold; display: block; margin-bottom: 8px; color: var(--text-primary); }
.empty-desc { color: var(--text-secondary); font-size: 15px; display: block; margin-bottom: 24px; }
.btn-primary { background: var(--accent-primary); color: #fff; border: none; border-radius: 9999px; padding: 16px 32px; font-size: 17px; font-weight: bold; }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
