package dk.test.klient.model.read;

import dk.test.kafka.events.annotations.EventHandler;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.write.KlientItem;
import dk.test.klient.model.eventsobject.KlientOprettetObject;
import dk.test.klient.model.eventsobject.KlientRettetObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class KlientReadModelService {
    private AtomicLong currentEventVersion = new AtomicLong(Long.valueOf(-1));

    @Autowired
    KlientJpaRepository repository;


    public List<KlientDTO> getAllKlienter() {
        final List<KlientDTO> result = new ArrayList<>();
        repository.findAll().stream().forEach(klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build()));
        return result;
    }


    public Optional<KlientDTO> getKlient(String cpr) {
        final Optional<KlientItem> optionalKlientItem = repository.findById(cpr);
        KlientDTO klient = null;
        if (optionalKlientItem.isPresent()) {
            KlientItem klientItem = optionalKlientItem.get();
            klient = KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build();
        }
        return Optional.ofNullable(klient);
    }

    private void retKlient(KlientRettetObject klient, long version) throws Exception {
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
        if (!currentEventVersion.compareAndSet(version-1,version)) {
            log.error("Invalid version when writing to tho klient re<d model.");
        }
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    private void opretKlient(KlientOprettetObject klient, long version) throws Exception {
        KlientItem klientItem = new KlientItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }


    @EventHandler
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception {
        KlientRettetObject klient = event.getObject();
        retKlient(klient, event.getVersion());
        System.out.println("Klient rettet i read-model");
    }

    @EventHandler
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception {
        KlientOprettetObject klient = event.getObject();
        opretKlient(klient, event.getVersion());
        System.out.println("Klient oprettet i read-model");
    }


}
