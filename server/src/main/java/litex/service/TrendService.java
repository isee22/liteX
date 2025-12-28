package litex.service;

import litex.DB;
import litex.entity.Trend;
import litex.mapper.TrendMapper;
import java.time.LocalDateTime;
import java.util.List;

public class TrendService {
    
    public List<Trend> getHotTrends() {
        return DB.execute(TrendMapper.class, m -> m.getHotTrends(10));
    }
    
    public void incrementTag(String tag) {
        Trend trend = DB.execute(TrendMapper.class, m -> m.findByTag(tag));
        if (trend == null) {
            trend = new Trend();
            trend.tag = tag;
            trend.count = 1;
            trend.updatedat = LocalDateTime.now();
            Trend t = trend;
            DB.execute(TrendMapper.class, m -> m.insert(t));
        } else {
            trend.count++;
            trend.updatedat = LocalDateTime.now();
            Trend t = trend;
            DB.execute(TrendMapper.class, m -> m.update(t));
        }
    }
}
