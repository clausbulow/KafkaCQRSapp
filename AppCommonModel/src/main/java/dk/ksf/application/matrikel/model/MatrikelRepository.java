package dk.ksf.application.matrikel.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatrikelRepository extends JpaRepository<Matrikel, String> {

}


