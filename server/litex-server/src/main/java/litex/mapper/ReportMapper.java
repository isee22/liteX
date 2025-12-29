package litex.mapper;

import litex.entity.Report;
import org.apache.ibatis.annotations.*;

public interface ReportMapper {
    
    @Insert("INSERT INTO report (userid, type, targetuserid, targettweetid, reason, status, createdat) " +
            "VALUES (#{userid}, #{type}, #{targetuserid}, #{targettweetid}, #{reason}, #{status}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Report report);
}
