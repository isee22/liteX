<template>
  <view class="new-message-page">
    <view class="header">
      <text class="close-btn" @click="goBack">✕</text>
      <text class="title">新私信</text>
      <button class="next-btn" :disabled="!selectedUser" @click="startChat">下一步</button>
    </view>
    
    <view class="search-section">
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <input 
          class="search-input" 
          placeholder="搜索用户" 
          v-model="searchText"
          @input="onSearch"
        />
      </view>
    </view>
    
    <!-- 已选用户 -->
    <view v-if="selectedUser" class="selected-section">
      <view class="selected-user">
        <image class="selected-avatar" :src="selectedUser.avatar || '/static/default-avatar.png'" />
        <text class="selected-name">{{ selectedUser.nickname }}</text>
        <text class="remove-btn" @click="removeSelected">✕</text>
      </view>
    </view>
    
    <!-- 搜索结果 -->
    <scroll-view class="user-list" scroll-y>
      <view v-if="loading" class="loading">
        <text>搜索中...</text>
      </view>
      
      <view v-else-if="searchText && users.length === 0" class="empty">
        <text>未找到用户</text>
      </view>
      
      <view v-else>
        <!-- 推荐用户（无搜索时显示） -->
        <view v-if="!searchText && recommendUsers.length" class="section">
          <text class="section-title">推荐用户</text>
          <view 
            v-for="user in recommendUsers" 
            :key="user.id" 
            class="user-item"
            :class="{ selected: selectedUser?.id === user.id }"
            @click="selectUser(user)"
          >
            <image class="user-avatar" :src="user.avatar || '/static/default-avatar.png'" />
            <view class="user-info">
              <view class="user-name-row">
                <text class="user-name">{{ user.nickname }}</text>
                <text v-if="user.verified" class="verified">✓</text>
              </view>
              <text class="user-handle">@{{ user.username }}</text>
            </view>
            <view v-if="selectedUser?.id === user.id" class="check-icon">✓</view>
          </view>
        </view>
        
        <!-- 搜索结果 -->
        <view 
          v-for="user in users" 
          :key="user.id" 
          class="user-item"
          :class="{ selected: selectedUser?.id === user.id }"
          @click="selectUser(user)"
        >
          <image class="user-avatar" :src="user.avatar || '/static/default-avatar.png'" />
          <view class="user-info">
            <view class="user-name-row">
              <text class="user-name">{{ user.nickname }}</text>
              <text v-if="user.verified" class="verified">✓</text>
            </view>
            <text class="user-handle">@{{ user.username }}</text>
            <text v-if="user.bio" class="user-bio">{{ user.bio }}</text>
          </view>
          <view v-if="selectedUser?.id === user.id" class="check-icon">✓</view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const searchText = ref('')
const users = ref([])
const recommendUsers = ref([])
const selectedUser = ref(null)
const loading = ref(false)
let searchTimer = null

onMounted(() => {
  fetchRecommend()
})

const fetchRecommend = async () => {
  try {
    const res = await get('/user/recommend')
    const list = res.data?.list || res.data || []
    recommendUsers.value = list.map(item => item.user || item)
  } catch (e) {}
}

const onSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    if (searchText.value.trim()) {
      searchUsers()
    } else {
      users.value = []
    }
  }, 300)
}

const searchUsers = async () => {
  loading.value = true
  try {
    const res = await get('/search/users', { q: searchText.value })
    const list = res.data?.list || res.data || []
    users.value = list.map(item => item.user || item)
  } catch (e) {
    users.value = []
  } finally {
    loading.value = false
  }
}

const selectUser = (user) => {
  if (selectedUser.value?.id === user.id) {
    selectedUser.value = null
  } else {
    selectedUser.value = user
  }
}

const removeSelected = () => {
  selectedUser.value = null
}

const startChat = () => {
  if (!selectedUser.value) return
  uni.redirectTo({ url: `/pages/messages/chat?userId=${selectedUser.value.id}` })
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.new-message-page {
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
}
.header {
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 53px;
  border-bottom: 1px solid var(--border-color);
}
.close-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-primary);
  padding: 8px;
}
.title {
  flex: 1;
  font-size: 20px;
  font-weight: bold;
  margin-left: 24px;
}
.next-btn {
  background: var(--text-primary);
  color: var(--bg-primary);
  border: none;
  border-radius: 9999px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: bold;
}
.next-btn:disabled {
  opacity: 0.5;
}
.search-section {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
}
.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-tertiary);
  border-radius: 9999px;
  padding: 12px 16px;
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
.selected-section {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
}
.selected-user {
  display: inline-flex;
  align-items: center;
  background: var(--accent-primary);
  border-radius: 9999px;
  padding: 4px 12px 4px 4px;
  gap: 8px;
}
.selected-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}
.selected-name {
  font-size: 14px;
  color: #fff;
}
.remove-btn {
  font-size: 14px;
  color: #fff;
  cursor: pointer;
}
.user-list {
  height: calc(100vh - 150px);
}
.section {
  padding-top: 12px;
}
.section-title {
  font-size: 20px;
  font-weight: bold;
  padding: 0 16px 12px;
  display: block;
}
.user-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
}
.user-item:hover {
  background: var(--bg-hover);
}
.user-item.selected {
  background: var(--bg-secondary);
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
  min-width: 0;
}
.user-name-row {
  display: flex;
  align-items: center;
  gap: 4px;
}
.user-name {
  font-weight: bold;
  font-size: 15px;
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
  color: var(--text-secondary);
  font-size: 14px;
  display: block;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.check-icon {
  color: var(--accent-primary);
  font-size: 20px;
}
.loading, .empty {
  text-align: center;
  padding: 32px;
  color: var(--text-secondary);
}
</style>
