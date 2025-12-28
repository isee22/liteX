package litex.controller;

import litex.Service;
import litejava.Context;
import litejava.Routes;

public class TrendController {
    
    public Routes routes() {
        return new Routes()
            .get("/api/trends", this::list)
            .end();
    }
    
    void list(Context ctx) {
        ctx.ok(Service.trend.getHotTrends());
    }
}
