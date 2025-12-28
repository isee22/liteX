# LiteX 功能开发文档

> 仿推特社交应用 - 基于 LiteJava + UniApp

## 进度说明

- ✅ 已完成
- 🚧 部分完成
- ❌ 未实现

---

## 一、服务端 (server)

基于 LiteJava 框架，使用 Hibernate + MyBatis 双 ORM。

### 基础架构

| 功能 | 状态 | 说明 |
|------|------|------|
| LiteJava 集成 | ✅ | 使用 LiteJava.create() 快速启动 |
| Hibernate DDL | ✅ | 自动建表 |
| MyBatis 查询 | ✅ | SQL 映射 |
| CORS 跨域 | ✅ | CorsPlugin |
| JWT 认证 | ✅ | 中间件 + 路径排除 |
| 配置文件 | ✅ | application.yml |

### 数据实体 (entity/)

| 实体 | 状态 | 说明 |
|------|------|------|
| User | ✅ | 用户 |
| Tweet | ✅ | 推文 |
| Comment | ✅ | 评论 |
| Like | ✅ | 点赞 |
| Follow | ✅ | 关注关系 |
| Notification | ✅ | 通知 |
| Message | ✅ | 私信 |
| Trend | ✅ | 热门话题 |
| Bookmark | ✅ | 书签收藏 |
| Block | ✅ | 屏蔽关系 |
| Report | ✅ | 举报记录 |
| Poll | ✅ | 投票 |
| PollOption | ✅ | 投票选项 |
| PollVote | ✅ | 投票记录 |

### API 接口 (controller/)

#### 认证模块 (AuthController)

| 接口 | 状态 | 说明 |
|------|------|------|
| POST /api/auth/login | ✅ | 登录 |
| POST /api/auth/register | ✅ | 注册 |
| POST /api/auth/logout | ❌ | 登出（可选，客户端清除 token 即可） |
| POST /api/auth/refresh | ❌ | 刷新 token |

#### 用户模块 (UserController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/user/me | ✅ | 获取当前用户 |
| GET /api/user/:id | ✅ | 获取用户信息 |
| GET /api/user/:id/tweets | ✅ | 用户推文列表 |
| GET /api/user/:id/likes | ✅ | 用户喜欢列表 |
| GET /api/user/:id/followers | ✅ | 粉丝列表 |
| GET /api/user/:id/following | ✅ | 关注列表 |
| POST /api/user/:id/follow | ✅ | 关注/取关 |
| PUT /api/user/profile | ✅ | 更新资料（昵称/简介/头像/背景图） |
| GET /api/user/recommend | ✅ | 推荐关注 |

#### 推文模块 (TweetController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/tweets/timeline | ✅ | 时间线 |
| POST /api/tweets | ✅ | 发推（支持图片、#话题、@提及） |
| GET /api/tweets/:id | ✅ | 推文详情 |
| DELETE /api/tweets/:id | ✅ | 删除推文 |
| POST /api/tweets/:id/like | ✅ | 点赞/取消 |
| POST /api/tweets/:id/retweet | ✅ | 转推 |
| GET /api/tweets/:id/comments | ✅ | 评论列表 |
| POST /api/tweets/:id/comments | ✅ | 发表评论 |
| POST /api/tweets/:id/quote | ✅ | 引用推文（带评论转推） |

#### 搜索模块 (SearchController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/search/tweets | ✅ | 搜索推文 |
| GET /api/search/users | ✅ | 搜索用户 |
| GET /api/search/hashtag/:tag | ✅ | 话题搜索 |

#### 通知模块 (NotificationController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/notifications | ✅ | 通知列表 |
| GET /api/notifications/unread | ✅ | 未读数量 |
| POST /api/notifications/read | ✅ | 标记已读 |
| DELETE /api/notifications/:id | ❌ | 删除通知 |

