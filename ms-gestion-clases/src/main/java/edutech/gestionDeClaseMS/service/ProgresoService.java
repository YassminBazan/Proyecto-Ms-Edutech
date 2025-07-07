package edutech.gestionDeClaseMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoProgreso;
import edutech.gestionDeClaseMS.model.Progreso;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.ProgresoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final ClaseRepository claseRepository; 

    @Autowired
    public ProgresoService(ProgresoRepository progresoRepository, ClaseRepository claseRepository) {
        this.progresoRepository = progresoRepository;
        this.claseRepository = claseRepository;
    }

    @Transactional
    public Progreso crearOActualizarProgreso(Long idUsuario, Long claseId, Double porcentajeAvance, Double notaPromedio) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        
        Optional<Progreso> existingProgreso = progresoRepository.findByIdUsuarioAndClaseId(idUsuario, claseId);

        Progreso progreso;
        if (existingProgreso.isPresent()) {
            progreso = existingProgreso.get();
        } else {
            progreso = new Progreso();
            progreso.setIdUsuario(idUsuario);
            progreso.setClase(clase);
            progreso.setEstado(EstadoProgreso.INICIADO); 
        }

        if (porcentajeAvance != null) {
            if (porcentajeAvance < 0 || porcentajeAvance > 100) {
                throw new IllegalArgumentException("El porcentaje de avance debe estar entre 0 y 100.");
            }
            progreso.setPorcentajeAvance(porcentajeAvance);
          
            if (porcentajeAvance == 100.0) {
                progreso.setEstado(EstadoProgreso.COMPLETADO);
            } else if (porcentajeAvance > 0 && progreso.getEstado() != EstadoProgreso.COMPLETADO) {
                progreso.setEstado(EstadoProgreso.EN_PROGRESO);
            }
        }

      
        if (notaPromedio != null) {
             if (notaPromedio < 0 || notaPromedio > 10) { 
                 throw new IllegalArgumentException("La nota promedio debe estar entre 0 y 10.");
             }
            progreso.setNotaPromedio(notaPromedio);
        }

        return progresoRepository.save(progreso);
    }

    
    @Transactional
    public Progreso actualizarProgreso(Long idProgreso, Double porcentajeAvance, Double notaPromedio) {
        Progreso progreso = progresoRepository.findById(idProgreso)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado con ID: " + idProgreso));

       
        if (porcentajeAvance != null) {
            if (porcentajeAvance < 0 || porcentajeAvance > 100) {
                throw new IllegalArgumentException("El porcentaje de avance debe estar entre 0 y 100.");
            }
            progreso.setPorcentajeAvance(porcentajeAvance);
           
            if (porcentajeAvance == 100.0) {
                progreso.setEstado(EstadoProgreso.COMPLETADO);
            } else if (porcentajeAvance > 0 && progreso.getEstado() != EstadoProgreso.COMPLETADO) {
                progreso.setEstado(EstadoProgreso.EN_PROGRESO);
            }
        }

      
        if (notaPromedio != null) {
             if (notaPromedio < 0 || notaPromedio > 10) {
                 throw new IllegalArgumentException("La nota promedio debe estar entre 0 y 10.");
             }
            progreso.setNotaPromedio(notaPromedio);
        }

        return progresoRepository.save(progreso);
    }


    public Optional<Progreso> obtenerProgresoPorId(Long idProgreso) {
        return progresoRepository.findById(idProgreso);
    }

    public Optional<Progreso> obtenerProgresoPorUsuarioYClase(Long idUsuario, Long claseId) {
        return progresoRepository.findByIdUsuarioAndClaseId(idUsuario, claseId);
    }

    public List<Progreso> listarProgresosPorEstudiante(Long idUsuario) {
        return progresoRepository.findByIdUsuario(idUsuario);
    }

    public List<Progreso> listarProgresosPorClase(Long claseId) {
        return progresoRepository.findByClaseId(claseId);
    }

    
    public Double calcularPorcentajeDeCompletitud(Long idProgreso) {
        Progreso progreso = progresoRepository.findById(idProgreso)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado con ID: " + idProgreso));
        return progreso.getPorcentajeAvance();
    }
}
