package litex.middleware;

import litejava.Context;
import litejava.MiddlewarePlugin;
import litejava.Next;
import litejava.util.Maps;
import litex.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证中间件
 * 
 * 验证请求中的 JWT token，提取 userid 存入 Context.state
 * 支持配置排除路径（如 /api/auth/*）
 */
public class AuthMiddleware extends MiddlewarePlugin {
    
    /** 排除的路径前缀列表，这些路径不需要认证 */
    public List<String> excludePaths = new ArrayList<>();
    
    public AuthMiddleware() {}
    
    public AuthMiddleware(List<String> excludePaths) {
        this.excludePaths = excludePaths != null ? excludePaths : new ArrayList<>();
    }
    
    /**
     * 添加排除路径
     * @param path 路径前缀，如 "/api/auth"
     * @return this（支持链式调用）
     */
    public AuthMiddleware exclude(String path) {
        this.excludePaths.add(path);
        return this;
    }
    
    @Override
    public void handle(Context ctx, Next next) throws Exception {
        // 只拦截 /api/ 开头的路径，其他路径放行
        if (!ctx.path.startsWith("/api/")) {
            next.run();
            return;
        }
        
        // 检查是否为排除路径
        if (isExcludedPath(ctx.path)) {
            next.run();
            return;
        }
        
        // 获取 Authorization header
        String auth = ctx.headers.get("Authorization");
        if (auth == null) {
            auth = ctx.headers.get("authorization");
        }
        
        // 检查 header 是否存在
        if (auth == null || auth.isEmpty()) {
            ctx.json(Maps.of("code", 401, "msg", "未登录"));
            return;
        }
        
        // 检查 Bearer 格式
        if (!auth.startsWith("Bearer ")) {
            ctx.json(Maps.of("code", 401, "msg", "无效的token"));
            return;
        }
        
        // 提取并验证 token
        String token = auth.substring(7);
        Long userid = JwtUtil.getUserId(token);
        
        if (userid == null) {
            ctx.json(Maps.of("code", 401, "msg", "token已过期"));
            return;
        }
        
        // 存储 userid 到 Context.state
        ctx.state.put("userid", userid);
        
        // 继续执行后续中间件和 handler
        next.run();
    }
    
    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcludedPath(String path) {
        if (path == null) return false;
        
        for (String excludePath : excludePaths) {
            // 支持通配符匹配，如 /api/auth/* 匹配 /api/auth/login
            if (excludePath.endsWith("/*")) {
                String prefix = excludePath.substring(0, excludePath.length() - 2);
                if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                    return true;
                }
            } else if (path.equals(excludePath) || path.startsWith(excludePath + "/")) {
                return true;
            }
        }
        return false;
    }
}
