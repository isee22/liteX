package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import java.util.Map;

public class TweetController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/tweets/timeline", this::timeline)
            .post("/api/tweets", this::create)
            .get("/api/tweets/:id", this::get)
            .delete("/api/tweets/:id", this::delete)
            .post("/api/tweets/:id/like", this::like)
            .post("/api/tweets/:id/retweet", this::retweet)
            .post("/api/tweets/:id/quote", this::quote)
            .get("/api/tweets/:id/comments", this::getComments)
            .post("/api/tweets/:id/comments", this::addComment)
            .end();
    }
    
    void timeline(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.tweet.getTimeline(userId, page, size));
    }
    
    void create(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        String content = (String) body.get("content");
        String images = body.get("images") != null ? body.get("images").toString() : null;
        ctx.ok(Service.tweet.create(userId, content, images));
    }
    
    void get(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long id = ctx.pathParamLong("id");
        Map<String, Object> result = Service.tweet.findById(id, userId);
        if (result == null) {
            ctx.status(404).fail("推文不存在");
            return;
        }
        ctx.ok(result);
    }
    
    void delete(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long id = ctx.pathParamLong("id");
        Service.tweet.delete(id, userId);
        ctx.ok("删除成功");
    }
    
    void like(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Service.tweet.toggleLike(tweetId, userId);
        ctx.ok("操作成功");
    }
    
    void retweet(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Service.tweet.retweet(tweetId, userId);
        ctx.ok("转推成功");
    }
    
    void quote(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        String content = (String) body.get("content");
        Map<String, Object> result = Service.tweet.quote(tweetId, userId, content);
        if (result == null) {
            ctx.status(404).fail("推文不存在");
            return;
        }
        ctx.ok(result);
    }
    
    void getComments(Context ctx) {
        long tweetId = ctx.pathParamLong("id");
        ctx.ok(Service.tweet.getComments(tweetId));
    }
    
    void addComment(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        Service.tweet.addComment(tweetId, userId, (String) body.get("content"));
        ctx.ok("评论成功");
    }
}
