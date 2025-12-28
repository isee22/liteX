package litex.plugin;

import litejava.Plugin;
import litex.entity.*;
import litex.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 机器人插件 - 自动生成测试数据
 * 
 * 通过 Service 层操作数据库，不直接访问 DB/Mapper
 */
public class BotPlugin extends Plugin {
    
    private static final Logger log = LoggerFactory.getLogger(BotPlugin.class);
    
    // 配置字段
    public boolean enabled = false;
    public int intervalSeconds = 60;
    public int weightRegister = 10;
    public int weightTweet = 30;
    public int weightTweetWithHashtag = 20;
    public int weightLike = 25;
    public int weightRetweet = 10;
    public int weightFollow = 15;
    public int weightMessage = 5;
    
    // Service 依赖
    public UserService userService;
    public TweetService tweetService;
    public MessageService messageService;
    public TrendService trendService;
    
    private ScheduledExecutorService scheduler;
    private final Random random = new Random();
    
    // 随机内容素材
    private static final String[] CONTENTS = {
        "今天天气真好！",
        "刚刚看了一部很棒的电影",
        "分享一个有趣的想法",
        "早安，新的一天开始了",
        "晚安，好梦",
        "周末愉快！",
        "工作中，勿扰",
        "学习新技术中...",
        "美食分享时间",
        "运动打卡",
        "读书笔记",
        "旅行中",
        "咖啡时间",
        "音乐推荐",
        "今日心情不错",
        "加油！",
        "生活需要仪式感",
        "简单的快乐",
        "记录美好瞬间",
        "感恩每一天"
    };
    
    private static final String[] HASHTAGS = {
        "日常", "生活", "分享", "打卡", "记录",
        "美食", "旅行", "读书", "电影", "音乐",
        "健身", "学习", "工作", "周末", "早安",
        "晚安", "心情", "随拍", "好物", "推荐"
    };
    
    private static final String[] NICKNAMES = {
        "小明", "小红", "小刚", "小美", "阿强",
        "小李", "老王", "小张", "阿花", "大壮",
        "小雪", "小云", "阿杰", "小敏", "大海"
    };
    
    @Override
    public void config() {
        enabled = app.conf.getBool("bot", "enabled", enabled);
        intervalSeconds = app.conf.getInt("bot", "intervalSeconds", intervalSeconds);
        weightRegister = app.conf.getInt("bot", "weightRegister", weightRegister);
        weightTweet = app.conf.getInt("bot", "weightTweet", weightTweet);
        weightTweetWithHashtag = app.conf.getInt("bot", "weightTweetWithHashtag", weightTweetWithHashtag);
        weightLike = app.conf.getInt("bot", "weightLike", weightLike);
        weightRetweet = app.conf.getInt("bot", "weightRetweet", weightRetweet);
        weightFollow = app.conf.getInt("bot", "weightFollow", weightFollow);
        weightMessage = app.conf.getInt("bot", "weightMessage", weightMessage);
        
        if (enabled) {
            startBot();
        }
    }
    
    private void startBot() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "bot-worker");
            t.setDaemon(true);
            return t;
        });
        
        scheduler.scheduleAtFixedRate(this::doAction, 5, intervalSeconds, TimeUnit.SECONDS);
        log.info("机器人已启动，间隔 {} 秒", intervalSeconds);
    }
    
    private void doAction() {
        try {
            int totalWeight = weightRegister + weightTweet + weightTweetWithHashtag 
                + weightLike + weightRetweet + weightFollow + weightMessage;
            int r = random.nextInt(totalWeight);
            
            int cumulative = 0;
            if ((cumulative += weightRegister) > r) {
                registerUser();
            } else if ((cumulative += weightTweet) > r) {
                postTweet(false);
            } else if ((cumulative += weightTweetWithHashtag) > r) {
                postTweet(true);
            } else if ((cumulative += weightLike) > r) {
                likeTweet();
            } else if ((cumulative += weightRetweet) > r) {
                retweetTweet();
            } else if ((cumulative += weightFollow) > r) {
                followUser();
            } else {
                sendMessage();
            }
        } catch (Exception e) {
            log.error("机器人操作失败", e);
        }
    }

    
    private void registerUser() {
        String username = "bot_" + System.currentTimeMillis() % 100000;
        String nickname = NICKNAMES[random.nextInt(NICKNAMES.length)] + random.nextInt(1000);
        
        User user = userService.register(username, "bot123456", nickname, null);
        if (user != null) {
            log.info("[Bot] 注册用户: {} ({})", nickname, username);
        }
    }
    
    private void postTweet(boolean withHashtag) {
        User user = userService.getRandomBotUser();
        if (user == null) {
            registerUser();
            return;
        }
        
        String content = CONTENTS[random.nextInt(CONTENTS.length)];
        if (withHashtag) {
            String tag = HASHTAGS[random.nextInt(HASHTAGS.length)];
            content += " #" + tag;
        }
        
        Map<String, Object> result = tweetService.create(user.id, content, null);
        if (result != null) {
            log.info("[Bot] {} 发帖: {}", user.nickname, content);
        }
    }
    
    private void likeTweet() {
        User user = userService.getRandomBotUser();
        Tweet tweet = tweetService.getRandomTweet();
        if (user == null || tweet == null) return;
        
        tweetService.toggleLike(tweet.id, user.id);
        log.info("[Bot] {} 点赞了推文 #{}", user.nickname, tweet.id);
    }
    
    private void retweetTweet() {
        User user = userService.getRandomBotUser();
        Tweet tweet = tweetService.getRandomTweet();
        if (user == null || tweet == null) return;
        if (tweet.userid.equals(user.id)) return;
        
        tweetService.retweet(tweet.id, user.id);
        log.info("[Bot] {} 转推了 #{}", user.nickname, tweet.id);
    }
    
    private void followUser() {
        User follower = userService.getRandomBotUser();
        User target = userService.getRandomUser();
        if (follower == null || target == null) return;
        if (follower.id.equals(target.id)) return;
        
        userService.toggleFollow(follower.id, target.id);
        log.info("[Bot] {} 关注了 {}", follower.nickname, target.nickname);
    }
    
    private void sendMessage() {
        User from = userService.getRandomBotUser();
        User to = userService.getRandomUser();
        if (from == null || to == null) return;
        if (from.id.equals(to.id)) return;
        
        String content = CONTENTS[random.nextInt(CONTENTS.length)];
        messageService.send(from.id, to.id, content);
        log.info("[Bot] {} 给 {} 发私信", from.nickname, to.nickname);
    }
    
    @Override
    public void uninstall() {
        if (scheduler != null) {
            scheduler.shutdown();
            log.info("机器人已停止");
        }
    }
}
