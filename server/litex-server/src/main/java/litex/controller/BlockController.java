package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

@Tag(name = "屏蔽", description = "用户屏蔽管理")
public class BlockController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/blocks", this::list, "list")
            .post("/api/user/:id/block", this::block, "block")
            .delete("/api/user/:id/block", this::unblock, "unblock")
            .end();
    }
    
    @Operation(summary = "屏蔽列表", description = "获取已屏蔽的用户")
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.block.list(userId));
    }
    
    @Operation(summary = "屏蔽用户", description = "屏蔽指定用户")
    @Parameter(name = "id", description = "目标用户ID", required = true, in = ParameterIn.PATH)
    void block(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        Integer days = body.get("days") != null ? ((Number) body.get("days")).intValue() : null;
        Service.block.block(userId, targetId, days);
        ctx.ok();
    }
    
    @Operation(summary = "取消屏蔽", description = "取消屏蔽用户")
    @Parameter(name = "id", description = "目标用户ID", required = true, in = ParameterIn.PATH)
    void unblock(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Service.block.unblock(userId, targetId);
        ctx.ok();
    }
}
