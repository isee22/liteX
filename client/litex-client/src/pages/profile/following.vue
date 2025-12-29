<template>
  <view class="twitter-layout">
    <LeftSidebar />
    <view class="main-content">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <view class="header-info">
          <text class="header-name">{{ user?.nickname }}</text>
          <text v-if="user?.verified" class="verified">✓</text>
          <text class="header-handle">@{{ user?.username }}</text>
        </view>
      </view>
      <view class="tabs">
        <view class="tab active">正在关注</view>
        <view class="tab" @click="goFollowers">关注者</view>
      </view>
      <view class="user-list">
        <view v-for="item in following" :key="item.id" class="user-item" @click="goProfile(item.user?.id)">
          <image class="user-avatar" :src="item.user?.avatar || '/static/default-avatar.png'" />
          <view class="user-info">
            <text class="user-name">{{ item.user?.nickname }}</text>
            <text class="user-handle">@{{ item.user?.username }}</text>
            <text class="user-bio">{{ item.user?.bio }}</text>
          </view>
          <button v-if="!isMe(item.user?.id)" class="follow-btn following" @click.stop="unfollow(item.user)">已关注</button>
        </view>
        <view v-if="!following.length" class="empty">暂未关注任何人</view>
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

const userStore = useUserStore()
const user = ref(null)
const following = ref([])
const userId = ref('')

onLoad((options) => {
  userId.value = options?.id || ''
  fetchUser()
  fetchFollowing()
})

const fetchUser = async () => {
  const id = userId.value || 'me'
  const res = await get(`/user/${id}`)
  user.value = res.data
}

const fetchFollowing = async () => {
  const id = userId.value || 'me'
  const res = await get(`/user/${id}/following`)
  following.value = res.data?.list || res.data || []
}

const isMe = (id) => id == userStore.userInfo?.id

const unfollow = async (targetUser) => {
  await post(`/user/${targetUser.id}/follow`)
  following.value = following.value.filter(item => item.user?.id !== targetUser.id)
}

const goBack = () => uni.navigateBack()
const goFollowers = () => uni.redirectTo({ url: `/pages/profile/followers?id=${userId.value}` })
const goProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.back-btn { font-size: 20px; margin-right: 32px; cursor: pointer; color: var(--text-primary); }
.header-name { font-size: 20px; font-weight: bold; display: block; color: var(--text-primary); }
.header-handle { font-size: 13px; color: var(--text-secondary); }
.tabs { display: flex; border-bottom: 1px solid var(--border-color); }
.tab { flex: 1; text-align: center; padding: 16px; color: var(--text-secondary); cursor: pointer; position: relative; }
.tab:hover { background: var(--bg-hover); }
.tab.active { color: var(--text-primary); font-weight: bold; }
.tab.active::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 56px; height: 4px; background: var(--accent-primary); border-radius: 2px; }
.user-item { display: flex; align-items: flex-start; padding: 16px; border-bottom: 1px solid var(--border-color); cursor: pointer; }
.user-item:hover { background: var(--bg-hover); }
.user-avatar { width: 48px; height: 48px; border-radius: 50%; }
.user-info { flex: 1; margin-left: 12px; }
.user-name { font-weight: bold; font-size: 15px; display: block; color: var(--text-primary); }
.user-handle { color: var(--text-secondary); font-size: 15px; display: block; }
.user-bio { font-size: 15px; display: block; margin-top: 4px; color: var(--text-primary); }
.follow-btn { background: transparent; border: 1px solid var(--text-secondary); color: var(--text-primary); border-radius: 9999px; padding: 8px 16px; font-size: 14px; font-weight: bold; }
.empty { text-align: center; padding: 32px; color: var(--text-secondary); }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
