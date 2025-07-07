package edutech.proveedorMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.proveedorMS.model.Comuna;
import edutech.proveedorMS.repository.ComunaRepository;
import edutech.proveedorMS.repository.RegionRepository;

import java.util.List;
import java.util.Optional;



@Service
public class ComunaService {

    private final ComunaRepository comunaRepository;
    private final RegionRepository regionRepository;

    @Autowired
    public ComunaService(ComunaRepository comunaRepository, RegionRepository regionRepository) {
        this.comunaRepository = comunaRepository;
        this.regionRepository = regionRepository;
    }

    @Transactional
    public Comuna crearComuna(Comuna comuna, Long regionId) {
        if (comunaRepository.findByNombre(comuna.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una comuna con el nombre: " + comuna.getNombre());
        }
        edutech.proveedorMS.model.Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new RuntimeException("Región no encontrada con ID: " + regionId));
        comuna.setRegion(region);
        return comunaRepository.save(comuna);
    }

     @Transactional
    public Comuna actualizarComuna(Long id, Comuna comunaActualizada, Long newRegionId) {
        return comunaRepository.findById(id)
                .map(comunaExistente -> {
                    comunaExistente.setNombre(comunaActualizada.getNombre());
                    if (newRegionId != null && !comunaExistente.getRegion().getId().equals(newRegionId)) {
                        edutech.proveedorMS.model.Region nuevaRegion = regionRepository.findById(newRegionId)
                                .orElseThrow(() -> new RuntimeException("Nueva Región no encontrada con ID: " + newRegionId));
                        comunaExistente.setRegion(nuevaRegion);
                    }
                    return comunaRepository.save(comunaExistente);
                }).orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + id));
    }
                    

               
    @Transactional
    public void eliminarComuna(Long id) {
        if (!comunaRepository.existsById(id)) {
            throw new RuntimeException("Comuna no encontrada con ID: " + id);
        }
        
        comunaRepository.deleteById(id);
    }

    public Optional<Comuna> obtenerComunaPorId(Long id) {
        return comunaRepository.findById(id);
    }

    public List<Comuna> listarTodasLasComunas() {
        return comunaRepository.findAll();
    }

    public Optional<Comuna> obtenerComunaPorNombre(String nombre) {
        return comunaRepository.findByNombre(nombre);
    }

    public List<Comuna> listarComunasPorRegion(Long regionId) {
        return comunaRepository.findByRegionId(regionId);
    }
}
