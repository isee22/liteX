<template>
  <view class="tweet-card" @click="goDetail">
    <view class="avatar-wrap">
      <image class="avatar" :src="tweet.user?.avatar || '/static/default-avatar.png'" mode="aspectFill" />
    </view>
    <view class="content-wrap">
      <view class="header">
        <text class="name">{{ tweet.user?.nickname || '用户' }}</text>
        <text class="username">@{{ tweet.user?.username }}</text>
        <text class="time">· {{ formatTime(tweet.createdAt) }}</text>
      </view>
      <text class="content">{{ tweet.content }}</text>
      <view v-if="tweet.images?.length" class="images">
        <image v-for="(img, i) in tweet.images" :key="i" :src="img" mode="aspectFill" class="tweet-img" @click.stop="previewImage(img)" />
      </view>
      <view class="actions">
        <view class="action" @click.stop="onComment">
          <text class="icon">💬</text>
          <text>{{ tweet.commentCount || 0 }}</text>
        </view>
        <view class="action" :class="{ retweeted: tweet.retweeted }" @click.stop="onRetweet">
          <text class="icon">🔁</text>
          <text>{{ tweet.retweetCount || 0 }}</text>
        </view>
        <view class="action" :class="{ liked: tweet.liked }" @click.stop="onLike">
          <text class="icon">❤</text>
          <text>{{ tweet.likeCount || 0 }}</text>
        </view>
        <view class="action" :class="{ collected: tweet.bookmarked }" @click.stop="onCollect">
          <text class="icon">{{ tweet.bookmarked ? '★' : '☆' }}</text>
        </view>
        <view class="action" @click.stop="onShare">
          <text class="icon">↗</text>
        </view>
      </view>
    </view>
    
    <!-- 分享菜单 -->
    <view v-if="showShareMenu" class="share-overlay" @click.stop="showShareMenu = false">
      <view class="share-menu" @click.stop>
        <view class="share-title">分享到</view>
        <view class="share-platforms">
          <view class="share-item" @click="shareTo('wechat')">
            <view class="share-icon wechat">微</view>
            <text>微信好友</text>
          </view>
          <view class="share-item" @click="shareTo('moments')">
            <view class="share-icon moments">圈</view>
            <text>朋友圈</text>
          </view>
          <view class="share-item" @click="shareTo('douyin')">
            <view class="share-icon douyin">抖</view>
            <text>抖音</text>
          </view>
          <view class="share-item" @click="shareTo('weibo')">
            <view class="share-icon weibo">微</view>
            <text>微博</text>
          </view>
        </view>
        <view class="share-divider"></view>
        <view class="share-actions">
          <view class="share-action" @click="copyLink">
            <text class="action-icon">🔗</text>
            <text>复制链接</text>
          </view>
        </view>
        <view class="share-cancel" @click="showShareMenu = false">取消</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useTweetStore } from '@/stores/tweet'
import { post } from '@/utils/request'

const props = defineProps({ tweet: Object })
const emit = defineEmits(['comment'])
const tweetStore = useTweetStore()
const showShareMenu = ref(false)

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const goDetail = () => uni.navigateTo({ url: `/pages/tweet/detail?id=${props.tweet.id}` })
const onComment = () => emit('comment', props.tweet)
const onRetweet = () => tweetStore.retweet(props.tweet.id)
const onLike = () => tweetStore.likeTweet(props.tweet.id)

const onCollect = async () => {
  try {
    await post(`/tweets/${props.tweet.id}/bookmark`)
    props.tweet.bookmarked = !props.tweet.bookmarked
    uni.showToast({ title: props.tweet.bookmarked ? '已收藏' : '已取消收藏', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const onShare = () => {
  showShareMenu.value = true
}

const shareTo = (platform) => {
  showShareMenu.value = false
  const platformNames = { wechat: '微信好友', moments: '朋友圈', douyin: '抖音', weibo: '微博' }
  uni.showToast({ title: `分享到${platformNames[platform]}功能开发中`, icon: 'none' })
}

const copyLink = () => {
  showShareMenu.value = false
  uni.setClipboardData({ data: `${location.origin}/#/pages/tweet/detail?id=${props.tweet.id}` })
  uni.showToast({ title: '链接已复制', icon: 'success' })
}

const previewImage = (url) => uni.previewImage({ urls: props.tweet.images, current: url })
</script>

<style scoped>
.tweet-card {
  display: flex;
  padding: 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
  position: relative;
}
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.content-wrap { flex: 1; margin-left: 20rpx; }
.header { display: flex; align-items: center; gap: 8rpx; }
.name { font-weight: bold; font-size: 28rpx; }
.username, .time { color: #657786; font-size: 26rpx; }
.content { font-size: 30rpx; line-height: 1.5; margin: 12rpx 0; display: block; }
.images { display: flex; flex-wrap: wrap; gap: 10rpx; margin-top: 16rpx; }
.tweet-img { width: 200rpx; height: 200rpx; border-radius: 16rpx; }
.actions { display: flex; justify-content: space-between; margin-top: 20rpx; padding-right: 40rpx; }
.action { display: flex; align-items: center; gap: 8rpx; color: #657786; font-size: 26rpx; cursor: pointer; }
.action:hover { color: #1da1f2; }
.action.liked { color: #E0245E; }
.action.retweeted { color: #17bf63; }
.action.collected { color: #ffcc00; }

/* 分享菜单 */
.share-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.share-menu {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  width: 100%;
  max-width: 750rpx;
  padding: 32rpx;
}
.share-title {
  text-align: center;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 32rpx;
}
.share-platforms {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 0;
}
.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  cursor: pointer;
}
.share-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #fff;
}
.share-icon.wechat { background: #07c160; }
.share-icon.moments { background: #576b95; }
.share-icon.douyin { background: #000; }
.share-icon.weibo { background: #e6162d; }
.share-item text { font-size: 24rpx; color: #666; }
.share-divider {
  height: 1rpx;
  background: #eee;
  margin: 24rpx 0;
}
.share-actions {
  padding: 16rpx 0;
}
.share-action {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;
  cursor: pointer;
}
.share-action:hover { background: #f5f5f5; border-radius: 12rpx; }
.action-icon { font-size: 36rpx; }
.share-cancel {
  text-align: center;
  padding: 24rpx;
  color: #666;
  font-size: 30rpx;
  cursor: pointer;
  border-top: 1rpx solid #eee;
  margin-top: 16rpx;
}
</style>
