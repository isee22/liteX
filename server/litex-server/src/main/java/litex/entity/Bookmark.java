package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userid", "tweetid"})
}, indexes = {
    @Index(name = "idx_bookmark_userid", columnList = "userid"),
    @Index(name = "idx_bookmark_tweetid", columnList = "tweetid")
})
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long tweetid;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
