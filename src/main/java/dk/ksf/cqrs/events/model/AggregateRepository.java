package dk.ksf.cqrs.events.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AggregateRepository extends JpaRepository <AggregateItem,UUID> {
    @Query("select a from AggregateItem a where a.aggregatetype = ?1" )
    public List<AggregateItem> findByTypeAndKey(AggregateTypes type);

    @Query("select a from AggregateItem a where a.aggregatetype = ?1 and a.businesskey = ?2" )
    public AggregateItem findByTypeAndKey(AggregateTypes type, String key);

}
