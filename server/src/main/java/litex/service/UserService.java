package litex.service;

import litex.DB;
import litex.entity.*;
import litex.mapper.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.*;

public class UserService {
    
    public User findById(long id) {
        User user = DB.execute(UserMapper.class, m -> m.findById(id));
        if (user != null) user.password = null;
        return user;
    }
    
    public Map<String, Object> getUserWithFollowStatus(long userid, Long currentUserid) {
        User user = DB.execute(UserMapper.class, m -> m.findById(userid));
        if (user == null) return null;
        user.password = null;
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.id);
        result.put("username", user.username);
        result.put("nickname", user.nickname);
        result.put("email", user.email);
        result.put("avatar", user.avatar);
        result.put("banner", user.banner);
        result.put("bio", user.bio);
        result.put("followerscount", user.followerscount);
        result.put("followingcount", user.followingcount);
        result.put("createdat", user.createdat);
        
        boolean isfollowing = false;
        if (currentUserid != null && currentUserid != userid) {
            isfollowing = DB.execute(FollowMapper.class, m -> m.find(currentUserid, userid)) != null;
        }
        result.put("isfollowing", isfollowing);
        
        return result;
    }
    
    public User findByUsername(String username) {
        return DB.execute(UserMapper.class, m -> m.findByUsername(username));
    }
    
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.password)) {
            return null;
        }
        user.password = null;
        return user;
    }
    
    public User register(String username, String password, String nickname, String email) {
        if (findByUsername(username) != null) {
            return null;
        }
        User user = new User();
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.nickname = nickname != null ? nickname : username;
        user.email = email;
        DB.execute(UserMapper.class, m -> m.insert(user));
        return user;
    }
    
    public void toggleFollow(long userid, long targetid) {
        Follow existing = DB.execute(FollowMapper.class, m -> m.find(userid, targetid));
        User me = DB.execute(UserMapper.class, m -> m.findById(userid));
        User target = DB.execute(UserMapper.class, m -> m.findById(targetid));
        
        if (existing != null) {
            DB.execute(FollowMapper.class, m -> m.delete(existing.id));
            me.followingcount--;
            target.followerscount--;
        } else {
            Follow follow = new Follow();
            follow.userid = userid;
            follow.targetid = targetid;
            DB.execute(FollowMapper.class, m -> m.insert(follow));
            me.followingcount++;
            target.followerscount++;
        }
        DB.execute(UserMapper.class, m -> m.update(me));
        DB.execute(UserMapper.class, m -> m.update(target));
    }
    
    public void updateProfile(long userid, Map<String, Object> data) {
        User user = DB.execute(UserMapper.class, m -> m.findById(userid));
        if (data.containsKey("nickname")) user.nickname = (String) data.get("nickname");
        if (data.containsKey("bio")) user.bio = (String) data.get("bio");
        if (data.containsKey("avatar")) user.avatar = (String) data.get("avatar");
        if (data.containsKey("banner")) user.banner = (String) data.get("banner");
        DB.execute(UserMapper.class, m -> m.update(user));
    }
    
    public List<Map<String, Object>> getLikedTweets(long userid) {
        List<Like> likes = DB.execute(LikeMapper.class, m -> m.findByUserId(userid));
        List<Map<String, Object>> list = new ArrayList<>();
        for (Like like : likes) {
            Tweet t = DB.execute(TweetMapper.class, m -> m.findById(like.tweetid));
            if (t != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("tweet", t);
                item.put("user", DB.execute(UserMapper.class, m -> m.findById(t.userid)));
                item.put("liked", true);
                list.add(item);
            }
        }
        return list;
    }
    
    public Map<String, Object> getFollowers(long userid, long currentUserid, int page, int size) {
        int offset = (page - 1) * size;
        List<Long> followerIds = DB.execute(FollowMapper.class, m -> m.findFollowers(userid, offset, size));
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Long followerId : followerIds) {
            User user = DB.execute(UserMapper.class, m -> m.findById(followerId));
            if (user != null) {
                user.password = null;
                Map<String, Object> item = new HashMap<>();
                item.put("user", user);
                item.put("followed", DB.execute(FollowMapper.class, m -> m.find(currentUserid, user.id)) != null);
                list.add(item);
            }
        }
        return Map.of("list", list);
    }
    
    public Map<String, Object> getFollowing(long userid, long currentUserid, int page, int size) {
        int offset = (page - 1) * size;
        List<Long> followingIds = DB.execute(FollowMapper.class, m -> m.findFollowing(userid, offset, size));
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Long followingId : followingIds) {
            User user = DB.execute(UserMapper.class, m -> m.findById(followingId));
            if (user != null) {
                user.password = null;
                Map<String, Object> item = new HashMap<>();
                item.put("user", user);
                item.put("followed", DB.execute(FollowMapper.class, m -> m.find(currentUserid, user.id)) != null);
                list.add(item);
            }
        }
        return Map.of("list", list);
    }
    
    public List<Map<String, Object>> getRecommend(Long userid, int limit) {
        long uid = userid != null ? userid : 0L;
        List<User> users = DB.execute(UserMapper.class, m -> m.findRecommend(uid, limit));
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users) {
            u.password = null;
            Map<String, Object> item = new HashMap<>();
            item.put("user", u);
            item.put("followed", false);
            list.add(item);
        }
        return list;
    }
}
