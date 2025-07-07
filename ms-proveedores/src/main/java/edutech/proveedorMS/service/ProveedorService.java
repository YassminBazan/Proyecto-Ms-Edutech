package edutech.proveedorMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.proveedorMS.model.Categoria;
import edutech.proveedorMS.model.Comuna;
import edutech.proveedorMS.model.EstadoProveedor;
import edutech.proveedorMS.model.Proveedor;
import edutech.proveedorMS.repository.CategoriaRepository;
import edutech.proveedorMS.repository.ComunaRepository;
import edutech.proveedorMS.repository.ProveedorRepository;
import edutech.proveedorMS.repository.RegionRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.plaf.synth.Region;



@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public ProveedorService(ProveedorRepository proveedorRepository,
                            RegionRepository regionRepository,
                            ComunaRepository comunaRepository,
                            CategoriaRepository categoriaRepository) {
        this.proveedorRepository = proveedorRepository;
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Proveedor crearProveedor(Proveedor proveedor, Long regionId, Long comunaId, Set<Long> categoriaIds) {
        
        if (proveedorRepository.findByRun(proveedor.getRun()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un proveedor con el RUN: " + proveedor.getRun());
        }
        if (proveedorRepository.findByEmail(proveedor.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un proveedor con el email: " + proveedor.getEmail());
        }
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío.");
        }

        
        proveedor.setFechaRegistro(LocalDate.now());
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(EstadoProveedor.PENDIENTE_APROBACION); 
        }

        
        if (regionId != null) {
            edutech.proveedorMS.model.Region region = regionRepository.findById(regionId)
                    .orElseThrow(() -> new RuntimeException("Región no encontrada con ID: " + regionId));
            proveedor.setRegion(region);
        }

        
        if (comunaId != null) {
            Comuna comuna = comunaRepository.findById(comunaId)
                    .orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + comunaId));
            
            if (proveedor.getRegion() != null && !comuna.getRegion().getId().equals(proveedor.getRegion().getId())) {
                 throw new IllegalArgumentException("La comuna no pertenece a la región especificada.");
            }
            proveedor.setComuna(comuna);
        }

        
        if (categoriaIds != null && !categoriaIds.isEmpty()) {
            Set<Categoria> categorias = categoriaIds.stream()
                    .map(id -> categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id)))
                    .collect(Collectors.toSet());
            proveedor.setCategorias(categorias);
        }

        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor actualizarProveedor(Long id, Proveedor proveedorActualizado, Long regionId, Long comunaId, Set<Long> categoriaIds) {
        return proveedorRepository.findById(id)
                .map(proveedorExistente -> {
                    
                    proveedorExistente.setNombre(proveedorActualizado.getNombre());
                    proveedorExistente.setApellido(proveedorActualizado.getApellido());
                    proveedorExistente.setRazonSocial(proveedorActualizado.getRazonSocial());
                    proveedorExistente.setDireccion(proveedorActualizado.getDireccion());
                    proveedorExistente.setTelefono(proveedorActualizado.getTelefono());
                    proveedorExistente.setEmail(proveedorActualizado.getEmail());
                    proveedorExistente.setEstado(proveedorActualizado.getEstado());

                   
                    if (regionId != null) {
                        edutech.proveedorMS.model.Region region = regionRepository.findById(regionId)
                                .orElseThrow(() -> new RuntimeException("Región no encontrada con ID: " + regionId));
                        proveedorExistente.setRegion(region);
                    } else {
                        proveedorExistente.setRegion(null); 
                    }

                   
                    if (comunaId != null) {
                        Comuna comuna = comunaRepository.findById(comunaId)
                                .orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + comunaId));
                        if (proveedorExistente.getRegion() != null && !comuna.getRegion().getId().equals(proveedorExistente.getRegion().getId())) {
                             throw new IllegalArgumentException("La comuna no pertenece a la región especificada.");
                        }
                        proveedorExistente.setComuna(comuna);
                    } else {
                        proveedorExistente.setComuna(null);
                    }

                   
                    if (categoriaIds != null) {
                        Set<Categoria> categorias = categoriaIds.stream()
                                .map(catId -> categoriaRepository.findById(catId).orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + catId)))
                                .collect(Collectors.toSet());
                        proveedorExistente.setCategorias(categorias);
                    } else {
                        proveedorExistente.setCategorias(new HashSet<>()); 
                    }


                   
                    return proveedorRepository.save(proveedorExistente);
                }).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + id);
        }
        
        proveedorRepository.deleteById(id);
    }

    public Optional<Proveedor> obtenerProveedorPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> listarTodosLosProveedores() {
        return proveedorRepository.findAll();
    }

    public List<Proveedor> listarProveedoresPorEstado(EstadoProveedor estado) {
        return proveedorRepository.findByEstado(estado);
    }

    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        return proveedorRepository.findByNombre(nombre);
    }

    public List<Proveedor> buscarProveedoresPorRazonSocial(String razonSocial) {
        return proveedorRepository.findByRazonSocial(razonSocial);
    }

    public List<Proveedor> listarProveedoresPorRegion(String nombreRegion) {
        return proveedorRepository.findByRegionNombre(nombreRegion);
    }

    public List<Proveedor> listarProveedoresPorComuna(String nombreComuna) {
        return proveedorRepository.findByComunaNombre(nombreComuna);
    }

    public List<Proveedor> listarProveedoresPorCategoria(String nombreCategoria) {
        return proveedorRepository.findByCategorias_Nombre(nombreCategoria);
    }
}