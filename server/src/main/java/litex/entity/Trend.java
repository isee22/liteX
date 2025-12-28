package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trend")
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, length = 50)
    public String tag;
    
    public Integer count = 0;
    public LocalDateTime updatedat = LocalDateTime.now();
}
