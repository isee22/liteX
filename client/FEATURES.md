# LiteX 客户端

基于 uni-app + Vue 3 + Pinia 的 Twitter 风格社交应用。

## 功能完成进度

### ✅ 已完成

| 页面/功能 | 状态 | 说明 |
|----------|------|------|
| 登录 | ✅ | 用户名密码登录 |
| 注册 | ✅ | 基本注册功能 |
| 首页时间线 | ✅ | 推荐/关注切换、下拉加载 |
| 发帖 | ✅ | 文字发布（图片上传待验证） |
| 推文详情 | ✅ | 查看推文、评论 |
| 点赞 | ✅ | 点赞/取消点赞 |
| 转推 | ✅ | 转推/取消转推 |
| 书签 | ✅ | 添加/移除书签 |
| 个人主页 | ✅ | 查看用户信息、推文列表 |
| 关注/取关 | ✅ | 关注用户功能 |
| 通知列表 | ✅ | 查看通知 |
| 私信列表 | ✅ | 查看会话列表 |
| 热门话题 | ✅ | 发现页话题列表 |
| 搜索 | ✅ | 搜索推文/用户 |
| 主题切换 | ✅ | 深色/暗淡/浅色主题 |
| 主题色 | ✅ | 6种主题色切换 |
| 退出登录 | ✅ | 清除登录状态 |
| 忘记密码 | ✅ | 发送重置邮件（页面已实现） |
| 服务条款 | ✅ | 查看服务条款 |
| 隐私政策 | ✅ | 查看隐私政策 |
| 账号设置 | ✅ | 查看账号信息 |
| 修改密码 | ✅ | 修改登录密码 |
| 静音账号 | ✅ | 管理静音用户 |
| 字体大小调节 | ✅ | 点击滑块可调节 |

### ⚠️ 部分完成

| 页面/功能 | 状态 | 问题 |
|----------|------|------|
| 发帖弹窗 | ✅ | 已修复，可正常发帖 |
| 图片上传 | ⚠️ | 选择图片功能有，但上传到服务器待验证 |
| 编辑个人资料 | ✅ | 页面已实现 |
| 关注列表 | ✅ | 页面已实现 |
| 粉丝列表 | ✅ | 页面已实现 |
| 私信聊天 | ✅ | 页面已实现 |
| 新建私信 | ✅ | 页面已实现 |

### ❌ 未实现（有按钮但无功能）

| 页面/功能 | 位置 | 说明 |
|----------|------|------|
| Google 登录 | 登录页 | 提示"第三方登录暂未开放" |
| Apple 登录 | 登录页 | 提示"第三方登录暂未开放" |
| 安全和账号访问 | 设置页 | 提示"功能开发中" |
| 隐私和安全 | 设置页 | 提示"功能开发中" |
| 通知设置 | 设置页 | 提示"功能开发中" |
| 草稿 | 发帖页/弹窗 | 提示"功能开发中" |
| 可见性设置 | 发帖页/弹窗 | 提示"功能开发中" |
| 回复设置 | 发帖页/弹窗 | 提示"功能开发中" |
| 表情选择 | 发帖页/弹窗/私信 | 提示"功能开发中" |
| 投票 | 发帖页/弹窗 | 提示"功能开发中" |
| 日程 | 发帖页/弹窗 | 提示"功能开发中" |
| 位置 | 发帖页/弹窗 | 提示"功能开发中" |
| 拍照 | 发帖页/弹窗/私信 | 提示"功能开发中" |
| 添加推文 | 发帖页/弹窗 | 提示"功能开发中" |
| 通知设置 | 通知页 | 提示"功能开发中" |
| 探索设置 | 发现页 | 提示"功能开发中" |
| 私信设置 | 私信页 | 提示"功能开发中" |
| 会话详情 | 私信聊天页 | 提示"功能开发中" |
| 出生日期编辑 | 编辑资料页 | 提示"功能开发中" |

### 📋 待检查页面

以下页面已存在，需验证功能是否正常：

- ✅ `/pages/profile/edit` - 编辑个人资料
- ✅ `/pages/profile/following` - 关注列表
- ✅ `/pages/profile/followers` - 粉丝列表
- ✅ `/pages/messages/chat` - 私信聊天
- ✅ `/pages/messages/new` - 新建私信
- ✅ `/pages/settings/blocked` - 已屏蔽账号
- ✅ `/pages/search/result` - 搜索结果

---

## 技术栈

- uni-app (H5 + 小程序)
- Vue 3 Composition API
- Pinia 状态管理
- CSS 变量主题系统

## 项目结构

