package edutech.proveedorMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.proveedorMS.model.Region;
import edutech.proveedorMS.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Transactional
    public Region crearRegion(Region region) {
        if (regionRepository.findByNombre(region.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una región con el nombre: " + region.getNombre());
        }
        return regionRepository.save(region);
    }

    @Transactional
    public Region actualizarRegion(Long id, Region regionActualizada) {
        return regionRepository.findById(id)
                .map(regionExistente -> {
                    regionExistente.setNombre(regionActualizada.getNombre());
                    
                    return regionRepository.save(regionExistente);
                }).orElseThrow(() -> new RuntimeException("Región no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminarRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new RuntimeException("Región no encontrada con ID: " + id);
        }
        
        regionRepository.deleteById(id);
    }

    public Optional<Region> obtenerRegionPorId(Long id) {
        return regionRepository.findById(id);
    }

    public List<Region> listarTodasLasRegiones() {
        return regionRepository.findAll();
    }

    public Optional<Region> obtenerRegionPorNombre(String nombre) {
        return regionRepository.findByNombre(nombre);
    }
}
