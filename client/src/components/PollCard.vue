<template>
  <view class="poll-card">
    <view v-for="option in poll.options" :key="option.id" class="option" :class="{ voted: poll.voted, selected: option.id === poll.votedOptionId }" @click="handleVote(option.id)">
      <view class="bar" :style="{ width: poll.voted ? getPercent(option) + '%' : '0%' }"></view>
      <text class="text">{{ option.text }}</text>
      <text v-if="poll.voted" class="percent">{{ getPercent(option) }}%</text>
    </view>
    <view class="footer">
      <text class="votes">{{ poll.totalVotes || 0 }} 票</text>
      <text class="time">{{ poll.expired ? '已结束' : formatRemaining() }}</text>
    </view>
  </view>
</template>

<script setup>
import { post } from '@/utils/request'

const props = defineProps({ poll: Object, tweetid: [String, Number] })
const emit = defineEmits(['voted'])

const getPercent = (option) => {
  if (!props.poll.totalVotes) return 0
  return Math.round((option.voteCount / props.poll.totalVotes) * 100)
}

const formatRemaining = () => {
  if (!props.poll.expiresat) return ''
  const diff = new Date(props.poll.expiresat) - new Date()
  if (diff <= 0) return '已结束'
  const hours = Math.floor(diff / 3600000)
  if (hours >= 24) return Math.floor(hours / 24) + '天后结束'
  return hours + '小时后结束'
}

const handleVote = async (optionid) => {
  if (props.poll.voted || props.poll.expired) return
  await post(`/tweets/${props.tweetid}/vote`, { optionid })
  emit('voted', optionid)
}
</script>

<style scoped>
.poll-card { padding: 16rpx 0; }
.option { position: relative; padding: 20rpx; margin-bottom: 16rpx; border: 1rpx solid #cfd9de; border-radius: 8rpx; overflow: hidden; }
.option.voted { pointer-events: none; }
.option.selected { border-color: #1da1f2; }
.bar { position: absolute; left: 0; top: 0; bottom: 0; background: #e8f5fd; z-index: 0; transition: width 0.3s; }
.text { position: relative; z-index: 1; font-size: 28rpx; }
.percent { position: relative; z-index: 1; float: right; font-size: 28rpx; font-weight: bold; }
.footer { display: flex; gap: 20rpx; color: #657786; font-size: 26rpx; margin-top: 8rpx; }
</style>
