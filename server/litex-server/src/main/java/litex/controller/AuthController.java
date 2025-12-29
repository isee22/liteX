package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litex.entity.User;
import litex.util.JwtUtil;
import litejava.Context;
import litejava.Routes;
import litejava.util.Maps;
import java.util.Map;

@Tag(name = "认证", description = "用户登录注册相关接口")
public class AuthController {
    
    public Routes routes() {
        return new Routes(this)
            .post("/api/auth/login", this::login, "login")
            .post("/api/auth/register", this::register, "register")
            .post("/api/auth/forgot-password", this::forgotPassword, "forgotPassword")
            .end();
    }
    
    @Operation(summary = "用户登录", description = "使用用户名和密码登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"code\":0,\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"user\":{\"id\":1,\"username\":\"test\",\"nickname\":\"Test User\"}}}")
        )),
        @ApiResponse(responseCode = "400", description = "登录失败", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"code\":-1,\"msg\":\"用户名或密码错误\"}")
        ))
    })
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
    
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"code\":0,\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"user\":{\"id\":1,\"username\":\"newuser\",\"nickname\":\"New User\"}}}")
        )),
        @ApiResponse(responseCode = "400", description = "注册失败", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"code\":-1,\"msg\":\"用户名已存在\"}")
        ))
    })
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
    
    @Operation(summary = "忘记密码", description = "发送密码重置邮件")
    void forgotPassword(Context ctx) {
        Map<String, Object> body = ctx.bindJSON();
        String email = (String) body.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            ctx.fail("请输入邮箱");
            return;
        }
        
        // TODO: 实际发送邮件逻辑
        User user = Service.user.findByEmail(email);
        if (user == null) {
            ctx.fail("该邮箱未注册");
            return;
        }
        
        ctx.ok(Maps.of("msg", "重置链接已发送"));
    }
}
