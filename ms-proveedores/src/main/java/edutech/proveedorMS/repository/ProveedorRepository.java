package edutech.proveedorMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.proveedorMS.model.EstadoProveedor;
import edutech.proveedorMS.model.Proveedor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRun(String run); 
    Optional<Proveedor> findByEmail(String email); 
    List<Proveedor> findByEstado(EstadoProveedor estado); 
    List<Proveedor> findByNombre(String nombre); 
    List<Proveedor> findByRazonSocial(String razonSocial);
    List<Proveedor> findByRegionNombre(String nombreRegion);
    List<Proveedor> findByComunaNombre(String nombreComuna);

   
    List<Proveedor> findByCategorias_Nombre(String nombreCategoria);
}