<template>
  <view class="login-page">
    <view class="login-card">
      <view class="logo">𝕏</view>
      <text class="title">登录</text>

      <button class="btn-oauth" @click="showOAuthTip">
        <text>🔵 使用 Google 账号登录</text>
      </button>
      <button class="btn-oauth" @click="showOAuthTip">
        <text>🍎 使用 Apple 账号登录</text>
      </button>

      <view class="divider">
        <view class="line"></view>
        <text class="or">或</text>
        <view class="line"></view>
      </view>

      <view class="form">
        <view class="search-box">
          <input class="search-input" placeholder="手机号、邮箱或用户名" v-model="form.username" />
        </view>
        <view class="search-box">
          <input class="search-input" type="password" placeholder="密码" v-model="form.password" @confirm="handleLogin" />
        </view>
        <button class="btn-login" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </view>

      <button class="btn-forgot" @click="goForgot">忘记密码？</button>

      <view class="register-link">
        <text>没有账号？</text>
        <text class="link" @click="goRegister">注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.username || !form.password) {
    return uni.showToast({ title: '请填写完整', icon: 'none' })
  }
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    uni.switchTab({ url: '/pages/home/index' })
  } catch (e) {
    // request.js 已经显示了错误提示
  } finally {
    loading.value = false
  }
}

const goRegister = () => uni.navigateTo({ url: '/pages/login/register' })
const goForgot = () => uni.navigateTo({ url: '/pages/login/forgot' })
const showOAuthTip = () => uni.showToast({ title: '第三方登录暂未开放', icon: 'none' })
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}
.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-secondary);
  border-radius: 16px;
  padding: 32px;
}
.logo {
  text-align: center;
  font-size: 48px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 24px;
}
.title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  display: block;
  margin-bottom: 24px;
}
.btn-oauth {
  width: 100%;
  background: var(--btn-primary-bg);
  color: var(--btn-primary-text);
  font-weight: bold;
  padding: 12px 16px;
  border-radius: 9999px;
  margin-bottom: 12px;
  border: none;
  font-size: 15px;
}
.divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
}
.line {
  flex: 1;
  height: 1px;
  background: var(--border-color);
}
.or {
  padding: 0 16px;
  color: var(--text-secondary);
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 16px;
}
.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-tertiary);
  border-radius: 4px;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
}
.search-box:focus-within {
  border-color: var(--accent-primary);
}
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 16px;
}
.btn-login {
  width: 100%;
  background: var(--btn-primary-bg);
  color: var(--btn-primary-text);
  font-weight: bold;
  padding: 16px;
  border-radius: 9999px;
  border: none;
  font-size: 16px;
  margin-top: 8px;
}
.btn-login:disabled {
  opacity: 0.5;
}
.btn-forgot {
  width: 100%;
  background: transparent;
  border: 1px solid var(--text-secondary);
  color: var(--text-primary);
  font-weight: bold;
  padding: 16px;
  border-radius: 9999px;
  margin-bottom: 24px;
  font-size: 15px;
}
.register-link {
  text-align: center;
  color: var(--text-secondary);
}
.link {
  color: var(--accent-primary);
  margin-left: 4px;
}
</style>
