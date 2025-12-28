package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mute", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userid", "targetid"})
}, indexes = {
    @Index(name = "idx_mute_userid", columnList = "userid")
})
public class Mute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long targetid;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
