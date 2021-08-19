package dk.ksf.testclasses;

import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestRepository implements CrudRepository<TestKlientAggregate, String> {
    Map<String, TestKlientAggregate>  data = new HashMap<> ();

    @Override
    public <S extends TestKlientAggregate> S save(S s) {
        data.put(s.id, s);
        return s;
    }

    @Override
    public <S extends TestKlientAggregate> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<TestKlientAggregate> findById(String s) {
        TestKlientAggregate result = data.get(s);
        result.setId(s);
        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<TestKlientAggregate> findAll() {
        return null;
    }

    @Override
    public Iterable<TestKlientAggregate> findAllById(Iterable<String> iterable) {
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
    public void delete(TestKlientAggregate testAggregate1) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> iterable) {

    }

    @Override
    public void deleteAll(Iterable<? extends TestKlientAggregate> iterable) {

    }


    @Override
    public void deleteAll() {

    }
}
