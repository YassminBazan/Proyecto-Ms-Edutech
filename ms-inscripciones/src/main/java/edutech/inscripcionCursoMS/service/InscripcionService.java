package edutech.inscripcionCursoMS.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.inscripcionCursoMS.client.CursoClient;
import edutech.inscripcionCursoMS.model.CursoDTO;
import edutech.inscripcionCursoMS.model.Descuento;
import edutech.inscripcionCursoMS.model.EstadoInscripcion;
import edutech.inscripcionCursoMS.model.Inscripcion;
import edutech.inscripcionCursoMS.repository.DescuentoRepository;
import edutech.inscripcionCursoMS.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoClient cursoClient;
    private final DescuentoRepository descuentoRepository; 


    @Transactional
    public Inscripcion crearInscripcion(Inscripcion inscripcion, String codigoCupon) {
        //Validar que no temga inscripciones activas
        boolean yaInscrito = inscripcionRepository.existsByIdClienteAndIdCursoAndEstado(inscripcion.getIdCliente(), inscripcion.getIdCurso(), EstadoInscripcion.ACTIVA);
        if (yaInscrito) {
            throw new RuntimeException("El cliente ya está inscrito activamente en este curso.");
        }

        //Obtiene curso de Ms Gestion de Cursos para Verificar que el curso exista
        CursoDTO curso = cursoClient.obtenerCursoPorId(inscripcion.getIdCurso());

        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        inscripcion.setDescuentoAplicado(0.0); 
        inscripcion.setPrecioFinal(curso.getValorCurso()); 

        if (codigoCupon != null && !codigoCupon.isEmpty()) {
            Descuento descuento = aplicarDescuento(curso.getValorCurso(), codigoCupon);
            inscripcion.setPrecioFinal(calcularPrecioFinalPostDescuento(curso.getValorCurso(), descuento.getPorcentajeDescuento()));
            inscripcion.setDescuentoAplicado(descuento.getPorcentajeDescuento());
            inscripcion.setDescuentoUtilizado(descuento); 
        }

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion actualizarInscripcion(Long id, Inscripcion inscripcionActualizada) {
        return inscripcionRepository.findById(id)
                .map(inscripcionExistente -> {
                    
                    inscripcionExistente.setEstado(inscripcionActualizada.getEstado());
                   
                    return inscripcionRepository.save(inscripcionExistente);
                }).orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + id));
    }

     @Transactional
    public Inscripcion cancelarInscripcion(Long id) {
        return inscripcionRepository.findById(id)
                .map(inscripcion -> {
                    
                    if (inscripcion.getEstado() == EstadoInscripcion.ACTIVA) {
                        inscripcion.setEstado(EstadoInscripcion.CANCELADA);
                        return inscripcionRepository.save(inscripcion);
                    } else {
                        throw new RuntimeException("La inscripción no puede ser cancelada en su estado actual: " + inscripcion.getEstado());
                    }
                }).orElseThrow(() -> new RuntimeException("Inscripción no encontrada."));
    }

    public Optional<Inscripcion> obtenerInscripcionPorId(Long id) {
        return inscripcionRepository.findById(id);
    }

    public List<Inscripcion> listarTodasLasInscripciones() {
        return inscripcionRepository.findAll();
    }

    public List<Inscripcion> listarInscripcionesPorCliente(Long idCliente) {
        return inscripcionRepository.findByIdCliente(idCliente);
    }

    public List<Inscripcion> listarInscripcionesPorCurso(Long idCurso) {
        return inscripcionRepository.findByIdCurso(idCurso);
    }

    
    public Descuento aplicarDescuento(Double precioOriginal, String codigoCupon) {
        Descuento descuento = descuentoRepository.findByCodigoCupon(codigoCupon)
                .orElseThrow(() -> new RuntimeException("Cupón de descuento inválido: " + codigoCupon));

        LocalDate hoy = LocalDate.now();
        if (!descuento.isActivo() || hoy.isBefore(descuento.getFechaInicioValidez()) || hoy.isAfter(descuento.getFechaFinValidez())) {
            throw new RuntimeException("El cupón de descuento no está activo o ha expirado.");
        }
        
        return descuento;
    }

   
    public Double calcularPrecioFinalPostDescuento(Double precioOriginal, Double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 1.0) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 1.");
        }
        return precioOriginal * (1.0 - porcentajeDescuento);
    }
}
