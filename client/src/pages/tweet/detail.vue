<template>
  <view class="twitter-layout">
    <LeftSidebar />
    <view class="main-content">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <text class="title">帖子</text>
      </view>

      <view v-if="tweet" class="tweet-detail">
        <view class="user-row">
          <image class="avatar" :src="tweet.user?.avatar || '/static/default-avatar.png'" @click="goProfile" />
          <view class="user-info">
            <text class="name">{{ tweet.user?.nickname }}</text>
            <text class="handle">@{{ tweet.user?.username }}</text>
          </view>
          <text class="more" @click="showMenu">···</text>
        </view>
        <text class="content">{{ tweet.content }}</text>
        <view v-if="images.length" class="images">
          <image v-for="(img, i) in images" :key="i" :src="img" mode="aspectFill" class="tweet-img" @click="previewImage(img)" />
        </view>
        <text class="time">{{ formatFullTime(tweet.createdat) }}</text>
        <view class="stats">
          <text class="stat"><text class="num">{{ tweet.retweetcount || 0 }}</text> 转推</text>
          <text class="stat"><text class="num">{{ tweet.quotecount || 0 }}</text> 引用</text>
          <text class="stat"><text class="num">{{ tweet.likecount || 0 }}</text> 喜欢</text>
          <text class="stat"><text class="num">{{ tweet.bookmarkcount || 0 }}</text> 书签</text>
        </view>
        <view class="actions">
          <view class="action" @click="focusReply"><text>💬</text></view>
          <view class="action" :class="{ retweeted: tweet.retweeted }" @click="onRetweet"><text>🔁</text></view>
          <view class="action" :class="{ liked: tweet.liked }" @click="onLike"><text>{{ tweet.liked ? '❤️' : '🤍' }}</text></view>
          <view class="action" @click="onBookmark"><text>🔖</text></view>
          <view class="action" @click="onShare"><text>📤</text></view>
        </view>
      </view>

      <!-- 回复输入框 -->
      <view class="reply-box">
        <image class="reply-avatar" :src="userStore.userInfo?.avatar || '/static/default-avatar.png'" />
        <input ref="replyInput" v-model="commentText" class="reply-input" placeholder="发布你的回复" />
        <button class="reply-btn" :disabled="!commentText.trim()" @click="submitComment">回复</button>
      </view>

      <!-- 回复列表 -->
      <view class="comments">
        <view v-for="comment in comments" :key="comment.id" class="comment-item">
          <image class="comment-avatar" :src="comment.user?.avatar || '/static/default-avatar.png'" @click="goUserProfile(comment.user?.id)" />
          <view class="comment-content">
            <view class="comment-header">
              <text class="comment-name">{{ comment.user?.nickname }}</text>
              <text class="comment-handle">@{{ comment.user?.username }}</text>
              <text class="comment-time">· {{ formatTime(comment.createdat) }}</text>
            </view>
            <text class="reply-to">回复 <text class="mention">@{{ tweet?.user?.username }}</text></text>
            <text class="comment-text">{{ comment.content }}</text>
            <view class="comment-actions">
              <view class="action"><text>💬</text><text>{{ comment.replycount || 0 }}</text></view>
              <view class="action"><text>🔁</text><text>0</text></view>
              <view class="action"><text>🤍</text><text>{{ comment.likecount || 0 }}</text></view>
              <view class="action"><text>📊</text></view>
            </view>
          </view>
        </view>
        <view v-if="!comments.length && !loading" class="empty">暂无回复</view>
      </view>
    </view>
    <RightSidebar />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/stores/user'
import LeftSidebar from '@/components/LeftSidebar.vue'
import RightSidebar from '@/components/RightSidebar.vue'

const userStore = useUserStore()
const tweet = ref(null)
const comments = ref([])
const commentText = ref('')
const tweetId = ref('')
const loading = ref(false)

const images = computed(() => {
  if (!tweet.value?.images) return []
  if (Array.isArray(tweet.value.images)) return tweet.value.images
  return tweet.value.images.split(',').filter(Boolean)
})

onLoad((options) => {
  tweetId.value = options?.id
  if (tweetId.value) {
    fetchTweet()
    fetchComments()
  }
})

const fetchTweet = async () => {
  try {
    const res = await get(`/tweets/${tweetId.value}`)
    if (res.data?.tweet) {
      tweet.value = { ...res.data.tweet, user: res.data.user, liked: res.data.liked }
    } else {
      tweet.value = res.data
    }
  } catch (e) {}
}

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await get(`/tweets/${tweetId.value}/comments`)
    comments.value = (res.data || []).map(item => item.comment ? { ...item.comment, user: item.user } : item)
  } catch (e) {}
  loading.value = false
}

