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
import java.util.Map;

@Tag(name = "私信", description = "私信会话和消息")
public class MessageController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/messages/chats", this::getChats, "getChats")
            .get("/api/messages/chat/:userId", this::getChatMessages, "getChatMessages")
            .get("/api/messages/unread", this::unreadCount, "unreadCount")
            .post("/api/messages/send", this::send, "send")
            .end();
    }
    
    @Operation(summary = "会话列表", description = "获取私信会话列表")
    void getChats(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.message.getChats(userId));
    }
    
    @Operation(summary = "聊天记录", description = "获取与指定用户的聊天记录")
    @Parameter(name = "userId", description = "对方用户ID", required = true, in = ParameterIn.PATH)
    void getChatMessages(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("userId");
        ctx.ok(Service.message.getMessages(userId, targetId));
    }
    
    @Operation(summary = "未读数量", description = "获取未读私信数量")
    void unreadCount(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int count = Service.message.countUnread(userId);
        ctx.ok(Maps.of("count", count));
    }
    
    @Operation(summary = "发送私信", description = "发送私信给指定用户")
    void send(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        long toUserId = ((Number) body.get("toUserId")).longValue();
        String content = (String) body.get("content");
        Service.message.send(userId, toUserId, content);
        ctx.ok();
    }
}
