package dk.ksf.cqrs.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends JpaRepository<EventStoreItem, Long> {
    @Query("select e from EventStoreItem e where e.businesskey= ?1 and e.version > ?2 order by e.version")
    List<EventStoreItem> getEventStoreItemByAggregateIdAndVersion(String businesskey, long version);
}
