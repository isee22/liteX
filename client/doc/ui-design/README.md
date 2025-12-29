# Twitter Clone UI Design

静态 HTML 界面设计原型，用于参考和开发。

## 页面列表

| 文件 | 说明 |
|------|------|
| [index.html](index.html) | 首页 - 推文时间线 |
| [explore.html](explore.html) | 探索 - 热门话题 |
| [notifications.html](notifications.html) | 通知 - 点赞/转推/关注/评论 |
| [messages.html](messages.html) | 私信 - 会话列表和聊天 |
| [profile.html](profile.html) | 个人资料 - 用户主页 |
| [bookmarks.html](bookmarks.html) | 书签 - 收藏的推文 |
| [settings.html](settings.html) | 设置 - 账号/隐私/显示 |
| [tweet-detail.html](tweet-detail.html) | 推文详情 - 评论列表 |
| [compose.html](compose.html) | 发帖 - 编辑器弹窗 |
| [login.html](login.html) | 登录 |
| [register.html](register.html) | 注册 |
| [poll.html](poll.html) | 投票功能 |

## 设计规范

### 颜色

| 用途 | 颜色值 |
|------|--------|
| 背景 | `#000000` |
| 卡片背景 | `#16181c` |
| 边框 | `#2f3336` |
| 主文字 | `#e7e9ea` |
| 次要文字 | `#71767b` |
| 主色调 | `#1d9bf0` |
| 点赞 | `#f91880` |
| 转推 | `#00ba7c` |
| 警告 | `#f4212e` |

### 布局

- 最大宽度: 1280px
- 左侧导航: 275px
- 中间内容: 600px (flex: 1)
- 右侧边栏: 350px

### 响应式断点

- `1280px`: 右侧栏缩小到 290px
- `1000px`: 隐藏右侧栏
- `768px`: 左侧导航只显示图标
- `500px`: 底部导航栏

### 组件

- 圆角按钮: `border-radius: 9999px`
- 卡片圆角: `border-radius: 16px`
- 头像: 40px (列表) / 48px (详情) / 134px (个人资料)
- 毛玻璃效果: `backdrop-filter: blur(12px)`

## 使用方法

直接在浏览器中打开 HTML 文件即可预览。

```bash
# 或使用本地服务器
npx serve liteX/doc/ui-design
```
