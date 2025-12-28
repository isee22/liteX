package litex.controller;

import litex.Service;
import litex.entity.User;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import java.util.Map;

public class UserController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/user/me", this::me)
            .get("/api/user/recommend", this::recommend)
            .get("/api/user/:id", this::getUser)
            .get("/api/user/:id/tweets", this::getUserTweets)
            .get("/api/user/:id/likes", this::getUserLikes)
            .get("/api/user/:id/followers", this::getFollowers)
            .get("/api/user/:id/following", this::getFollowing)
            .post("/api/user/:id/follow", this::follow)
            .put("/api/user/profile", this::updateProfile)
            .end();
    }
    
    void me(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.user.findById(userId));
    }
    
    void recommend(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int limit = ctx.queryParamInt("limit", 10);
        ctx.ok(Service.user.getRecommend(userId, limit));
    }
    
    void getUser(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? currentUserId : Long.parseLong(idStr);
        Map<String, Object> result = Service.user.getUserWithFollowStatus(userId, currentUserId);
        if (result == null) {
            ctx.status(404).fail("用户不存在");
            return;
        }
        ctx.ok(result);
    }
    
    void getUserTweets(Context ctx) {
        String idStr = ctx.pathParam("id");
        long userId = "me".equals(idStr) ? Auth.getUserId(ctx) : Long.parseLong(idStr);
        ctx.ok(Service.tweet.findByUserId(userId));
    }
    
    void getUserLikes(Context ctx) {
        String idStr = ctx.pathParam("id");
        long userId = "me".equals(idStr) ? Auth.getUserId(ctx) : Long.parseLong(idStr);
        ctx.ok(Service.user.getLikedTweets(userId));
    }
    
    void getFollowers(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? (currentUserId != null ? currentUserId : 0L) : Long.parseLong(idStr);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.user.getFollowers(userId, currentUserId != null ? currentUserId : 0L, page, size));
    }
    
    void getFollowing(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? (currentUserId != null ? currentUserId : 0L) : Long.parseLong(idStr);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.user.getFollowing(userId, currentUserId != null ? currentUserId : 0L, page, size));
    }
    
    void follow(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Service.user.toggleFollow(userId, targetId);
        ctx.ok("操作成功");
    }
    
    void updateProfile(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        Service.user.updateProfile(userId, body);
        ctx.ok("更新成功");
    }
}
