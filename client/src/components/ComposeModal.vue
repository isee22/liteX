<template>
  <view v-if="composeStore.visible" class="compose-modal">
    <view class="overlay" @click="close"></view>
    <view class="compose-dialog">
      <view class="compose-header">
        <text class="close-btn" @click="close">✕</text>
        <view class="spacer"></view>
        <text class="draft-btn">草稿</text>
      </view>

      <view class="compose-body">
        <image class="avatar" :src="userStore.userInfo?.avatar || '/static/default-avatar.png'" />
        <view class="compose-content">
          <view class="visibility-btn">所有人 ▾</view>
          <textarea 
            v-model="content" 
            class="compose-input" 
            placeholder="有什么新鲜事？" 
            :maxlength="280" 
            auto-height 
          />
          <view v-if="images.length" class="preview-images">
            <view v-for="(img, i) in images" :key="i" class="preview-item">
              <image :src="img" mode="aspectFill" class="preview-img" />
              <text class="remove-btn" @click="removeImage(i)">✕</text>
            </view>
          </view>
          <view class="reply-setting">
            <text class="reply-text">🌍 所有人都可以回复</text>
          </view>
        </view>
      </view>

      <view class="compose-footer">
        <view class="toolbar">
          <text class="tool-icon" @click="chooseImage">🖼️</text>
          <text class="tool-icon">📷</text>
          <text class="tool-icon">📊</text>
          <text class="tool-icon">😊</text>
          <text class="tool-icon">📅</text>
          <text class="tool-icon">📍</text>
        </view>
        <view class="footer-right">
          <view class="char-progress" :style="{ '--progress': (content.length / 280) * 100 + '%' }">
            <view class="progress-ring"></view>
          </view>
          <view class="divider"></view>
          <text class="add-btn">+</text>
          <button class="post-btn" :disabled="!content.trim() || loading" @click="submitTweet">
            {{ loading ? '发布中...' : '发布' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useTweetStore } from '@/stores/tweet'
import { useComposeStore } from '@/stores/compose'

const userStore = useUserStore()
const tweetStore = useTweetStore()
const composeStore = useComposeStore()

const content = ref('')
const images = ref([])
const loading = ref(false)

const close = () => {
  composeStore.close()
  content.value = ''
  images.value = []
}

const chooseImage = () => {
  uni.chooseImage({
    count: 4 - images.value.length,
    success: (res) => images.value.push(...res.tempFilePaths)
  })
}

const removeImage = (i) => images.value.splice(i, 1)

const submitTweet = async () => {
  if (!content.value.trim() || loading.value) return
  loading.value = true
  try {
    await tweetStore.createTweet(content.value, images.value)
    uni.showToast({ title: '发布成功', icon: 'success' })
    close()
  } catch (e) {
    uni.showToast({ title: e.message || '发布失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.compose-modal { position: fixed; inset: 0; z-index: 1000; display: flex; align-items: flex-start; justify-content: center; padding-top: 5vh; }
.overlay { position: absolute; inset: 0; background: rgba(91, 112, 131, 0.4); }
.compose-dialog { position: relative; background: var(--bg-primary); border-radius: 16px; width: 100%; max-width: 600px; max-height: 90vh; overflow: hidden; }
.compose-header { display: flex; align-items: center; padding: 0 16px; height: 53px; border-bottom: 1px solid var(--border-color); }
.close-btn { font-size: 20px; cursor: pointer; padding: 8px; border-radius: 50%; color: var(--text-primary); }
.spacer { flex: 1; }
.draft-btn { color: var(--accent-primary); font-size: 14px; font-weight: bold; cursor: pointer; }
.compose-body { padding: 16px; display: flex; }
.avatar { width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0; background: var(--bg-tertiary); }
.compose-content { flex: 1; margin-left: 12px; }
.visibility-btn { display: inline-block; background: transparent; border: 1px solid var(--text-secondary); color: var(--accent-primary); border-radius: 9999px; padding: 4px 12px; font-size: 14px; font-weight: bold; margin-bottom: 12px; cursor: pointer; }
.compose-input { width: 100%; background: transparent; border: none; color: var(--text-primary); font-size: 20px; resize: none; outline: none; min-height: 120px; line-height: 1.4; }
.preview-images { display: grid; grid-template-columns: repeat(2, 1fr); gap: 4px; margin: 12px 0; border-radius: 16px; overflow: hidden; }
.preview-item { position: relative; height: 150px; }
.preview-img { width: 100%; height: 100%; object-fit: cover; }
.remove-btn { position: absolute; top: 8px; right: 8px; background: rgba(0, 0, 0, 0.75); width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 12px; color: #fff; }
.reply-setting { padding: 12px 0; border-top: 1px solid var(--border-color); }
.reply-text { color: var(--accent-primary); font-size: 14px; cursor: pointer; }
.compose-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border-color); }
.toolbar { display: flex; gap: 4px; }
.tool-icon { width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: var(--accent-primary); cursor: pointer; font-size: 18px; }
.tool-icon:hover { background: var(--accent-primary-alpha); }
.footer-right { display: flex; align-items: center; gap: 12px; }
.char-progress { width: 24px; height: 24px; border: 2px solid var(--border-color); border-radius: 50%; position: relative; }
.progress-ring { position: absolute; inset: 2px; background: conic-gradient(var(--accent-primary) var(--progress, 0%), transparent var(--progress, 0%)); border-radius: 50%; }
.divider { width: 1px; height: 24px; background: var(--border-color); }
.add-btn { width: 24px; height: 24px; border: 1px solid var(--text-secondary); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: var(--accent-primary); cursor: pointer; font-size: 14px; }
.post-btn { background: var(--accent-primary); color: #fff; border: none; border-radius: 9999px; padding: 8px 16px; font-size: 15px; font-weight: bold; cursor: pointer; }
.post-btn:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
