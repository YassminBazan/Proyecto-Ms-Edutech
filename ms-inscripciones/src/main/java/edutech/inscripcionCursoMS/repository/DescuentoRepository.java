package edutech.inscripcionCursoMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutech.inscripcionCursoMS.model.Descuento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    Optional<Descuento> findByCodigoCupon(String codigoCupon);
    List<Descuento> findByActivoTrueAndFechaInicioValidezBeforeAndFechaFinValidezAfter(   LocalDate currentDate, LocalDate currentDate2); 
}