package litex.entity;

import javax.persistence.*;

@Entity
@Table(name = "polloption", indexes = {
    @Index(name = "idx_polloption_pollid", columnList = "pollid")
})
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false)
    public Long pollid;
    
    @Column(nullable = false)
    public String text;
    
    public Integer votecount = 0;
}
