package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litejava.Context;
import litejava.Routes;

@Tag(name = "热门", description = "热门话题")
public class TrendController {
    
    public Routes routes() {
        return new Routes(this)
            .get("/api/trends", this::list, "list")
            .end();
    }
    
    @Operation(summary = "热门话题", description = "获取热门话题列表")
    void list(Context ctx) {
        ctx.ok(Service.trend.getHotTrends());
    }
}