```
src/
├── components/          # 公共组件
│   ├── ComposeModal.vue    # 发帖弹窗
│   ├── LeftSidebar.vue     # 左侧导航
│   ├── RightSidebar.vue    # 右侧推荐
│   ├── TweetItem.vue       # 推文卡片
│   ├── LoginModal.vue      # 登录弹窗
│   └── UserCard.vue        # 用户卡片
├── pages/               # 页面
│   ├── home/               # 首页
│   ├── explore/            # 发现
│   ├── notifications/      # 通知
│   ├── messages/           # 私信
│   ├── profile/            # 个人主页
│   ├── login/              # 登录/注册
│   ├── settings/           # 设置
│   └── tweet/              # 推文详情
├── stores/              # Pinia 状态
│   ├── user.js             # 用户状态
│   ├── tweet.js            # 推文状态
│   ├── compose.js          # 发帖弹窗状态
│   ├── theme.js            # 主题状态
│   ├── notification.js     # 通知状态
│   └── message.js          # 私信状态
├── styles/              # 样式
│   ├── themes.css          # 主题变量 (dark/dim/light)
│   ├── base.css            # 基础组件样式
│   └── tailwind.css        # 工具类
├── utils/
│   └── request.js          # 请求封装
├── App.vue              # 根组件
└── main.js              # 入口
```

## 开发规范

### 请求处理

所有 API 请求通过 `request.js` 统一处理：

```javascript
import { get, post, put, del } from '@/utils/request'

// 成功返回 { code: 0, data: {...}, msg: 'success' }
// 失败返回 { code: -1, data: null, msg: '错误信息' }
const res = await get('/tweets')
const list = res.data.list  // 注意：res 是整个响应，res.data 才是业务数据
```

**错误处理**：`request.js` 会自动显示错误 toast，页面 catch 里不需要再显示：

```javascript
// ✅ 正确
try {
  await post('/tweets', { content })
  uni.showToast({ title: '发布成功', icon: 'success' })
} catch (e) {
  // request.js 已显示错误提示，这里不需要处理
}

// ❌ 错误 - 会导致重复提示
catch (e) {
  uni.showToast({ title: e.msg || '失败', icon: 'none' })  // 多余
}
```

### 样式规范

使用 `base.css` 中的统一样式类：

```html
<!-- 按钮 -->
<button class="btn btn-primary">主要按钮</button>
<button class="btn btn-secondary">次要按钮</button>
<button class="btn btn-outline btn-sm">小按钮</button>

<!-- 输入框 -->
<view class="input-box">
  <input placeholder="搜索" />
</view>

<!-- 头像 -->
<image class="avatar avatar-md" :src="user.avatar" />

<!-- 标签页 -->
<view class="tabs">
  <view class="tab active">推荐</view>
  <view class="tab">关注</view>
</view>
```

### 主题系统

通过 CSS 变量实现，支持 dark/dim/light 三种主题：

```css
/* 使用主题变量 */
.my-component {
  background: var(--bg-primary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

/* 主要变量 */
--bg-primary      /* 主背景 */
--bg-secondary    /* 次背景 */
--bg-hover        /* 悬停背景 */
--text-primary    /* 主文字 */
--text-secondary  /* 次文字 */
--accent-primary  /* 主题色 (蓝色) */
--border-color    /* 边框色 */
```

### Store 使用

```javascript
import { useUserStore } from '@/stores/user'
import { useComposeStore } from '@/stores/compose'

const userStore = useUserStore()
const composeStore = useComposeStore()

// 检查登录
if (!userStore.isLoggedIn) {
  userStore.openLoginModal()
  return
}

// 打开发帖弹窗
composeStore.open()
```

### 页面参数

uni-app 页面参数必须用 `onLoad` 获取：

```javascript
import { onLoad } from '@dcloudio/uni-app'

onLoad((options) => {
  const id = options?.id
  loadData(id)
})
```

### 导航方式

```javascript
// tabBar 页面
uni.switchTab({ url: '/pages/home/index' })

// 普通页面
uni.navigateTo({ url: '/pages/tweet/detail?id=123' })

// 替换页面
uni.redirectTo({ url: '/pages/login/index' })

// 返回
uni.navigateBack()
```

## 响应式布局

- 电脑端 (>500px)：三栏布局，隐藏 tabBar
- 手机端 (≤500px)：单栏布局，显示 tabBar

```javascript
const isMobile = ref(false)
const checkMobile = () => {
  const info = uni.getSystemInfoSync()
  isMobile.value = info.windowWidth <= 500
}
```

## 启动命令

```bash
cd client/litex-client

# 安装依赖
npm install

# H5 开发
npm run dev:h5

# 微信小程序
npm run dev:mp-weixin

# 构建 H5
npm run build:h5
```

## 后端 API

默认连接 `http://localhost:8080/api`，在 `utils/request.js` 中配置。

响应格式：
- `code: 0` - 成功
- `code: -1` - 业务错误
- `code: 401` - 未登录（自动跳转登录页）
