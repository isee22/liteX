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

@Tag(name = "举报", description = "举报用户或推文")
public class ReportController {
    
    public Routes routes() {
        return new Routes(this)
            .post("/api/report/user/:id", this::reportUser, "reportUser")
            .post("/api/report/tweet/:id", this::reportTweet, "reportTweet")
            .end();
    }
    
    @Operation(summary = "举报用户", description = "举报指定用户")
    @Parameter(name = "id", description = "目标用户ID", required = true, in = ParameterIn.PATH)
    void reportUser(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        String reason = (String) body.get("reason");
        Service.report.reportUser(userId, targetId, reason);
        ctx.ok();
    }
    
    @Operation(summary = "举报推文", description = "举报指定推文")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void reportTweet(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        String reason = (String) body.get("reason");
        Service.report.reportTweet(userId, tweetId, reason);
        ctx.ok();
    }
}
