package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poll", indexes = {
    @Index(name = "idx_poll_tweetid", columnList = "tweetid")
})
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long tweetid;
    
    public LocalDateTime expireat;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
