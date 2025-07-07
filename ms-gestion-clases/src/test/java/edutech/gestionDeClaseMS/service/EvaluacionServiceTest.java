package edutech.gestionDeClaseMS.service;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoEvaluacion;
import edutech.gestionDeClaseMS.model.Evaluacion;
import edutech.gestionDeClaseMS.model.TipoEvaluacion;
import edutech.gestionDeClaseMS.repository.ClaseRepository;
import edutech.gestionDeClaseMS.repository.EvaluacionRepository;
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
public class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private EvaluacionService evaluacionService;

    private Clase clase;
    private Evaluacion evaluacion;

    @BeforeEach
    void setUp() {
        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        evaluacion = new Evaluacion(1L, "Quiz 1", "Primer quiz de la unidad 1", TipoEvaluacion.QUIZ, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), EstadoEvaluacion.PENDIENTE, clase);
    }

    @Test
    void crearEvaluacion_Success() {
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        Evaluacion nuevaEvaluacion = evaluacionService.crearEvaluacion(evaluacion, 1L);

        assertNotNull(nuevaEvaluacion);
        assertEquals("Quiz 1", nuevaEvaluacion.getTitulo());
        assertEquals(EstadoEvaluacion.PENDIENTE, nuevaEvaluacion.getEstado());
        verify(claseRepository, times(1)).findById(1L);
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    @Test
    void crearEvaluacion_ClaseNotFound_ThrowsException() {
        when(claseRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            evaluacionService.crearEvaluacion(evaluacion, 2L);
        });
        assertEquals("Clase no encontrada con ID: 2", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void crearEvaluacion_FechaInicioAfterFechaTermino_ThrowsException() {
        evaluacion.setFechaInicio(LocalDateTime.now().plusDays(5));
        evaluacion.setFechaTermino(LocalDateTime.now().plusDays(1));
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            evaluacionService.crearEvaluacion(evaluacion, 1L);
        });
        assertEquals("La fecha de inicio de la evaluación no puede ser posterior a la de término.", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void actualizarEvaluacion_Success() {
        Evaluacion evaluacionActualizada = new Evaluacion(1L, "Examen Final", "Examen del curso", TipoEvaluacion.EXAMEN, LocalDateTime.now(), LocalDateTime.now().plusDays(2), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacionActualizada);

        Evaluacion result = evaluacionService.actualizarEvaluacion(1L, evaluacionActualizada);

        assertNotNull(result);
        assertEquals("Examen Final", result.getTitulo());
        assertEquals(TipoEvaluacion.EXAMEN, result.getTipoEvaluacion());
        assertEquals(EstadoEvaluacion.ACTIVA, result.getEstado());
        verify(evaluacionRepository, times(1)).findById(1L);
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    @Test
    void actualizarEvaluacion_NotFound_ThrowsException() {
        Evaluacion evaluacionActualizada = new Evaluacion(1L, "Examen Final", "Examen del curso", TipoEvaluacion.EXAMEN, LocalDateTime.now(), LocalDateTime.now().plusDays(2), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            evaluacionService.actualizarEvaluacion(2L, evaluacionActualizada);
        });
        assertEquals("Evaluación no encontrada con ID: 2", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void actualizarEvaluacion_FechaInicioAfterFechaTermino_ThrowsException() {
        evaluacion.setFechaInicio(LocalDateTime.now().plusDays(5));
        evaluacion.setFechaTermino(LocalDateTime.now().plusDays(1));
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            evaluacionService.actualizarEvaluacion(1L, evaluacion);
        });
        assertEquals("La fecha de inicio de la evaluación no puede ser posterior a la de término.", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void eliminarEvaluacion_Success() {
        when(evaluacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(evaluacionRepository).deleteById(1L);

        evaluacionService.eliminarEvaluacion(1L);

        verify(evaluacionRepository, times(1)).existsById(1L);
        verify(evaluacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarEvaluacion_NotFound_ThrowsException() {
        when(evaluacionRepository.existsById(2L)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            evaluacionService.eliminarEvaluacion(2L);
        });
        assertEquals("Evaluación no encontrada con ID: 2", thrown.getMessage());
        verify(evaluacionRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerEvaluacionPorId_Found() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        Optional<Evaluacion> result = evaluacionService.obtenerEvaluacionPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(evaluacion, result.get());
        verify(evaluacionRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerEvaluacionPorId_NotFound() {
        when(evaluacionRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Evaluacion> result = evaluacionService.obtenerEvaluacionPorId(2L);

        assertFalse(result.isPresent());
        verify(evaluacionRepository, times(1)).findById(2L);
    }

    @Test
    void listarTodasLasEvaluaciones_Success() {
        List<Evaluacion> evaluaciones = Arrays.asList(evaluacion, new Evaluacion(2L, "Tarea 1", "Tarea de la unidad 2", TipoEvaluacion.TAREA, LocalDateTime.now(), LocalDateTime.now().plusDays(5), EstadoEvaluacion.PENDIENTE, clase));
        when(evaluacionRepository.findAll()).thenReturn(evaluaciones);

        List<Evaluacion> result = evaluacionService.listarTodasLasEvaluaciones();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(evaluacionRepository, times(1)).findAll();
    }

    @Test
    void listarEvaluacionesPorClase_Success() {
        List<Evaluacion> evaluacionesPorClase = Arrays.asList(evaluacion);
        when(evaluacionRepository.findByClaseId(1L)).thenReturn(evaluacionesPorClase);

        List<Evaluacion> result = evaluacionService.listarEvaluacionesPorClase(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getClase().getId());
        verify(evaluacionRepository, times(1)).findByClaseId(1L);
    }

    @Test
    void listarEvaluacionesActivas_Success() {
        Evaluacion activa = new Evaluacion(3L, "Quiz Activo", "Activo", TipoEvaluacion.QUIZ, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionRepository.findByFechaInicioBetweenAndFechaTerminoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(activa));

        List<Evaluacion> result = evaluacionService.listarEvaluacionesActivas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Quiz Activo", result.get(0).getTitulo());
        verify(evaluacionRepository, times(1)).findByFechaInicioBetweenAndFechaTerminoBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void marcarComoCorregida_Success() {
        evaluacion.setEstado(EstadoEvaluacion.CERRADA);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        Evaluacion result = evaluacionService.marcarComoCorregida(1L);

        assertNotNull(result);
        assertEquals(EstadoEvaluacion.CORREGIDA, result.getEstado());
        verify(evaluacionRepository, times(1)).findById(1L);
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    @Test
    void marcarComoCorregida_NotFound_ThrowsException() {
        when(evaluacionRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            evaluacionService.marcarComoCorregida(2L);
        });
        assertEquals("Evaluación no encontrada.", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void marcarComoCorregida_NotCerrada_ThrowsException() {
        evaluacion.setEstado(EstadoEvaluacion.PENDIENTE); 
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            evaluacionService.marcarComoCorregida(1L);
        });
        assertEquals("La evaluación debe estar CERRADA para ser corregida.", thrown.getMessage());
        verify(evaluacionRepository, never()).save(any(Evaluacion.class));
    }
}