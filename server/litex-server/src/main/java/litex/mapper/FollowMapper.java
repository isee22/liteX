package litex.mapper;

import litex.entity.Follow;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface FollowMapper {
    
    @Select("SELECT * FROM follow WHERE userid = #{userid} AND targetid = #{targetid}")
    Follow find(@Param("userid") Long userid, @Param("targetid") Long targetid);
    
    @Insert("INSERT INTO follow (userid, targetid, createdat) VALUES (#{userid}, #{targetid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Follow follow);
    
    @Delete("DELETE FROM follow WHERE id = #{id}")
    int delete(Long id);
    
    // Find users who follow the target user (followers)
    @Select("SELECT userid FROM follow WHERE targetid = #{targetid} ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Long> findFollowers(@Param("targetid") Long targetid, @Param("offset") int offset, @Param("limit") int limit);
    
    // Find users that the user is following
    @Select("SELECT targetid FROM follow WHERE userid = #{userid} ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Long> findFollowing(@Param("userid") Long userid, @Param("offset") int offset, @Param("limit") int limit);
}
