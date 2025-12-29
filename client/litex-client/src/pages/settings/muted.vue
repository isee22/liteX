<template>
  <view class="settings-page">
    <view class="header">
      <text class="back" @click="goBack">←</text>
      <text class="title">已静音的账号</text>
    </view>
    <view class="content">
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="!list.length" class="empty">
        <text class="empty-title">你还没有静音任何账号</text>
        <text class="empty-desc">静音后，你将不会在时间线看到该用户的帖子</text>
      </view>
      <view v-else class="user-list">
        <view v-for="item in list" :key="item.user.id" class="user-item">
          <image class="avatar" :src="item.user.avatar || '/static/default-avatar.png'" @click="goProfile(item.user.id)" />
          <view class="user-info" @click="goProfile(item.user.id)">
            <text class="name">{{ item.user.nickname }}</text>
            <text class="handle">@{{ item.user.username }}</text>
          </view>
          <button class="btn btn-secondary btn-sm" @click="unmute(item.user.id)">取消静音</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'

const list = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await get('/user/muted')
    list.value = res.data || []
  } catch (e) {}
  loading.value = false
})

const unmute = async (userId) => {
  try {
    await post(`/user/${userId}/mute`)
    list.value = list.value.filter(item => item.user.id !== userId)
    uni.showToast({ title: '已取消静音', icon: 'success' })
  } catch (e) {}
}

const goBack = () => uni.navigateBack()
const goProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })
</script>

<style scoped>
.settings-page { min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; border-bottom: 1px solid var(--border-color); position: sticky; top: 0; background: var(--bg-modal); backdrop-filter: blur(12px); }
.back { font-size: 24px; padding: 8px; margin-right: 24px; cursor: pointer; }
.title { font-size: 20px; font-weight: bold; }
.content { padding: 0; }
.loading { text-align: center; padding: 32px; color: var(--text-secondary); }
.empty { text-align: center; padding: 48px 24px; }
.empty-title { font-size: 20px; font-weight: bold; display: block; margin-bottom: 8px; }
.empty-desc { color: var(--text-secondary); font-size: 15px; }
.user-item { display: flex; align-items: center; padding: 16px; border-bottom: 1px solid var(--border-color); }
.avatar { width: 48px; height: 48px; border-radius: 50%; }
.user-info { flex: 1; margin-left: 12px; }
.name { font-weight: bold; display: block; }
.handle { color: var(--text-secondary); font-size: 14px; }
</style>
