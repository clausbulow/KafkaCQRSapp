package dk.ksf.application.readmodel;

import dk.ksf.application.common.KlientItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientJpaRepository extends JpaRepository<KlientItem, String> {
}
