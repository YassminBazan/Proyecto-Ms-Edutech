package edutech.proveedorMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.proveedorMS.model.Categoria;

import java.util.Optional;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findByDescripcionContainingIgnoreCase(String descripcion);
}