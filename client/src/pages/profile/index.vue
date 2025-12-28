<template>
  <view class="twitter-layout">
    <LeftSidebar current="profile" />
    <view class="main-content">
      <!-- 顶部导航 -->
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <view class="header-info">
          <text class="header-name">{{ user?.nickname }}</text>
          <text class="header-count">{{ user?.tweetcount || 0 }} 帖子</text>
        </view>
      </view>

      <!-- 封面图 -->
      <view class="banner" :style="{ backgroundImage: user?.banner ? `url(${user.banner})` : '' }"></view>

      <!-- 个人信息 -->
      <view class="profile-info">
        <view class="avatar-row">
          <image class="avatar" :src="user?.avatar || '/static/default-avatar.png'" />
          <button v-if="isMe" class="btn-edit" @click="editProfile">编辑个人资料</button>
          <button v-else-if="user?.isfollowing" class="btn-following" @click="toggleFollow">正在关注</button>
          <button v-else class="btn-follow" @click="toggleFollow">关注</button>
        </view>
        <text class="name">{{ user?.nickname }}</text>
        <text class="handle">@{{ user?.username }}</text>
        <text v-if="user?.bio" class="bio">{{ user?.bio }}</text>
        <view class="meta">
          <text v-if="user?.location" class="meta-item">📍 {{ user.location }}</text>
          <text v-if="user?.website" class="meta-item">🔗 <text class="link">{{ user.website }}</text></text>
          <text class="meta-item">📅 {{ formatJoinDate(user?.createdat) }} 加入</text>
        </view>
        <view class="stats">
          <view class="stat" @click="goFollowing">
            <text class="stat-num">{{ user?.followingcount || 0 }}</text>
            <text class="stat-label">正在关注</text>
          </view>
          <view class="stat" @click="goFollowers">
            <text class="stat-num">{{ user?.followerscount || 0 }}</text>
            <text class="stat-label">关注者</text>
          </view>
        </view>
      </view>

      <!-- 标签页 -->
      <view class="tabs">
        <view class="tab" :class="{ active: tab === 'tweets' }" @click="switchTab('tweets')">帖子</view>
        <view class="tab" :class="{ active: tab === 'replies' }" @click="switchTab('replies')">回复</view>
        <view class="tab" :class="{ active: tab === 'media' }" @click="switchTab('media')">媒体</view>
        <view class="tab" :class="{ active: tab === 'likes' }" @click="switchTab('likes')">点赞</view>
      </view>

      <!-- 推文列表 -->
      <view class="tweet-list">
        <TweetItem v-for="item in tweets" :key="item.id" :tweet="item" />
        <view v-if="!tweets.length && !loading" class="empty">暂无内容</view>
        <view v-if="loading" class="loading">加载中...</view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { get, post } from '@/utils/request'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'
import TweetItem from '@/components/TweetItem.vue'

const userStore = useUserStore()
const user = ref(null)
const tweets = ref([])
const tab = ref('tweets')
const userId = ref('')
const loading = ref(false)

const isMe = computed(() => !userId.value || userId.value == userStore.userInfo?.id)

onLoad((options) => {
  userId.value = options?.id || ''
  fetchUser()
  fetchTweets()
})

const fetchUser = async () => {
  try {
    const id = userId.value || 'me'
    const res = await get(`/user/${id}`)
    user.value = res.data
  } catch (e) {}
}

const fetchTweets = async () => {
  loading.value = true
  try {
    const id = userId.value || 'me'
    let endpoint = `/user/${id}/tweets`
    if (tab.value === 'replies') endpoint = `/user/${id}/replies`
    else if (tab.value === 'media') endpoint = `/user/${id}/media`
    else if (tab.value === 'likes') endpoint = `/user/${id}/likes`
    
    const res = await get(endpoint)
    tweets.value = (res.data || []).map(item => item.tweet ? { ...item.tweet, user: item.user, liked: item.liked } : item)
  } catch (e) {}
  loading.value = false
}

const switchTab = (t) => { tab.value = t; fetchTweets() }

const toggleFollow = async () => {
  try {
    await post(`/user/${userId.value}/follow`)
    user.value.isfollowing = !user.value.isfollowing
    user.value.followerscount += user.value.isfollowing ? 1 : -1
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const formatJoinDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月`
}

const goBack = () => {
  const pages = getCurrentPages()
  if (pages.length > 1) uni.navigateBack()
  else uni.switchTab({ url: '/pages/home/index' })
}
const editProfile = () => uni.navigateTo({ url: '/pages/profile/edit' })
const goFollowing = () => {
  const id = userId.value || userStore.userInfo?.id
  if (id) uni.navigateTo({ url: `/pages/profile/following?id=${id}` })
}
const goFollowers = () => {
  const id = userId.value || userStore.userInfo?.id
  if (id) uni.navigateTo({ url: `/pages/profile/followers?id=${id}` })
}
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.back-btn { font-size: 20px; margin-right: 32px; cursor: pointer; padding: 8px; border-radius: 50%; color: var(--text-primary); }
.back-btn:hover { background: var(--bg-hover); }
.header-info { flex: 1; }
.header-name { font-size: 20px; font-weight: bold; display: block; color: var(--text-primary); }
.header-count { font-size: 13px; color: var(--text-secondary); }
.banner { height: 200px; background: linear-gradient(135deg, #1a1a2e, #16213e); background-size: cover; background-position: center; }
.profile-info { padding: 12px 16px; position: relative; }
.avatar-row { display: flex; justify-content: space-between; align-items: flex-start; margin-top: -67px; }
.avatar { width: 134px; height: 134px; border-radius: 50%; border: 4px solid var(--bg-primary); background: var(--bg-tertiary); }
.btn-edit, .btn-following { border: 1px solid var(--text-secondary); background: transparent; color: var(--text-primary); border-radius: 9999px; padding: 8px 16px; font-size: 15px; font-weight: bold; margin-top: 67px; }
.btn-follow { background: var(--btn-primary-bg); color: var(--btn-primary-text); border: none; border-radius: 9999px; padding: 8px 16px; font-size: 15px; font-weight: bold; margin-top: 67px; }
.name { font-size: 20px; font-weight: bold; display: block; margin-top: 4px; color: var(--text-primary); }
.handle { font-size: 15px; color: var(--text-secondary); display: block; }
.bio { font-size: 15px; display: block; margin: 12px 0; white-space: pre-wrap; color: var(--text-primary); }
.meta { display: flex; flex-wrap: wrap; gap: 12px; color: var(--text-secondary); font-size: 15px; margin-bottom: 12px; }
.link { color: var(--accent-primary); }
.stats { display: flex; gap: 20px; }
.stat { cursor: pointer; color: var(--text-primary); }
.stat:hover { text-decoration: underline; }
.stat-num { font-weight: bold; }
.stat-label { color: var(--text-secondary); margin-left: 4px; }
.tabs { display: flex; border-bottom: 1px solid var(--border-color); position: sticky; top: 53px; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 9; }
.tab { flex: 1; text-align: center; padding: 16px; color: var(--text-secondary); cursor: pointer; position: relative; }
.tab:hover { background: var(--bg-hover); }
.tab.active { color: var(--text-primary); font-weight: bold; }
.tab.active::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 56px; height: 4px; background: var(--accent-primary); border-radius: 2px; }
.empty, .loading { text-align: center; padding: 32px; color: var(--text-secondary); }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
