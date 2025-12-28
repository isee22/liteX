<template>
  <view class="edit-modal">
    <view class="overlay" @click="goBack"></view>
    <view class="edit-dialog">
      <view class="edit-header">
        <text class="close-btn" @click="goBack">✕</text>
        <text class="title">编辑个人资料</text>
        <button class="save-btn" @click="saveProfile" :loading="saving">保存</button>
      </view>
      
      <view class="banner-section">
        <view class="banner" :style="{ backgroundImage: form.banner ? `url(${form.banner})` : '' }">
          <view class="banner-actions">
            <text class="edit-icon" @click="chooseBanner">📷</text>
            <text v-if="form.banner" class="edit-icon" @click="form.banner = ''">✕</text>
          </view>
        </view>
        <view class="avatar-wrap">
          <image class="avatar" :src="form.avatar || '/static/default-avatar.png'" />
          <text class="edit-icon avatar-edit" @click="chooseAvatar">📷</text>
        </view>
      </view>
      
      <view class="form">
        <view class="input-group">
          <text class="label" :class="{ up: nicknameFocused || form.nickname }">名字</text>
          <input v-model="form.nickname" class="input" maxlength="50" 
            @focus="nicknameFocused = true" @blur="nicknameFocused = false" />
          <text class="char-count">{{ form.nickname?.length || 0 }}/50</text>
        </view>

        <view class="input-group">
          <text class="label" :class="{ up: bioFocused || form.bio }">简介</text>
          <textarea v-model="form.bio" class="textarea" maxlength="160"
            @focus="bioFocused = true" @blur="bioFocused = false" />
          <text class="char-count">{{ form.bio?.length || 0 }}/160</text>
        </view>
        
        <view class="input-group">
          <text class="label" :class="{ up: locationFocused || form.location }">位置</text>
          <input v-model="form.location" class="input" maxlength="30"
            @focus="locationFocused = true" @blur="locationFocused = false" />
        </view>
        
        <view class="input-group">
          <text class="label" :class="{ up: websiteFocused || form.website }">网站</text>
          <input v-model="form.website" class="input"
            @focus="websiteFocused = true" @blur="websiteFocused = false" />
        </view>
        
        <view class="birth-section">
          <text class="section-label">出生日期</text>
          <text class="birth-value">{{ formatBirthday() }}</text>
          <text class="birth-hint">· 编辑</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { put } from '@/utils/request'

const userStore = useUserStore()
const saving = ref(false)
const nicknameFocused = ref(false)
const bioFocused = ref(false)
const locationFocused = ref(false)
const websiteFocused = ref(false)

const form = reactive({ 
  nickname: '', bio: '', location: '', website: '', avatar: '', banner: '', birthday: null 
})

onMounted(() => {
  const user = userStore.userInfo
  if (user) {
    form.nickname = user.nickname || ''
    form.bio = user.bio || ''
    form.location = user.location || ''
    form.website = user.website || ''
    form.avatar = user.avatar || ''
    form.banner = user.banner || ''
    form.birthday = user.birthday
  }
})

const chooseAvatar = () => {
  uni.chooseImage({ count: 1, success: (res) => { form.avatar = res.tempFilePaths[0] } })
}

const chooseBanner = () => {
  uni.chooseImage({ count: 1, success: (res) => { form.banner = res.tempFilePaths[0] } })
}

const formatBirthday = () => {
  if (!form.birthday) return '添加你的出生日期'
  const d = new Date(form.birthday)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const saveProfile = async () => {
  saving.value = true
  try {
    await put('/user/profile', form)
    await userStore.fetchUserInfo()
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    uni.showToast({ title: e.message || '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

const goBack = () => uni.navigateBack()
</script>

<style scoped>
.edit-modal { position: fixed; inset: 0; z-index: 100; display: flex; align-items: flex-start; justify-content: center; padding-top: 5vh; }
.overlay { position: absolute; inset: 0; background: rgba(91, 112, 131, 0.4); }
.edit-dialog { position: relative; background: var(--bg-primary); border-radius: 16px; width: 100%; max-width: 600px; max-height: 90vh; overflow: auto; }
.edit-header { display: flex; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.close-btn { font-size: 20px; cursor: pointer; color: var(--text-primary); }
.title { flex: 1; font-size: 20px; font-weight: bold; margin-left: 32px; color: var(--text-primary); }
.save-btn { background: var(--btn-primary-bg); color: var(--btn-primary-text); border: none; border-radius: 9999px; padding: 8px 16px; font-size: 14px; font-weight: bold; }
.banner-section { position: relative; }
.banner { height: 200px; background: var(--bg-tertiary); display: flex; align-items: center; justify-content: center; background-size: cover; background-position: center; }
.banner-actions { display: flex; gap: 16px; }
.edit-icon { background: rgba(0,0,0,0.6); padding: 12px; border-radius: 50%; cursor: pointer; font-size: 18px; }
.avatar-wrap { position: absolute; bottom: -67px; left: 16px; }
.avatar { width: 134px; height: 134px; border-radius: 50%; border: 4px solid var(--bg-primary); background: var(--bg-tertiary); }
.avatar-edit { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }

.form { padding: 80px 16px 16px; }
.input-group { position: relative; margin-bottom: 24px; border: 1px solid var(--bg-tertiary); border-radius: 4px; padding: 24px 12px 8px; }
.input-group:focus-within { border-color: var(--accent-primary); }
.label { position: absolute; left: 12px; top: 16px; color: var(--text-secondary); font-size: 17px; pointer-events: none; transition: all 0.2s; }
.label.up { top: 8px; font-size: 13px; color: var(--accent-primary); }
.input, .textarea { width: 100%; background: transparent; border: none; font-size: 17px; color: var(--text-primary); outline: none; }
.textarea { min-height: 80px; resize: none; }
.char-count { position: absolute; right: 12px; top: 8px; font-size: 13px; color: var(--text-secondary); }
.birth-section { padding: 16px 0; border-top: 1px solid var(--border-color); }
.section-label { font-size: 13px; color: var(--text-secondary); display: block; margin-bottom: 4px; }
.birth-value { font-size: 17px; color: var(--text-primary); }
.birth-hint { color: var(--accent-primary); font-size: 15px; cursor: pointer; }
</style>
