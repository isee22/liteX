package litex.mapper;

import litex.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface CommentMapper {
    
    @Select("SELECT * FROM comment WHERE tweetid = #{tweetid} ORDER BY createdat")
    List<Comment> findByTweetId(Long tweetid);
    
    @Select("SELECT * FROM comment WHERE userid = #{userid} ORDER BY createdat DESC")
    List<Comment> findByUserId(Long userid);
    
    @Insert("INSERT INTO comment (tweetid, userid, content, createdat) VALUES (#{tweetid}, #{userid}, #{content}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
}
