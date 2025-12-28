<template>
  <view class="twitter-layout">
    <LeftSidebar current="bookmarks" />
    <view class="main-content">
      <view class="header">
        <view class="header-info">
          <text class="title">书签</text>
          <text class="subtitle">@{{ userStore.userInfo?.username }}</text>
        </view>
        <text class="more" @click="showMenu = !showMenu">···</text>
        
        <!-- 更多菜单 -->
        <view v-if="showMenu" class="dropdown-menu">
          <view class="menu-item" @click="clearAll">
            <text class="menu-icon">🗑️</text>
            <text class="menu-text">清除所有书签</text>
          </view>
        </view>
      </view>
      
      <view class="bookmark-list">
        <TweetItem v-for="item in bookmarks" :key="item.id" :tweet="item" @refresh="loadBookmarks" />
        
        <view v-if="!bookmarks.length && !loading" class="empty">
          <text class="empty-icon">🔖</text>
          <text class="empty-title">保存帖子以便稍后阅读</text>
          <text class="empty-desc">将帖子添加到书签后，它们会显示在这里。</text>
        </view>
        
        <view v-if="loading" class="loading">加载中...</view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, del } from '@/utils/request'
import { useUserStore } from '@/stores/user'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'
import TweetItem from '@/components/TweetItem.vue'

const userStore = useUserStore()
const bookmarks = ref([])
const loading = ref(true)
const showMenu = ref(false)

onMounted(async () => {
  try {
    const res = await get('/bookmarks')
    const data = res.data?.list || res.data || []
    bookmarks.value = data.map(item => {
      if (item.tweet) {
        return { ...item.tweet, user: item.user, liked: item.liked, bookmarked: true }
      }
      return item
    })
  } catch (e) {}
  loading.value = false
})

const clearAll = () => {
  showMenu.value = false
  uni.showModal({
    title: '清除所有书签？',
    content: '此操作无法撤销。你确定要从书签中移除所有帖子吗？',
    confirmText: '清除',
    confirmColor: '#f4212e',
    success: async (res) => {
      if (res.confirm) {
        try {
          await del('/bookmarks')
          bookmarks.value = []
          uni.showToast({ title: '已清除', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style scoped>
.twitter-layout {
  display: flex;
  max-width: 1280px;
  margin: 0 auto;
  background: var(--bg-primary);
}
.main-content {
  flex: 1;
  max-width: 600px;
  min-width: 600px;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  min-height: 100vh;
  background: var(--bg-primary);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  position: sticky;
  top: 0;
  background: var(--bg-primary-alpha);
  backdrop-filter: blur(12px);
  z-index: 10;
}
.header-info {
  flex: 1;
}
.title {
  font-size: 20px;
  font-weight: bold;
  display: block;
  color: var(--text-primary);
}
.subtitle {
  font-size: 13px;
  color: var(--text-secondary);
}
.more {
  font-size: 20px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
}
.more:hover {
  background: var(--bg-hover);
}
.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 16px;
  background: var(--bg-primary);
  border-radius: 12px;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.2);
  overflow: hidden;
  min-width: 200px;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
}
.menu-item:hover {
  background: var(--bg-hover);
}
.menu-icon {
  margin-right: 12px;
  font-size: 18px;
}
.menu-text {
  font-size: 15px;
  color: #f4212e;
}
.empty {
  text-align: center;
  padding: 48px 32px;
}
.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
}
.empty-title {
  font-size: 31px;
  font-weight: bold;
  display: block;
  margin-bottom: 8px;
  color: var(--text-primary);
}
.empty-desc {
  color: var(--text-secondary);
  font-size: 15px;
  line-height: 1.4;
}
.loading {
  text-align: center;
  padding: 32px;
  color: var(--text-secondary);
}
@media (max-width: 1000px) {
  .main-content {
    max-width: 100%;
    min-width: auto;
    border: none;
  }
}
</style>
