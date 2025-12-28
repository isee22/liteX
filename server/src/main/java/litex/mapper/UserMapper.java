package litex.mapper;

import litex.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Insert("INSERT INTO user (username, password, nickname, avatar, banner, bio, followingcount, followerscount, createdat) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{avatar}, #{banner}, #{bio}, #{followingcount}, #{followerscount}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET nickname=#{nickname}, avatar=#{avatar}, banner=#{banner}, bio=#{bio}, " +
            "followingcount=#{followingcount}, followerscount=#{followerscount} WHERE id=#{id}")
    int update(User user);
    
    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{limit}")
    List<User> searchByNameOrNickname(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("<script>" +
            "SELECT * FROM user " +
            "<if test='userid != null and userid > 0'>" +
            "WHERE id != #{userid} AND id NOT IN (SELECT targetid FROM follow WHERE userid = #{userid}) " +
            "</if>" +
            "ORDER BY followerscount DESC LIMIT #{limit}" +
            "</script>")
    List<User> findRecommend(@Param("userid") Long userid, @Param("limit") int limit);
}
