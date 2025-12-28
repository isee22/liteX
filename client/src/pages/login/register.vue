<template>
  <view class="register-page">
    <view class="register-card">
      <view class="header">
        <text class="back" @click="goBack">←</text>
        <text class="logo">𝕏</text>
        <view class="placeholder"></view>
      </view>
      
      <text class="title">创建账号</text>

      <view class="form">
        <view class="search-box">
          <input class="search-input" placeholder="名字" v-model="form.nickname" />
        </view>
        <view class="search-box">
          <input class="search-input" placeholder="邮箱" v-model="form.email" />
        </view>
        <view class="search-box">
          <input class="search-input" placeholder="用户名" v-model="form.username" />
        </view>
        <view class="search-box">
          <input class="search-input" type="password" placeholder="密码" v-model="form.password" />
        </view>
        <view class="search-box">
          <input class="search-input" type="password" placeholder="确认密码" v-model="form.confirmPassword" @confirm="handleRegister" />
        </view>
      </view>

      <button class="btn-register" @click="handleRegister" :disabled="loading">
        {{ loading ? '注册中...' : '注册' }}
      </button>

      <view class="terms">
        <text>注册即表示你同意</text>
        <text class="link" @click="goTerms">服务条款</text>
        <text>和</text>
        <text class="link" @click="goPrivacy">隐私政策</text>
      </view>

      <view class="login-link">
        <text>已有账号？</text>
        <text class="link" @click="goLogin">登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({
  nickname: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!form.nickname || !form.email || !form.username || !form.password) {
    return uni.showToast({ title: '请填写完整', icon: 'none' })
  }
  if (form.password !== form.confirmPassword) {
    return uni.showToast({ title: '两次密码不一致', icon: 'none' })
  }
  loading.value = true
  try {
    await userStore.register(form)
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/home/index' }), 500)
  } catch (e) {
    // request.js 已经显示了错误提示
  } finally {
    loading.value = false
  }
}

const goBack = () => uni.navigateBack()
const goLogin = () => uni.navigateBack()
const goTerms = () => uni.navigateTo({ url: '/pages/login/terms' })
const goPrivacy = () => uni.navigateTo({ url: '/pages/login/privacy' })
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}
.register-card {
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
.placeholder {
  width: 40px;
}
.title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  display: block;
  margin-bottom: 24px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
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
.btn-register {
  width: 100%;
  background: var(--accent-primary);
  color: #fff;
  font-weight: bold;
  padding: 16px;
  border-radius: 9999px;
  border: none;
  font-size: 16px;
  margin-bottom: 16px;
}
.btn-register:disabled {
  opacity: 0.5;
}
.terms {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 24px;
}
.login-link {
  text-align: center;
  color: var(--text-secondary);
}
.link {
  color: var(--accent-primary);
  margin: 0 4px;
  cursor: pointer;
}
</style>
