package edutech.proveedorMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.proveedorMS.model.Categoria;
import edutech.proveedorMS.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        return categoriaRepository.findById(id)
                .map(categoriaExistente -> {
                    categoriaExistente.setNombre(categoriaActualizada.getNombre());
                    categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
                  
                    return categoriaRepository.save(categoriaExistente);
                }).orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public void borrarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        
        categoriaRepository.deleteById(id);
    }

    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public List<Categoria> listarTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> consultarCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    public List<Categoria> buscarCategoriasPorDescripcion(String descripcion) {
        return categoriaRepository.findByDescripcionContainingIgnoreCase(descripcion);
    }
}
