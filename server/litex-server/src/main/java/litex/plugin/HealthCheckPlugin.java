package litex.plugin;

import litejava.Plugin;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 健康检查插件 - 从客户端视角验证 API 功能是否正常
 * 
 * 使用方式：
 * app.use(new HealthCheckPlugin());
 * app.run();
 */
public class HealthCheckPlugin extends Plugin {
    
    public List<CheckResult> results = new ArrayList<>();
    public boolean allPassed = true;
    public boolean enabled = true;
    
    public String baseUrl;
    
    @Override
    public void config() {
        enabled = app.conf.getBool("healthcheck", "enabled", true);
        int port = app.conf.getInt("server", "port", 8080);
        baseUrl = "http://localhost:" + port + "/api";
    }
    
    @Override
    public void onStart() {
        if (enabled) {
            verify();
        }
    }
    
    /**
     * 执行健康检查
     */
    public void verify() {
        if (!enabled) return;
        
        System.out.println("\n========== 功能检查 ==========");
        
        // 公开接口（无需认证）
        check("首页时间线", () -> httpGet("/tweets/timeline"));
        check("热门话题", () -> httpGet("/trends"));
        check("搜索推文", () -> httpGet("/search/tweets?q=test"));
        check("搜索用户", () -> httpGet("/search/users?q=test"));
        
        // 输出结果
        System.out.println("------------------------------");
        if (allPassed) {
            System.out.println("✅ 所有功能检查通过 (" + results.size() + " 项)");
        } else {
            long failed = results.stream().filter(r -> !r.passed).count();
            System.out.println("❌ " + failed + " 项检查失败:");
            for (CheckResult r : results) {
                if (!r.passed) {
                    System.out.println("   - " + r.name + ": " + r.error);
                }
            }
        }
        System.out.println("==============================\n");
    }
    
    private void check(String name, Runnable checker) {
        try {
            checker.run();
            results.add(new CheckResult(name, true, null));
            System.out.println("✓ " + name);
        } catch (Exception e) {
            results.add(new CheckResult(name, false, e.getMessage()));
            System.out.println("✗ " + name + " - " + e.getMessage());
            allPassed = false;
        }
    }
    
    @SuppressWarnings("deprecation")
    private void httpGet(String path) {
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("HTTP " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static class CheckResult {
        public String name;
        public boolean passed;
        public String error;
        
        public CheckResult(String name, boolean passed, String error) {
            this.name = name;
            this.passed = passed;
            this.error = error;
        }
    }
}
