package deltaalpha.crypto.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "launch_pool_info")
public class LaunchPoolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exchange;
    private String launchPool;
    private String pools;
    private String period;
    private String status;
}