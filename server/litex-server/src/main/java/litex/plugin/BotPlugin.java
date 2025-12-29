package litex.plugin;

import litejava.Plugin;
import litex.Service;
import litex.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 机器人插件 - 自动生成测试数据
 * 
 * 功能：
 * 1. 自动注册用户、发帖、点赞、转发、关注、私信
 * 2. 帖子可带表情、图片、视频
 * 3. 回复只带文字和表情，不带图片
 * 4. 依赖 CrawlerPlugin 获取内容素材
 * 
 * 配置：
 * bot:
 *   enabled: true
 *   intervalSeconds: 30
 *   usernamePrefix: test_,demo_,test
 *   defaultPassword: 123456
 */
public class BotPlugin extends Plugin {
    
    public static final Logger log = LoggerFactory.getLogger(BotPlugin.class);
    
    // 配置字段
    public boolean enabled = false;
    public int intervalSeconds = 60;
    public String usernamePrefix = "test_";
    public String defaultPassword = "123456";
    
    // 权重配置
    public int weightRegister = 10;
    public int weightTweet = 30;
    public int weightTweetWithMedia = 15;
    public int weightReply = 20;
    public int weightLike = 25;
    public int weightRetweet = 10;
    public int weightFollow = 15;
    public int weightMessage = 5;
    
    // 内容配置
    public int emojiChance = 30;      // 带表情概率 %
    public int imageChance = 20;      // 帖子带图片概率 %
    public int hashtagChance = 40;    // 带话题概率 %
    
    // 依赖
    public CrawlerPlugin crawler;
    
    public ScheduledExecutorService scheduler;
    public final List<String> usernamePrefixes = new CopyOnWriteArrayList<>();
    public final Random random = new Random();
    
    @Override
    public void config() {
        enabled = app.conf.getBool("bot", "enabled", enabled);
        intervalSeconds = app.conf.getInt("bot", "intervalSeconds", intervalSeconds);
        usernamePrefix = app.conf.getString("bot", "usernamePrefix", usernamePrefix);
        defaultPassword = app.conf.getString("bot", "defaultPassword", defaultPassword);
        
        weightRegister = app.conf.getInt("bot", "weightRegister", weightRegister);
        weightTweet = app.conf.getInt("bot", "weightTweet", weightTweet);
        weightTweetWithMedia = app.conf.getInt("bot", "weightTweetWithMedia", weightTweetWithMedia);
        weightReply = app.conf.getInt("bot", "weightReply", weightReply);
        weightLike = app.conf.getInt("bot", "weightLike", weightLike);
        weightRetweet = app.conf.getInt("bot", "weightRetweet", weightRetweet);
        weightFollow = app.conf.getInt("bot", "weightFollow", weightFollow);
        weightMessage = app.conf.getInt("bot", "weightMessage", weightMessage);
        
        emojiChance = app.conf.getInt("bot", "emojiChance", emojiChance);
        imageChance = app.conf.getInt("bot", "imageChance", imageChance);
        hashtagChance = app.conf.getInt("bot", "hashtagChance", hashtagChance);
        
        // 解析用户名前缀
        for (String prefix : usernamePrefix.split(",")) {
            String trimmed = prefix.trim();
            if (!trimmed.isEmpty()) {
                usernamePrefixes.add(trimmed);
            }
        }
        if (usernamePrefixes.isEmpty()) {
            usernamePrefixes.add("test_");
        }
        
        // 获取爬虫插件
        crawler = (CrawlerPlugin) app.plugins.get("CrawlerPlugin");
    }
    
