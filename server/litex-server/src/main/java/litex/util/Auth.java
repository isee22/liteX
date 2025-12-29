package litex.util;

import litejava.Context;

public class Auth {
    public static Long getUserId(Context ctx) {
        String auth = ctx.headers.get("Authorization");
        if (auth == null) auth = ctx.headers.get("authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        return JwtUtil.getUserId(auth.substring(7));
    }
}
