package deltaalpha.crypto.repo;

import deltaalpha.crypto.entity.LaunchPoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaunchPoolRepo extends JpaRepository<LaunchPoolEntity, Long> {
}