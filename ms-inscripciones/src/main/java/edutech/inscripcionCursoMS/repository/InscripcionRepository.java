package edutech.inscripcionCursoMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.inscripcionCursoMS.model.EstadoInscripcion;
import edutech.inscripcionCursoMS.model.Inscripcion;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByIdCliente(Long idCliente);
    List<Inscripcion> findByIdCurso(Long idCurso);
    List<Inscripcion> findByFechaInscripcionBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Inscripcion> findByEstado(EstadoInscripcion estado);
    boolean existsByIdClienteAndIdCursoAndEstado(Long idCliente, Long idCurso, EstadoInscripcion estado);
}