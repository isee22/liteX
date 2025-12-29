package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;
import litejava.util.Maps;

@Tag(name = "书签", description = "推文收藏")
public class BookmarkController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/bookmarks", this::list, "list")
            .delete("/api/bookmarks", this::clearAll, "clearAll")
            .post("/api/tweets/:id/bookmark", this::toggle, "toggle")
            .end();
    }
    
    @Operation(summary = "书签列表", description = "获取收藏的推文")
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.bookmark.list(userId, page, size));
    }
    
    @Operation(summary = "清空书签", description = "清除所有收藏")
    void clearAll(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Service.bookmark.clearAll(userId);
        ctx.ok();
    }
    
    @Operation(summary = "收藏/取消收藏", description = "切换推文收藏状态")
    @Parameter(name = "id", description = "推文ID", required = true, in = ParameterIn.PATH)
    void toggle(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        boolean bookmarked = Service.bookmark.toggle(userId, tweetId);
        ctx.ok(Maps.of("bookmarked", bookmarked));
    }
}
