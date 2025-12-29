package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification", indexes = {
    @Index(name = "idx_userid", columnList = "userid")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    public Long fromuserid;
    public Long tweetid;
    
    @Column(length = 20)
    public String type;
    
    public Boolean isread = false;
    public LocalDateTime createdat = LocalDateTime.now();
}
