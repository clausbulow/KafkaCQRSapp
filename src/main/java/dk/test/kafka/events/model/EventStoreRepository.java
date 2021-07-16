package dk.test.kafka.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends JpaRepository<EventStoreItem, Long> {
    @Query("select e from EventStoreItem e where e.businesskey= ?1 order by e.version")
    List<EventStoreItem> getEventStoreItemByAggregateId(String businesskey);
}
