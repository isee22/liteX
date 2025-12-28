package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

public class ReportController {
    
    public Routes routes() {
        return new Routes()
            .post("/api/report/user/:id", this::reportUser)
            .post("/api/report/tweet/:id", this::reportTweet)
            .end();
    }
    
    void reportUser(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        String reason = (String) body.get("reason");
        Service.report.reportUser(userId, targetId, reason);
        ctx.ok("举报成功");
    }
    
    void reportTweet(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        String reason = (String) body.get("reason");
        Service.report.reportTweet(userId, tweetId, reason);
        ctx.ok("举报成功");
    }
}
