package litex.mapper;

import litex.entity.Trend;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface TrendMapper {
    
    @Select("SELECT * FROM trend ORDER BY count DESC LIMIT #{limit}")
    List<Trend> getHotTrends(int limit);
    
    @Select("SELECT * FROM trend WHERE tag = #{tag}")
    Trend findByTag(String tag);
    
    @Insert("INSERT INTO trend (tag, count, updatedat) VALUES (#{tag}, #{count}, #{updatedat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Trend trend);
    
    @Update("UPDATE trend SET count = #{count}, updatedat = #{updatedat} WHERE id = #{id}")
    int update(Trend trend);
}
