package edutech.gestionDeClaseMS.service;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoMaterial;
import edutech.gestionDeClaseMS.model.Material;
import edutech.gestionDeClaseMS.model.TipoMaterial;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private MaterialService materialService;

    private Clase clase;
    private Material material;

    @BeforeEach
    void setUp() {
        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        material = new Material(1L, "Introducción a Java", "Conceptos básicos de Java", TipoMaterial.PDF, LocalDateTime.now(), EstadoMaterial.PENDIENTE, clase);
    }

    @Test
    void crearMaterial_Success() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        Material nuevoMaterial = materialService.crearMaterial(material, 1L);

        assertNotNull(nuevoMaterial);
        assertEquals("Introducción a Java", nuevoMaterial.getTitulo());
        assertEquals(EstadoMaterial.PENDIENTE, nuevoMaterial.getEstadoMaterial());
        verify(claseRepository, times(1)).findById(1L);
        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void crearMaterial_ClaseNotFound_ThrowsException() {
        when(claseRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            materialService.crearMaterial(material, 2L);
        });
        assertEquals("Clase no encontrada con ID: 2", thrown.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void crearMaterial_TituloVacio_ThrowsException() {
        material.setTitulo("");
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            materialService.crearMaterial(material, 1L);
        });
        assertEquals("El título del material no puede estar vacío.", thrown.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void actualizarMaterial_Success() {
        Material materialActualizado = new Material(1L, "Java Intermedio", "Temas avanzados", TipoMaterial.VIDEO, LocalDateTime.now(), EstadoMaterial.APROBADO, clase);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(materialActualizado);

        Material result = materialService.actualizarMaterial(1L, materialActualizado);

        assertNotNull(result);
        assertEquals("Java Intermedio", result.getTitulo());
        assertEquals(TipoMaterial.VIDEO, result.getTipoMaterial());
        assertEquals(EstadoMaterial.APROBADO, result.getEstadoMaterial());
        verify(materialRepository, times(1)).findById(1L);
        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void actualizarMaterial_NotFound_ThrowsException() {
        Material materialActualizado = new Material(1L, "Java Intermedio", "Temas avanzados", TipoMaterial.VIDEO, LocalDateTime.now(), EstadoMaterial.APROBADO, clase);
        when(materialRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            materialService.actualizarMaterial(2L, materialActualizado);
        });
        assertEquals("Material no encontrado con ID: 2", thrown.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void eliminarMaterial_Success() {
        when(materialRepository.existsById(1L)).thenReturn(true);
        doNothing().when(materialRepository).deleteById(1L);

        materialService.eliminarMaterial(1L);

        verify(materialRepository, times(1)).existsById(1L);
        verify(materialRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarMaterial_NotFound_ThrowsException() {
        when(materialRepository.existsById(2L)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            materialService.eliminarMaterial(2L);
        });
        assertEquals("Material no encontrado con ID: 2", thrown.getMessage());
        verify(materialRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerMaterialPorId_Found() {
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        Optional<Material> result = materialService.obtenerMaterialPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(material, result.get());
        verify(materialRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerMaterialPorId_NotFound() {
        when(materialRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Material> result = materialService.obtenerMaterialPorId(2L);

        assertFalse(result.isPresent());
        verify(materialRepository, times(1)).findById(2L);
    }

    @Test
    void listarTodosLosMateriales_Success() {
        List<Material> materiales = Arrays.asList(material, new Material(2L, "Matrices en Java", "Ejercicios", TipoMaterial.LECTURA, LocalDateTime.now(), EstadoMaterial.APROBADO, clase));
        when(materialRepository.findAll()).thenReturn(materiales);

        List<Material> result = materialService.listarTodosLosMateriales();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(materialRepository, times(1)).findAll();
    }

    @Test
    void listarMaterialesPorClase_Success() {
        List<Material> materialesPorClase = Arrays.asList(material);
        when(materialRepository.findByClaseId(1L)).thenReturn(materialesPorClase);

        List<Material> result = materialService.listarMaterialesPorClase(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getClase().getId());
        verify(materialRepository, times(1)).findByClaseId(1L);
    }

    @Test
    void listarMaterialesPorTipo_Success() {
        List<Material> materialesPorTipo = Arrays.asList(material);
        when(materialRepository.findByTipoMaterial(TipoMaterial.PDF)).thenReturn(materialesPorTipo);

        List<Material> result = materialService.listarMaterialesPorTipo(TipoMaterial.PDF);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TipoMaterial.PDF, result.get(0).getTipoMaterial());
        verify(materialRepository, times(1)).findByTipoMaterial(TipoMaterial.PDF);
    }

    @Test
    void aprobarMaterial_Success() {
        material.setEstadoMaterial(EstadoMaterial.PENDIENTE);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        Material result = materialService.aprobarMaterial(1L);

        assertNotNull(result);
        assertEquals(EstadoMaterial.APROBADO, result.getEstadoMaterial());
        verify(materialRepository, times(1)).findById(1L);
        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void aprobarMaterial_NotFound_ThrowsException() {
        when(materialRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            materialService.aprobarMaterial(2L);
        });
        assertEquals("Material no encontrado con ID: 2", thrown.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void rechazarMaterial_Success() {
        material.setEstadoMaterial(EstadoMaterial.PENDIENTE);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        Material result = materialService.rechazarMaterial(1L);

        assertNotNull(result);
        assertEquals(EstadoMaterial.RECHAZADO, result.getEstadoMaterial());
        verify(materialRepository, times(1)).findById(1L);
        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void rechazarMaterial_NotFound_ThrowsException() {
        when(materialRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            materialService.rechazarMaterial(2L);
        });
        assertEquals("Material no encontrado con ID: 2", thrown.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void listarMaterialesPorEstado_Success() {
        List<Material> materialesPendientes = Arrays.asList(material);
        when(materialRepository.findByEstadoMaterial(EstadoMaterial.PENDIENTE)).thenReturn(materialesPendientes);

        List<Material> result = materialService.listarMaterialesPorEstado(EstadoMaterial.PENDIENTE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EstadoMaterial.PENDIENTE, result.get(0).getEstadoMaterial());
        verify(materialRepository, times(1)).findByEstadoMaterial(EstadoMaterial.PENDIENTE);
    }
}