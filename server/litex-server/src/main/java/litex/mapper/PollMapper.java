package litex.mapper;

import litex.entity.Poll;
import litex.entity.PollOption;
import litex.entity.PollVote;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface PollMapper {
    
    @Select("SELECT * FROM poll WHERE tweetid = #{tweetid}")
    Poll findByTweetId(Long tweetid);
    
    @Insert("INSERT INTO poll (tweetid, expireat, createdat) VALUES (#{tweetid}, #{expireat}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPoll(Poll poll);
    
    @Select("SELECT * FROM polloption WHERE pollid = #{pollid}")
    List<PollOption> findOptions(Long pollid);
    
    @Insert("INSERT INTO polloption (pollid, text, votecount) VALUES (#{pollid}, #{text}, #{votecount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOption(PollOption option);
    
    @Update("UPDATE polloption SET votecount = votecount + 1 WHERE id = #{id}")
    int incrementVoteCount(Long id);
    
    @Select("SELECT * FROM pollvote WHERE pollid = #{pollid} AND userid = #{userid}")
    PollVote findVote(@Param("pollid") Long pollid, @Param("userid") Long userid);
    
    @Insert("INSERT INTO pollvote (pollid, userid, optionid, createdat) VALUES (#{pollid}, #{userid}, #{optionid}, #{createdat})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertVote(PollVote vote);
}
