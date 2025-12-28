<template>
  <view class="twitter-layout">
    <LeftSidebar current="explore" />
    <view class="main-content">
      <!-- 搜索框 -->
      <view class="header">
        <view class="search-box">
          <text class="search-icon">🔍</text>
          <input class="search-input" placeholder="搜索" v-model="keyword" @confirm="search" />
        </view>
        <text class="settings-icon" @click="showSettingsTip">⚙️</text>
      </view>

      <!-- 标签页 -->
      <view class="tabs">
        <view class="tab" :class="{ active: tab === 'trending' }" @click="tab = 'trending'">为你推荐</view>
        <view class="tab" :class="{ active: tab === 'news' }" @click="tab = 'news'">热门</view>
        <view class="tab" :class="{ active: tab === 'sports' }" @click="tab = 'sports'">新闻</view>
        <view class="tab" :class="{ active: tab === 'entertainment' }" @click="tab = 'entertainment'">体育</view>
      </view>

      <!-- 热门话题大图 -->
      <view v-if="featuredTrend" class="featured-trend" :style="featuredTrend.image ? { backgroundImage: 'url(' + featuredTrend.image + ')' } : {}" @click="searchTag(featuredTrend.tag)">
        <view class="featured-overlay">
          <text class="featured-category">{{ featuredTrend.category || '热门' }}</text>
          <text class="featured-title">#{{ featuredTrend.tag }}</text>
          <text class="featured-count">{{ formatCount(featuredTrend.count) }} 帖子</text>
        </view>
      </view>

      <!-- 热门话题列表 -->
      <view class="trends-section">
        <view v-for="(trend, i) in trends" :key="trend.id" class="trend-item" @click="searchTag(trend.tag)">
          <view class="trend-meta">
            <text class="trend-rank">{{ i + 1 }} · 热门话题</text>
            <text class="trend-more" @click.stop>···</text>
          </view>
          <text class="trend-tag">#{{ trend.tag }}</text>
          <text class="trend-count">{{ formatCount(trend.count) }} 帖子</text>
        </view>
        <view v-if="!trends.length" class="empty">暂无热门话题</view>
        <view class="show-more" @click="loadMore">显示更多</view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { get } from '@/utils/request'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'

const keyword = ref('')
const tab = ref('trending')
const allTrends = ref([])
const showCount = ref(10)

const featuredTrend = computed(() => allTrends.value[0])
const trends = computed(() => allTrends.value.slice(1, showCount.value))

onMounted(async () => {
  try {
    const res = await get('/trends')
    allTrends.value = res.data || []
  } catch (e) {}
})

const formatCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

const search = () => {
  if (keyword.value.trim()) {
    uni.navigateTo({ url: `/pages/search/result?q=${encodeURIComponent(keyword.value)}` })
  }
}

const searchTag = (tag) => uni.navigateTo({ url: `/pages/search/result?q=${encodeURIComponent('#' + tag)}` })

const loadMore = () => {
  showCount.value += 10
}

const showSettingsTip = () => uni.showToast({ title: '探索设置功能开发中', icon: 'none' })
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; min-height: 100vh; background: var(--bg-primary); color: var(--text-primary); }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; align-items: center; padding: 8px 16px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; gap: 16px; }
.search-box { flex: 1; display: flex; align-items: center; background: var(--bg-tertiary); border-radius: 9999px; padding: 12px 16px; }
.search-icon { margin-right: 12px; color: var(--text-secondary); }
.search-input { flex: 1; background: transparent; border: none; color: var(--text-primary); font-size: 15px; }
.settings-icon { font-size: 20px; cursor: pointer; color: var(--text-primary); }
.tabs { display: flex; border-bottom: 1px solid var(--border-color); overflow-x: auto; }
.tab { flex: 1; text-align: center; padding: 16px 12px; color: var(--text-secondary); cursor: pointer; position: relative; white-space: nowrap; min-width: 80px; }
.tab:hover { background: var(--bg-hover); }
.tab.active { color: var(--text-primary); font-weight: bold; }
.tab.active::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 56px; height: 4px; background: var(--accent-primary); border-radius: 2px; }
.featured-trend { position: relative; height: 300px; background: linear-gradient(135deg, #1a1a2e, #16213e); cursor: pointer; background-size: cover; background-position: center; }
.featured-overlay { position: absolute; bottom: 16px; left: 16px; right: 16px; }
.featured-category { background: var(--accent-primary); padding: 4px 8px; border-radius: 4px; font-size: 13px; color: #fff; }
.featured-title { font-size: 23px; font-weight: bold; display: block; margin: 8px 0; color: var(--text-primary); }
.featured-count { color: var(--text-secondary); font-size: 15px; }
.trends-section { padding: 0; }
.trend-item { padding: 12px 16px; border-bottom: 1px solid var(--border-color); cursor: pointer; }
.trend-item:hover { background: var(--bg-hover); }
.trend-meta { display: flex; justify-content: space-between; align-items: center; }
.trend-rank { font-size: 13px; color: var(--text-secondary); }
.trend-more { color: var(--text-secondary); padding: 4px; }
.trend-tag { font-size: 15px; font-weight: bold; display: block; margin: 4px 0; color: var(--text-primary); }
.trend-count { font-size: 13px; color: var(--text-secondary); }
.empty { text-align: center; padding: 32px; color: var(--text-secondary); }
.show-more { padding: 16px; color: var(--accent-primary); cursor: pointer; }
.show-more:hover { background: var(--bg-secondary); }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
