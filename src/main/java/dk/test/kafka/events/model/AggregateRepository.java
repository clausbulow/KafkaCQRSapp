package dk.test.kafka.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AggregateRepository extends JpaRepository <AggregateItem, UUID> {
    @Query("select a from AggregateItem a where a.type = ?1")
    public AggregateItem findByType(String type);
}