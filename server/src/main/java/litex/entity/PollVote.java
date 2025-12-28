package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pollvote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pollid", "userid"})
}, indexes = {
    @Index(name = "idx_pollvote_pollid", columnList = "pollid"),
    @Index(name = "idx_pollvote_userid", columnList = "userid")
})
public class PollVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long pollid;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long optionid;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
