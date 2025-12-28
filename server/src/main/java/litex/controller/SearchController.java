package litex.controller;

import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

public class SearchController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/search", this::search)
            .get("/api/search/tweets", this::searchTweets)
            .get("/api/search/users", this::searchUsers)
            .get("/api/search/hashtag/:tag", this::searchHashtag)
            .end();
    }
    
    void search(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        String sort = ctx.queryParam("sort", "top");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchTweets(q, userId != null ? userId : 0L, page, size));
    }
    
    void searchTweets(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchTweets(q, userId != null ? userId : 0L, page, size));
    }
    
    void searchUsers(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchUsers(q, userId != null ? userId : 0L, page, size));
    }
    
    void searchHashtag(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String tag = ctx.pathParam("tag");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchByHashtag(tag, userId != null ? userId : 0L, page, size));
    }
}
