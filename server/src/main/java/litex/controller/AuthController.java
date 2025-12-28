package litex.controller;

import litex.Service;
import litex.entity.User;
import litex.util.JwtUtil;
import litejava.Context;
import litejava.Routes;
import java.util.Map;

public class AuthController {
    
    public Routes routes() {
        return new Routes()
            .post("/api/auth/login", this::login)
            .post("/api/auth/register", this::register)
            .end();
    }
    
    void login(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        
        User user = Service.user.authenticate(username, password);
        if (user == null) {
            ctx.status(401).fail("用户名或密码错误");
            return;
        }
        
        String token = JwtUtil.generateToken(user.id);
        ctx.ok(Map.of("token", token, "user", user));
    }
    
    void register(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String nickname = (String) body.getOrDefault("nickname", username);
        String email = (String) body.get("email");
        
        User user = Service.user.register(username, password, nickname, email);
        if (user == null) {
            ctx.status(400).fail("用户名已存在");
            return;
        }
        
        String token = JwtUtil.generateToken(user.id);
        user.password = null;
        ctx.ok(Map.of("token", token, "user", user));
    }
}
