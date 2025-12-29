package litex.plugin;

import litejava.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 爬虫插件 - 基于 WebMagic 框架
 * 
 * 功能：
 * 1. 从配置的目标站点爬取内容（热搜、句子、图片）
 * 2. 支持 CSS/XPath/正则 选择器
 * 3. 下载图片到本地
 * 
 * 配置示例：
 * crawler:
 *   enabled: true
 *   intervalMinutes: 60
 *   imageDir: ./uploads/crawler
 *   threadNum: 2
 *   sleepTime: 1000
 *   targets:
 *     - name: 百度热搜
 *       url: https://top.baidu.com/board?tab=realtime
 *       type: content
 *       selector: .c-single-text-ellipsis
 *       selectorType: css
 *     - name: 一言API
 *       url: https://v1.hitokoto.cn/?encode=text
 *       type: content
 *       selector: body
 *       selectorType: css
 *     - name: 随机图片
 *       url: https://picsum.photos/800/600
 *       type: image
 */
public class CrawlerPlugin extends Plugin {
    
    public static final Logger log = LoggerFactory.getLogger(CrawlerPlugin.class);
    
    // 配置字段
    public boolean enabled = true;
    public int intervalMinutes = 60;
    public String imageDir = "./uploads/crawler";
    public int maxContents = 500;
    public int maxImages = 200;
    public int threadNum = 2;
    public int sleepTime = 1000;
    public int timeout = 10000;
    public int retryTimes = 2;
    
    // 目标站点配置
    public List<CrawlTarget> targets = new CopyOnWriteArrayList<>();
    
    // 内容池
    public final List<String> contents = new CopyOnWriteArrayList<>();
    public final List<String> hashtags = new CopyOnWriteArrayList<>();
    public final List<String> nicknames = new CopyOnWriteArrayList<>();
    public final List<String> images = new CopyOnWriteArrayList<>();
    
    public ScheduledExecutorService scheduler;
    public final Random random = new Random();
    
    /**
     * 爬取目标配置
     */
    public static class CrawlTarget {
        public String name;
        public String url;
        public String type = "content";  // content, hashtag, image
        public String selector;
        public String selectorType = "css";  // css, xpath, regex
        public int maxItems = 50;
        
        public CrawlTarget() {}
        
        public CrawlTarget(String name, String url, String type, String selector, String selectorType) {
            this.name = name;
            this.url = url;
            this.type = type;
            this.selector = selector;
            this.selectorType = selectorType;
        }
    }
    
    @Override
    public void config() {
        enabled = app.conf.getBool("crawler", "enabled", enabled);
        intervalMinutes = app.conf.getInt("crawler", "intervalMinutes", intervalMinutes);
        imageDir = app.conf.getString("crawler", "imageDir", imageDir);
        maxContents = app.conf.getInt("crawler", "maxContents", maxContents);
        maxImages = app.conf.getInt("crawler", "maxImages", maxImages);
        threadNum = app.conf.getInt("crawler", "threadNum", threadNum);
        sleepTime = app.conf.getInt("crawler", "sleepTime", sleepTime);
        timeout = app.conf.getInt("crawler", "timeout", timeout);
        retryTimes = app.conf.getInt("crawler", "retryTimes", retryTimes);
        
        // 解析目标站点配置
        parseTargets();
        
        // 如果没有配置目标，使用默认
        if (targets.isEmpty()) {
            initDefaultTargets();
        }
        
        // 初始化默认内容
        initDefaults();
        
        // 创建图片目录
        try {
            Files.createDirectories(Paths.get(imageDir));
        } catch (IOException e) {
            log.warn("创建图片目录失败: {}", e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void parseTargets() {
        Object targetsObj = app.conf.get("crawler.targets");
        if (targetsObj instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) targetsObj;
            for (Map<String, Object> item : list) {
                CrawlTarget target = new CrawlTarget();
                target.name = (String) item.getOrDefault("name", "未命名");
                target.url = (String) item.get("url");
                target.type = (String) item.getOrDefault("type", "content");
                target.selector = (String) item.get("selector");
                target.selectorType = (String) item.getOrDefault("selectorType", "css");
                target.maxItems = (Integer) item.getOrDefault("maxItems", 50);
                
                if (target.url != null) {
                    targets.add(target);
                }
            }
        }
    }
    
    public void initDefaultTargets() {
        // 一言 API（纯文本）
        targets.add(new CrawlTarget("一言", "https://v1.hitokoto.cn/?encode=text", 
            "content", "body", "css"));
        
        // 古诗词 API
        targets.add(new CrawlTarget("古诗词", "https://v1.jinrishici.com/all.txt", 
            "content", "body", "css"));
        
        // 随机图片
        targets.add(new CrawlTarget("随机图片", "https://picsum.photos/800/600", 
            "image", null, null));
    }
    
    @Override
    public void onStart() {
        if (!enabled) return;
        
        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "crawler-worker");
            t.setDaemon(true);
            return t;
        });
        
