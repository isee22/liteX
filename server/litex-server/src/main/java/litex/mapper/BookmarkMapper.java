package litex.mapper;

import litex.entity.Bookmark;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface BookmarkMapper {
    
    @Select("SELECT * FROM bookmark WHERE userid = #{userid} AND tweetid = #{tweetid}")
    Bookmark find(@Param("userid") Long userid, @Param("tweetid") Long tweetid);
    
    @Select("SELECT * FROM bookmark WHERE userid = #{userid} ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Bookmark> findByUserId(@Param("userid") Long userid, @Param("offset") int offset, @Param("limit") int limit);
    
    @Insert("INSERT INTO bookmark (userid, tweetid, createdat) VALUES (#{userid}, #{tweetid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Bookmark bookmark);
    
    @Delete("DELETE FROM bookmark WHERE userid = #{userid} AND tweetid = #{tweetid}")
    int delete(@Param("userid") Long userid, @Param("tweetid") Long tweetid);
    
    @Delete("DELETE FROM bookmark WHERE userid = #{userid}")
    int deleteByUserId(@Param("userid") Long userid);
}
