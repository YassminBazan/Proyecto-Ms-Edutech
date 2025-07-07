package edutech.inscripcionCursoMS.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.inscripcionCursoMS.model.Descuento;
import edutech.inscripcionCursoMS.repository.DescuentoRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DescuentoService {

    private final DescuentoRepository descuentoRepository;

    @Transactional
    public Descuento crearDescuento(Descuento descuento) {
       
        if (descuentoRepository.findByCodigoCupon(descuento.getCodigoCupon()).isPresent()) {
            throw new RuntimeException("Ya existe un cupón con el código: " + descuento.getCodigoCupon());
        }
        if (descuento.getPorcentajeDescuento() < 0 || descuento.getPorcentajeDescuento() > 1.0) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0.0 y 1.0.");
        }
        if (descuento.getFechaInicioValidez().isAfter(descuento.getFechaFinValidez())) {
            throw new IllegalArgumentException("La fecha de inicio de validez no puede ser posterior a la fecha de fin.");
        }
        return descuentoRepository.save(descuento);
    }

    @Transactional
    public Descuento actualizarDescuento(Long id, Descuento descuentoActualizado) {
        return descuentoRepository.findById(id)
                .map(descuentoExistente -> {
                    descuentoExistente.setCodigoCupon(descuentoActualizado.getCodigoCupon());
                    descuentoExistente.setPorcentajeDescuento(descuentoActualizado.getPorcentajeDescuento());
                    descuentoExistente.setFechaInicioValidez(descuentoActualizado.getFechaInicioValidez());
                    descuentoExistente.setFechaFinValidez(descuentoActualizado.getFechaFinValidez());
                    descuentoExistente.setActivo(descuentoActualizado.isActivo());
                   
                    if (descuentoExistente.getPorcentajeDescuento() < 0 || descuentoExistente.getPorcentajeDescuento() > 1.0) {
                        throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0.0 y 1.0.");
                    }
                    if (descuentoExistente.getFechaInicioValidez().isAfter(descuentoExistente.getFechaFinValidez())) {
                        throw new IllegalArgumentException("La fecha de inicio de validez no puede ser posterior a la fecha de fin.");
                    }
                    return descuentoRepository.save(descuentoExistente);
                }).orElseThrow(() -> new RuntimeException("Descuento no encontrado con ID: " + id));
    }

    @Transactional
    public void borrarDescuento(Long id) {
        if (!descuentoRepository.existsById(id)) {
            throw new RuntimeException("Descuento no encontrado con ID: " + id);
        }
        descuentoRepository.deleteById(id);
    }

    public Optional<Descuento> obtenerDescuentoPorId(Long id) {
        return descuentoRepository.findById(id);
    }

    public Optional<Descuento> obtenerDescuentoPorCodigoCupon(String codigoCupon) {
        return descuentoRepository.findByCodigoCupon(codigoCupon);
    }

    public List<Descuento> listarTodosLosDescuentos() {
        return descuentoRepository.findAll();
    }

    public List<Descuento> listarDescuentosActivosYValidosHoy() {
        LocalDate hoy = LocalDate.now();
        return descuentoRepository.findByActivoTrueAndFechaInicioValidezBeforeAndFechaFinValidezAfter(hoy, hoy);
    }
}