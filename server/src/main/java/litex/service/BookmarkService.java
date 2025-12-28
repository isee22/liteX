package litex.service;

import litex.DB;
import litex.entity.Bookmark;
import litex.entity.Tweet;
import litex.mapper.*;
import java.util.*;

public class BookmarkService {
    
    public Map<String, Object> list(long userid, int page, int size) {
        int offset = (page - 1) * size;
        List<Bookmark> bookmarks = DB.execute(BookmarkMapper.class, m -> m.findByUserId(userid, offset, size));
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            Map<String, Object> item = new HashMap<>();
            Tweet tweet = DB.execute(TweetMapper.class, m -> m.findById(b.tweetid));
            if (tweet != null) {
                item.put("tweet", tweet);
                item.put("user", DB.execute(UserMapper.class, m -> m.findById(tweet.userid)));
                item.put("liked", DB.execute(LikeMapper.class, m -> m.find(userid, tweet.id)) != null);
                item.put("bookmarked", true);
                item.put("bookmarkat", b.createdat);
                list.add(item);
            }
        }
        return Map.of("list", list);
    }
    
    public boolean toggle(long userid, long tweetid) {
        Bookmark existing = DB.execute(BookmarkMapper.class, m -> m.find(userid, tweetid));
        if (existing != null) {
            DB.execute(BookmarkMapper.class, m -> m.delete(userid, tweetid));
            return false;
        } else {
            Bookmark b = new Bookmark();
            b.userid = userid;
            b.tweetid = tweetid;
            DB.execute(BookmarkMapper.class, m -> m.insert(b));
            return true;
        }
    }
    
    public boolean isBookmarked(long userid, long tweetid) {
        return DB.execute(BookmarkMapper.class, m -> m.find(userid, tweetid)) != null;
    }
    
    public void clearAll(long userid) {
        DB.execute(BookmarkMapper.class, m -> m.deleteByUserId(userid));
    }
}
