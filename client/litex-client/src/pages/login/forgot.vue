<template>
  <view class="forgot-page">
    <view class="forgot-card">
      <view class="header">
        <text class="back" @click="goBack">←</text>
        <text class="logo">𝕏</text>
        <view class="placeholder"></view>
      </view>

      <text class="title">找回密码</text>
      <text class="desc">输入你的邮箱，我们将发送重置密码链接</text>

      <view class="form">
        <view class="input-box input-rect">
          <input placeholder="邮箱地址" v-model="email" type="email" />
        </view>
        <button class="btn btn-primary btn-block" @click="handleSubmit" :disabled="loading || !email">
          {{ loading ? '发送中...' : '发送重置链接' }}
        </button>
      </view>

      <view v-if="sent" class="success-msg">
        <text>✅ 重置链接已发送到你的邮箱，请查收</text>
      </view>

      <view class="back-login">
        <text class="link" @click="goBack">返回登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { post } from '@/utils/request'

const email = ref('')
const loading = ref(false)
const sent = ref(false)

const handleSubmit = async () => {
  if (!email.value || loading.value) return
  loading.value = true
  try {
    await post('/auth/forgot-password', { email: email.value })
    sent.value = true
    uni.showToast({ title: '发送成功', icon: 'success' })
  } catch (e) {
    // request.js 已显示错误
  } finally {
    loading.value = false
  }
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.forgot-page {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}
.forgot-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-secondary);
  border-radius: 16px;
  padding: 24px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.back {
  font-size: 24px;
  color: var(--text-primary);
  padding: 8px;
  cursor: pointer;
}
.logo {
  font-size: 32px;
  font-weight: bold;
  color: var(--text-primary);
}
.placeholder { width: 40px; }
.title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  display: block;
  margin-bottom: 8px;
}
.desc {
  color: var(--text-secondary);
  font-size: 15px;
  display: block;
  margin-bottom: 24px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}
.success-msg {
  background: var(--bg-tertiary);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  color: var(--success);
  text-align: center;
}
.back-login {
  text-align: center;
}
.link {
  color: var(--accent-primary);
  cursor: pointer;
}
</style>
