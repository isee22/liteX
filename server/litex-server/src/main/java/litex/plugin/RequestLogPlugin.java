package litex.plugin;

import litejava.Context;
import litejava.MiddlewarePlugin;
import litejava.Next;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 请求日志插件 - 打印请求参数、响应内容和耗时
 */
public class RequestLogPlugin extends MiddlewarePlugin {
    
    private static final Logger log = LoggerFactory.getLogger(RequestLogPlugin.class);
    
    @Override
    public void handle(Context ctx, Next next) throws Exception {
        long start = System.currentTimeMillis();
        
        // 打印请求信息
        StringBuilder req = new StringBuilder();
        req.append("\n┌─────────────────────────────────────────────────────────────");
        req.append("\n│ ").append(ctx.method).append(" ").append(ctx.path);
        if (ctx.query != null && !ctx.query.isEmpty()) {
            req.append("?").append(ctx.query);
        }
        if (!ctx.headers.isEmpty()) {
            String contentType = ctx.headers.get("Content-Type");
            if (contentType == null) contentType = ctx.headers.get("content-type");
            if (contentType != null) {
                req.append("\n│ Content-Type: ").append(contentType);
            }
            String auth = ctx.headers.get("Authorization");
            if (auth == null) auth = ctx.headers.get("authorization");
            if (auth != null) {
                req.append("\n│ Authorization: ").append(auth.length() > 30 ? auth.substring(0, 30) + "..." : auth);
            }
        }
        String reqBody = ctx.getString();
        if (reqBody != null && !reqBody.isEmpty()) {
            String body = reqBody.length() > 500 ? reqBody.substring(0, 500) + "..." : reqBody;
            req.append("\n│ Body: ").append(body);
        }
        log.info(req.toString());
        
        // 执行请求
        try {
            next.run();
        } finally {
            long cost = System.currentTimeMillis() - start;
            
            // 打印响应信息
            StringBuilder res = new StringBuilder();
            res.append("\n│ Status: ").append(ctx.getResponseStatus());
            byte[] respBytes = ctx.getResponseBody();
            if (respBytes != null && respBytes.length > 0) {
                String respBody = new String(respBytes, StandardCharsets.UTF_8);
                if (respBody.length() > 500) {
                    respBody = respBody.substring(0, 500) + "...";
                }
                res.append("\n│ Response: ").append(respBody);
            }
            res.append("\n│ Cost: ").append(cost).append("ms");
            res.append("\n└─────────────────────────────────────────────────────────────");
            log.info(res.toString());
        }
    }
}
