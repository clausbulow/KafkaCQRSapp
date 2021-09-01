package dk.ksf.testclasses;


import dk.ksf.internalmessages.TestEventDispatcher;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestKlientAggregateRepository implements CrudRepository<TestEventDispatcher, String> {
    final Map<String, TestEventDispatcher> data = new HashMap<>();

    @Override
    public <S extends TestEventDispatcher> S save(S s) {
        data.put(s.getId(), s);
        return s;
    }

    @Override
    public <S extends TestEventDispatcher> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<TestEventDispatcher> findById(String s) {
        TestEventDispatcher result = data.get(s);
        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<TestEventDispatcher> findAll() {
        return null;
    }

    @Override
    public Iterable<TestEventDispatcher> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(TestEventDispatcher testAggregate1) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> iterable) {

    }

    @Override
    public void deleteAll(Iterable<? extends TestEventDispatcher> iterable) {

    }


    @Override
    public void deleteAll() {

    }
}
