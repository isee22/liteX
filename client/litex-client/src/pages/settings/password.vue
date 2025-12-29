<template>
  <view class="settings-page">
    <view class="header">
      <text class="back" @click="goBack">←</text>
      <text class="title">更改密码</text>
    </view>
    <view class="content">
      <view class="form">
        <view class="input-box input-rect">
          <input type="password" placeholder="当前密码" v-model="form.oldPassword" />
        </view>
        <view class="input-box input-rect">
          <input type="password" placeholder="新密码" v-model="form.newPassword" />
        </view>
        <view class="input-box input-rect">
          <input type="password" placeholder="确认新密码" v-model="form.confirmPassword" />
        </view>
        <button class="btn btn-primary btn-block" @click="handleSubmit" :disabled="loading">
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </view>
      <text class="tip">密码至少需要6个字符</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { post } from '@/utils/request'

const loading = ref(false)
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const handleSubmit = async () => {
  if (!form.oldPassword || !form.newPassword) {
    return uni.showToast({ title: '请填写完整', icon: 'none' })
  }
  if (form.newPassword.length < 6) {
    return uni.showToast({ title: '密码至少6个字符', icon: 'none' })
  }
  if (form.newPassword !== form.confirmPassword) {
    return uni.showToast({ title: '两次密码不一致', icon: 'none' })
  }
  loading.value = true
  try {
    await post('/user/password', { oldPassword: form.oldPassword, newPassword: form.newPassword })
    uni.showToast({ title: '修改成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1000)
  } catch (e) {}
  loading.value = false
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.settings-page { min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; border-bottom: 1px solid var(--border-color); position: sticky; top: 0; background: var(--bg-modal); backdrop-filter: blur(12px); }
.back { font-size: 24px; padding: 8px; margin-right: 24px; cursor: pointer; }
.title { font-size: 20px; font-weight: bold; }
.content { padding: 24px 16px; }
.form { display: flex; flex-direction: column; gap: 16px; }
.tip { display: block; margin-top: 16px; color: var(--text-secondary); font-size: 13px; }
</style>
