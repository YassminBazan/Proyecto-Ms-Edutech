package edutech.gestionDeClaseMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoMaterial; // Importar el nuevo Enum
import edutech.gestionDeClaseMS.model.Material;
import edutech.gestionDeClaseMS.model.TipoMaterial;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.MaterialRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final ClaseRepository claseRepository; 

    @Autowired
    public MaterialService(MaterialRepository materialRepository, ClaseRepository claseRepository) {
        this.materialRepository = materialRepository;
        this.claseRepository = claseRepository;
    }

    @Transactional
    public Material crearMaterial(Material material, Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        material.setClase(clase);
        material.setFechaCreacion(LocalDateTime.now());
        material.setEstadoMaterial(EstadoMaterial.PENDIENTE); // Establecer estado inicial como PENDIENTE
       
        if (material.getTitulo() == null || material.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del material no puede estar vacío.");
        }
        return materialRepository.save(material);
    }

    @Transactional
    public Material actualizarMaterial(Long id, Material materialActualizado) {
        return materialRepository.findById(id)
                .map(materialExistente -> {
                    materialExistente.setTitulo(materialActualizado.getTitulo());
                    materialExistente.setDescripcion(materialActualizado.getDescripcion());
                    materialExistente.setTipoMaterial(materialActualizado.getTipoMaterial());
                    materialExistente.setEstadoMaterial(materialActualizado.getEstadoMaterial()); // Permitir actualización del estado
                   
                    return materialRepository.save(materialExistente);
                }).orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminarMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material no encontrado con ID: " + id);
        }
        materialRepository.deleteById(id);
    }

    public Optional<Material> obtenerMaterialPorId(Long id) {
        return materialRepository.findById(id);
    }

    public List<Material> listarTodosLosMateriales() {
        return materialRepository.findAll();
    }

    public List<Material> listarMaterialesPorClase(Long claseId) {
        return materialRepository.findByClaseId(claseId);
    }

    public List<Material> listarMaterialesPorTipo(TipoMaterial tipoMaterial) {
        return materialRepository.findByTipoMaterial(tipoMaterial);
    }

    @Transactional // Nuevo método para aprobar material
    public Material aprobarMaterial(Long id) {
        return materialRepository.findById(id)
                .map(material -> {
                    material.setEstadoMaterial(EstadoMaterial.APROBADO);
                    return materialRepository.save(material);
                }).orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
    }

    @Transactional // Nuevo método para rechazar material
    public Material rechazarMaterial(Long id) {
        return materialRepository.findById(id)
                .map(material -> {
                    material.setEstadoMaterial(EstadoMaterial.RECHAZADO);
                    return materialRepository.save(material);
                }).orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
    }

    public List<Material> listarMaterialesPorEstado(EstadoMaterial estadoMaterial) { // Nuevo método para listar por estado
        return materialRepository.findByEstadoMaterial(estadoMaterial);
    }
}