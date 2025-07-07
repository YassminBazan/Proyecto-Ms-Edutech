package edutech.gestionDeClaseMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.gestionDeClaseMS.model.Progreso;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoRepository extends JpaRepository<Progreso, Long> {
    Optional<Progreso> findByIdUsuarioAndClaseId(Long idUsuario, Long claseId); 
    List<Progreso> findByIdUsuario(Long idUsuario); 
    List<Progreso> findByClaseId(Long claseId); 
}
