package litex.service;

import litex.DB;
import litex.entity.Report;
import litex.mapper.ReportMapper;

public class ReportService {
    
    public void reportUser(long userid, long targetuserid, String reason) {
        Report r = new Report();
        r.userid = userid;
        r.type = "user";
        r.targetuserid = targetuserid;
        r.reason = reason;
        DB.execute(ReportMapper.class, m -> m.insert(r));
    }
    
    public void reportTweet(long userid, long targettweetid, String reason) {
        Report r = new Report();
        r.userid = userid;
        r.type = "tweet";
        r.targettweetid = targettweetid;
        r.reason = reason;
        DB.execute(ReportMapper.class, m -> m.insert(r));
    }
}
