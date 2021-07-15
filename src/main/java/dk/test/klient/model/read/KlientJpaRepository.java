package dk.test.klient.model.read;

import dk.test.klient.model.KlientItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientJpaRepository extends JpaRepository<KlientItem, String> {
}
