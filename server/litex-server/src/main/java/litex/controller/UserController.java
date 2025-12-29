package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.entity.User;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import java.util.Map;

@Tag(name = "用户", description = "用户信息和关系管理")
public class UserController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/user/me", this::me, "me")
            .get("/api/user/recommend", this::recommend, "recommend")
            .get("/api/user/muted", this::getMuted, "getMuted")
            .get("/api/user/:id", this::getUser, "getUser")
            .get("/api/user/:id/tweets", this::getUserTweets, "getUserTweets")
            .get("/api/user/:id/replies", this::getUserReplies, "getUserReplies")
            .get("/api/user/:id/media", this::getUserMedia, "getUserMedia")
            .get("/api/user/:id/likes", this::getUserLikes, "getUserLikes")
            .get("/api/user/:id/followers", this::getFollowers, "getFollowers")
            .get("/api/user/:id/following", this::getFollowing, "getFollowing")
            .post("/api/user/:id/follow", this::follow, "follow")
            .post("/api/user/:id/mute", this::mute, "mute")
            .post("/api/user/password", this::changePassword, "changePassword")
            .put("/api/user/profile", this::updateProfile, "updateProfile")
            .end();
    }
    
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    void me(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.user.findById(userId));
    }
    
    @Operation(summary = "推荐关注", description = "获取推荐关注的用户列表")
    @Parameter(name = "limit", description = "返回数量", in = ParameterIn.QUERY)
    void recommend(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int limit = ctx.queryParamInt("limit", 10);
        ctx.ok(Service.user.getRecommend(userId, limit));
    }
    
    @Operation(summary = "获取用户信息", description = "根据ID获取用户详情")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
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
    
    @Operation(summary = "用户推文列表", description = "获取用户发布的推文")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    void getUserTweets(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? currentUserId : Long.parseLong(idStr);
        ctx.ok(Service.user.getUserTweets(userId, currentUserId));
    }
    
    @Operation(summary = "用户回复列表", description = "获取用户的回复")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    void getUserReplies(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? currentUserId : Long.parseLong(idStr);
        ctx.ok(Service.user.getUserReplies(userId, currentUserId));
    }
    
    @Operation(summary = "用户媒体列表", description = "获取用户发布的媒体内容")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    void getUserMedia(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? currentUserId : Long.parseLong(idStr);
        ctx.ok(Service.user.getUserMedia(userId, currentUserId));
    }
    
    @Operation(summary = "用户喜欢列表", description = "获取用户点赞的推文")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    void getUserLikes(Context ctx) {
        String idStr = ctx.pathParam("id");
        long userId = "me".equals(idStr) ? Auth.getUserId(ctx) : Long.parseLong(idStr);
        ctx.ok(Service.user.getLikedTweets(userId));
    }
    
    @Operation(summary = "粉丝列表", description = "获取用户的粉丝")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void getFollowers(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? (currentUserId != null ? currentUserId : 0L) : Long.parseLong(idStr);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.user.getFollowers(userId, currentUserId != null ? currentUserId : 0L, page, size));
    }
    
    @Operation(summary = "关注列表", description = "获取用户关注的人")
    @Parameter(name = "id", description = "用户ID或me", required = true, in = ParameterIn.PATH)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void getFollowing(Context ctx) {
        String idStr = ctx.pathParam("id");
        Long currentUserId = Auth.getUserId(ctx);
        long userId = "me".equals(idStr) ? (currentUserId != null ? currentUserId : 0L) : Long.parseLong(idStr);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.user.getFollowing(userId, currentUserId != null ? currentUserId : 0L, page, size));
    }
    
    @Operation(summary = "关注/取关", description = "切换关注状态")
    @Parameter(name = "id", description = "目标用户ID", required = true, in = ParameterIn.PATH)
    void follow(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Service.user.toggleFollow(userId, targetId);
        ctx.ok();
    }
    
    @Operation(summary = "更新资料", description = "更新用户个人资料")
    void updateProfile(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        Service.user.updateProfile(userId, body);
        ctx.ok();
    }
    
    @Operation(summary = "修改密码", description = "修改登录密码")
    void changePassword(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        String oldPassword = (String) body.get("oldPassword");
        String newPassword = (String) body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            ctx.fail("请填写完整");
            return;
        }
        if (newPassword.length() < 6) {
            ctx.fail("密码至少6个字符");
            return;
        }
        boolean success = Service.user.changePassword(userId, oldPassword, newPassword);
        if (success) {
            ctx.ok();
        } else {
            ctx.fail("原密码错误");
        }
    }
    
    @Operation(summary = "静音列表", description = "获取已静音的用户")
    void getMuted(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        ctx.ok(Service.user.getMutedUsers(userId));
    }
    
    @Operation(summary = "静音/取消静音", description = "切换静音状态")
    @Parameter(name = "id", description = "目标用户ID", required = true, in = ParameterIn.PATH)
    void mute(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long targetId = ctx.pathParamLong("id");
        Service.user.toggleMute(userId, targetId);
        ctx.ok();
    }
}
