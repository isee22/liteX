package litex.mapper;

import litex.entity.Mute;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface MuteMapper {
    
    @Select("SELECT * FROM mute WHERE userid = #{userid} AND targetid = #{targetid}")
    Mute find(@Param("userid") Long userid, @Param("targetid") Long targetid);
    
    @Select("SELECT * FROM mute WHERE userid = #{userid} ORDER BY createdat DESC")
    List<Mute> findByUserId(Long userid);
    
    @Insert("INSERT INTO mute (userid, targetid, createdat) VALUES (#{userid}, #{targetid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Mute mute);
    
    @Delete("DELETE FROM mute WHERE id = #{id}")
    int delete(Long id);
}
