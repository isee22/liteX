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

@Tag(name = "推文", description = "推文发布、点赞、转推、评论")
public class TweetController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/tweets/timeline", this::timeline, "timeline")
            .get("/api/tweets/following", this::followingTimeline, "followingTimeline")
            .post("/api/tweets", this::create, "create")
            .get("/api/tweets/:id", this::get, "get")
            .delete("/api/tweets/:id", this::delete, "delete")
            .post("/api/tweets/:id/like", this::like, "like")
            .post("/api/tweets/:id/retweet", this::retweet, "retweet")
            .post("/api/tweets/:id/quote", this::quote, "quote")
            .get("/api/tweets/:id/comments", this::getComments, "getComments")
            .post("/api/tweets/:id/comments", this::addComment, "addComment")
            .end();
    }
    
    @Operation(summary = "推荐时间线", description = "获取推荐推文列表")
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void timeline(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.tweet.getTimeline(userId, page, size));
    }
    
    @Operation(summary = "关注时间线", description = "获取关注用户的推文")
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void followingTimeline(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.tweet.getFollowingTimeline(userId, page, size));
    }
    
    @Operation(summary = "发布推文", description = "发布新推文，支持图片")
    void create(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Map<String, Object> body = ctx.bindJSON();
        String content = (String) body.get("content");
        Object imagesObj = body.get("images");
        String images = null;
        if (imagesObj != null) {
            if (imagesObj instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) imagesObj;
                if (!list.isEmpty()) {
                    images = String.join(",", list.stream().map(Object::toString).toArray(String[]::new));
                }
            } else {
                String str = imagesObj.toString();
                if (!str.isEmpty() && !"[]".equals(str)) {
                    images = str;
                }
            }
        }
        ctx.ok(Service.tweet.create(userId, content, images));
    }
    
    @Operation(summary = "推文详情", description = "获取推文详情")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
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
    
    @Operation(summary = "删除推文", description = "删除自己的推文")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void delete(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long id = ctx.pathParamLong("id");
        Service.tweet.delete(id, userId);
        ctx.ok();
    }
    
    @Operation(summary = "点赞/取消点赞", description = "切换推文点赞状态")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void like(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Service.tweet.toggleLike(tweetId, userId);
        ctx.ok();
    }
    
    @Operation(summary = "转推", description = "转发推文")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void retweet(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Service.tweet.retweet(tweetId, userId);
        ctx.ok();
    }
    
    @Operation(summary = "引用推文", description = "带评论转发推文")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
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
    
    @Operation(summary = "评论列表", description = "获取推文的评论")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void getComments(Context ctx) {
        long tweetId = ctx.pathParamLong("id");
        ctx.ok(Service.tweet.getComments(tweetId));
    }
    
    @Operation(summary = "发表评论", description = "对推文发表评论")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void addComment(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        Map<String, Object> body = ctx.bindJSON();
        Service.tweet.addComment(tweetId, userId, (String) body.get("content"));
        ctx.ok();
    }
}
