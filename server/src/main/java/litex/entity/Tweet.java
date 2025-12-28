package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweet", indexes = {
    @Index(name = "idx_userid", columnList = "userid"),
    @Index(name = "idx_createdat", columnList = "createdat")
})
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(length = 280)
    public String content;
    
    @Column(columnDefinition = "TEXT")
    public String images;
    
    public Integer likecount = 0;
    public Integer retweetcount = 0;
    public Integer commentcount = 0;
    public Long retweetid;
    public LocalDateTime createdat = LocalDateTime.now();
}
