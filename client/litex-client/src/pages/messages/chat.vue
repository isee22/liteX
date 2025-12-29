<template>
  <view class="chat-page">
    <view class="chat-header">
      <text class="back-btn" @click="goBack">←</text>
      <view class="user-info" @click="goProfile">
        <view class="user-main">
          <text class="user-name">{{ targetUser?.nickname }}</text>
          <text v-if="targetUser?.verified" class="verified">✓</text>
        </view>
        <text class="user-handle">@{{ targetUser?.username }}</text>
      </view>
      <text class="more" @click="showTip('会话详情')">ℹ️</text>
    </view>
    
    <!-- 用户信息卡片 -->
    <view v-if="messages.length === 0 && targetUser" class="user-card">
      <image class="card-avatar" :src="targetUser.avatar || '/static/default-avatar.png'" />
      <text class="card-name">{{ targetUser.nickname }}</text>
      <text class="card-handle">@{{ targetUser.username }}</text>
      <text class="card-bio">{{ targetUser.bio }}</text>
      <text class="card-meta">{{ targetUser.followerscount || 0 }} 位关注者 · 已加入 {{ formatJoinDate(targetUser.createdat) }}</text>
    </view>
    
    <scroll-view class="message-list" scroll-y :scroll-top="scrollTop" @scrolltoupper="loadMore">
      <view v-for="(msg, index) in messages" :key="msg.id" class="message-wrap">
        <!-- 日期分隔 -->
        <view v-if="showDateDivider(index)" class="date-divider">
          <text>{{ formatDate(msg.createdat) }}</text>
        </view>
        
        <view class="message-item" :class="{ mine: msg.fromuserid == userStore.userInfo?.id }">
          <image 
            v-if="msg.fromuserid != userStore.userInfo?.id" 
            class="msg-avatar" 
            :src="targetUser?.avatar || '/static/default-avatar.png'" 
          />
          <view class="msg-bubble">
            <text class="msg-content">{{ msg.content }}</text>
          </view>
          <text class="msg-time">{{ formatTime(msg.createdat) }}</text>
        </view>
      </view>
    </scroll-view>
    
    <view class="input-bar">
      <view class="input-tools">
        <text class="tool-icon" @click="chooseImage">🖼️</text>
        <text class="tool-icon" @click="showTip('拍照')">📷</text>
        <text class="tool-icon" @click="showTip('表情')">😊</text>
      </view>
      <view class="input-wrap">
        <input 
          v-model="inputText" 
          class="msg-input" 
          placeholder="发起新私信" 
          @confirm="sendMessage"
          confirm-type="send"
        />
      </view>
      <text 
        class="send-icon" 
        :class="{ active: inputText.trim() }" 
        @click="sendMessage"
      >➤</text>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const targetUser = ref(null)
const messages = ref([])
const inputText = ref('')
const scrollTop = ref(0)
const targetUserId = ref('')

onLoad((options) => {
  targetUserId.value = options?.userId
  fetchUser()
  fetchMessages()
})

const fetchUser = async () => {
  try {
    const res = await get(`/user/${targetUserId.value}`)
    targetUser.value = res.data
  } catch (e) {}
}

const fetchMessages = async () => {
  try {
    const res = await get(`/messages/chat/${targetUserId.value}`)
    messages.value = res.data || []
    nextTick(() => { scrollTop.value = 99999 })
  } catch (e) {}
}

const loadMore = async () => {
  // 加载更多历史消息
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return
  try {
    await post('/messages/send', { toUserId: targetUserId.value, content: inputText.value })
    inputText.value = ''
    fetchMessages()
  } catch (e) {}
}

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      // 发送图片消息
    }
  })
}

const showDateDivider = (index) => {
  if (index === 0) return true
  const curr = new Date(messages.value[index].createdat).toDateString()
  const prev = new Date(messages.value[index - 1].createdat).toDateString()
  return curr !== prev
}

const formatDate = (date) => {
  const d = new Date(date)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) return '今天'
  if (d.getFullYear() === now.getFullYear()) {
    return `${d.getMonth() + 1}月${d.getDate()}日`
  }
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const formatTime = (date) => {
  const d = new Date(date)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const formatJoinDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月`
}

const goBack = () => uni.navigateBack()
const goProfile = () => uni.navigateTo({ url: `/pages/profile/index?id=${targetUserId.value}` })
const showTip = (name) => uni.showToast({ title: `${name}功能开发中`, icon: 'none' })
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-primary);
}
.chat-header {
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 53px;
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
}
.back-btn {
  font-size: 20px;
  margin-right: 24px;
  cursor: pointer;
  color: var(--text-primary);
}
.user-info {
  flex: 1;
  cursor: pointer;
}
.user-main {
  display: flex;
  align-items: center;
  gap: 4px;
}
.user-name {
  font-size: 17px;
  font-weight: bold;
  color: var(--text-primary);
}
.verified {
  color: var(--accent-primary);
  font-size: 14px;
}
.user-handle {
  font-size: 13px;
  color: var(--text-secondary);
}
.more {
  font-size: 20px;
  color: var(--text-secondary);
  cursor: pointer;
}
.user-card {
  padding: 32px 16px;
  text-align: center;
  border-bottom: 1px solid var(--border-color);
}
.card-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin-bottom: 12px;
  background: var(--bg-tertiary);
}
.card-name {
  font-size: 20px;
  font-weight: bold;
  display: block;
  color: var(--text-primary);
}
.card-handle {
  font-size: 15px;
  color: var(--text-secondary);
  display: block;
  margin-bottom: 8px;
}
.card-bio {
  font-size: 15px;
  display: block;
  margin-bottom: 8px;
  line-height: 1.4;
  color: var(--text-primary);
}
.card-meta {
  font-size: 15px;
  color: var(--text-secondary);
}
.message-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
.date-divider {
  text-align: center;
  padding: 16px 0;
  color: var(--text-secondary);
  font-size: 13px;
}
.message-wrap {
  margin-bottom: 4px;
}
.message-item {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}
.message-item.mine {
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
  background: var(--bg-tertiary);
}
.msg-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 24px;
  background: var(--border-color);
}
.message-item.mine .msg-bubble {
  background: var(--accent-primary);
}
.msg-content {
  font-size: 15px;
  line-height: 1.4;
  word-break: break-word;
  color: var(--text-primary);
}
.msg-time {
  font-size: 12px;
  color: var(--text-secondary);
  flex-shrink: 0;
}
.input-bar {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border-top: 1px solid var(--border-color);
  gap: 8px;
  flex-shrink: 0;
}
.input-tools {
  display: flex;
  gap: 4px;
}
.tool-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--accent-primary);
  cursor: pointer;
  border-radius: 50%;
}
.tool-icon:hover {
  background: var(--accent-primary-alpha);
}
.input-wrap {
  flex: 1;
}
.msg-input {
  width: 100%;
  background: var(--bg-tertiary);
  border: none;
  border-radius: 9999px;
  padding: 12px 16px;
  color: var(--text-primary);
  font-size: 15px;
}
.send-icon {
  font-size: 20px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 8px;
}
.send-icon.active {
  color: var(--accent-primary);
}
</style>
