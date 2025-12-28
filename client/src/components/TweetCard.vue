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
        <view class="action" @click.stop="onRetweet">
          <text class="icon">🔁</text>
          <text>{{ tweet.retweetCount || 0 }}</text>
        </view>
        <view class="action" :class="{ liked: tweet.liked }" @click.stop="onLike">
          <text class="icon">{{ tweet.liked ? '👍' : '👍🏻' }}</text>
          <text>{{ tweet.likeCount || 0 }}</text>
        </view>
        <view class="action" @click.stop="onShare">
          <text class="icon">📤</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useTweetStore } from '@/stores/tweet'

const props = defineProps({ tweet: Object })
const emit = defineEmits(['comment'])
const tweetStore = useTweetStore()

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
const onShare = () => uni.share?.({ title: props.tweet.content })
const previewImage = (url) => uni.previewImage({ urls: props.tweet.images, current: url })
</script>

<style scoped>
.tweet-card {
  display: flex;
  padding: 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.content-wrap { flex: 1; margin-left: 20rpx; }
.header { display: flex; align-items: center; gap: 8rpx; }
.name { font-weight: bold; font-size: 28rpx; }
.username, .time { color: #657786; font-size: 26rpx; }
.content { font-size: 30rpx; line-height: 1.5; margin: 12rpx 0; display: block; }
.images { display: flex; flex-wrap: wrap; gap: 10rpx; margin-top: 16rpx; }
.tweet-img { width: 200rpx; height: 200rpx; border-radius: 16rpx; }
.actions { display: flex; justify-content: space-between; margin-top: 20rpx; padding-right: 60rpx; }
.action { display: flex; align-items: center; gap: 8rpx; color: #657786; font-size: 26rpx; }
.action.liked { color: #E0245E; }
</style>
