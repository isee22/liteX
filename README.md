# LiteX

仿推特社交应用，支持多平台。

## 技术栈

### 客户端
- UniApp (Vue3)
- Pinia 状态管理
- 支持平台：iOS、Android、H5、微信小程序

### 服务端
- LiteJava 框架
- Hibernate + MyBatis 双 ORM
- JWT 认证
- BCrypt 密码加密

## 项目结构

```
liteX/
├── client/                     # 客户端
│   ├── litex-client/           # UniApp 前端代码
│   │   ├── src/
│   │   │   ├── pages/          # 页面
│   │   │   ├── components/     # 组件
│   │   │   ├── stores/         # Pinia 状态
│   │   │   └── utils/          # 工具函数
│   │   └── package.json
│   ├── doc/                    # 前端文档
│   └── FEATURES.md             # 客户端功能文档
│
├── server/                     # 服务端
│   ├── litex-server/           # Java 后端代码
│   │   ├── src/main/java/litex/
│   │   │   ├── controller/     # 控制器
│   │   │   ├── service/        # 服务层
│   │   │   ├── entity/         # 实体类
│   │   │   └── util/           # 工具类
│   │   ├── static/             # 静态文件
│   │   ├── uploads/            # 上传文件
│   │   └── pom.xml
│   ├── logs/                   # 服务日志
│   ├── sql/                    # SQL 脚本
│   └── FEATURES.md             # 服务端功能文档
│
└── README.md
```

## 启动方式

### 客户端
```bash
cd client/litex-client
npm install
npm run dev:h5        # H5
npm run dev:mp-weixin # 微信小程序
```

### 服务端
```bash
cd server/litex-server
mvn clean package
java -jar target/litex-server.jar
```

## API 接口

- POST /api/auth/login - 登录
- POST /api/auth/register - 注册
- GET /api/tweets/timeline - 获取时间线
- POST /api/tweets - 发推
- POST /api/tweets/{id}/like - 点赞
- POST /api/tweets/{id}/retweet - 转推

## 文档

- [客户端功能文档](client/FEATURES.md)
- [服务端功能文档](server/FEATURES.md)
