package dk.test.klient.model.write;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KlientWriteModelRepository {
    private Map<String, KlientItem> klienter = new HashMap<>();

    public Optional<KlientItem> findById(String id){
        return Optional.of(klienter.get(id));
    }

    public void save(KlientItem klientItem) {
        klienter.put(klientItem.getCpr(), klientItem);
    }

    public Collection<KlientItem> findAll() {
        return klienter.values();
    }
}
