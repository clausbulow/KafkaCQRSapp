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

//Services exposed by the read model - perspectiv on customers mainained by BusninessEvents in the @EventHandler annotated
//Methods
@Service
@Slf4j
public class KlientReadModelService {

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

    //Private method - only called during event-handling
    private void retKlient(KlientRettetObject klient, long version) throws Exception {
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
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


    //Eventhandling...
    @EventHandler
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception {
        KlientRettetObject klient = event.getObject();
        retKlient(klient, event.getVersion());
        log.info("Klient rettet i read-model");
    }

    @EventHandler
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception {
        KlientOprettetObject klient = event.getObject();
        opretKlient(klient, event.getVersion());
        log.info("Klient oprettet i read-model");
    }


}
