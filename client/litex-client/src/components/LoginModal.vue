<template>
  <view v-if="visible" class="login-modal">
    <view class="overlay" @click="close"></view>
    <view class="login-dialog">
      <view class="login-header">
        <text class="close-btn" @click="close">✕</text>
        <text class="logo">𝕏</text>
        <view class="placeholder"></view>
      </view>
      
      <text class="title">登录到 X</text>

      <view class="form">
        <view class="input-box">
          <input class="input" placeholder="手机号、邮箱或用户名" v-model="form.username" />
        </view>
        <view class="input-box">
          <input class="input" type="password" placeholder="密码" v-model="form.password" @confirm="handleLogin" />
        </view>
        <button class="btn-login" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </view>

      <button class="btn-forgot">忘记密码？</button>

      <view class="register-link">
        <text>没有账号？</text>
        <text class="link" @click="goRegister">注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const visible = computed(() => userStore.showLoginModal)

const handleLogin = async () => {
  if (!form.username || !form.password) {
    return uni.showToast({ title: '请填写完整', icon: 'none' })
  }
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    uni.showToast({ title: '登录成功', icon: 'success' })
    close()
  } catch (e) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const close = () => {
  userStore.hideLoginModal()
  form.username = ''
  form.password = ''
}

const goRegister = () => {
  close()
  uni.navigateTo({ url: '/pages/login/register' })
}
</script>

<style scoped>
.login-modal {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.overlay {
  position: absolute;
  inset: 0;
  background: rgba(91, 112, 131, 0.4);
}
.login-dialog {
  position: relative;
  background: var(--bg-primary);
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  padding: 24px;
}
.login-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.close-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  color: var(--text-primary);
}
.logo {
  font-size: 32px;
  font-weight: bold;
  color: var(--text-primary);
}
.placeholder { width: 36px; }
.title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  display: block;
  text-align: center;
  margin-bottom: 24px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 16px;
}
.input-box {
  background: var(--bg-tertiary);
  border-radius: 4px;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
}
.input-box:focus-within {
  border-color: var(--accent-primary);
}
.input {
  width: 100%;
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
  padding: 14px;
  border-radius: 9999px;
  border: none;
  font-size: 16px;
}
.btn-login:disabled { opacity: 0.5; }
.btn-forgot {
  width: 100%;
  background: transparent;
  border: 1px solid var(--text-secondary);
  color: var(--text-primary);
  font-weight: bold;
  padding: 14px;
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
  cursor: pointer;
}
</style>