const submitComment = async () => {
  if (!commentText.value.trim()) return
  try {
    await post(`/tweets/${tweetId.value}/comments`, { content: commentText.value })
    commentText.value = ''
    fetchComments()
    tweet.value.commentcount = (tweet.value.commentcount || 0) + 1
    uni.showToast({ title: '回复成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const onLike = async () => {
  try {
    await post(`/tweets/${tweetId.value}/like`)
    tweet.value.liked = !tweet.value.liked
    tweet.value.likecount = (tweet.value.likecount || 0) + (tweet.value.liked ? 1 : -1)
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const onRetweet = async () => {
  try {
    await post(`/tweets/${tweetId.value}/retweet`)
    tweet.value.retweeted = true
    tweet.value.retweetcount = (tweet.value.retweetcount || 0) + 1
    uni.showToast({ title: '转推成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const onBookmark = async () => {
  try {
    await post(`/tweets/${tweetId.value}/bookmark`)
    uni.showToast({ title: '已添加书签', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '请先登录', icon: 'none' })
  }
}

const onShare = () => {
  uni.setClipboardData({ data: `${location.origin}/#/pages/tweet/detail?id=${tweetId.value}` })
  uni.showToast({ title: '链接已复制', icon: 'success' })
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时'
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const formatFullTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')} · ${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const goBack = () => uni.navigateBack()
const goProfile = () => uni.navigateTo({ url: `/pages/profile/index?id=${tweet.value?.user?.id}` })
const goUserProfile = (id) => uni.navigateTo({ url: `/pages/profile/index?id=${id}` })
const previewImage = (url) => uni.previewImage({ urls: images.value, current: url })
const focusReply = () => {}
const showMenu = () => {
  uni.showActionSheet({
    itemList: ['举报', '屏蔽 @' + tweet.value?.user?.username],
    success: () => {}
  })
}
</script>

<style scoped>
.twitter-layout { display: flex; max-width: 1280px; margin: 0 auto; }
.main-content { flex: 1; max-width: 600px; border-left: 1px solid var(--border-color); border-right: 1px solid var(--border-color); min-height: 100vh; background: var(--bg-primary); }
.header { display: flex; align-items: center; padding: 0 16px; height: 53px; position: sticky; top: 0; background: var(--bg-primary-alpha); backdrop-filter: blur(12px); z-index: 10; }
.back-btn { font-size: 20px; margin-right: 32px; cursor: pointer; padding: 8px; border-radius: 50%; color: var(--text-primary); }
.back-btn:hover { background: var(--bg-hover); }
.title { font-size: 20px; font-weight: bold; color: var(--text-primary); }
.tweet-detail { padding: 16px; border-bottom: 1px solid var(--border-color); }
.user-row { display: flex; align-items: center; }
.avatar { width: 48px; height: 48px; border-radius: 50%; cursor: pointer; }
.user-info { flex: 1; margin-left: 12px; }
.name { font-size: 15px; font-weight: bold; display: block; color: var(--text-primary); }
.handle { font-size: 15px; color: var(--text-secondary); }
.more { color: var(--text-secondary); padding: 8px; cursor: pointer; }
.content { font-size: 23px; line-height: 1.3; margin: 16px 0; display: block; white-space: pre-wrap; color: var(--text-primary); }
.images { border-radius: 16px; overflow: hidden; margin-bottom: 16px; }
.tweet-img { width: 100%; max-height: 500px; object-fit: cover; }
.time { color: var(--text-secondary); font-size: 15px; display: block; padding: 16px 0; border-bottom: 1px solid var(--border-color); }
.stats { display: flex; flex-wrap: wrap; gap: 20px; padding: 16px 0; border-bottom: 1px solid var(--border-color); font-size: 15px; color: var(--text-secondary); }
.num { font-weight: bold; color: var(--text-primary); }
.actions { display: flex; justify-content: space-around; padding: 12px 0; }
.action { font-size: 22px; padding: 8px; cursor: pointer; border-radius: 50%; color: var(--text-secondary); }
.action:hover { background: var(--accent-primary-alpha); }
.action.liked { color: #f91880; }
.action.retweeted { color: #00ba7c; }
.reply-box { display: flex; align-items: center; padding: 16px; border-bottom: 1px solid var(--border-color); }
.reply-avatar { width: 40px; height: 40px; border-radius: 50%; }
.reply-input { flex: 1; background: transparent; border: none; color: var(--text-primary); font-size: 20px; margin-left: 12px; }
.reply-btn { background: var(--accent-primary); color: #fff; border: none; border-radius: 9999px; padding: 8px 16px; font-size: 15px; font-weight: bold; }
.reply-btn:disabled { opacity: 0.5; }
.comment-item { display: flex; padding: 16px; border-bottom: 1px solid var(--border-color); }
.comment-avatar { width: 40px; height: 40px; border-radius: 50%; cursor: pointer; }
.comment-content { flex: 1; margin-left: 12px; }
.comment-header { display: flex; align-items: center; gap: 4px; }
.comment-name { font-weight: bold; font-size: 15px; color: var(--text-primary); }
.comment-handle, .comment-time { color: var(--text-secondary); font-size: 15px; }
.reply-to { color: var(--text-secondary); font-size: 15px; display: block; }
.mention { color: var(--accent-primary); }
.comment-text { font-size: 15px; display: block; margin-top: 4px; color: var(--text-primary); }
.comment-actions { display: flex; gap: 48px; margin-top: 12px; color: var(--text-secondary); font-size: 13px; }
.comment-actions .action { display: flex; align-items: center; gap: 8px; font-size: 15px; }
.empty { text-align: center; padding: 32px; color: var(--text-secondary); }
@media (max-width: 768px) { .main-content { max-width: 100%; border: none; } }
</style>
