package es.cic.curso25.proy014.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy014.model.Multa;

public interface MultaRepository extends JpaRepository<Multa, Long>{
    
}
