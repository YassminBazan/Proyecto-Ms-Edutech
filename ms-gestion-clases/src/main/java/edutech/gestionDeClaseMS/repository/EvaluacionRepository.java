package edutech.gestionDeClaseMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.gestionDeClaseMS.model.EstadoEvaluacion;
import edutech.gestionDeClaseMS.model.Evaluacion;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByClaseId(Long claseId); 
    List<Evaluacion> findByEstado(EstadoEvaluacion estado);
    List<Evaluacion> findByFechaInicioBetweenAndFechaTerminoBetween(
    LocalDateTime fechaInicioStart, LocalDateTime fechaInicioEnd,
    LocalDateTime fechaTerminoStart, LocalDateTime fechaTerminoEnd
);

}