#### 私信模块 (MessageController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/messages/chats | ✅ | 会话列表 |
| GET /api/messages/chat/:userId | ✅ | 聊天记录 |
| GET /api/messages/unread | ✅ | 未读数量 |
| POST /api/messages/send | ✅ | 发送私信 |
| DELETE /api/messages/:id | ❌ | 删除消息 |

#### 热门话题 (TrendController)

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/trends | ✅ | 热门话题列表 |

#### 文件上传 (UploadController)

| 接口 | 状态 | 说明 |
|------|------|------|
| POST /api/upload/image | ✅ | 图片上传（jpg/png/gif/webp，最大 5MB） |
| POST /api/upload/video | ✅ | 视频上传（mp4/webm/mov/avi，最大 50MB） |

#### 书签模块 (BookmarkController) ✅

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/bookmarks | ✅ | 书签列表 |
| POST /api/tweets/:id/bookmark | ✅ | 添加/移除书签 |

#### 屏蔽模块 (BlockController) ✅

| 接口 | 状态 | 说明 |
|------|------|------|
| GET /api/blocks | ✅ | 屏蔽列表 |
| POST /api/user/:id/block | ✅ | 屏蔽用户（可设置天数） |
| DELETE /api/user/:id/block | ✅ | 取消屏蔽 |

#### 举报模块 (ReportController) ✅

| 接口 | 状态 | 说明 |
|------|------|------|
| POST /api/report/tweet/:id | ✅ | 举报推文 |
| POST /api/report/user/:id | ✅ | 举报用户 |

#### 投票模块 (PollController) ✅

| 接口 | 状态 | 说明 |
|------|------|------|
| POST /api/tweets/:id/vote | ✅ | 投票 |
| GET /api/tweets/:id/poll | ✅ | 投票结果 |

### 业务服务 (service/)

| 服务 | 状态 | 说明 |
|------|------|------|
| UserService | ✅ | 用户业务 |
| TweetService | ✅ | 推文业务（含 hashtag/mention 解析） |
| SearchService | ✅ | 搜索业务 |
| NotificationService | ✅ | 通知业务 |
| MessageService | ✅ | 私信业务 |
| TrendService | ✅ | 热门话题 |
| UploadService | ✅ | 文件上传 |
| BookmarkService | ✅ | 书签业务 |
| BlockService | ✅ | 屏蔽业务 |
| ReportService | ✅ | 举报业务 |
| PollService | ✅ | 投票业务 |

### 中间件 (middleware/)

| 中间件 | 状态 | 说明 |
|------|------|------|
| AuthMiddleware | ✅ | JWT 认证，支持路径排除 |

### 工具类 (util/)

| 工具 | 状态 | 说明 |
|------|------|------|
| JwtUtil | ✅ | JWT 生成/验证 |
| Auth | ✅ | 从 Context 获取用户 ID |

---

## 二、客户端 (client)

基于 UniApp (Vue3) + Pinia，支持多平台。

### 页面 (pages/)

| 页面 | 状态 | 说明 |
|------|------|------|
| home/index | ✅ | 首页时间线 |
| login/index | ✅ | 登录页 |
| login/register | ✅ | 注册页 |
| profile/index | ✅ | 个人主页 |
| profile/edit | ✅ | 编辑资料 |
| profile/followers | ✅ | 粉丝列表 |
| profile/following | ✅ | 关注列表 |
| tweet/compose | ✅ | 发推页 |
| tweet/detail | ✅ | 推文详情 |
| explore/index | ✅ | 发现页（热门话题） |
| notifications/index | ✅ | 通知页 |
| messages/index | ✅ | 私信列表 |
| messages/chat | ✅ | 聊天页 |
| messages/new | ✅ | 新建私信 |
| search/result | ✅ | 搜索结果 |
| bookmarks/index | ✅ | 书签列表 |
| settings/index | ✅ | 设置页 |
| settings/blocked | ✅ | 屏蔽列表 |

### 组件 (components/)

