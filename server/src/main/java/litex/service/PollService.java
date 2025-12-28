package litex.service;

import litex.DB;
import litex.entity.*;
import litex.mapper.PollMapper;
import java.time.LocalDateTime;
import java.util.*;

public class PollService {
    
    public Poll createPoll(long tweetid, List<String> options, Integer durationHours) {
        Poll poll = new Poll();
        poll.tweetid = tweetid;
        if (durationHours != null && durationHours > 0) {
            poll.expireat = LocalDateTime.now().plusHours(durationHours);
        }
        DB.execute(PollMapper.class, m -> m.insertPoll(poll));
        
        for (String text : options) {
            PollOption option = new PollOption();
            option.pollid = poll.id;
            option.text = text;
            DB.execute(PollMapper.class, m -> m.insertOption(option));
        }
        return poll;
    }
    
    public Map<String, Object> getPoll(long tweetid, long userid) {
        Poll poll = DB.execute(PollMapper.class, m -> m.findByTweetId(tweetid));
        if (poll == null) return null;
        
        List<PollOption> options = DB.execute(PollMapper.class, m -> m.findOptions(poll.id));
        PollVote userVote = DB.execute(PollMapper.class, m -> m.findVote(poll.id, userid));
        
        int totalVotes = options.stream().mapToInt(o -> o.votecount).sum();
        boolean expired = poll.expireat != null && poll.expireat.isBefore(LocalDateTime.now());
        
        Map<String, Object> result = new HashMap<>();
        result.put("poll", poll);
        result.put("options", options);
        result.put("totalVotes", totalVotes);
        result.put("voted", userVote != null);
        result.put("votedOptionId", userVote != null ? userVote.optionid : null);
        result.put("expired", expired);
        return result;
    }
    
    public boolean vote(long tweetid, long userid, long optionid) {
        Poll poll = DB.execute(PollMapper.class, m -> m.findByTweetId(tweetid));
        if (poll == null) return false;
        
        // Check if expired
        if (poll.expireat != null && poll.expireat.isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Check if already voted
        PollVote existing = DB.execute(PollMapper.class, m -> m.findVote(poll.id, userid));
        if (existing != null) return false;
        
        // Record vote
        PollVote vote = new PollVote();
        vote.pollid = poll.id;
        vote.userid = userid;
        vote.optionid = optionid;
        DB.execute(PollMapper.class, m -> m.insertVote(vote));
        DB.execute(PollMapper.class, m -> m.incrementVoteCount(optionid));
        return true;
    }
}
