package litex;

import litex.controller.*;
import litex.middleware.AuthMiddleware;
import litex.plugin.HealthCheckPlugin;
import litejava.App;
import litejava.plugins.LiteJava;
import litejava.plugins.database.HibernatePlugin;
import litejava.plugins.database.MyBatisPlugin;
import litejava.plugins.json.JacksonPlugin;
import litejava.plugins.security.CorsPlugin;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Application {
    
    public static void main(String[] args) {
        // 配置 Jackson 支持 Java 8 日期时间，输出 ISO 字符串格式
        JacksonPlugin.mapper.registerModule(new JavaTimeModule());
        JacksonPlugin.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        App app = LiteJava.create();
        
        // Hibernate - 只用于 DDL 自动建表
        app.use(new HibernatePlugin());
        
        // MyBatis - 用于查询（mapper 通过配置自动扫描）
        app.use(new MyBatisPlugin());
        
        // 健康检查
        app.use(new HealthCheckPlugin());
        
        // CORS
        app.use(new CorsPlugin());
        
        // JWT 认证中间件 - 排除公开接口
        app.use(new AuthMiddleware()
            .exclude("/api/auth")
            .exclude("/api/tweets")
            .exclude("/api/trends")
            .exclude("/api/search")
            .exclude("/api/user"));
        
        // 路由
        app.register(new AuthController().routes());
        app.register(new TweetController().routes());
        app.register(new UserController().routes());
        app.register(new NotificationController().routes());
        app.register(new TrendController().routes());
        app.register(new MessageController().routes());
        app.register(new SearchController().routes());
        app.register(new UploadController().routes());
        app.register(new BookmarkController().routes());
        app.register(new BlockController().routes());
        app.register(new ReportController().routes());
        app.register(new PollController().routes());
        
        app.run();
    }
}
