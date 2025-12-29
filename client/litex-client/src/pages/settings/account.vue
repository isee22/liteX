<template>
  <view class="settings-page">
    <view class="header">
      <text class="back" @click="goBack">←</text>
      <text class="title">你的账号</text>
    </view>
    <view class="content">
      <view class="settings-item" @click="goChangePassword">
        <text class="item-icon">🔑</text>
        <view class="item-info">
          <text class="item-title">更改密码</text>
          <text class="item-desc">随时更改你的密码</text>
        </view>
        <text class="item-arrow">›</text>
      </view>
      <view class="settings-item">
        <text class="item-icon">📧</text>
        <view class="item-info">
          <text class="item-title">邮箱</text>
          <text class="item-desc">{{ userStore.userInfo?.email || '未设置' }}</text>
        </view>
      </view>
      <view class="settings-item">
        <text class="item-icon">👤</text>
        <view class="item-info">
          <text class="item-title">用户名</text>
          <text class="item-desc">@{{ userStore.userInfo?.username }}</text>
        </view>
      </view>
      <view class="settings-item danger" @click="handleDeactivate">
        <text class="item-icon">⚠️</text>
        <view class="item-info">
          <text class="item-title">停用账号</text>
          <text class="item-desc">了解如何停用你的账号</text>
        </view>
        <text class="item-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const goBack = () => uni.navigateBack()
const goChangePassword = () => uni.navigateTo({ url: '/pages/settings/password' })
const handleDeactivate = () => {
  uni.showModal({
    title: '停用账号',
    content: '停用后你的账号将被隐藏，30天内可恢复。确定要停用吗？',
    confirmColor: '#f4212e',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '功能开发中', icon: 'none' })
      }
    }
  })
}
</script>

<style scoped>
.settings-page { min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; border-bottom: 1px solid var(--border-color); position: sticky; top: 0; background: var(--bg-modal); backdrop-filter: blur(12px); }
.back { font-size: 24px; padding: 8px; margin-right: 24px; cursor: pointer; }
.title { font-size: 20px; font-weight: bold; }
.settings-item { display: flex; align-items: center; padding: 16px; cursor: pointer; border-bottom: 1px solid var(--border-color); }
.settings-item:hover { background: var(--bg-hover); }
.item-icon { font-size: 20px; margin-right: 16px; }
.item-info { flex: 1; }
.item-title { font-size: 15px; display: block; }
.item-desc { font-size: 13px; color: var(--text-secondary); display: block; margin-top: 2px; }
.item-arrow { color: var(--text-secondary); font-size: 20px; }
.danger .item-title { color: var(--error); }
</style>
