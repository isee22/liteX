package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "block", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userid", "blockeduserid"})
}, indexes = {
    @Index(name = "idx_block_userid", columnList = "userid"),
    @Index(name = "idx_block_blockeduserid", columnList = "blockeduserid")
})
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long userid;
    
    @Column(nullable = false)
    public Long blockeduserid;
    
    public LocalDateTime expireat;
    
    public LocalDateTime createdat = LocalDateTime.now();
}
