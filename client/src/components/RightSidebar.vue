<template>
  <view v-if="!isSmallScreen" class="right-sidebar">
    <!-- 搜索框 -->
    <view class="search-box">
      <text class="search-icon">🔍</text>
      <input class="search-input" placeholder="搜索" v-model="searchText" @confirm="doSearch" />
    </view>

    <!-- 订阅 Premium -->
    <view class="card premium-card">
      <text class="card-title">订阅 Premium</text>
      <text class="card-desc">订阅即可解锁新功能，符合条件的话，还能获得广告收入分成。</text>
      <button class="btn-subscribe">订阅</button>
    </view>

    <!-- 热门话题 -->
    <view class="card">
      <text class="card-title">热门话题</text>
      <view v-for="trend in trends" :key="trend.id" class="trend-item" @click="searchTag(trend.tag)">
        <view class="trend-info">
          <text class="trend-category">热门 · {{ trend.category || '中国' }}</text>
          <text class="trend-tag">#{{ trend.tag }}</text>
          <text class="trend-count">{{ formatCount(trend.count) }} 帖子</text>
        </view>
        <text class="trend-more">···</text>
      </view>
      <view class="card-link" @click="goExplore">显示更多</view>
    </view>

    <!-- 推荐关注 -->
    <view class="card">
      <text class="card-title">推荐关注</text>
      <view v-for="item in recommendUsers" :key="item.id" class="user-item">
        <image class="user-avatar" :src="item.user?.avatar || item.avatar || '/static/default-avatar.png'" @click="goProfile(item.user?.id || item.id)" />
        <view class="user-info" @click="goProfile(item.user?.id || item.id)">
          <text class="user-name">{{ item.user?.nickname || item.nickname }}</text>
          <text class="user-handle">@{{ item.user?.username || item.username }}</text>
        </view>
        <button class="btn-follow" @click="followUser(item.user?.id || item.id)">关注</button>
      </view>
      <view class="card-link" @click="showMoreUsers">显示更多</view>
    </view>

    <!-- 底部链接 -->
    <view class="footer-links">
      <text @click="goTerms">服务条款</text>
      <text @click="goPrivacy">隐私政策</text>
      <text>Cookie政策</text>
      <text>© 2024 X Corp.</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { post } from '@/utils/request'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
const searchText = ref('')
const isSmallScreen = ref(false)

const trends = computed(() => appStore.trends)
const recommendUsers = computed(() => appStore.recommendUsers)

const checkScreen = () => {
  const info = uni.getSystemInfoSync()
  isSmallScreen.value = info.windowWidth <= 1000
}

onMounted(() => {
  checkScreen()
  appStore.fetchTrends()
  appStore.fetchRecommend()
  // #ifdef H5
  window.addEventListener('resize', checkScreen)
  // #endif
})

onUnmounted(() => {
  // #ifdef H5
  window.removeEventListener('resize', checkScreen)
  // #endif
})

const formatCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

const doSearch = () => {
  if (searchText.value.trim()) {
    uni.navigateTo({ url: `/pages/search/result?q=${encodeURIComponent(searchText.value)}` })
  }
}

const searchTag = (tag) => uni.navigateTo({ url: `/pages/search/result?q=${encodeURIComponent('#' + tag)}` })
const goExplore = () => {
  // #ifdef H5
  window.location.href = '/#/pages/explore/index'
  // #endif
  // #ifndef H5
  uni.switchTab({ url: '/pages/explore/index' })
  // #endif
}
const goProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })

const followUser = async (id) => {
  try {
    await post(`/user/${id}/follow`)
    appStore.removeRecommendUser(id)
    uni.showToast({ title: '关注成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const showMoreUsers = () => uni.navigateTo({ url: '/pages/explore/index' })
const goTerms = () => uni.navigateTo({ url: '/pages/login/terms' })
const goPrivacy = () => uni.navigateTo({ url: '/pages/login/privacy' })
</script>

<style scoped>
.right-sidebar {
  width: 350px;
  padding: 0 16px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-tertiary);
  border-radius: 9999px;
  padding: 12px 16px;
  margin: 8px 0;
}

.search-icon { margin-right: 12px; color: var(--text-secondary); }
.search-input { flex: 1; background: transparent; border: none; color: var(--text-primary); font-size: 15px; }

.card { background: var(--bg-secondary); border-radius: 16px; overflow: hidden; margin-bottom: 16px; }
.premium-card { padding: 16px; }
.card-title { font-size: 20px; font-weight: bold; display: block; padding: 16px; }
.premium-card .card-title { padding: 0; margin-bottom: 8px; }
.card-desc { display: block; margin-bottom: 12px; line-height: 1.4; }
.btn-subscribe { background: var(--accent-primary); color: #fff; font-weight: bold; padding: 8px 16px; border-radius: 9999px; border: none; font-size: 15px; }

.trend-item { display: flex; align-items: center; padding: 12px 16px; cursor: pointer; }
.trend-item:hover { background: var(--bg-hover); }
.trend-info { flex: 1; }
.trend-category { font-size: 13px; color: var(--text-secondary); display: block; }
.trend-tag { font-weight: bold; display: block; margin: 2px 0; }
.trend-count { font-size: 13px; color: var(--text-secondary); }
.trend-more { color: var(--text-secondary); padding: 8px; }
.card-link { padding: 16px; color: var(--accent-primary); cursor: pointer; }
.card-link:hover { background: var(--bg-hover); }

.user-item { display: flex; align-items: center; padding: 12px 16px; }
.user-item:hover { background: var(--bg-hover); }
.user-avatar { width: 40px; height: 40px; border-radius: 50%; cursor: pointer; }
.user-info { flex: 1; margin-left: 12px; cursor: pointer; }
.user-name { font-weight: bold; display: block; }
.user-handle { font-size: 14px; color: var(--text-secondary); }
.btn-follow { background: var(--btn-primary-bg); color: var(--btn-primary-text); font-weight: bold; padding: 8px 16px; border-radius: 9999px; border: none; font-size: 14px; }

.footer-links { display: flex; flex-wrap: wrap; gap: 12px; padding: 16px; font-size: 13px; color: var(--text-secondary); }

@media (max-width: 1280px) { .right-sidebar { width: 290px; } }
@media (max-width: 1000px) { .right-sidebar { display: none; } }
</style>
