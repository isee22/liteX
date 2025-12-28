<template>
  <view class="twitter-layout">
    <LeftSidebar />
    <view class="main-content">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <text class="title">已屏蔽的账号</text>
      </view>
      <view class="blocked-list">
        <view v-for="item in blockedUsers" :key="item.id" class="user-item">
          <image class="user-avatar" :src="item.user?.avatar || '/static/default-avatar.png'" @click="goProfile(item.user?.id)" />
          <view class="user-info" @click="goProfile(item.user?.id)">
            <text class="user-name">{{ item.user?.nickname }}</text>
            <text class="user-handle">@{{ item.user?.username }}</text>
          </view>
          <button class="unblock-btn" @click="unblockUser(item.user?.id)">取消屏蔽</button>
        </view>
        <view v-if="!blockedUsers.length" class="empty">
          <text class="empty-title">你没有屏蔽任何人</text>
          <text class="empty-desc">当你屏蔽某人时，他们将无法关注你或查看你的帖子</text>
        </view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, del } from '@/utils/request'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'

const blockedUsers = ref([])

onMounted(async () => {
  try {
    const res = await get('/blocks')
    blockedUsers.value = res.data || []
  } catch (e) {}
})

const unblockUser = async (userId) => {
  try {
    await del(`/user/${userId}/block`)
    blockedUsers.value = blockedUsers.value.filter(item => item.user?.id !== userId)
    uni.showToast({ title: '已取消屏蔽', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const goBack = () => uni.navigateBack()
const goProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.back-btn { font-size: 20px; margin-right: 32px; cursor: pointer; color: var(--text-primary); }
.title { font-size: 20px; font-weight: bold; color: var(--text-primary); }
.user-item { display: flex; align-items: center; padding: 16px; border-bottom: 1px solid var(--border-color); }
.user-avatar { width: 48px; height: 48px; border-radius: 50%; cursor: pointer; }
.user-info { flex: 1; margin-left: 12px; cursor: pointer; }
.user-name { font-weight: bold; font-size: 15px; display: block; color: var(--text-primary); }
.user-handle { color: var(--text-secondary); font-size: 15px; }
.unblock-btn { background: transparent; border: 1px solid #f4212e; color: #f4212e; border-radius: 9999px; padding: 8px 16px; font-size: 14px; font-weight: bold; }
.empty { text-align: center; padding: 48px 32px; }
.empty-title { font-size: 31px; font-weight: bold; display: block; margin-bottom: 8px; color: var(--text-primary); }
.empty-desc { color: var(--text-secondary); font-size: 15px; }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
