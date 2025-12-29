package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweetlike", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userid", "tweetid"})
}, indexes = {
    @Index(name = "idx_userid", columnList = "userid"),
    @Index(name = "idx_tweetid", columnList = "tweetid")
})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long tweetid;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
