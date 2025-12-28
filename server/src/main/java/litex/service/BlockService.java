package litex.service;

import litex.DB;
import litex.entity.Block;
import litex.mapper.*;
import java.time.LocalDateTime;
import java.util.*;

public class BlockService {
    
    public List<Map<String, Object>> list(long userid) {
        List<Block> blocks = DB.execute(BlockMapper.class, m -> m.findByUserId(userid));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Block b : blocks) {
            Map<String, Object> item = new HashMap<>();
            item.put("block", b);
            item.put("user", DB.execute(UserMapper.class, m -> m.findById(b.blockeduserid)));
            result.add(item);
        }
        return result;
    }
    
    public void block(long userid, long blockeduserid, Integer days) {
        DB.execute(BlockMapper.class, m -> m.delete(userid, blockeduserid));
        Block b = new Block();
        b.userid = userid;
        b.blockeduserid = blockeduserid;
        if (days != null && days > 0) {
            b.expireat = LocalDateTime.now().plusDays(days);
        }
        DB.execute(BlockMapper.class, m -> m.insert(b));
    }
    
    public void unblock(long userid, long blockeduserid) {
        DB.execute(BlockMapper.class, m -> m.delete(userid, blockeduserid));
    }
    
    public boolean isBlocked(long userid, long blockeduserid) {
        return DB.execute(BlockMapper.class, m -> m.find(userid, blockeduserid)) != null;
    }
}
