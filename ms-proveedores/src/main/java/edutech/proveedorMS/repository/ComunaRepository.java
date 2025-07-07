package edutech.proveedorMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.proveedorMS.model.Comuna;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {
    Optional<Comuna> findByNombre(String nombre);
    List<Comuna> findByRegionId(Long regionId); 
}
