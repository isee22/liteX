# 编码规范

## 测试规则

- 不写测试代码，不运行测试
- 不创建 test 目录和测试文件
- 专注于业务代码实现

## 代码质量

- 有问题直接解决根本原因，不要写"修复"或"确保"类的补丁代码
- 不要用 CSS hack 或 workaround，找到问题源头修正

## 配置原则

- 动态配置必须放在配置文件（application.yml），不要硬编码在代码中
- 代码只负责读取配置，不负责定义配置值

## 数据库命名

- Java 字段和数据库列名统一使用全小写，不要下划线和驼峰
- 例如：`userid`, `tweetid`, `createdat`, `fromuserid`（不是 `user_id` 或 `userId`）
- 考虑分库分表，关键表需要包含分片键字段

## Entity 设计

- Entity 只包含数据库字段，不要用 `@Transient` 挂载关联对象
- 关联数据在 Service 层用 `Map<String, Object>` 组装返回
- 保持 Entity 简单纯粹，避免 ORM 高级特性影响性能

## 分库分表设计

- 用户相关表以 `userid` 为分片键
- 推文相关表以 `tweetid` 或 `userid` 为分片键
- 私信表以 `fromuserid` 或会话 ID 为分片键
- 所有关联查询需要带上分片键
