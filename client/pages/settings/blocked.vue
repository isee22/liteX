<template>
  <view class="blocked-page">
    <view class="list" v-if="list.length">
      <view class="user-item" v-for="item in list" :key="item.block.id">
        <image :src="item.user?.avatar || '/static/default-avatar.png'" class="avatar" />
        <view class="info">
          <text class="nickname">{{ item.user?.nickname }}</text>
          <text class="username">@{{ item.user?.username }}</text>
          <text class="expire" v-if="item.block.expireat">到期: {{ formatDate(item.block.expireat) }}</text>
        </view>
        <button class="unblock-btn" @click="unblock(item)">取消屏蔽</button>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无屏蔽用户</view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, del } from '@/utils/request'

const list = ref([])
const loading = ref(false)

onMounted(() => fetchBlocked())

const fetchBlocked = async () => {
  loading.value = true
  try {
    const res = await get('/blocks')
    list.value = res.data || res || []
  } finally {
    loading.value = false
  }
}

const unblock = async (item) => {
  await del(`/user/${item.user.id}/block`)
  list.value = list.value.filter(i => i.block.id !== item.block.id)
  uni.showToast({ title: '已取消屏蔽', icon: 'success' })
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}
</script>

<style scoped>
.blocked-page { background: #fff; min-height: 100vh; }
.user-item { display: flex; align-items: center; padding: 24rpx 30rpx; border-bottom: 1rpx solid #eee; }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-right: 20rpx; }
.info { flex: 1; }
.nickname { font-size: 28rpx; font-weight: bold; display: block; }
.username { font-size: 24rpx; color: #666; display: block; }
.expire { font-size: 22rpx; color: #999; display: block; margin-top: 4rpx; }
.unblock-btn { font-size: 24rpx; padding: 10rpx 24rpx; border-radius: 40rpx; background: #fff; color: #e0245e; border: 2rpx solid #e0245e; }
.empty { text-align: center; padding: 100rpx; color: #999; }
</style>
