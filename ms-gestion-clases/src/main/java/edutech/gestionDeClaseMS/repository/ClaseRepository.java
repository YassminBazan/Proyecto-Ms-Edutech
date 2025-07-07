package edutech.gestionDeClaseMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.gestionDeClaseMS.model.Clase;

import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByIdCurso(Long idCurso); 
    List<Clase> findByNombre(String nombre);
    
}