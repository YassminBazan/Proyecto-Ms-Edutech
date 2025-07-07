package edutech.gestionDeClaseMS.service;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
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
public class ClaseServiceTest {

    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private ClaseService claseService;

    private Clase clase;

    @BeforeEach
    void setUp() {
        
        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), 
                new ArrayList<>(), 
                new ArrayList<>()  
        );
    }

    @Test
    void crearClase_Success() {
        when(claseRepository.save(any(Clase.class))).thenReturn(clase);

        Clase nuevaClase = claseService.crearClase(clase);

        assertNotNull(nuevaClase);
        assertEquals("Programación Java", nuevaClase.getNombre());
        assertNotNull(nuevaClase.getFechaInicio()); 
        verify(claseRepository, times(1)).save(any(Clase.class));
    }

    @Test
    void crearClase_IdCursoNulo_ThrowsException() {
        clase.setIdCurso(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            claseService.crearClase(clase);
        });
        assertEquals("El ID del curso no puede ser nulo.", thrown.getMessage());
        verify(claseRepository, never()).save(any(Clase.class));
    }

    @Test
    void crearClase_NombreVacio_ThrowsException() {
        clase.setNombre("");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            claseService.crearClase(clase);
        });
        assertEquals("El nombre de la clase no puede estar vacío.", thrown.getMessage());
        verify(claseRepository, never()).save(any(Clase.class));
    }

    @Test
    void actualizarClase_Success() {
        Clase claseActualizada = new Clase(1L, "Java Avanzado", "Curso de Java para expertos", LocalDateTime.now(), LocalDateTime.now().plusDays(60), 101L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(claseRepository.save(any(Clase.class))).thenReturn(claseActualizada);

        Clase result = claseService.actualizarClase(1L, claseActualizada);

        assertNotNull(result);
        assertEquals("Java Avanzado", result.getNombre());
        verify(claseRepository, times(1)).findById(1L);
        verify(claseRepository, times(1)).save(any(Clase.class));
    }

    @Test
    void actualizarClase_NotFound_ThrowsException() {
        Clase claseActualizada = new Clase(1L, "Java Avanzado", "Curso de Java para expertos", LocalDateTime.now(), LocalDateTime.now().plusDays(60), 101L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(claseRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            claseService.actualizarClase(2L, claseActualizada);
        });
        assertEquals("Clase no encontrada con ID: 2", thrown.getMessage());
        verify(claseRepository, never()).save(any(Clase.class));
    }

    @Test
    void eliminarClase_Success() {
        when(claseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(claseRepository).deleteById(1L);

        claseService.eliminarClase(1L);

        verify(claseRepository, times(1)).existsById(1L);
        verify(claseRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarClase_NotFound_ThrowsException() {
        when(claseRepository.existsById(2L)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            claseService.eliminarClase(2L);
        });
        assertEquals("Clase no encontrada con ID: 2", thrown.getMessage());
        verify(claseRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerClasePorId_Found() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        Optional<Clase> result = claseService.obtenerClasePorId(1L);

        assertTrue(result.isPresent());
        assertEquals(clase, result.get());
        verify(claseRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerClasePorId_NotFound() {
        when(claseRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Clase> result = claseService.obtenerClasePorId(2L);

        assertFalse(result.isPresent());
        verify(claseRepository, times(1)).findById(2L);
    }

    @Test
    void listarTodasLasClases_Success() {
        List<Clase> clases = Arrays.asList(clase, new Clase(2L, "Python Básico", "Introducción a Python", LocalDateTime.now(), LocalDateTime.now().plusDays(20), 102L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        when(claseRepository.findAll()).thenReturn(clases);

        List<Clase> result = claseService.listarTodasLasClases();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(claseRepository, times(1)).findAll();
    }

    @Test
    void listarClasesPorCurso_Success() {
        List<Clase> clasesPorCurso = Arrays.asList(clase);
        when(claseRepository.findByIdCurso(101L)).thenReturn(clasesPorCurso);

        List<Clase> result = claseService.listarClasesPorCurso(101L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getIdCurso());
        verify(claseRepository, times(1)).findByIdCurso(101L);
    }

    @Test
    void listarClasesPorCurso_NoClasesFound() {
        when(claseRepository.findByIdCurso(999L)).thenReturn(Arrays.asList());

        List<Clase> result = claseService.listarClasesPorCurso(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(claseRepository, times(1)).findByIdCurso(999L);
    }
}