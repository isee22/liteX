package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

public class PollController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/tweets/:id/poll", this::getPoll)
            .post("/api/tweets/:id/vote", this::vote)
            .end();
    }
    
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
    
    void vote(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        long optionId = ((Number) body.get("optionId")).longValue();
        boolean success = Service.poll.vote(tweetId, userId, optionId);
        if (success) {
            ctx.ok("投票成功");
        } else {
            ctx.status(400).fail("投票失败");
        }
    }
}
