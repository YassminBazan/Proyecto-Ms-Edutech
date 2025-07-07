package edutech.gestionDeClaseMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.ClaseDTO;
import edutech.gestionDeClaseMS.repository.ClaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClaseService {

    private final ClaseRepository claseRepository;

    @Autowired
    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    @Transactional
    public Clase crearClase(Clase clase) {
        clase.setFechaInicio(LocalDateTime.now()); 
        if (clase.getIdCurso() == null) {
            throw new IllegalArgumentException("El ID del curso no puede ser nulo.");
        }
        if (clase.getNombre() == null || clase.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la clase no puede estar vacío.");
        }
        return claseRepository.save(clase);
    }

    @Transactional
    public Clase actualizarClase(Long id, Clase claseActualizada) {
        return claseRepository.findById(id)
                .map(claseExistente -> {
                    claseExistente.setNombre(claseActualizada.getNombre());
                    claseExistente.setDescripcion(claseActualizada.getDescripcion());
                    claseExistente.setFechaInicio(claseActualizada.getFechaInicio());
                    claseExistente.setFechaTermino(claseActualizada.getFechaTermino());
                    
                    return claseRepository.save(claseExistente);
                }).orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminarClase(Long id) {
        if (!claseRepository.existsById(id)) {
            throw new RuntimeException("Clase no encontrada con ID: " + id);
        }
       
        claseRepository.deleteById(id);
    }

    public Optional<Clase> obtenerClasePorId(Long id) {
        return claseRepository.findById(id);
    }

    public List<Clase> listarTodasLasClases() {
        return claseRepository.findAll();
    }


    //Metodos para Ms Gestion de Cursos
    public List<ClaseDTO> obtenerClasesDtoPorCurso(Long idCurso) {
        List<Clase> clases = claseRepository.findByIdCurso(idCurso);
        return clases.stream().map(this::mapToDto).toList();
    }

    private ClaseDTO mapToDto(Clase clase) {
        ClaseDTO dto = new ClaseDTO();
        dto.setId(clase.getId());
        dto.setNombre(clase.getNombre());
        dto.setDescripcion(clase.getDescripcion());
        dto.setFechaInicio(clase.getFechaInicio());
        dto.setFechaTermino(clase.getFechaTermino());
        dto.setIdCurso(clase.getIdCurso());
        return dto;
    }

    public void eliminarClasesPorCurso(Long idCurso) {
        List<Clase> clases = claseRepository.findByIdCurso(idCurso);
        claseRepository.deleteAll(clases);
    }



}