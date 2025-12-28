package litex.mapper;

import litex.entity.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

public interface MessageMapper {
    
    @Select("SELECT CASE WHEN fromuserid = #{userid} THEN touserid ELSE fromuserid END as userid, " +
            "MAX(createdat) as lasttime " +
            "FROM message WHERE fromuserid = #{userid} OR touserid = #{userid} " +
            "GROUP BY CASE WHEN fromuserid = #{userid} THEN touserid ELSE fromuserid END " +
            "ORDER BY lasttime DESC")
    List<Map<String, Object>> getChats(Long userid);
    
    @Select("SELECT * FROM message WHERE " +
            "(fromuserid = #{userid} AND touserid = #{targetid}) OR " +
            "(fromuserid = #{targetid} AND touserid = #{userid}) " +
            "ORDER BY createdat")
    List<Message> getMessages(@Param("userid") Long userid, @Param("targetid") Long targetid);
    
    @Insert("INSERT INTO message (fromuserid, touserid, content, isread, createdat) " +
            "VALUES (#{fromuserid}, #{touserid}, #{content}, #{isread}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);
    
    @Select("SELECT COUNT(*) FROM message WHERE touserid = #{userid} AND isread = false")
    int countUnread(Long userid);
}
