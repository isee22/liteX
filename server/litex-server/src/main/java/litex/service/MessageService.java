package litex.service;

import litex.DB;
import litex.entity.Message;
import litex.mapper.*;
import java.util.*;

public class MessageService {
    
    public List<Map<String, Object>> getChats(long userid) {
        List<Map<String, Object>> rawChats = DB.execute(MessageMapper.class, m -> m.getChats(userid));
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> raw : rawChats) {
            Map<String, Object> chat = new HashMap<>();
            Long otherUserId = (Long) raw.get("userid");
            chat.put("user", DB.execute(UserMapper.class, m -> m.findById(otherUserId)));
            chat.put("lasttime", raw.get("lasttime"));
            result.add(chat);
        }
        return result;
    }
    
    public List<Map<String, Object>> getMessages(long userid, long targetid) {
        List<Message> messages = DB.execute(MessageMapper.class, m -> m.getMessages(userid, targetid));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> item = new HashMap<>();
            item.put("message", msg);
            item.put("fromuser", DB.execute(UserMapper.class, m -> m.findById(msg.fromuserid)));
            result.add(item);
        }
        return result;
    }
    
    public void send(long fromuserid, long touserid, String content) {
        Message m = new Message();
        m.fromuserid = fromuserid;
        m.touserid = touserid;
        m.content = content;
        DB.execute(MessageMapper.class, mapper -> mapper.insert(m));
    }
    
    public int countUnread(long userid) {
        return DB.execute(MessageMapper.class, m -> m.countUnread(userid));
    }
}
