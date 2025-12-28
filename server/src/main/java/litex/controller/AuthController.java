package litex.controller;

import litex.Service;
import litex.entity.User;
import litex.util.JwtUtil;
import litejava.Context;
import litejava.Routes;
import litejava.util.Maps;
import java.util.Map;

public class AuthController {
    
    public Routes routes() {
        return new Routes()
            .post("/api/auth/login", this::login)
            .post("/api/auth/register", this::register)
            .post("/api/auth/forgot-password", this::forgotPassword)
            .end();
    }
    
    void login(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        
        User user = Service.user.authenticate(username, password);
        if (user == null) {
            ctx.fail("用户名或密码错误");
            return;
        }
        
        String token = JwtUtil.generateToken(user.id);
        ctx.ok(Maps.of("token", token, "user", user));
    }
    
    void register(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String nickname = (String) body.getOrDefault("nickname", username);
        String email = (String) body.get("email");
        
        User user = Service.user.register(username, password, nickname, email);
        if (user == null) {
            ctx.fail("用户名已存在");
            return;
        }
        
        String token = JwtUtil.generateToken(user.id);
        user.password = null;
        ctx.ok(Maps.of("token", token, "user", user));
    }
    
    void forgotPassword(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String email = (String) body.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            ctx.fail("请输入邮箱");
            return;
        }
        
        // TODO: 实际发送邮件逻辑
        // 这里只是模拟，实际需要集成邮件服务
        User user = Service.user.findByEmail(email);
        if (user == null) {
            ctx.fail("该邮箱未注册");
            return;
        }
        
        // 模拟发送成功
        ctx.ok(Maps.of("msg", "重置链接已发送"));
    }
}
