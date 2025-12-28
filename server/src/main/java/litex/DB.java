package litex;

import litejava.plugins.database.MyBatisPlugin;
import java.util.function.Function;

public class DB {
    public static <T, R> R execute(Class<T> mapperClass, Function<T, R> action) {
        return MyBatisPlugin.instance.execute(mapperClass, action);
    }
}
