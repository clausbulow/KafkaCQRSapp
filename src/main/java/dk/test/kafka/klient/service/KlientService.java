package dk.test.kafka.klient.service;

import dk.test.kafka.klient.model.events.KlientOprettetObject;
import dk.test.kafka.klient.model.events.KlientRettetObject;
import dk.test.kafka.klient.model.persistance.KlientItem;
import dk.test.kafka.klient.model.persistance.KlientJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KlientService {
    @Autowired
    KlientJpaRepository repository;

    public void retKlient(KlientRettetObject klient) throws Exception{
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    public void opretKlient(KlientOprettetObject klient) throws Exception{
        KlientItem klientItem = new KlientItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    public List <KlientItem> getAllKlienter(){
        return repository.findAll();
    }

}
