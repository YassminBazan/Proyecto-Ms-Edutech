package edutech.incidenciaMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.incidenciaMS.model.Tecnico;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    Optional<Tecnico> findByRut(String rut);
    List<Tecnico> findByDisponibilidad(boolean disponibilidad);
    List<Tecnico> findByEspecialidad(String especialidad);
}