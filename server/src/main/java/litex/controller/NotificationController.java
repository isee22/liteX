package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

public class NotificationController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/notifications", this::list)
            .get("/api/notifications/unread", this::unreadCount)
            .post("/api/notifications/read", this::markRead)
            .end();
    }
    
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String type = ctx.queryParam("type", "all");
        ctx.ok(Service.notification.list(userId, type));
    }
    
    void unreadCount(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int count = Service.notification.countUnread(userId);
        ctx.ok(Map.of("count", count));
    }
    
    void markRead(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Service.notification.markAllRead(userId);
        ctx.ok("已读");
    }
}
