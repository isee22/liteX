# Requirements Document

## Introduction

本文档定义了 LiteX Twitter Clone 项目的功能需求。LiteX 是一个基于 LiteJava 框架的 Twitter 克隆应用，目标是实现 Twitter 的核心功能。

## Twitter 功能对比

### 已实现功能 ✅

| 功能模块 | 功能点 | API |
|---------|--------|-----|
| **认证** | 登录 | POST /api/auth/login |
| | 注册 | POST /api/auth/register |
| **推文** | 发布推文 | POST /api/tweets |
| | 删除推文 | DELETE /api/tweets/:id |
| | 获取推文详情 | GET /api/tweets/:id |
| | 时间线 | GET /api/tweets/timeline |
| | 点赞/取消点赞 | POST /api/tweets/:id/like |
| | 转推 | POST /api/tweets/:id/retweet |
| | 评论列表 | GET /api/tweets/:id/comments |
| | 发表评论 | POST /api/tweets/:id/comments |
| **用户** | 获取个人信息 | GET /api/user/me |
| | 获取用户信息 | GET /api/user/:id |
| | 用户推文列表 | GET /api/user/:id/tweets |
| | 用户点赞列表 | GET /api/user/:id/likes |
| | 关注/取关 | POST /api/user/:id/follow |
| | 编辑资料 | PUT /api/user/profile |
| **私信** | 会话列表 | GET /api/messages/chats |
| | 聊天记录 | GET /api/messages/chat/:userId |
| | 发送私信 | POST /api/messages/send |
| **通知** | 通知列表 | GET /api/notifications |
| | 标记已读 | POST /api/notifications/read |
| **热门** | 热门话题 | GET /api/trends |

### 未实现功能 ❌

| 功能模块 | 功能点 | 优先级 | 说明 |
|---------|--------|--------|------|
| **安全** | JWT 中间件 | P0 | 当前无路由保护，任何请求都能访问 |
| **搜索** | 搜索推文 | P0 | 按关键词搜索推文内容 |
| | 搜索用户 | P0 | 按用户名/昵称搜索用户 |
| | 搜索话题 | P1 | 按 #hashtag 搜索 |
| **推文增强** | Hashtag 解析 | P1 | 解析 #话题 并关联到 Trend |
| | @提及 | P1 | 解析 @用户 并发送通知 |
| | 引用推文 | P2 | 带评论的转推 |
| | 投票 | P3 | 推文内嵌投票功能 |
| **用户** | 粉丝列表 | P1 | GET /api/user/:id/followers |
| | 关注列表 | P1 | GET /api/user/:id/following |
| | 推荐关注 | P2 | 基于关系推荐用户 |
| **媒体** | 图片上传 | P1 | 上传图片到服务器/OSS |
| | 视频上传 | P3 | 上传视频 |
| **收藏** | 收藏推文 | P2 | 书签功能 |
| | 收藏列表 | P2 | 获取收藏的推文 |
| **实时** | WebSocket 通知 | P2 | 实时推送通知 |
| | WebSocket 私信 | P2 | 实时推送私信 |
| **其他** | 屏蔽用户 | P3 | 屏蔽某用户的推文 |
| | 举报 | P3 | 举报推文/用户 |

## Glossary

- **LiteX**: 基于 LiteJava 框架的 Twitter 克隆应用
- **JWT**: JSON Web Token，用于用户身份验证的令牌
- **AuthMiddleware**: JWT 认证中间件，验证请求中的 token
- **SearchService**: 搜索服务，处理用户和推文的搜索逻辑
- **Timeline**: 推文时间线，按时间倒序排列的推文列表
- **Hashtag**: 话题标签，以 # 开头的关键词
- **Mention**: @提及，以 @ 开头的用户名引用

## Requirements

### Requirement 1

**User Story:** As a developer, I want a JWT authentication middleware, so that I can protect API routes that require login.

#### Acceptance Criteria

