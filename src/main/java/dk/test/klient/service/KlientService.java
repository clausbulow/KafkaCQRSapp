package dk.test.klient.service;

import dk.test.klient.controller.KlientDTO;
import dk.test.klient.model.events.KlientOprettetObject;
import dk.test.klient.model.events.KlientRettetObject;
import dk.test.klient.model.repos.KlientItem;
import dk.test.klient.model.repos.KlientJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List <KlientDTO> getAllKlienter(){
        List<KlientDTO> result = new ArrayList<>();
        repository.findAll().stream().forEach( klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getFornavn()).build()));
        return result;
    }

}
