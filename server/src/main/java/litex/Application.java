package litex;

import litex.controller.*;
import litex.middleware.AuthMiddleware;
import litex.plugin.RequestLogPlugin;
import litex.plugin.BotPlugin;
import litejava.plugins.AutoConfigPlugin;
import litejava.App;
import litejava.plugins.LiteJava;
import litejava.plugins.database.HibernatePlugin;
import litejava.plugins.database.MyBatisPlugin;
import litejava.plugins.dataSource.HikariPlugin;
import litejava.plugins.json.JacksonPlugin;
import litejava.plugins.security.CorsPlugin;
import litejava.plugins.http.ExceptionPlugin;
import litejava.plugin.StaticFilePlugin;
import litejava.util.Maps;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
        // 配置 Jackson 支持 Java 8 日期时间，输出 ISO 字符串格式
        JacksonPlugin.mapper.registerModule(new JavaTimeModule());
        JacksonPlugin.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        App app = LiteJava.create();
        
        // 自动装配边角插件（根据配置文件）
        app.use(new AutoConfigPlugin());
        
        // 数据源
        HikariPlugin hikari = new HikariPlugin();
        app.use(hikari);
        
        // Hibernate - 只用于 DDL 自动建表
        app.use(new HibernatePlugin());
        
        // MyBatis - 用于查询（mapper 通过配置自动扫描）
        MyBatisPlugin mybatis = new MyBatisPlugin(hikari);
        app.use(mybatis);
        
        // 初始化 DB 工具类
        DB.init(mybatis);
        
        // 初始化 UploadService
        Service.upload.init(app.conf);
        
        // 静态文件服务 - 访问上传的文件
        String uploadPath = app.conf.getString("upload", "path", "uploads");
        app.use(new StaticFilePlugin("/uploads", uploadPath, true));
        
        // CORS
        app.use(new CorsPlugin());
        
        // 全局异常处理 - 自动捕获异常，返回统一格式
        // 业务代码用 ctx.error(400, "参数错误") 抛出异常
        app.use(new ExceptionPlugin((ctx, e) -> {
            int status = 500;
            if (e instanceof litejava.exception.LiteJavaException) {
                status = ((litejava.exception.LiteJavaException) e).statusCode;
            }
            
            // 根据状态码返回友好消息
            String msg = e.getMessage();
            
            // switch (status) {
            //     case 400: msg = "参数错误"; break;
            //     case 404: msg = "接口不存在"; break;
            //     case 405: msg = "请求方法不允许"; break;
            // }
            
            if (status >= 500) {
                log.error("请求异常: {} {}", ctx.method, ctx.path, e);
            }
            ctx.status(status).json(Maps.of("code", status, "msg", msg, "data", null));
        }));
        
        // JWT 认证中间件 - 排除公开接口
        app.use(new AuthMiddleware()
            .exclude("/health")
            .exclude("/api/auth")
            .exclude("/api/tweets")
            .exclude("/api/trends")
            .exclude("/api/search")
            .exclude("/api/user")
            .exclude("/uploads"));
        
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
        
        // 机器人插件
        BotPlugin bot = new BotPlugin();
        bot.userService = Service.user;
        bot.tweetService = Service.tweet;
        bot.messageService = Service.message;
        bot.trendService = Service.trend;
        app.use(bot);
        
        app.run();
    }
}
