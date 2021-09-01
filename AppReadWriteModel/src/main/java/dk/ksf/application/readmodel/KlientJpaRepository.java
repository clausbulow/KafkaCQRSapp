package dk.ksf.application.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientJpaRepository extends JpaRepository<KlientReadModelItem, String> {
}
