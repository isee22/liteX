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

@Tag(name = "投票", description = "推文投票功能")
public class PollController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/tweets/:id/poll", this::getPoll, "getPoll")
            .post("/api/tweets/:id/vote", this::vote, "vote")
            .end();
    }
    
    @Operation(summary = "投票详情", description = "获取推文的投票信息")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void getPoll(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> result = Service.poll.getPoll(tweetId, userId);
        if (result == null) {
            ctx.status(404).fail("投票不存在");
            return;
        }
        ctx.ok(result);
    }
    
    @Operation(summary = "投票", description = "对推文投票选择选项")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void vote(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        long optionId = ((Number) body.get("optionId")).longValue();
        boolean success = Service.poll.vote(tweetId, userId, optionId);
        if (success) {
            ctx.ok();
        } else {
            ctx.status(400).fail("投票失败");
        }
    }
}