    @Override
    public void onStart() {
        if (!enabled) return;
        
        if (crawler == null) {
            log.warn("BotPlugin 需要 CrawlerPlugin，请先注册 CrawlerPlugin");
            return;
        }
        
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "bot-worker");
            t.setDaemon(true);
            return t;
        });
        
        scheduler.scheduleAtFixedRate(this::doAction, 5, intervalSeconds, TimeUnit.SECONDS);
        log.info("机器人已启动，间隔 {} 秒", intervalSeconds);
    }
    
    @Override
    public void uninstall() {
        if (scheduler != null) {
            scheduler.shutdown();
            log.info("机器人已停止");
        }
    }

    
    // ==================== 动作调度 ====================
    
    public void doAction() {
        try {
            int totalWeight = weightRegister + weightTweet + weightTweetWithMedia 
                + weightReply + weightLike + weightRetweet + weightFollow + weightMessage;
            int r = random.nextInt(totalWeight);
            
            int cumulative = 0;
            if ((cumulative += weightRegister) > r) {
                registerUser();
            } else if ((cumulative += weightTweet) > r) {
                postTweet(false);
            } else if ((cumulative += weightTweetWithMedia) > r) {
                postTweet(true);
            } else if ((cumulative += weightReply) > r) {
                replyTweet();
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
    
    // ==================== 用户操作 ====================
    
    /**
     * 注册新用户
     */
    public void registerUser() {
        String prefix = usernamePrefixes.get(random.nextInt(usernamePrefixes.size()));
        String username = prefix + System.currentTimeMillis() % 100000;
        String nickname = crawler.getRandomNickname();
        
        User user = Service.user.register(username, defaultPassword, nickname, null);
        if (user != null) {
            log.info("[Bot] 注册用户: {} ({})", nickname, username);
        }
    }
    
    /**
     * 关注用户
     */
    public void followUser() {
        User follower = Service.user.getRandomBotUser();
        User target = Service.user.getRandomUser();
        if (follower == null || target == null) return;
        if (follower.id.equals(target.id)) return;
        
        Service.user.toggleFollow(follower.id, target.id);
        log.info("[Bot] {} 关注了 {}", follower.nickname, target.nickname);
    }
    
    // ==================== 帖子操作 ====================
    
    /**
     * 发帖（可带图片/视频）
     */
    public void postTweet(boolean withMedia) {
        User user = Service.user.getRandomBotUser();
        if (user == null) {
            registerUser();
            return;
        }
        
        // 构建内容
        String content = buildTweetContent();
        
        // 媒体文件
        String mediaStr = null;
        if (withMedia && random.nextInt(100) < imageChance) {
            String image = crawler.getRandomImage();
            if (image != null) {
                mediaStr = image;
            }
        }
        
        Map<String, Object> result = Service.tweet.create(user.id, content, mediaStr);
        if (result != null) {
            String preview = content.length() > 30 ? content.substring(0, 30) + "..." : content;
            String mediaInfo = mediaStr != null ? " [图片]" : "";
            log.info("[Bot] {} 发帖: {}{}", user.nickname, preview, mediaInfo);
        }
    }
    
    /**
     * 回复帖子（只带文字和表情，不带图片）
     */
    public void replyTweet() {
        User user = Service.user.getRandomBotUser();
        Tweet tweet = Service.tweet.getRandomTweet();
        if (user == null || tweet == null) return;
        
        // 回复内容（只有文字和表情）
        String content = buildReplyContent();
        
        Service.tweet.addComment(tweet.id, user.id, content);
        log.info("[Bot] {} 回复了 #{}: {}", user.nickname, tweet.id, content);
    }
    
    /**
     * 点赞
     */
    public void likeTweet() {
        User user = Service.user.getRandomBotUser();
        Tweet tweet = Service.tweet.getRandomTweet();
        if (user == null || tweet == null) return;
        
        Service.tweet.toggleLike(tweet.id, user.id);
        log.info("[Bot] {} 点赞了 #{}", user.nickname, tweet.id);
    }
    
    /**
     * 转发
     */
    public void retweetTweet() {
        User user = Service.user.getRandomBotUser();
        Tweet tweet = Service.tweet.getRandomTweet();
        if (user == null || tweet == null) return;
        if (tweet.userid.equals(user.id)) return;
        
        Service.tweet.retweet(tweet.id, user.id);
        log.info("[Bot] {} 转发了 #{}", user.nickname, tweet.id);
    }
    
    // ==================== 私信操作 ====================
    
    /**
     * 发私信（只有文字和表情）
     */
    public void sendMessage() {
        User from = Service.user.getRandomBotUser();
        User to = Service.user.getRandomUser();
        if (from == null || to == null) return;
        if (from.id.equals(to.id)) return;
        
        String content = buildReplyContent();
        Service.message.send(from.id, to.id, content);
        log.info("[Bot] {} 给 {} 发私信: {}", from.nickname, to.nickname, content);
    }

    
    // ==================== 表情定义 ====================
    
    public static final String[] EMOJIS = {
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "😊",
        "😇", "🥰", "😍", "🤩", "😘", "😋", "😛", "🤔", "🤗", "🤭",
        "😎", "🥳", "😏", "😌", "😴", "🤤", "😷", "🤒", "👍", "👏",
        "🙌", "💪", "❤️", "🔥", "⭐", "🌟", "✨", "💯", "🎉", "🎊",
        "👀", "💕", "💖", "💗", "💙", "💚", "💛", "🧡", "💜", "🖤"
    };
    
    public static final String[] REPLY_TEMPLATES = {
        "说得好！", "赞同", "有道理", "学习了", "厉害",
        "哈哈哈", "太真实了", "确实", "支持", "加油",
        "不错不错", "很棒", "同感", "是的呢", "对对对",
        "哇塞", "绝了", "太强了", "牛", "6666"
    };
    
    // ==================== 内容构建 ====================
    
    /**
     * 构建帖子内容（可带表情和话题）
     */
    public String buildTweetContent() {
        StringBuilder sb = new StringBuilder();
        
        // 主内容
        sb.append(crawler.getRandomContent());
        
        // 随机加表情
        if (random.nextInt(100) < emojiChance) {
            sb.append(" ").append(getRandomEmojis());
        }
        
        // 随机加话题
        if (random.nextInt(100) < hashtagChance) {
            sb.append(" #").append(crawler.getRandomHashtag());
        }
        
        return sb.toString();
    }
    
    /**
     * 构建回复/私信内容（只有文字和表情，不带图片）
     */
    public String buildReplyContent() {
        StringBuilder sb = new StringBuilder();
        
        // 50% 用模板，50% 用爬取内容
        if (random.nextBoolean()) {
            sb.append(REPLY_TEMPLATES[random.nextInt(REPLY_TEMPLATES.length)]);
        } else {
            String content = crawler.getRandomContent();
            // 回复内容限制长度
            if (content.length() > 30) {
                content = content.substring(0, 30);
            }
            sb.append(content);
        }
        
        // 随机加表情（回复更容易带表情）
        if (random.nextInt(100) < emojiChance + 20) {
            sb.append(" ").append(getRandomEmojis());
        }
        
        return sb.toString();
    }
    
    /**
     * 获取随机表情
     */
    public String getRandomEmoji() {
        return EMOJIS[random.nextInt(EMOJIS.length)];
    }
    
    /**
     * 获取随机表情串（1-3个）
     */
    public String getRandomEmojis() {
        int count = random.nextInt(3) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(getRandomEmoji());
        }
        return sb.toString();
    }
}
