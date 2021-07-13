package dk.test.kafka.klient.model.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientJpaRepository extends JpaRepository<KlientItem, String> {
}
