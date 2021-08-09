package dk.ksf.application.writemodel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KlientWriteModelRepository implements CrudRepository<KlientAggregate, String> {
    private Map<String, KlientAggregate> klienter = new HashMap<>();


    @Override
    public <S extends KlientAggregate> S save(S s) {
        klienter.put(s.getCpr(), s);
        return s;
    }

    @Override
    public <S extends KlientAggregate> Iterable<S> saveAll(Iterable<S> iterable) {
        iterable.forEach(klient -> save(klient));
        return iterable;
    }

    public Optional<KlientAggregate> findById(String id){
        return Optional.ofNullable(klienter.get(id));
    }

    @Override
    public boolean existsById(String s) {
        return klienter.containsKey(s);
    }


    public Collection<KlientAggregate> findAll() {
        return klienter.values();
    }

    @Override
    public Iterable<KlientAggregate> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return klienter.size();
    }

    @Override
    public void deleteById(String s) {
        klienter.remove(s);

    }

    @Override
    public void delete(KlientAggregate klientAggregate) {
        klienter.remove(klientAggregate.getCpr());

    }

    @Override
    public void deleteAllById(Iterable<? extends String> iterable) {
        iterable.forEach(id -> klienter.remove(id));

    }

    @Override
    public void deleteAll(Iterable<? extends KlientAggregate> iterable) {
        iterable.forEach(klient -> klienter.remove(klient.getCpr()));

    }

    @Override
    public void deleteAll() {
        klienter.clear();

    }
}
