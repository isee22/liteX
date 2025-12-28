package litex;

import litejava.plugins.database.MyBatisPlugin;
import java.util.function.Function;

public class DB {
    private static MyBatisPlugin mybatis;
    
    public static void init(MyBatisPlugin plugin) {
        mybatis = plugin;
    }
    
    public static <T, R> R execute(Class<T> mapperClass, Function<T, R> action) {
        return mybatis.execute(mapperClass, action);
    }
}
