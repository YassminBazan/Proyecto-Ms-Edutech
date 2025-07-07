package edutech.gestionDeClaseMS.service;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoProgreso;
import edutech.gestionDeClaseMS.model.Progreso;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.ProgresoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; // Importar ArgumentCaptor
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
public class ProgresoServiceTest {

    @Mock
    private ProgresoRepository progresoRepository;

    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private ProgresoService progresoService;

    private Clase clase;
    private Progreso progreso;

    @BeforeEach
    void setUp() {
        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        // El estado inicial del objeto 'progreso' en el setUp
        // ya no importa tanto para los asserts después de guardar,
        // ya que capturaremos el objeto *después* de la modificación del servicio.
        progreso = new Progreso(1L, 10L, clase, EstadoProgreso.INICIADO, 0.0, null);
    }

    @Test
    void crearOActualizarProgreso_NewProgreso_Success() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(progresoRepository.findByIdUsuarioAndClaseId(10L, 1L)).thenReturn(Optional.empty());

        // Usar doAnswer para simular que save devuelve el mismo objeto que recibe,
        // pero después de que el servicio lo haya modificado.
        // O más comúnmente, usar ArgumentCaptor para verificar el objeto pasado al save.
        ArgumentCaptor<Progreso> progresoCaptor = ArgumentCaptor.forClass(Progreso.class);
        when(progresoRepository.save(progresoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Progreso result = progresoService.crearOActualizarProgreso(10L, 1L, 25.0, null);

        assertNotNull(result);
        // Afirmar el estado del objeto capturado
        assertEquals(EstadoProgreso.EN_PROGRESO, progresoCaptor.getValue().getEstado());
        assertEquals(25.0, progresoCaptor.getValue().getPorcentajeAvance());
        // También podemos afirmar el resultado devuelto, que ahora es el objeto modificado.
        assertEquals(EstadoProgreso.EN_PROGRESO, result.getEstado());
        assertEquals(25.0, result.getPorcentajeAvance());

        verify(claseRepository, times(1)).findById(1L);
        verify(progresoRepository, times(1)).findByIdUsuarioAndClaseId(10L, 1L);
        verify(progresoRepository, times(1)).save(any(Progreso.class)); // Verificar que save fue llamado
    }

    @Test
    void crearOActualizarProgreso_ExistingProgreso_Success() {
        progreso.setPorcentajeAvance(50.0);
        progreso.setEstado(EstadoProgreso.EN_PROGRESO);
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(progresoRepository.findByIdUsuarioAndClaseId(10L, 1L)).thenReturn(Optional.of(progreso));
        
        ArgumentCaptor<Progreso> progresoCaptor = ArgumentCaptor.forClass(Progreso.class);
        when(progresoRepository.save(progresoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Progreso result = progresoService.crearOActualizarProgreso(10L, 1L, 75.0, 8.5);

        assertNotNull(result);
        assertEquals(EstadoProgreso.EN_PROGRESO, progresoCaptor.getValue().getEstado());
        assertEquals(75.0, progresoCaptor.getValue().getPorcentajeAvance());
        assertEquals(8.5, progresoCaptor.getValue().getNotaPromedio());
        verify(claseRepository, times(1)).findById(1L);
        verify(progresoRepository, times(1)).findByIdUsuarioAndClaseId(10L, 1L);
        verify(progresoRepository, times(1)).save(any(Progreso.class));
    }

    @Test
    void crearOActualizarProgreso_ClaseNotFound_ThrowsException() {
        when(claseRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            progresoService.crearOActualizarProgreso(10L, 2L, 50.0, null);
        });
        assertEquals("Clase no encontrada con ID: 2", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void crearOActualizarProgreso_InvalidPorcentajeAvance_ThrowsException() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            progresoService.crearOActualizarProgreso(10L, 1L, 101.0, null);
        });
        assertEquals("El porcentaje de avance debe estar entre 0 y 100.", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void crearOActualizarProgreso_InvalidNotaPromedio_ThrowsException() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            progresoService.crearOActualizarProgreso(10L, 1L, 50.0, 11.0);
        });
        assertEquals("La nota promedio debe estar entre 0 y 10.", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void actualizarProgreso_Success() {
        progreso.setPorcentajeAvance(50.0);
        progreso.setEstado(EstadoProgreso.EN_PROGRESO);
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));
        
        ArgumentCaptor<Progreso> progresoCaptor = ArgumentCaptor.forClass(Progreso.class);
        when(progresoRepository.save(progresoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Progreso result = progresoService.actualizarProgreso(1L, 75.0, 9.0);

        assertNotNull(result);
        assertEquals(75.0, progresoCaptor.getValue().getPorcentajeAvance());
        assertEquals(9.0, progresoCaptor.getValue().getNotaPromedio());
        verify(progresoRepository, times(1)).findById(1L);
        verify(progresoRepository, times(1)).save(any(Progreso.class));
    }

    @Test
    void actualizarProgreso_NotFound_ThrowsException() {
        when(progresoRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            progresoService.actualizarProgreso(2L, 50.0, null);
        });
        assertEquals("Progreso no encontrado con ID: 2", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void actualizarProgreso_InvalidPorcentajeAvance_ThrowsException() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            progresoService.actualizarProgreso(1L, -10.0, null);
        });
        assertEquals("El porcentaje de avance debe estar entre 0 y 100.", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void actualizarProgreso_InvalidNotaPromedio_ThrowsException() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            progresoService.actualizarProgreso(1L, null, -5.0);
        });
        assertEquals("La nota promedio debe estar entre 0 y 10.", thrown.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    @Test
    void obtenerProgresoPorId_Found() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        Optional<Progreso> result = progresoService.obtenerProgresoPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(progreso, result.get());
        verify(progresoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerProgresoPorId_NotFound() {
        when(progresoRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Progreso> result = progresoService.obtenerProgresoPorId(2L);

        assertFalse(result.isPresent());
        verify(progresoRepository, times(1)).findById(2L);
    }

    @Test
    void obtenerProgresoPorUsuarioYClase_Found() {
        when(progresoRepository.findByIdUsuarioAndClaseId(10L, 1L)).thenReturn(Optional.of(progreso));

        Optional<Progreso> result = progresoService.obtenerProgresoPorUsuarioYClase(10L, 1L);

        assertTrue(result.isPresent());
        assertEquals(progreso, result.get());
        verify(progresoRepository, times(1)).findByIdUsuarioAndClaseId(10L, 1L);
    }

    @Test
    void obtenerProgresoPorUsuarioYClase_NotFound() {
        when(progresoRepository.findByIdUsuarioAndClaseId(10L, 2L)).thenReturn(Optional.empty());

        Optional<Progreso> result = progresoService.obtenerProgresoPorUsuarioYClase(10L, 2L);

        assertFalse(result.isPresent());
        verify(progresoRepository, times(1)).findByIdUsuarioAndClaseId(10L, 2L);
    }

    @Test
    void listarProgresosPorEstudiante_Success() {
        Progreso progreso2 = new Progreso(2L, 10L, clase, EstadoProgreso.COMPLETADO, 100.0, 9.5);
        when(progresoRepository.findByIdUsuario(10L)).thenReturn(Arrays.asList(progreso, progreso2));

        List<Progreso> result = progresoService.listarProgresosPorEstudiante(10L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(progresoRepository, times(1)).findByIdUsuario(10L);
    }

    @Test
    void listarProgresosPorClase_Success() {
        Progreso progreso2 = new Progreso(2L, 11L, clase, EstadoProgreso.EN_PROGRESO, 60.0, null);
        when(progresoRepository.findByClaseId(1L)).thenReturn(Arrays.asList(progreso, progreso2));

        List<Progreso> result = progresoService.listarProgresosPorClase(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(progresoRepository, times(1)).findByClaseId(1L);
    }

    @Test
    void calcularPorcentajeDeCompletitud_Success() {
        progreso.setPorcentajeAvance(75.0);
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        Double result = progresoService.calcularPorcentajeDeCompletitud(1L);

        assertNotNull(result);
        assertEquals(75.0, result);
        verify(progresoRepository, times(1)).findById(1L);
    }

    @Test
    void calcularPorcentajeDeCompletitud_NotFound_ThrowsException() {
        when(progresoRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            progresoService.calcularPorcentajeDeCompletitud(2L);
        });
        assertEquals("Progreso no encontrado con ID: 2", thrown.getMessage());
        verify(progresoRepository, times(1)).findById(2L);
    }
}