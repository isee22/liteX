package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message", indexes = {
    @Index(name = "idx_fromuserid", columnList = "fromuserid"),
    @Index(name = "idx_touserid", columnList = "touserid")
})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long fromuserid;
    
    @Column(nullable = false)
    public Long touserid;
    
    @Column(length = 500)
    public String content;
    
    public Boolean isread = false;
    public LocalDateTime createdat = LocalDateTime.now();
}