        // 启动时立即爬取
        scheduler.submit(this::crawlAll);
        
        // 定时爬取
        scheduler.scheduleAtFixedRate(this::crawlAll, 
            intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
        
        log.info("爬虫插件已启动，{} 个目标，间隔 {} 分钟", targets.size(), intervalMinutes);
    }
    
    @Override
    public void uninstall() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    
    // ==================== 爬取逻辑 ====================
    
    public void crawlAll() {
        log.info("[Crawler] 开始爬取 {} 个目标...", targets.size());
        long start = System.currentTimeMillis();
        
        for (CrawlTarget target : targets) {
            try {
                crawlTarget(target);
            } catch (Exception e) {
                log.warn("[Crawler] {} 爬取失败: {}", target.name, e.getMessage());
            }
        }
        
        // 生成昵称
        generateNicknames();
        
        long cost = System.currentTimeMillis() - start;
        log.info("[Crawler] 爬取完成 ({}ms): {} 内容, {} 话题, {} 图片", 
            cost, contents.size(), hashtags.size(), images.size());
    }
    
    public void crawlTarget(CrawlTarget target) {
        if ("image".equals(target.type)) {
            crawlImage(target);
            return;
        }
        
        // 使用 WebMagic 爬取
        Spider spider = Spider.create(new ContentProcessor(target))
            .addUrl(target.url)
            .addPipeline(new ContentPipeline(target))
            .thread(1);
        
        // 配置
        Site site = Site.me()
            .setRetryTimes(retryTimes)
            .setSleepTime(sleepTime)
            .setTimeOut(timeout)
            .setUserAgent(randomUserAgent());
        
        spider.run();
        log.debug("[Crawler] {} 完成", target.name);
    }
    
    /**
     * 内容处理器
     */
    class ContentProcessor implements PageProcessor {
        public CrawlTarget target;
        public Site site = Site.me()
            .setRetryTimes(retryTimes)
            .setSleepTime(sleepTime)
            .setTimeOut(timeout)
            .setUserAgent(randomUserAgent());
        
        public ContentProcessor(CrawlTarget target) {
            this.target = target;
        }
        
        @Override
        public void process(Page page) {
            if (target.selector == null || target.selector.isEmpty()) {
                // 无选择器，取整个 body
                String text = page.getHtml().xpath("//body/text()").get();
                if (text != null && !text.trim().isEmpty()) {
                    page.putField("items", Collections.singletonList(text.trim()));
                }
                return;
            }
            
            Selectable selectable;
            switch (target.selectorType) {
                case "xpath":
                    selectable = page.getHtml().xpath(target.selector);
                    break;
                case "regex":
                    selectable = page.getHtml().regex(target.selector);
                    break;
                default: // css
                    selectable = page.getHtml().css(target.selector);
            }
            
            List<String> items = selectable.all();
            if (items != null && !items.isEmpty()) {
                // 清理 HTML 标签
                List<String> cleaned = new ArrayList<>();
                for (String item : items) {
                    String text = item.replaceAll("<[^>]+>", "").trim();
                    if (!text.isEmpty() && text.length() <= 200) {
                        cleaned.add(text);
                        if (cleaned.size() >= target.maxItems) break;
                    }
                }
                page.putField("items", cleaned);
            }
        }
        
        @Override
        public Site getSite() {
            return site;
        }
    }
    
    /**
     * 内容管道
     */
    class ContentPipeline implements Pipeline {
        public CrawlTarget target;
        
        public ContentPipeline(CrawlTarget target) {
            this.target = target;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void process(ResultItems resultItems, Task task) {
            List<String> items = resultItems.get("items");
            if (items == null) return;
            
            for (String item : items) {
                switch (target.type) {
                    case "hashtag":
                        addHashtag(item);
                        break;
                    case "content":
                    default:
                        addContent(item);
                        // 短内容也加入话题
                        if (item.length() <= 8) {
                            addHashtag(item);
                        }
                }
            }
        }
    }
    
    // ==================== 图片爬取 ====================
    
