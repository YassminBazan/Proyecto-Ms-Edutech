package edutech.gestionDeClaseMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoEvaluacion;
import edutech.gestionDeClaseMS.model.Evaluacion;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.EvaluacionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ClaseRepository claseRepository; 

    @Autowired
    public EvaluacionService(EvaluacionRepository evaluacionRepository, ClaseRepository claseRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.claseRepository = claseRepository;
    }

    @Transactional
    public Evaluacion crearEvaluacion(Evaluacion evaluacion, Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        evaluacion.setClase(clase);
        evaluacion.setEstado(EstadoEvaluacion.PENDIENTE); 
        
        if (evaluacion.getFechaInicio().isAfter(evaluacion.getFechaTermino())) {
            throw new IllegalArgumentException("La fecha de inicio de la evaluación no puede ser posterior a la de término.");
        }
        return evaluacionRepository.save(evaluacion);
    }

    @Transactional
    public Evaluacion actualizarEvaluacion(Long id, Evaluacion evaluacionActualizada) {
        return evaluacionRepository.findById(id)
                .map(evaluacionExistente -> {
                    evaluacionExistente.setTitulo(evaluacionActualizada.getTitulo());
                    evaluacionExistente.setDescripcion(evaluacionActualizada.getDescripcion());
                    evaluacionExistente.setTipoEvaluacion(evaluacionActualizada.getTipoEvaluacion());
                    evaluacionExistente.setFechaInicio(evaluacionActualizada.getFechaInicio());
                    evaluacionExistente.setFechaTermino(evaluacionActualizada.getFechaTermino());
                    evaluacionExistente.setEstado(evaluacionActualizada.getEstado());
                   
                    if (evaluacionExistente.getFechaInicio().isAfter(evaluacionExistente.getFechaTermino())) {
                        throw new IllegalArgumentException("La fecha de inicio de la evaluación no puede ser posterior a la de término.");
                    }
                    return evaluacionRepository.save(evaluacionExistente);
                }).orElseThrow(() -> new RuntimeException("Evaluación no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminarEvaluacion(Long id) {
        if (!evaluacionRepository.existsById(id)) {
            throw new RuntimeException("Evaluación no encontrada con ID: " + id);
        }
        evaluacionRepository.deleteById(id);
    }

    public Optional<Evaluacion> obtenerEvaluacionPorId(Long id) {
        return evaluacionRepository.findById(id);
    }

    public List<Evaluacion> listarTodasLasEvaluaciones() {
        return evaluacionRepository.findAll();
    }

    public List<Evaluacion> listarEvaluacionesPorClase(Long claseId) {
        return evaluacionRepository.findByClaseId(claseId);
    }

    public List<Evaluacion> listarEvaluacionesActivas() {
        LocalDateTime now = LocalDateTime.now();
        return evaluacionRepository.findByFechaInicioBetweenAndFechaTerminoBetween(now, now, now, now);
    }

    
    @Transactional
    public Evaluacion marcarComoCorregida(Long id) {
        return evaluacionRepository.findById(id)
                .map(evaluacion -> {
                    if (evaluacion.getEstado() == EstadoEvaluacion.CERRADA) { 
                        evaluacion.setEstado(EstadoEvaluacion.CORREGIDA);
                        return evaluacionRepository.save(evaluacion);
                    } else {
                        throw new RuntimeException("La evaluación debe estar CERRADA para ser corregida.");
                    }
                }).orElseThrow(() -> new RuntimeException("Evaluación no encontrada."));
    }
}
