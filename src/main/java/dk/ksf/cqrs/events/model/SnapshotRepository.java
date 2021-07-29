package dk.ksf.cqrs.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotRepository extends JpaRepository<SnapshotItem, Long> {
    @Query("select a from SnapshotItem a where a.aggregatetype = ?1 and a.version =  (select max(b.version) from SnapshotItem b where b.businesskey = a.businesskey group by b.businesskey)" )
    List<SnapshotItem> findLatestSnapShotsForAggregate(AggregateTypes aggregateType);
}
