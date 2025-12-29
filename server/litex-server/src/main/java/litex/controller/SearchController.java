package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.util.Auth;
import litejava.Context;
import litejava.Routes;

@Tag(name = "搜索", description = "搜索推文和用户")
public class SearchController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/search", this::search, "search")
            .get("/api/search/tweets", this::searchTweets, "searchTweets")
            .get("/api/search/users", this::searchUsers, "searchUsers")
            .get("/api/search/hashtag/:tag", this::searchHashtag, "searchHashtag")
            .end();
    }
    
    @Operation(summary = "综合搜索", description = "搜索推文")
    @Parameter(name = "q", description = "搜索关键词", in = ParameterIn.QUERY)
    @Parameter(name = "sort", description = "排序方式: top/latest", in = ParameterIn.QUERY)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void search(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        String sort = ctx.queryParam("sort", "top");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchTweets(q, userId != null ? userId : 0L, page, size));
    }
    
    @Operation(summary = "搜索推文", description = "搜索推文内容")
    @Parameter(name = "q", description = "搜索关键词", in = ParameterIn.QUERY)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void searchTweets(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchTweets(q, userId != null ? userId : 0L, page, size));
    }
    
    @Operation(summary = "搜索用户", description = "搜索用户名或昵称")
    @Parameter(name = "q", description = "搜索关键词", in = ParameterIn.QUERY)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void searchUsers(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String q = ctx.queryParam("q", "");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchUsers(q, userId != null ? userId : 0L, page, size));
    }
    
    @Operation(summary = "话题搜索", description = "搜索指定话题的推文")
    @Parameter(name = "tag", description = "话题标签", required = true, in = ParameterIn.PATH)
    @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY)
    @Parameter(name = "size", description = "每页数量", in = ParameterIn.QUERY)
    void searchHashtag(Context ctx) {
        Long userId = Auth.getUserId(ctx);
        String tag = ctx.pathParam("tag");
        int page = ctx.queryParamInt("page", 1);
        int size = ctx.queryParamInt("size", 20);
        ctx.ok(Service.search.searchByHashtag(tag, userId != null ? userId : 0L, page, size));
    }
}