| 组件 | 状态 | 说明 |
|------|------|------|
| TweetCard | ✅ | 推文卡片 |
| UserCard | ✅ | 用户卡片 |
| ImagePreview | 🚧 | 图片预览 |
| PollCard | ✅ | 投票卡片 |

### 状态管理 (stores/)

| Store | 状态 | 说明 |
|------|------|------|
| user | ✅ | 用户状态 |
| tweet | ✅ | 推文状态 |
| notification | ✅ | 通知状态（未读数） |
| message | ✅ | 私信状态（未读数） |

### 工具 (utils/)

| 工具 | 状态 | 说明 |
|------|------|------|
| request | ✅ | HTTP 请求封装 |
| websocket | ❌ | WebSocket 封装（仅私信聊天需要） |

---

## 三、功能特性

### 核心功能（已完成）

| 功能 | 状态 | 说明 |
|------|------|------|
| 用户注册/登录 | ✅ | JWT 认证 |
| 发布推文 | ✅ | 支持图片 |
| 时间线 | ✅ | 分页加载 |
| 点赞 | ✅ | 切换点赞状态 |
| 转推 | ✅ | 转发推文 |
| 评论 | ✅ | 推文评论 |
| 关注/取关 | ✅ | 用户关系 |
| 个人主页 | ✅ | 推文/喜欢列表 |
| 编辑资料 | ✅ | 昵称/简介/头像 |
| 搜索 | ✅ | 推文/用户/话题 |
| 热门话题 | ✅ | Hashtag 统计 |
| #话题解析 | ✅ | 自动关联 Trend |
| @提及 | ✅ | 解析并通知 |
| 通知 | ✅ | 点赞/转推/关注/提及 |
| 私信 | ✅ | 一对一聊天 |
| 图片上传 | ✅ | jpg/png/gif/webp，最大 5MB |

### 待开发功能

| 功能 | 优先级 | 说明 |
|------|--------|------|
| **P1 - 高优先级** |||
| 未读数接口 | ✅ | 通知/私信未读数（客户端轮询） |
| **P2 - 中优先级** |||
| 书签收藏 | ✅ | 收藏推文到书签 |
| 引用推文 | ✅ | 带评论的转推 |
| 推荐关注 | ✅ | 基于关系推荐用户 |
| 图片预览 | P2 | 大图查看/滑动 |
| 实时私信 | P2 | WebSocket 聊天（仅私信需要） |
| **P3 - 低优先级** |||
| 屏蔽用户 | ✅ | 屏蔽 X 天，期间不可见 |
| 举报功能 | ✅ | 举报推文/用户 |
| 投票功能 | ✅ | 推文内嵌投票 |
| 视频上传 | ✅ | mp4/webm/mov/avi，最大 50MB |
| 多语言 | P3 | i18n 国际化 |

---

## 四、平台支持

| 平台 | 状态 | 说明 |
|------|------|------|
| H5 | ✅ | 浏览器 |
| 微信小程序 | 🚧 | 需适配 |
| iOS | 🚧 | 需打包 |
| Android | 🚧 | 需打包 |

---

## 五、技术栈

### 服务端
- LiteJava 框架
- Hibernate (DDL 自动建表)
- MyBatis (SQL 查询)
- JWT 认证
- BCrypt 密码加密

### 客户端
- UniApp (Vue3)
- Pinia 状态管理
- Vite 构建

---

## 六、启动方式

### 服务端
```bash
cd server
mvn clean package
java -jar target/litex-server.jar
```

### 客户端
```bash
cd client
npm install
npm run dev:h5        # H5
npm run dev:mp-weixin # 微信小程序
```

---

## 七、配置说明

### application.yml

```yaml
server:
  port: 8080

hibernate:
  url: jdbc:mysql://localhost:3306/litex
  username: root
  password: 123456

mybatis:
  url: jdbc:mysql://localhost:3306/litex
  username: root
  password: 123456

upload:
  path: uploads        # 上传目录
  maxSize: 5242880     # 最大 5MB

jwt:
  secret: your-secret-key
  expireHours: 24
```
