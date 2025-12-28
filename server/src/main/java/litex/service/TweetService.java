package litex.service;

import litex.DB;
import litex.entity.*;
import litex.mapper.*;
import litejava.util.Maps;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetService {
    
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([\\w\\u4e00-\\u9fa5]+)");
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]+)");
    
    private NotificationService notificationService;
    
    public TweetService() {
    }
    
    public TweetService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Extract hashtags from content.
     * @param content tweet content
     * @return list of hashtag strings (without #)
     */
    public List<String> parseHashtags(String content) {
        List<String> hashtags = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return hashtags;
        }
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            hashtags.add(matcher.group(1));
        }
        return hashtags;
    }
    
    /**
     * Extract @mentions from content.
     * @param content tweet content
     * @return list of username strings (without @)
     */
    public List<String> parseMentions(String content) {
        List<String> mentions = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return mentions;
        }
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return mentions;
    }
    
    public Map<String, Object> getTimeline(Long userid, int page, int size) {
        List<Tweet> tweets = DB.execute(TweetMapper.class, m -> m.findTimeline((page - 1) * size, size));
        return buildTimelineResult(tweets, userid);
    }
    
    public Map<String, Object> getFollowingTimeline(Long userid, int page, int size) {
        if (userid == null) {
            return Maps.of("list", new ArrayList<>());
        }
        List<Tweet> tweets = DB.execute(TweetMapper.class, m -> m.findFollowingTimeline(userid, (page - 1) * size, size));
        return buildTimelineResult(tweets, userid);
    }
    
    private Map<String, Object> buildTimelineResult(List<Tweet> tweets, Long userid) {
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Tweet t : tweets) {
            Map<String, Object> item = new HashMap<>();
            item.put("tweet", t);
            User user = DB.execute(UserMapper.class, m -> m.findById(t.userid));
            if (user != null) user.password = null;
            item.put("user", user);
            item.put("liked", userid != null && DB.execute(LikeMapper.class, m -> m.find(userid, t.id)) != null);
            item.put("retweeted", userid != null && DB.execute(TweetMapper.class, m -> m.findRetweet(userid, t.id)) != null);
            list.add(item);
        }
        return Maps.of("list", list);
    }
    
    public Map<String, Object> create(Long userid, String content, String images) {
        if (userid == null) return null;
        Tweet tweet = new Tweet();
        tweet.userid = userid;
        tweet.content = content;
        tweet.images = images;
        DB.execute(TweetMapper.class, m -> m.insert(tweet));
        
        // Parse and update hashtag trends
        List<String> hashtags = parseHashtags(content);
        for (String tag : hashtags) {
            updateTrendCount(tag);
        }
        
        // Parse mentions and create notifications
        List<String> mentions = parseMentions(content);
        for (String username : mentions) {
            createMentionNotification(username, userid, tweet.id);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("tweet", tweet);
        result.put("user", DB.execute(UserMapper.class, m -> m.findById(userid)));
        return result;
    }
    
    /**
     * Create notification for a mentioned user.
     * Only creates notification if the username exists.
     */
    private void createMentionNotification(String username, long fromuserid, Long tweetid) {
        User mentionedUser = DB.execute(UserMapper.class, m -> m.findByUsername(username));
        if (mentionedUser != null && notificationService != null) {
            notificationService.create(mentionedUser.id, fromuserid, "mention", tweetid);
        }
    }
    
    /**
     * Update trend count for a hashtag.
     * Creates new trend if not exists, otherwise increments count.
     */
    private void updateTrendCount(String tag) {
        Trend existing = DB.execute(TrendMapper.class, m -> m.findByTag(tag));
        if (existing == null) {
            Trend trend = new Trend();
            trend.tag = tag;
            trend.count = 1;
            DB.execute(TrendMapper.class, m -> m.insert(trend));
        } else {
            existing.count++;
            existing.updatedat = java.time.LocalDateTime.now();
            DB.execute(TrendMapper.class, m -> m.update(existing));
        }
    }
    
    public Map<String, Object> findById(long id, Long userid) {
        Tweet tweet = DB.execute(TweetMapper.class, m -> m.findById(id));
        if (tweet == null) return null;
        
        Map<String, Object> result = new HashMap<>();
        result.put("tweet", tweet);
        User user = DB.execute(UserMapper.class, m -> m.findById(tweet.userid));
        if (user != null) user.password = null;
        result.put("user", user);
        result.put("liked", userid != null && DB.execute(LikeMapper.class, m -> m.find(userid, id)) != null);
        result.put("retweeted", userid != null && DB.execute(TweetMapper.class, m -> m.findRetweet(userid, id)) != null);
        return result;
    }
    
    public void delete(long id, Long userid) {
        if (userid == null) return;
        Tweet tweet = DB.execute(TweetMapper.class, m -> m.findById(id));
        if (tweet != null && tweet.userid.equals(userid)) {
            // 删帖时减少 hashtag 计数
            List<String> hashtags = parseHashtags(tweet.content);
            for (String tag : hashtags) {
                decrementTrendCount(tag);
            }
            DB.execute(TweetMapper.class, m -> m.delete(id));
        }
    }
    
    private void decrementTrendCount(String tag) {
        Trend existing = DB.execute(TrendMapper.class, m -> m.findByTag(tag));
        if (existing != null && existing.count > 0) {
            existing.count--;
            DB.execute(TrendMapper.class, m -> m.update(existing));
        }
    }
    
    public void toggleLike(long tweetid, Long userid) {
        if (userid == null) return;
        Like existing = DB.execute(LikeMapper.class, m -> m.find(userid, tweetid));
        Tweet tweet = DB.execute(TweetMapper.class, m -> m.findById(tweetid));
        if (tweet == null) return;
        
        if (existing != null) {
            DB.execute(LikeMapper.class, m -> m.delete(existing.id));
            tweet.likecount--;
        } else {
            Like like = new Like();
            like.userid = userid;
            like.tweetid = tweetid;
            DB.execute(LikeMapper.class, m -> m.insert(like));
            tweet.likecount++;
        }
        DB.execute(TweetMapper.class, m -> m.update(tweet));
    }
    
    public void retweet(long tweetid, Long userid) {
        if (userid == null) return;
        Tweet original = DB.execute(TweetMapper.class, m -> m.findById(tweetid));
        if (original == null) return;
        
        // 检查是否已转推
        Tweet existing = DB.execute(TweetMapper.class, m -> m.findRetweet(userid, tweetid));
        if (existing != null) {
            // 取消转推
            DB.execute(TweetMapper.class, m -> m.delete(existing.id));
            original.retweetcount = Math.max(0, original.retweetcount - 1);
        } else {
            // 转推
            Tweet retweet = new Tweet();
            retweet.userid = userid;
            retweet.retweetid = tweetid;
            retweet.content = original.content;
            retweet.images = original.images;
            DB.execute(TweetMapper.class, m -> m.insert(retweet));
            original.retweetcount++;
        }
        DB.execute(TweetMapper.class, m -> m.update(original));
    }
    
    public Map<String, Object> quote(long tweetid, Long userid, String content) {
        if (userid == null) return null;
        Tweet original = DB.execute(TweetMapper.class, m -> m.findById(tweetid));
        if (original == null) return null;
        
        Tweet quote = new Tweet();
        quote.userid = userid;
        quote.retweetid = tweetid;
        quote.content = content;
        DB.execute(TweetMapper.class, m -> m.insert(quote));
        
        original.retweetcount++;
        DB.execute(TweetMapper.class, m -> m.update(original));
        
        // Parse hashtags and mentions in quote content
        List<String> hashtags = parseHashtags(content);
        for (String tag : hashtags) {
            updateTrendCount(tag);
        }
        List<String> mentions = parseMentions(content);
        for (String username : mentions) {
            createMentionNotification(username, userid, quote.id);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("tweet", quote);
        result.put("user", DB.execute(UserMapper.class, m -> m.findById(userid)));
        result.put("originalTweet", original);
        result.put("originalUser", DB.execute(UserMapper.class, m -> m.findById(original.userid)));
        return result;
    }
    
    public List<Map<String, Object>> getComments(long tweetid) {
        List<Comment> comments = DB.execute(CommentMapper.class, m -> m.findByTweetId(tweetid));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment c : comments) {
            Map<String, Object> item = new HashMap<>();
            item.put("comment", c);
            item.put("user", DB.execute(UserMapper.class, m -> m.findById(c.userid)));
            result.add(item);
        }
        return result;
    }
    
    public void addComment(long tweetid, Long userid, String content) {
        if (userid == null) return;
        Comment comment = new Comment();
        comment.tweetid = tweetid;
        comment.userid = userid;
        comment.content = content;
        DB.execute(CommentMapper.class, m -> m.insert(comment));
        
        Tweet tweet = DB.execute(TweetMapper.class, m -> m.findById(tweetid));
        if (tweet != null) {
            tweet.commentcount++;
            DB.execute(TweetMapper.class, m -> m.update(tweet));
        }
    }
    
    public List<Tweet> findByUserId(long userid) {
        return DB.execute(TweetMapper.class, m -> m.findByUserId(userid));
    }
    
    /**
     * 获取随机推文（用于 BotPlugin）
     */
    public Tweet getRandomTweet() {
        List<Tweet> tweets = DB.execute(TweetMapper.class, m -> m.findTimeline(0, 50));
        if (tweets.isEmpty()) return null;
        return tweets.get(new Random().nextInt(tweets.size()));
    }
}
