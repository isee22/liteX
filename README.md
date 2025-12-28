# Twitter Clone

仿推特社交应用，支持多平台。

## 技术栈

### 客户端
- UniApp (Vue3)
- Pinia 状态管理
- 支持平台：iOS、Android、H5、微信小程序

### 服务端
- LiteJava 框架
- JWT 认证
- BCrypt 密码加密

## 项目结构

```
├── client/                 # UniApp 客户端
│   ├── pages/             # 页面
│   ├── components/        # 组件
│   ├── stores/            # Pinia 状态
│   └── utils/             # 工具函数
└── server/                # Java 服务端
    └── src/main/java/
        └── com/twitter/clone/
            ├── controller/  # 控制器
            ├── service/     # 服务层
            ├── entity/      # 实体类
            └── util/        # 工具类
```

## 启动方式

### 客户端
```bash
cd client
npm install
npm run dev:h5        # H5
npm run dev:mp-weixin # 微信小程序
```

### 服务端
```bash
cd server
mvn clean package
java -jar target/twitter-clone-server-1.0.0.jar
```

## API 接口

- POST /api/auth/login - 登录
- POST /api/auth/register - 注册
- GET /api/tweets/timeline - 获取时间线
- POST /api/tweets - 发推
- POST /api/tweets/{id}/like - 点赞
- POST /api/tweets/{id}/retweet - 转推