1. WHEN a request arrives at a protected route without Authorization header THEN the AuthMiddleware SHALL return 401 status with error message
2. WHEN a request arrives with invalid or expired JWT token THEN the AuthMiddleware SHALL return 401 status with error message
3. WHEN a request arrives with valid JWT token THEN the AuthMiddleware SHALL extract userid and store it in Context for downstream handlers
4. WHEN registering routes THEN the Application SHALL apply AuthMiddleware to all routes except /api/auth/* paths

### Requirement 2

**User Story:** As a user, I want to search for tweets by keyword, so that I can find content I'm interested in.

#### Acceptance Criteria

1. WHEN a user submits a search query THEN the SearchService SHALL return tweets containing the keyword in content field
2. WHEN search results are returned THEN the SearchService SHALL include user information and like status for each tweet
3. WHEN search results are returned THEN the SearchService SHALL order results by createdat descending
4. WHEN search query is empty or whitespace THEN the SearchService SHALL return empty list without error

### Requirement 3

**User Story:** As a user, I want to search for other users by username or nickname, so that I can find and follow people.

#### Acceptance Criteria

1. WHEN a user searches for users THEN the SearchService SHALL return users matching username or nickname
2. WHEN user search results are returned THEN the SearchService SHALL include follow status for current user
3. WHEN user search query is empty or whitespace THEN the SearchService SHALL return empty list without error

### Requirement 4

**User Story:** As a user, I want search results to be paginated, so that I can browse through large result sets efficiently.

#### Acceptance Criteria

1. WHEN a search request includes page and size parameters THEN the SearchService SHALL return paginated results
2. WHEN page parameter is not provided THEN the SearchService SHALL default to page 1
3. WHEN size parameter is not provided THEN the SearchService SHALL default to 20 items per page

---

## Phase 2 Requirements (Future)

### Requirement 5

**User Story:** As a user, I want to see a user's followers list, so that I can discover who follows them.

#### Acceptance Criteria

1. WHEN a user views another user's profile THEN the UserService SHALL provide an API to list followers
2. WHEN followers list is returned THEN the UserService SHALL include follow status for current user
3. WHEN followers list is returned THEN the UserService SHALL support pagination

### Requirement 6

**User Story:** As a user, I want to see a user's following list, so that I can discover who they follow.

#### Acceptance Criteria

1. WHEN a user views another user's profile THEN the UserService SHALL provide an API to list following users
2. WHEN following list is returned THEN the UserService SHALL include follow status for current user
3. WHEN following list is returned THEN the UserService SHALL support pagination

### Requirement 7

**User Story:** As a user, I want to upload images for my tweets, so that I can share visual content.

#### Acceptance Criteria

1. WHEN a user uploads an image THEN the UploadService SHALL store the file and return a URL
2. WHEN an image is uploaded THEN the UploadService SHALL validate file type (jpg, png, gif, webp)
3. WHEN an image is uploaded THEN the UploadService SHALL validate file size (max 5MB)
4. WHEN an invalid file is uploaded THEN the UploadService SHALL return error with reason

### Requirement 8

**User Story:** As a user, I want hashtags in tweets to be clickable, so that I can discover related content.

#### Acceptance Criteria

1. WHEN a tweet is created with #hashtag THEN the TweetService SHALL parse and extract hashtags
2. WHEN hashtags are extracted THEN the TweetService SHALL update trend counts
3. WHEN a user clicks a hashtag THEN the SearchService SHALL return tweets containing that hashtag

### Requirement 9

**User Story:** As a user, I want to @mention other users in tweets, so that I can notify them.

#### Acceptance Criteria

1. WHEN a tweet is created with @username THEN the TweetService SHALL parse and extract mentions
2. WHEN mentions are extracted THEN the NotificationService SHALL create notifications for mentioned users
3. WHEN a user clicks a mention THEN the Application SHALL navigate to that user's profile

---

## Phase 3 Requirements (Future)

### Requirement 10

**User Story:** As a user, I want to receive real-time notifications, so that I can stay updated without refreshing.

#### Acceptance Criteria

1. WHEN a user connects to the notification WebSocket THEN the Server SHALL maintain the connection
2. WHEN a new notification is created THEN the Server SHALL push it to the connected user
3. WHEN the connection is lost THEN the Client SHALL attempt to reconnect

### Requirement 11

**User Story:** As a user, I want to bookmark tweets, so that I can save them for later.

#### Acceptance Criteria

1. WHEN a user bookmarks a tweet THEN the BookmarkService SHALL save the bookmark
2. WHEN a user removes a bookmark THEN the BookmarkService SHALL delete the bookmark
3. WHEN a user views bookmarks THEN the BookmarkService SHALL return paginated list
