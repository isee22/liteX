package litex.mapper;

import litex.entity.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface NotificationMapper {
    
    @Select("SELECT * FROM notification WHERE userid = #{userid} ORDER BY createdat DESC")
    List<Notification> findByUserId(Long userid);
    
    @Select("SELECT * FROM notification WHERE userid = #{userid} AND type = #{type} ORDER BY createdat DESC")
    List<Notification> findByUserIdAndType(@Param("userid") Long userid, @Param("type") String type);
    
    @Insert("INSERT INTO notification (userid, fromuserid, tweetid, type, isread, createdat) " +
            "VALUES (#{userid}, #{fromuserid}, #{tweetid}, #{type}, #{isread}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);
    
    @Update("UPDATE notification SET isread = true WHERE userid = #{userid}")
    int markAllRead(Long userid);
    
    @Select("SELECT COUNT(*) FROM notification WHERE userid = #{userid} AND isread = false")
    int countUnread(Long userid);
}
