package edutech.gestionDeClaseMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.gestionDeClaseMS.model.Material;
import edutech.gestionDeClaseMS.model.TipoMaterial;
import edutech.gestionDeClaseMS.model.EstadoMaterial; // Importar el nuevo Enum

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByClaseId(Long claseId);
    List<Material> findByTipoMaterial(TipoMaterial tipoMaterial);
    List<Material> findByEstadoMaterial(EstadoMaterial estadoMaterial); 
}