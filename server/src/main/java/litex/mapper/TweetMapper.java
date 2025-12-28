package litex.mapper;

import litex.entity.Tweet;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface TweetMapper {
    
    @Select("SELECT * FROM tweet WHERE id = #{id}")
    Tweet findById(Long id);
    
    @Select("SELECT * FROM tweet ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Tweet> findTimeline(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT * FROM tweet WHERE userid = #{userid} ORDER BY createdat DESC")
    List<Tweet> findByUserId(Long userid);
    
    @Select("SELECT * FROM tweet WHERE userid = #{userid} ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Tweet> findByUserIdPaged(@Param("userid") Long userid, @Param("offset") int offset, @Param("limit") int limit);
    
    @Insert("INSERT INTO tweet (userid, content, images, likecount, retweetcount, commentcount, retweetid, createdat) " +
            "VALUES (#{userid}, #{content}, #{images}, #{likecount}, #{retweetcount}, #{commentcount}, #{retweetid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tweet tweet);
    
    @Update("UPDATE tweet SET likecount=#{likecount}, retweetcount=#{retweetcount}, commentcount=#{commentcount} WHERE id=#{id}")
    int update(Tweet tweet);
    
    @Delete("DELETE FROM tweet WHERE id = #{id}")
    int delete(Long id);
    
    @Select("SELECT * FROM tweet WHERE content LIKE CONCAT('%', #{keyword}, '%') ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Tweet> searchByContent(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT * FROM tweet WHERE content LIKE CONCAT('%#', #{hashtag}, '%') ORDER BY createdat DESC LIMIT #{offset}, #{limit}")
    List<Tweet> searchByHashtag(@Param("hashtag") String hashtag, @Param("offset") int offset, @Param("limit") int limit);
}
