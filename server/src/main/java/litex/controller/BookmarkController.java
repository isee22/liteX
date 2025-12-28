package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

import java.util.Map;

public class BookmarkController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/bookmarks", this::list)
            .delete("/api/bookmarks", this::clearAll)
            .post("/api/tweets/:id/bookmark", this::toggle)
            .end();
    }
    
    void list(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.bookmark.list(userId, page, size));
    }
    
    void clearAll(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        Service.bookmark.clearAll(userId);
        ctx.ok("已清除所有书签");
    }
    
    void toggle(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        long tweetId = ctx.pathParamLong("id");
        boolean bookmarked = Service.bookmark.toggle(userId, tweetId);
        ctx.ok(Map.of("bookmarked", bookmarked));
    }
}
