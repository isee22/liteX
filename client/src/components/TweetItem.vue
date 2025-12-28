<template>
  <view class="tweet-item" @click="goDetail">
    <!-- 头像 -->
    <image class="avatar" :src="tweet.user?.avatar || '/static/default-avatar.png'" @click.stop="goProfile" />
    
    <view class="content">
      <!-- 头部信息 -->
      <view class="header">
        <view class="user-info">
          <text class="name" @click.stop="goProfile">{{ tweet.user?.nickname }}</text>
          <text v-if="tweet.user?.verified" class="verified">✓</text>
          <text class="handle">@{{ tweet.user?.username }}</text>
          <text class="dot">·</text>
          <text class="time">{{ formatTime(tweet.createdat) }}</text>
        </view>
        <text class="more" @click.stop="showMenu">···</text>
      </view>
      
      <!-- 内容 -->
      <view class="text">{{ tweet.content }}</view>
      
      <!-- 图片 -->
      <view v-if="parsedImages.length" class="images" :class="'images-' + Math.min(parsedImages.length, 4)">
        <image 
          v-for="(img, i) in parsedImages.slice(0, 4)" 
          :key="i" 
          :src="img" 
          mode="aspectFill" 
          class="image"
          @click.stop="previewImage(i)"
        />
      </view>
      
      <!-- 操作栏 -->
      <view class="actions">
        <view class="action" @click.stop="goDetail">
          <text>💬</text>
          <text v-if="tweet.replycount">{{ formatCount(tweet.replycount) }}</text>
        </view>
        <view class="action" :class="{ retweeted: tweet.retweeted }" @click.stop="handleRetweet">
          <text>🔁</text>
          <text v-if="tweet.retweetcount">{{ formatCount(tweet.retweetcount) }}</text>
        </view>
        <view class="action" :class="{ liked: tweet.liked }" @click.stop="handleLike">
          <text>{{ tweet.liked ? '❤️' : '🤍' }}</text>
          <text v-if="tweet.likecount">{{ formatCount(tweet.likecount) }}</text>
        </view>
        <view class="action" @click.stop>
          <text>📊</text>
          <text v-if="tweet.viewcount">{{ formatCount(tweet.viewcount) }}</text>
        </view>
        <view class="action" @click.stop="handleBookmark">
          <text>{{ tweet.bookmarked ? '🔖' : '📑' }}</text>
        </view>
        <view class="action" @click.stop="handleShare">
          <text>📤</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { post } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const props = defineProps({ tweet: Object })
const emit = defineEmits(['refresh'])
const userStore = useUserStore()

const parsedImages = computed(() => {
  if (!props.tweet?.images) return []
  if (Array.isArray(props.tweet.images)) return props.tweet.images
  return props.tweet.images.split(',').filter(Boolean)
})

const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时'
  if (d.getFullYear() === now.getFullYear()) return `${d.getMonth() + 1}月${d.getDate()}日`
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const formatCount = (count) => {
  if (!count) return ''
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  if (count >= 1000) return (count / 1000).toFixed(1) + 'K'
  return count.toString()
}

const goDetail = () => uni.navigateTo({ url: `/pages/tweet/detail?id=${props.tweet.id}` })
const goProfile = () => uni.navigateTo({ url: `/pages/profile/index?id=${props.tweet.user?.id}` })

const handleLike = async () => {
  if (!userStore.requireLogin()) return
  try {
    await post(`/tweets/${props.tweet.id}/like`)
    props.tweet.liked = !props.tweet.liked
    props.tweet.likecount = (props.tweet.likecount || 0) + (props.tweet.liked ? 1 : -1)
  } catch (e) { userStore.openLoginModal() }
}

const handleRetweet = async () => {
  if (!userStore.requireLogin()) return
  try {
    await post(`/tweets/${props.tweet.id}/retweet`)
    props.tweet.retweeted = !props.tweet.retweeted
    props.tweet.retweetcount = (props.tweet.retweetcount || 0) + (props.tweet.retweeted ? 1 : -1)
  } catch (e) { userStore.openLoginModal() }
}

const handleBookmark = async () => {
  if (!userStore.requireLogin()) return
  try {
    await post(`/tweets/${props.tweet.id}/bookmark`)
    props.tweet.bookmarked = !props.tweet.bookmarked
    uni.showToast({ title: props.tweet.bookmarked ? '已添加书签' : '已移除书签', icon: 'none' })
  } catch (e) { userStore.openLoginModal() }
}

const handleShare = () => {
  uni.showActionSheet({
    itemList: ['复制链接', '分享到...'],
    success: (res) => {
      if (res.tapIndex === 0) {
        uni.setClipboardData({ data: `https://x.com/status/${props.tweet.id}` })
      }
    }
  })
}

const showMenu = () => {
  uni.showActionSheet({
    itemList: ['不感兴趣', '举报'],
    success: () => {}
  })
}

const previewImage = (index) => {
  uni.previewImage({ urls: parsedImages.value, current: index })
}
</script>

<style scoped>
.tweet-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.2s;
}

.tweet-item:hover {
  background: var(--bg-hover);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.content {
  flex: 1;
  margin-left: 12px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.name {
  font-weight: bold;
  color: var(--text-primary);
}

.verified {
  color: var(--accent-primary);
}

.handle, .dot, .time {
  color: var(--text-secondary);
}

.more {
  color: var(--text-secondary);
  padding: 4px;
}

.more:hover {
  color: var(--accent-primary);
}

.text {
  margin-top: 4px;
  color: var(--text-primary);
  line-height: 1.5;
  white-space: pre-wrap;
}

.images {
  display: grid;
  gap: 4px;
  margin-top: 12px;
  border-radius: 16px;
  overflow: hidden;
}

.images-1 { grid-template-columns: 1fr; }
.images-2 { grid-template-columns: 1fr 1fr; }
.images-3 { grid-template-columns: 1fr 1fr; }
.images-4 { grid-template-columns: 1fr 1fr; }

.image {
  width: 100%;
  height: 160px;
  background: var(--bg-tertiary);
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  max-width: 400px;
}

.action {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s;
}

.action:hover {
  color: var(--accent-primary);
}

.action.liked {
  color: #f4212e;
}

.action.retweeted {
  color: #00ba7c;
}
</style>