    public void crawlImage(CrawlTarget target) {
        if (images.size() >= maxImages) return;
        
        for (int i = 0; i < 5 && images.size() < maxImages; i++) {
            String localPath = downloadImage(target.url);
            if (localPath != null) {
                addImage(localPath);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public String downloadImage(String imageUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("User-Agent", randomUserAgent());
            conn.setInstanceFollowRedirects(true);
            
            if (conn.getResponseCode() != 200) {
                return null;
            }
            
            String contentType = conn.getContentType();
            String ext = ".jpg";
            if (contentType != null) {
                if (contentType.contains("png")) ext = ".png";
                else if (contentType.contains("gif")) ext = ".gif";
                else if (contentType.contains("webp")) ext = ".webp";
            }
            
            String filename = "img_" + System.currentTimeMillis() + "_" + random.nextInt(1000) + ext;
            Path filePath = Paths.get(imageDir, filename);
            
            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return "/uploads/crawler/" + filename;
        } catch (Exception e) {
            return null;
        }
    }

    
    // ==================== 昵称生成 ====================
    
    public void generateNicknames() {
        String[] surnames = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴",
            "徐", "孙", "马", "朱", "胡", "郭", "何", "高", "林", "罗"};
        String[] names = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
            "勇", "艳", "杰", "娟", "涛", "明", "超", "华", "慧", "浩"};
        String[] prefixes = {"小", "大", "阿", "老"};
        String[] netNames = {"追风少年", "星辰大海", "月光宝盒", "快乐小鱼", "阳光灿烂",
            "梦想家", "旅行者", "摄影师", "美食家", "音乐人"};
        String[] enNames = {"Alex", "Emma", "Jack", "Lucy", "Tom", "Amy", "Leo", "Lily",
            "Max", "Mia", "Sam", "Zoe", "Ben", "Eva", "Jay", "Ivy"};
        
        for (int i = 0; i < 30; i++) {
            int type = random.nextInt(4);
            String nickname;
            switch (type) {
                case 0:
                    nickname = surnames[random.nextInt(surnames.length)] + 
                               names[random.nextInt(names.length)];
                    break;
                case 1:
                    nickname = prefixes[random.nextInt(prefixes.length)] + 
                               names[random.nextInt(names.length)];
                    break;
                case 2:
                    nickname = netNames[random.nextInt(netNames.length)];
                    break;
                default:
                    nickname = enNames[random.nextInt(enNames.length)] + random.nextInt(100);
            }
            addNickname(nickname);
        }
    }
    
    // ==================== 默认内容 ====================
    
    public void initDefaults() {
        String[] defaultContents = {
            "今天天气真好！", "刚刚看了一部很棒的电影", "分享一个有趣的想法",
            "早安，新的一天开始了", "晚安，好梦", "周末愉快！",
            "工作中，勿扰", "学习新技术中...", "美食分享时间",
            "运动打卡", "读书笔记", "旅行中", "咖啡时间", "音乐推荐",
            "今日心情不错", "加油！", "生活需要仪式感", "简单的快乐"
        };
        contents.addAll(Arrays.asList(defaultContents));
        
        String[] defaultHashtags = {
            "日常", "生活", "分享", "打卡", "记录", "美食", "旅行", "读书",
            "电影", "音乐", "健身", "学习", "工作", "周末", "早安", "晚安"
        };
        hashtags.addAll(Arrays.asList(defaultHashtags));
    }
    
    // ==================== 内容池操作 ====================
    
    public void addContent(String content) {
        if (content != null && !content.isEmpty() && !contents.contains(content)) {
            contents.add(content);
            while (contents.size() > maxContents) {
                contents.remove(0);
            }
        }
    }
    
    public void addHashtag(String tag) {
        if (tag != null && !tag.isEmpty() && !hashtags.contains(tag)) {
            hashtags.add(tag);
            while (hashtags.size() > 200) {
                hashtags.remove(0);
            }
        }
    }
    
    public void addNickname(String name) {
        if (name != null && !name.isEmpty() && !nicknames.contains(name)) {
            nicknames.add(name);
            while (nicknames.size() > 200) {
                nicknames.remove(0);
            }
        }
    }
    
    public void addImage(String path) {
        if (path != null && !path.isEmpty() && !images.contains(path)) {
            images.add(path);
            while (images.size() > maxImages) {
                String old = images.remove(0);
                try {
                    Files.deleteIfExists(Paths.get("." + old));
                } catch (IOException e) {
                    // 忽略
                }
            }
        }
    }
    
    // ==================== 公共获取方法 ====================
    
    public String getRandomContent() {
        if (contents.isEmpty()) return "今天天气真好！";
        return contents.get(random.nextInt(contents.size()));
    }
    
    public String getRandomHashtag() {
        if (hashtags.isEmpty()) return "日常";
        return hashtags.get(random.nextInt(hashtags.size()));
    }
    
    public String getRandomNickname() {
        if (nicknames.isEmpty()) return "用户" + random.nextInt(10000);
        String base = nicknames.get(random.nextInt(nicknames.size()));
        if (random.nextBoolean()) {
            return base + random.nextInt(100);
        }
        return base;
    }
    
    public String getRandomImage() {
        if (images.isEmpty()) return null;
        return images.get(random.nextInt(images.size()));
    }
    
    public String randomUserAgent() {
        String[] agents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0"
        };
        return agents[random.nextInt(agents.length)];
    }
}
