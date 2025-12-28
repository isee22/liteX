package litex.service;

import litex.DB;
import litex.entity.*;
import litex.mapper.*;
import java.util.*;

public class SearchService {
    
    public Map<String, Object> searchTweets(String keyword, long userid, int page, int size) {
        // Handle empty/whitespace queries by returning empty list
        if (keyword == null || keyword.trim().isEmpty()) {
            return Map.of("list", new ArrayList<>());
        }
        
        String trimmedKeyword = keyword.trim();
        int offset = (page - 1) * size;
        
        List<Tweet> tweets;
        // 如果是 hashtag 搜索，使用专门的方法
        if (trimmedKeyword.startsWith("#")) {
            String tag = trimmedKeyword.substring(1); // 去掉 # 号
            tweets = DB.execute(TweetMapper.class, m -> m.searchByHashtag(tag, offset, size));
        } else if (trimmedKeyword.startsWith("@")) {
            // @用户名 搜索该用户的帖子
            String username = trimmedKeyword.substring(1);
            User user = DB.execute(UserMapper.class, m -> m.findByUsername(username));
            if (user != null) {
                tweets = DB.execute(TweetMapper.class, m -> m.findByUserIdPaged(user.id, offset, size));
            } else {
                tweets = new ArrayList<>();
            }
        } else {
            tweets = DB.execute(TweetMapper.class, m -> m.searchByContent(trimmedKeyword, offset, size));
        }
        
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Tweet t : tweets) {
            Map<String, Object> item = new HashMap<>();
            item.put("tweet", t);
            User tweetUser = DB.execute(UserMapper.class, m -> m.findById(t.userid));
            if (tweetUser != null) tweetUser.password = null;
            item.put("user", tweetUser);
            item.put("liked", DB.execute(LikeMapper.class, m -> m.find(userid, t.id)) != null);
            list.add(item);
        }
        return Map.of("list", list);
    }
    
    public Map<String, Object> searchUsers(String keyword, long userid, int page, int size) {
        // Handle empty/whitespace queries by returning empty list
        if (keyword == null || keyword.trim().isEmpty()) {
            return Map.of("list", new ArrayList<>());
        }
        
        String trimmedKeyword = keyword.trim();
        int offset = (page - 1) * size;
        
        List<User> users = DB.execute(UserMapper.class, m -> m.searchByNameOrNickname(trimmedKeyword, offset, size));
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (User u : users) {
            u.password = null; // Don't expose password
            Map<String, Object> item = new HashMap<>();
            item.put("user", u);
            item.put("followed", DB.execute(FollowMapper.class, m -> m.find(userid, u.id)) != null);
            list.add(item);
        }
        return Map.of("list", list);
    }
    
    public Map<String, Object> searchByHashtag(String hashtag, long userid, int page, int size) {
        // Handle empty/whitespace queries by returning empty list
        if (hashtag == null || hashtag.trim().isEmpty()) {
            return Map.of("list", new ArrayList<>());
        }
        
        String trimmedHashtag = hashtag.trim();
        int offset = (page - 1) * size;
        
        List<Tweet> tweets = DB.execute(TweetMapper.class, m -> m.searchByHashtag(trimmedHashtag, offset, size));
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Tweet t : tweets) {
            Map<String, Object> item = new HashMap<>();
            item.put("tweet", t);
            item.put("user", DB.execute(UserMapper.class, m -> m.findById(t.userid)));
            item.put("liked", DB.execute(LikeMapper.class, m -> m.find(userid, t.id)) != null);
            list.add(item);
        }
        return Map.of("list", list);
    }
}
