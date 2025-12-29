package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import litejava.util.Maps;

@Tag(name = "通知", description = "通知消息管理")
public class NotificationController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/notifications", this::list, "list")
            .get("/api/notifications/unread", this::unreadCount, "unreadCount")
            .post("/api/notifications/read", this::markRead, "markRead")
            .end();
    }
    
    @Operation(summary = "通知列表", description = "获取通知列表")
    @Parameter(name = "type", description = "通知类型: all/mention/like/follow", in = ParameterIn.QUERY)
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String type = ctx.queryParam("type", "all");
        ctx.ok(Service.notification.list(userId, type));
    }
    
    @Operation(summary = "未读数量", description = "获取未读通知数量")
    void unreadCount(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int count = Service.notification.countUnread(userId);
        ctx.ok(Maps.of("count", count));
    }
    
    @Operation(summary = "标记已读", description = "标记所有通知为已读")
    void markRead(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Service.notification.markAllRead(userId);
        ctx.ok();
    }
}
