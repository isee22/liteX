package litex.mapper;

import litex.entity.Block;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface BlockMapper {
    
    @Select("SELECT * FROM block WHERE userid = #{userid} AND blockeduserid = #{blockeduserid} AND (expireat IS NULL OR expireat > NOW())")
    Block find(@Param("userid") Long userid, @Param("blockeduserid") Long blockeduserid);
    
    @Select("SELECT * FROM block WHERE userid = #{userid} AND (expireat IS NULL OR expireat > NOW()) ORDER BY createdat DESC")
    List<Block> findByUserId(Long userid);
    
    @Insert("INSERT INTO block (userid, blockeduserid, expireat, createdat) VALUES (#{userid}, #{blockeduserid}, #{expireat}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Block block);
    
    @Delete("DELETE FROM block WHERE userid = #{userid} AND blockeduserid = #{blockeduserid}")
    int delete(@Param("userid") Long userid, @Param("blockeduserid") Long blockeduserid);
}
