package dk.ksf.application.readmodel;

import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.Perspective;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.application.common.dto.RetKlientDTO;
import dk.ksf.application.common.eventobjects.KlientOprettetObject;
import dk.ksf.application.common.eventobjects.KlientRettetObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Services exposed by the read model - perspectiv on customers mainained by BusninessEvents in the @EventHandler annotated
//Methods
@Service
@Slf4j
@Perspective
public class KlientReadModelService {

    @Autowired
    KlientJpaRepository repository;


    public List<RetKlientDTO> getAllKlienter() {
        final List<RetKlientDTO> result = new ArrayList<>();
        repository.findAll().stream().forEach(klientItem -> result.add(RetKlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build()));
        return result;
    }


    public Optional<RetKlientDTO> getKlient(String cpr) {
        final Optional<KlientReadModelItem> optionalKlientItem = repository.findById(cpr);
        RetKlientDTO klient = null;
        if (optionalKlientItem.isPresent()) {
            KlientReadModelItem klientItem = optionalKlientItem.get();
            klient = RetKlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build();
        }
        return Optional.ofNullable(klient);
    }

    //Private method - only called during event-handling
    private void retKlient(KlientRettetObject klient, long version) throws Exception {
        KlientReadModelItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientReadModelItem());
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientItem.setVersion(version);
        repository.save(klientItem);
    }

    private void opretKlient(KlientOprettetObject klient, long version) throws Exception {
        KlientReadModelItem klientItem = new KlientReadModelItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientItem.setVersion(version);
        repository.save(klientItem);
    }


    //Eventhandling...
    @EventHandler
    @Transactional
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception {
        KlientRettetObject klient = event.getObject();
        retKlient(klient, event.getVersion());
        log.info("Klient rettet i read-model");
    }

    @EventHandler
    @Transactional
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception {
        KlientOprettetObject klient = event.getObject();
        opretKlient(klient, event.getVersion());
        log.info("Klient oprettet i read-model");
    }


}
