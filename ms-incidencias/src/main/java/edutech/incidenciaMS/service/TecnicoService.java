package edutech.incidenciaMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import edutech.incidenciaMS.model.Tecnico;
import edutech.incidenciaMS.repository.TecnicoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    @Autowired
    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    
    public Tecnico crearTecnico(Tecnico tecnico) {
        // aqui se hace validacion, ver si el rut ya existe
        if (tecnicoRepository.findByRut(tecnico.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un técnico con el RUT: " + tecnico.getRut());
        }
        return tecnicoRepository.save(tecnico);
    }

    
    public Tecnico actualizarTecnico(Long id, Tecnico tecnicoActualizado) {
        return tecnicoRepository.findById(id)
                .map(tecnicoExistente -> {
                    tecnicoExistente.setNombre(tecnicoActualizado.getNombre());
                    tecnicoExistente.setEspecialidad(tecnicoActualizado.getEspecialidad());
                    tecnicoExistente.setDisponibilidad(tecnicoActualizado.isDisponibilidad());
                    
                    tecnicoExistente.setNumeroContacto(tecnicoActualizado.getNumeroContacto());
                    return tecnicoRepository.save(tecnicoExistente);
                }).orElseThrow(() -> new RuntimeException("Técnico no encontrado con ID: " + id));
    }

    
    public void eliminarTecnico(Long id) {
        if (!tecnicoRepository.existsById(id)) {
            throw new RuntimeException("Técnico no encontrado con ID: " + id);
        }
        
        tecnicoRepository.deleteById(id);
    }

    public Optional<Tecnico> obtenerTecnicoPorId(Long id) {
        return tecnicoRepository.findById(id);
    }

    public List<Tecnico> listarTodosTecnicos() {
        return tecnicoRepository.findAll();
    }

  
}
