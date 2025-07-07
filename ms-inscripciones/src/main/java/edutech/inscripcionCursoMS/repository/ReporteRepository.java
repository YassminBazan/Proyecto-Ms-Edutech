package edutech.inscripcionCursoMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.inscripcionCursoMS.model.Reporte;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByFechaGeneracionBetween(LocalDateTime start, LocalDateTime end);
    List<Reporte> findByTipoReporte(String tipo);
}