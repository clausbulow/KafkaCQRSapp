package dk.test.klient.model.read;

import dk.test.kafka.events.annotations.EventHandler;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.KlientItem;
import dk.test.klient.model.eventsobject.KlientOprettetObject;
import dk.test.klient.model.eventsobject.KlientRettetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KlientReadModelService {
    @Autowired
    KlientJpaRepository repository;



    public List <KlientDTO> getAllKlienter(){
        final List<KlientDTO> result = new ArrayList<>();
        repository.findAll().stream().forEach( klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build()));
        return result;
    }


    public Optional<KlientDTO> getKlient(String cpr) {
        final Optional<KlientItem> optionalKlientItem = repository.findById(cpr);
        KlientDTO klient = null;
        if (optionalKlientItem.isPresent()){
            KlientItem klientItem = optionalKlientItem.get();
            klient = KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build();
        }
        return Optional.ofNullable(klient);
    }

    private void retKlient(KlientRettetObject klient) throws Exception{
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    private void opretKlient(KlientOprettetObject klient) throws Exception{
        KlientItem klientItem = new KlientItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }


    @EventHandler
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception{
        KlientRettetObject klient = event.getObject();
        retKlient(klient);
        System.out.println("Klient rettet i readmode");
    }

    @EventHandler
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception{
        KlientOprettetObject klient = event.getObject();
        opretKlient(klient);
        System.out.println("Klient oprettet i read-model");
    }



}
