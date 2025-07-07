package edutech.incidenciaMS.service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.incidenciaMS.model.CategoriaIncidencia;
import edutech.incidenciaMS.model.EstadoIncidencia;
import edutech.incidenciaMS.model.Incidencia;
import edutech.incidenciaMS.model.PrioridadIncidencia;
import edutech.incidenciaMS.model.Tecnico;
import edutech.incidenciaMS.repository.IncidenciaRepository;
import edutech.incidenciaMS.repository.TecnicoRepository;
@Service
public class IncidenciaService {
    private final IncidenciaRepository incidenciaRepository;
    private final TecnicoRepository tecnicoRepository; // To assign technicians

    @Autowired
    public IncidenciaService(IncidenciaRepository incidenciaRepository, TecnicoRepository tecnicoRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    // --- Métodos de Gestión de Incidencias ---

    @Transactional 
    public Incidencia crearIncidencia(Incidencia incidencia) {
        incidencia.setFechaCreacion(LocalDateTime.now());
        incidencia.setEstado(EstadoIncidencia.ACTIVA); 
       
        if (incidencia.getPrioridad() == null) {
            incidencia.setPrioridad(PrioridadIncidencia.MEDIA);
        }
        if (incidencia.getCategoria() == null) {
            incidencia.setCategoria(CategoriaIncidencia.OTRO);
        }
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public Incidencia actualizarIncidencia(Long id, Incidencia incidenciaActualizada) {
        return incidenciaRepository.findById(id)
                .map(incidenciaExistente -> {
                    incidenciaExistente.setTitulo(incidenciaActualizada.getTitulo());
                    incidenciaExistente.setDescripcion(incidenciaActualizada.getDescripcion());
                    incidenciaExistente.setTiempoEstimadoResolucionHoras(incidenciaActualizada.getTiempoEstimadoResolucionHoras());
                    incidenciaExistente.setPrioridad(incidenciaActualizada.getPrioridad());
                    incidenciaExistente.setCategoria(incidenciaActualizada.getCategoria());
                    
                    return incidenciaRepository.save(incidenciaExistente);
                }).orElseThrow(() -> new RuntimeException("Incidencia no encontrada con ID: " + id)); 
    }

    @Transactional
    public void eliminarIncidencia(Long id) {
        if (!incidenciaRepository.existsById(id)) {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
        incidenciaRepository.deleteById(id);
    }

    public Optional<Incidencia> obtenerIncidenciaPorId(Long id) {
        return incidenciaRepository.findById(id);
    }

    public List<Incidencia> listarTodasIncidencias() {
        return incidenciaRepository.findAll();
    }

    public List<Incidencia> listarIncidenciasPorEstado(EstadoIncidencia estado) {
        return incidenciaRepository.findByEstado(estado);
    }

  

    @Transactional
    public Incidencia asignarTecnico(Long incidenciaId, Long tecnicoId) {
        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada."));
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado."));

        if (!tecnico.isDisponibilidad()) {
            throw new RuntimeException("El técnico no está disponible.");
        }

        incidencia.setTecnicoAsignado(tecnico);
        if (incidencia.getEstado() == EstadoIncidencia.ACTIVA) {
            incidencia.setEstado(EstadoIncidencia.EN_PROCESO); 
        }
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public Incidencia resolverIncidencia(Long id) {
        return incidenciaRepository.findById(id)
                .map(incidencia -> {
                    if (incidencia.getEstado() == EstadoIncidencia.EN_PROCESO || incidencia.getEstado() == EstadoIncidencia.ACTIVA) {
                        incidencia.setEstado(EstadoIncidencia.RESUELTA);
                        
                        return incidenciaRepository.save(incidencia);
                    } else {
                        throw new RuntimeException("La incidencia no puede ser resuelta en su estado actual: " + incidencia.getEstado());
                    }
                }).orElseThrow(() -> new RuntimeException("Incidencia no encontrada."));
    }

    @Transactional
    public Incidencia cerrarIncidencia(Long id) {
        return incidenciaRepository.findById(id)
                .map(incidencia -> {
                    if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
                        incidencia.setEstado(EstadoIncidencia.CERRADA);
                        return incidenciaRepository.save(incidencia);
                    } else {
                        throw new RuntimeException("La incidencia debe estar RESUELTA para ser cerrada. Estado actual: " + incidencia.getEstado());
                    }
                }).orElseThrow(() -> new RuntimeException("Incidencia no encontrada."));
    }

    @Transactional
    public Incidencia reabrirIncidencia(Long id) {
        return incidenciaRepository.findById(id)
                .map(incidencia -> {
                    if (incidencia.getEstado() == EstadoIncidencia.RESUELTA || incidencia.getEstado() == EstadoIncidencia.CERRADA) {
                        incidencia.setEstado(EstadoIncidencia.EN_PROCESO); 
                        incidencia.setTecnicoAsignado(null); 
                        return incidenciaRepository.save(incidencia);
                    } else {
                        throw new RuntimeException("Solo las incidencias RESUELTAS o CERRADAS pueden ser reabiertas.");
                    }
                }).orElseThrow(() -> new RuntimeException("Incidencia no encontrada."));
    }

    
    public long calcularTiempoDesdeCreacionHoras(Long id) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada."));
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(incidencia.getFechaCreacion(), now);
        return duration.toHours();
    }
}

