<template>
  <view class="user-card" @click="goProfile">
    <image class="avatar" :src="user.avatar || '/static/default-avatar.png'" mode="aspectFill" />
    <view class="info">
      <text class="name">{{ user.nickname || '用户' }}</text>
      <text class="username">@{{ user.username }}</text>
      <text v-if="user.bio" class="bio">{{ user.bio }}</text>
    </view>
    <view v-if="showFollow && !isSelf" class="follow-btn" :class="{ following: user.followed }" @click.stop="handleFollow">
      {{ user.followed ? '已关注' : '关注' }}
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { post, del } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  user: { type: Object, required: true },
  showFollow: { type: Boolean, default: true }
})

const emit = defineEmits(['update'])
const userStore = useUserStore()

const isSelf = computed(() => userStore.userInfo?.id === props.user.id)

const goProfile = () => uni.navigateTo({ url: `/pages/profile/index?id=${props.user.id}` })

const handleFollow = async () => {
  if (props.user.followed) {
    await del(`/user/${props.user.id}/follow`)
  } else {
    await post(`/user/${props.user.id}/follow`)
  }
  emit('update', { ...props.user, followed: !props.user.followed })
}
</script>

<style scoped>
.user-card { display: flex; align-items: center; padding: 24rpx; background: #fff; border-bottom: 1rpx solid #eee; }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.info { flex: 1; margin-left: 20rpx; }
.name { font-size: 30rpx; font-weight: bold; display: block; }
.username { font-size: 26rpx; color: #657786; display: block; }
.bio { font-size: 26rpx; color: #333; margin-top: 8rpx; display: block; }
.follow-btn { padding: 12rpx 28rpx; background: #1da1f2; color: #fff; border-radius: 30rpx; font-size: 26rpx; }
.follow-btn.following { background: #fff; color: #1da1f2; border: 1rpx solid #1da1f2; }
</style>
