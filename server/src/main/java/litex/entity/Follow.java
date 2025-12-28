package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follow", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userid", "targetid"})
}, indexes = {
    @Index(name = "idx_userid", columnList = "userid"),
    @Index(name = "idx_targetid", columnList = "targetid")
})
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long targetid;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
