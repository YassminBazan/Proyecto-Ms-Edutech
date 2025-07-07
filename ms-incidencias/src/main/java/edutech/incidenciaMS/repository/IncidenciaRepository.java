package edutech.incidenciaMS.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.incidenciaMS.model.EstadoIncidencia;
import edutech.incidenciaMS.model.Incidencia;
import edutech.incidenciaMS.model.PrioridadIncidencia;

import java.util.List;

@Repository 
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    

    
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    List<Incidencia> findByTecnicoAsignado_Id(Long tecnicoId);
    List<Incidencia> findByPrioridad(PrioridadIncidencia prioridad);
    
}