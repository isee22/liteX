package litex.service;

import litex.DB;
import litex.entity.Notification;
import litex.mapper.*;
import java.util.*;

public class NotificationService {
    
    public List<Map<String, Object>> list(Long userid, String type) {
        if (userid == null) return new ArrayList<>();
        List<Notification> notifications;
        if ("all".equals(type) || type == null || type.isEmpty()) {
            notifications = DB.execute(NotificationMapper.class, m -> m.findByUserId(userid));
        } else {
            notifications = DB.execute(NotificationMapper.class, m -> m.findByUserIdAndType(userid, type));
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification n : notifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("notification", n);
            if (n.fromuserid != null) {
                item.put("fromuser", DB.execute(UserMapper.class, m -> m.findById(n.fromuserid)));
            }
            result.add(item);
        }
        return result;
    }
    
    public void create(long userid, long fromuserid, String type, Long tweetid) {
        Notification n = new Notification();
        n.userid = userid;
        n.fromuserid = fromuserid;
        n.type = type;
        n.tweetid = tweetid;
        DB.execute(NotificationMapper.class, m -> m.insert(n));
    }
    
    public void markAllRead(Long userid) {
        if (userid == null) return;
        DB.execute(NotificationMapper.class, m -> m.markAllRead(userid));
    }
    
    public int countUnread(Long userid) {
        if (userid == null) return 0;
        return DB.execute(NotificationMapper.class, m -> m.countUnread(userid));
    }
}
