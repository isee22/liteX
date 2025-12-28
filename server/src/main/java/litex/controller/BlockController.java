package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

public class BlockController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/blocks", this::list)
            .post("/api/user/:id/block", this::block)
            .delete("/api/user/:id/block", this::unblock)
            .end();
    }
    
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.block.list(userId));
    }
    
    void block(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        Integer days = body.get("days") != null ? ((Number) body.get("days")).intValue() : null;
        Service.block.block(userId, targetId, days);
        ctx.ok("已屏蔽");
    }
    
    void unblock(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Service.block.unblock(userId, targetId);
        ctx.ok("已取消屏蔽");
    }
}
