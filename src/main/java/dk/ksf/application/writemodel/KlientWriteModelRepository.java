package dk.ksf.application.writemodel;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KlientWriteModelRepository {
    private Map<String, KlientWriteModelItem> klienter = new HashMap<>();

    public Optional<KlientWriteModelItem> findById(String id){
        return Optional.of(klienter.get(id));
    }

    public void save(KlientWriteModelItem klientItem) {
        klienter.put(klientItem.getCpr(), klientItem);
    }

    public Collection<KlientWriteModelItem> findAll() {
        return klienter.values();
    }
}
