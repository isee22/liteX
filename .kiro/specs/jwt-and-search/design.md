# Design Document

## Overview

本设计文档描述 JWT 认证中间件和搜索功能的技术实现方案。JWT 中间件采用 LiteJava 的洋葱模型中间件机制，搜索功能通过 MyBatis 的 LIKE 查询实现。

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Request                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      AuthMiddleware                          │
│  - 检查 Authorization header                                 │
│  - 验证 JWT token                                            │
│  - 提取 userid 存入 Context                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Route Handler                            │
│  - SearchController.searchTweets()                          │
│  - SearchController.searchUsers()                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     SearchService                            │
│  - searchTweets(keyword, userid, page, size)                │
│  - searchUsers(keyword, userid, page, size)                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   TweetMapper / UserMapper                   │
│  - searchByContent(keyword, offset, limit)                  │
│  - searchByNameOrNickname(keyword, offset, limit)           │
└─────────────────────────────────────────────────────────────┘
```

## Components and Interfaces

### AuthMiddleware

```java
public class AuthMiddleware extends MiddlewarePlugin {
    public List<String> excludePaths;  // 排除的路径前缀，如 "/api/auth"
    
    public void handle(Context ctx, Handler next);
}
```

### SearchController

```java
public class SearchController {
    public Routes routes();
    void searchTweets(Context ctx);  // GET /api/search/tweets?q=xxx&page=1&size=20
    void searchUsers(Context ctx);   // GET /api/search/users?q=xxx&page=1&size=20
}
```

### SearchService

```java
public class SearchService {
    public Map<String, Object> searchTweets(String keyword, long userid, int page, int size);
    public Map<String, Object> searchUsers(String keyword, long userid, int page, int size);
}
```

### Mapper Extensions

```java
// TweetMapper 新增
@Select("SELECT * FROM tweet WHERE content LIKE CONCAT('%', #{keyword}, '%') ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
List<Tweet> searchByContent(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

// UserMapper 新增
@Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{limit}")
List<User> searchByNameOrNickname(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
```

## Data Models

无需新增 Entity，复用现有的 Tweet 和 User 实体。

搜索结果使用 `Map<String, Object>` 返回：

```java
// Tweet 搜索结果
{
    "list": [
        {
            "tweet": Tweet,
            "user": User,
            "liked": boolean
        }
    ]
}

// User 搜索结果
{
    "list": [
        {
            "user": User,
            "followed": boolean
        }
    ]
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Invalid token rejection

*For any* request with missing, malformed, or expired JWT token, the AuthMiddleware should return 401 status code.

**Validates: Requirements 1.1, 1.2**

### Property 2: Valid token extraction

*For any* valid JWT token containing a userid, the AuthMiddleware should extract and store the same userid in Context.

**Validates: Requirements 1.3**

### Property 3: Tweet search content matching

*For any* non-empty search query and any tweet in the search results, the tweet content should contain the search keyword (case-insensitive).

**Validates: Requirements 2.1**

### Property 4: Tweet search result completeness

*For any* tweet search result, the result should include the tweet object, associated user object, and liked boolean status.

**Validates: Requirements 2.2**

### Property 5: Search result ordering

*For any* tweet search results list with more than one item, each item's createdat timestamp should be greater than or equal to the next item's createdat timestamp.

**Validates: Requirements 2.3**

### Property 6: User search name matching

*For any* non-empty search query and any user in the search results, the user's username or nickname should contain the search keyword (case-insensitive).

**Validates: Requirements 3.1**

### Property 7: User search result completeness

*For any* user search result, the result should include the user object and followed boolean status.

**Validates: Requirements 3.2**

### Property 8: Pagination size constraint

*For any* search request with size parameter N, the returned results list should contain at most N items.

**Validates: Requirements 4.1**

## Error Handling

| 场景 | HTTP Status | 错误信息 |
|------|-------------|----------|
| 缺少 Authorization header | 401 | "未登录" |
| Token 格式错误 | 401 | "无效的token" |
| Token 已过期 | 401 | "token已过期" |
| 搜索关键词为空 | 200 | 返回空列表 |

---

## Phase 2 Design

### Components and Interfaces (Phase 2)

#### UserController Extensions

```java
// 新增路由
.get("/api/user/:id/followers", this::getFollowers)
.get("/api/user/:id/following", this::getFollowing)
```

#### UserService Extensions

```java
public Map<String, Object> getFollowers(long userid, long currentUserid, int page, int size);
public Map<String, Object> getFollowing(long userid, long currentUserid, int page, int size);
```

#### UploadController

```java
public class UploadController {
    public Routes routes();
    void uploadImage(Context ctx);  // POST /api/upload/image
}
```

#### UploadService

```java
public class UploadService {
    public String uploadImage(byte[] data, String filename);
    public boolean validateFileType(String filename);  // jpg, png, gif, webp
    public boolean validateFileSize(long size);  // max 5MB
}
```

#### TweetService Extensions (Hashtag & Mention)

```java
public List<String> parseHashtags(String content);   // 解析 #hashtag
public List<String> parseMentions(String content);   // 解析 @username
```

### Data Models (Phase 2)

#### Bookmark Entity (新增)

```java
@Entity
@Table(name = "bookmark")
public class Bookmark {
    public Long id;
    public Long userid;      // 分片键
    public Long tweetid;
    public LocalDateTime createdat;
}
```

### Correctness Properties (Phase 2)

### Property 9: Follow list result completeness

*For any* user in a followers or following list result, the result should include the user object and followed boolean status indicating if the current user follows them.

**Validates: Requirements 5.2, 6.2**

### Property 10: File type validation

*For any* uploaded file with extension not in (jpg, jpeg, png, gif, webp), the UploadService should reject the upload with an error.

**Validates: Requirements 7.2**

### Property 11: File size validation

*For any* uploaded file with size greater than 5MB, the UploadService should reject the upload with an error.

**Validates: Requirements 7.3**

### Property 12: Hashtag parsing

*For any* tweet content containing patterns matching #[a-zA-Z0-9_]+, the parseHashtags function should extract all such patterns.

**Validates: Requirements 8.1**

### Property 13: Mention parsing

*For any* tweet content containing patterns matching @[a-zA-Z0-9_]+, the parseMentions function should extract all such patterns.

**Validates: Requirements 9.1**

### Property 14: Mention notification creation

*For any* valid @username mention in a tweet where the username exists, a notification should be created for that user.

**Validates: Requirements 9.2**

## Error Handling (Phase 2)

| 场景 | HTTP Status | 错误信息 |
|------|-------------|----------|
| 文件类型不支持 | 400 | "不支持的文件类型" |
| 文件过大 | 400 | "文件大小超过限制" |
