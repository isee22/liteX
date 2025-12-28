<template>
  <view class="terms-page">
    <view class="header">
      <text class="back" @click="goBack">←</text>
      <text class="title">服务条款</text>
      <view class="placeholder"></view>
    </view>
    <scroll-view scroll-y class="content">
      <text class="section-title">1. 服务说明</text>
      <text class="section-text">LiteX 是一个社交平台，允许用户发布、分享和互动内容。使用本服务即表示您同意遵守以下条款。</text>
      
      <text class="section-title">2. 用户行为规范</text>
      <text class="section-text">用户不得发布违法、侵权、虚假或有害内容。我们保留删除违规内容和封禁账号的权利。</text>
      
      <text class="section-title">3. 知识产权</text>
      <text class="section-text">用户发布的内容归用户所有，但授予平台展示和传播的权利。</text>
      
      <text class="section-title">4. 免责声明</text>
      <text class="section-text">平台不对用户发布的内容承担责任，不保证服务不中断。</text>
      
      <text class="section-title">5. 条款修改</text>
      <text class="section-text">我们可能随时修改服务条款，修改后继续使用即表示接受新条款。</text>
      
      <text class="update-time">最后更新：2024年12月</text>
    </scroll-view>
  </view>
</template>

<script setup>
const goBack = () => uni.navigateBack()
</script>

<style scoped>
.terms-page {
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 53px;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  background: var(--bg-modal);
  backdrop-filter: blur(12px);
}
.back { font-size: 24px; padding: 8px; cursor: pointer; }
.title { font-size: 18px; font-weight: bold; }
.placeholder { width: 40px; }
.content { padding: 24px 16px; height: calc(100vh - 53px); }
.section-title {
  font-size: 17px;
  font-weight: bold;
  display: block;
  margin: 24px 0 12px;
}
.section-title:first-child { margin-top: 0; }
.section-text {
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-secondary);
  display: block;
}
.update-time {
  display: block;
  margin-top: 32px;
  color: var(--text-secondary);
  font-size: 13px;
}
</style>
