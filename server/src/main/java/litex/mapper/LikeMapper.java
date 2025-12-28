package litex.mapper;

import litex.entity.Like;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface LikeMapper {
    
    @Select("SELECT * FROM tweetlike WHERE userid = #{userid} AND tweetid = #{tweetid}")
    Like find(@Param("userid") Long userid, @Param("tweetid") Long tweetid);
    
    @Select("SELECT * FROM tweetlike WHERE userid = #{userid}")
    List<Like> findByUserId(Long userid);
    
    @Insert("INSERT INTO tweetlike (userid, tweetid, createdat) VALUES (#{userid}, #{tweetid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);
    
    @Delete("DELETE FROM tweetlike WHERE id = #{id}")
    int delete(Long id);
}
