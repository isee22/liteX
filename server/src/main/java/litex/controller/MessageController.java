package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import java.util.Map;

public class MessageController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/messages/chats", this::getChats)
            .get("/api/messages/chat/:userId", this::getChatMessages)
            .get("/api/messages/unread", this::unreadCount)
            .post("/api/messages/send", this::send)
            .end();
    }
    
    void getChats(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.message.getChats(userId));
    }
    
    void getChatMessages(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("userId");
        ctx.ok(Service.message.getMessages(userId, targetId));
    }
    
    void unreadCount(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int count = Service.message.countUnread(userId);
        ctx.ok(Map.of("count", count));
    }
    
    void send(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        long toUserId = ((Number) body.get("toUserId")).longValue();
        String content = (String) body.get("content");
        Service.message.send(userId, toUserId, content);
        ctx.ok("发送成功");
    }
}
