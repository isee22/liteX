package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report", indexes = {
    @Index(name = "idx_report_userid", columnList = "userid"),
    @Index(name = "idx_report_type", columnList = "type")
})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public String type;
    
    public Long targetuserid;
    
    public Long targettweetid;
    
    public String reason;
    
    public String status = "pending";
    
    public LocalDateTime createdat = LocalDateTime.now();
}
