package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment", indexes = {
    @Index(name = "idx_tweetid", columnList = "tweetid")
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long tweetid;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(length = 280)
    public String content;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
