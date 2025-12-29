<template>
  <view class="privacy-page">
    <view class="header">
      <text class="back" @click="goBack">←</text>
      <text class="title">隐私政策</text>
      <view class="placeholder"></view>
    </view>
    <scroll-view scroll-y class="content">
      <text class="section-title">1. 信息收集</text>
      <text class="section-text">我们收集您提供的账号信息（用户名、邮箱）和使用数据（发布内容、互动记录）。</text>
      
      <text class="section-title">2. 信息使用</text>
      <text class="section-text">收集的信息用于提供服务、改善体验、发送通知。我们不会出售您的个人信息。</text>
      
      <text class="section-title">3. 信息共享</text>
      <text class="section-text">除法律要求外，我们不会与第三方共享您的个人信息。</text>
      
      <text class="section-title">4. 数据安全</text>
      <text class="section-text">我们采取合理措施保护您的数据安全，包括加密存储密码。</text>
      
      <text class="section-title">5. 您的权利</text>
      <text class="section-text">您可以随时访问、修改或删除您的个人信息。如需帮助，请联系我们。</text>
      
      <text class="section-title">6. Cookie</text>
      <text class="section-text">我们使用 Cookie 和类似技术来保持登录状态和改善服务。</text>
      
      <text class="update-time">最后更新：2024年12月</text>
    </scroll-view>
  </view>
</template>

<script setup>
const goBack = () => uni.navigateBack()
</script>

<style scoped>
.privacy-page {
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
