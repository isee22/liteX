-- 初始化测试数据
-- 密码都是 123456 (BCrypt 加密)

-- 清空现有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE notification;
TRUNCATE TABLE message;
TRUNCATE TABLE comment;
TRUNCATE TABLE tweetlike;
TRUNCATE TABLE follow;
TRUNCATE TABLE tweet;
TRUNCATE TABLE trend;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 用户数据
INSERT INTO user (id, username, password, nickname, bio, avatar, createdat) VALUES
(1, 'alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS3/r/7HfFJFgJ7tW', 'Alice', '热爱生活，热爱编程', NULL, NOW()),
(2, 'bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS3/r/7HfFJFgJ7tW', 'Bob', '全栈开发者', NULL, NOW()),
(3, 'charlie', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS3/r/7HfFJFgJ7tW', 'Charlie', '设计师 | UI/UX', NULL, NOW()),
(4, 'david', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS3/r/7HfFJFgJ7tW', 'David', '产品经理', NULL, NOW()),
(5, 'eve', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS3/r/7HfFJFgJ7tW', 'Eve', '数据分析师', NULL, NOW());

-- 关注关系
INSERT INTO follow (id, userid, targetid, createdat) VALUES
(1, 1, 2, NOW()),
(2, 1, 3, NOW()),
(3, 2, 1, NOW()),
(4, 2, 3, NOW()),
(5, 3, 1, NOW()),
(6, 4, 1, NOW()),
(7, 4, 2, NOW()),
(8, 5, 1, NOW());

-- 推文数据
INSERT INTO tweet (id, userid, content, createdat) VALUES
(1, 1, '今天天气真好！☀️ #生活', NOW() - INTERVAL 2 HOUR),
(2, 2, '刚完成一个新项目，感觉很有成就感 💪 #编程 #开发', NOW() - INTERVAL 1 HOUR),
(3, 3, '分享一个设计技巧：留白是最好的设计元素 #设计 #UI', NOW() - INTERVAL 30 MINUTE),
(4, 1, '学习 Java 的第 100 天，终于理解了多线程 🎉 #Java #学习', NOW() - INTERVAL 20 MINUTE),
(5, 4, '产品思维：用户需求 > 功能堆砌 #产品', NOW() - INTERVAL 10 MINUTE),
(6, 2, '@alice 你的代码写得真棒！', NOW() - INTERVAL 5 MINUTE),
(7, 5, '数据不会说谎，但解读数据的人会 📊 #数据分析', NOW());

-- 点赞数据
INSERT INTO tweetlike (id, userid, tweetid, createdat) VALUES
(1, 2, 1, NOW()),
(2, 3, 1, NOW()),
(3, 1, 2, NOW()),
(4, 3, 2, NOW()),
(5, 1, 3, NOW()),
(6, 2, 4, NOW()),
(7, 4, 4, NOW()),
(8, 5, 4, NOW());

-- 评论数据
INSERT INTO comment (id, userid, tweetid, content, createdat) VALUES
(1, 2, 1, '确实！今天阳光明媚', NOW()),
(2, 3, 2, '恭喜！什么项目？', NOW()),
(3, 1, 3, '学到了，谢谢分享', NOW()),
(4, 4, 4, '坚持就是胜利！', NOW());

-- 热门话题
INSERT INTO trend (id, tag, count, updatedat) VALUES
(1, '生活', 10, NOW()),
(2, '编程', 25, NOW()),
(3, '设计', 15, NOW()),
(4, 'Java', 30, NOW()),
(5, '产品', 12, NOW()),
(6, '数据分析', 8, NOW()),
(7, 'UI', 20, NOW()),
(8, '学习', 18, NOW());

-- 通知数据
INSERT INTO notification (id, userid, fromuserid, type, tweetid, isread, createdat) VALUES
(1, 1, 2, 'like', 1, 0, NOW()),
(2, 1, 3, 'like', 1, 0, NOW()),
(3, 2, 1, 'like', 2, 0, NOW()),
(4, 1, 2, 'follow', NULL, 0, NOW()),
(5, 1, 6, 'mention', 6, 0, NOW());

-- 私信数据
INSERT INTO message (id, fromuserid, touserid, content, isread, createdat) VALUES
(1, 2, 1, '嗨，最近怎么样？', 0, NOW() - INTERVAL 1 HOUR),
(2, 1, 2, '挺好的，在学习新技术', 1, NOW() - INTERVAL 50 MINUTE),
(3, 2, 1, '什么技术？分享一下', 0, NOW() - INTERVAL 40 MINUTE),
(4, 3, 1, '你的头像设计得不错', 0, NOW());
