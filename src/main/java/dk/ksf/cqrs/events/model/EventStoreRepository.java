package dk.ksf.cqrs.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends JpaRepository<EventStoreItem, Long> {
    @Query("select e from EventStoreItem e where e.aggregatetype= ?1 and e.businesskey= ?2 and e.version > ?3 order by e.version")
    List<EventStoreItem> getEventStoreItemByAggregateIdAndVersion(AggregateTypes aggregateType,String businesskey, long version);
}
