package litex.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    public String username;
    
    @Column(unique = true, length = 100)
    public String email;
    
    @Column(nullable = false, length = 100)
    public String password;
    
    @Column(length = 50)
    public String nickname;
    
    public String avatar;
    public String banner;
    
    @Column(length = 200)
    public String bio;
    
    public Integer followingcount = 0;
    public Integer followerscount = 0;
    public LocalDateTime createdat = LocalDateTime.now();
}
