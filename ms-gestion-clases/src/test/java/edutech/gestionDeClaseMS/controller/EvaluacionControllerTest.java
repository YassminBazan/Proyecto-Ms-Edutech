package edutech.gestionDeClaseMS.controller;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoEvaluacion;
import edutech.gestionDeClaseMS.model.Evaluacion;
import edutech.gestionDeClaseMS.model.TipoEvaluacion;
import edutech.gestionDeClaseMS.service.EvaluacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluacionController.class)
public class EvaluacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluacionService evaluacionService;

    private ObjectMapper objectMapper;
    private Clase clase;
    private Evaluacion evaluacion;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        evaluacion = new Evaluacion(1L, "Quiz 1", "Primer quiz de la unidad 1", TipoEvaluacion.QUIZ, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), EstadoEvaluacion.PENDIENTE, clase);
    }

    @Test
    void crearEvaluacion_Success() throws Exception {
        when(evaluacionService.crearEvaluacion(any(Evaluacion.class), eq(1L))).thenReturn(evaluacion);

        mockMvc.perform(post("/api/evaluaciones/clase/{claseId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evaluacion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Quiz 1"));

        verify(evaluacionService, times(1)).crearEvaluacion(any(Evaluacion.class), eq(1L));
    }

    @Test
    void crearEvaluacion_ClaseNotFound_ReturnsBadRequest() throws Exception {
        when(evaluacionService.crearEvaluacion(any(Evaluacion.class), eq(2L))).thenThrow(new RuntimeException("Clase no encontrada con ID: 2"));

        mockMvc.perform(post("/api/evaluaciones/clase/{claseId}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evaluacion)))
                .andExpect(status().isBadRequest());

        verify(evaluacionService, times(1)).crearEvaluacion(any(Evaluacion.class), eq(2L));
    }

    @Test
    void obtenerEvaluacionPorId_Found() throws Exception {
        when(evaluacionService.obtenerEvaluacionPorId(1L)).thenReturn(Optional.of(evaluacion));

        mockMvc.perform(get("/api/evaluaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Quiz 1"));

        verify(evaluacionService, times(1)).obtenerEvaluacionPorId(1L);
    }

    @Test
    void obtenerEvaluacionPorId_NotFound() throws Exception {
        when(evaluacionService.obtenerEvaluacionPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/evaluaciones/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(evaluacionService, times(1)).obtenerEvaluacionPorId(2L);
    }

    @Test
    void listarTodasLasEvaluaciones_Success() throws Exception {
        when(evaluacionService.listarTodasLasEvaluaciones()).thenReturn(Arrays.asList(evaluacion, new Evaluacion(2L, "Tarea 1", "Descripción", TipoEvaluacion.TAREA, LocalDateTime.now(), LocalDateTime.now(), EstadoEvaluacion.PENDIENTE, clase)));

        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Quiz 1"));

        verify(evaluacionService, times(1)).listarTodasLasEvaluaciones();
    }

    @Test
    void actualizarEvaluacion_Success() throws Exception {
        Evaluacion updatedEvaluacion = new Evaluacion(1L, "Examen Final", "Examen del curso", TipoEvaluacion.EXAMEN, LocalDateTime.now(), LocalDateTime.now().plusDays(2), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionService.actualizarEvaluacion(eq(1L), any(Evaluacion.class))).thenReturn(updatedEvaluacion);

        mockMvc.perform(put("/api/evaluaciones/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEvaluacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Examen Final"));

        verify(evaluacionService, times(1)).actualizarEvaluacion(eq(1L), any(Evaluacion.class));
    }

    @Test
    void actualizarEvaluacion_NotFound() throws Exception {
        Evaluacion updatedEvaluacion = new Evaluacion(2L, "Examen Final", "Examen del curso", TipoEvaluacion.EXAMEN, LocalDateTime.now(), LocalDateTime.now().plusDays(2), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionService.actualizarEvaluacion(eq(2L), any(Evaluacion.class))).thenThrow(new RuntimeException("Evaluación no encontrada con ID: 2"));

        mockMvc.perform(put("/api/evaluaciones/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEvaluacion)))
                .andExpect(status().isNotFound());

        verify(evaluacionService, times(1)).actualizarEvaluacion(eq(2L), any(Evaluacion.class));
    }

    @Test
    void eliminarEvaluacion_Success() throws Exception {
        doNothing().when(evaluacionService).eliminarEvaluacion(1L);

        mockMvc.perform(delete("/api/evaluaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(evaluacionService, times(1)).eliminarEvaluacion(1L);
    }

    @Test
    void eliminarEvaluacion_NotFound() throws Exception {
        doThrow(new RuntimeException("Evaluación no encontrada con ID: 2")).when(evaluacionService).eliminarEvaluacion(2L);

        mockMvc.perform(delete("/api/evaluaciones/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(evaluacionService, times(1)).eliminarEvaluacion(2L);
    }

    @Test
    void listarEvaluacionesPorClase_Success() throws Exception {
        when(evaluacionService.listarEvaluacionesPorClase(1L)).thenReturn(Arrays.asList(evaluacion));

        mockMvc.perform(get("/api/evaluaciones/por-clase/{claseId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clase.id").value(1L));

        verify(evaluacionService, times(1)).listarEvaluacionesPorClase(1L);
    }

    @Test
    void listarEvaluacionesActivas_Success() throws Exception {
        Evaluacion activeEvaluacion = new Evaluacion(3L, "Quiz Activo", "Activo", TipoEvaluacion.QUIZ, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), EstadoEvaluacion.ACTIVA, clase);
        when(evaluacionService.listarEvaluacionesActivas()).thenReturn(Arrays.asList(activeEvaluacion));

        mockMvc.perform(get("/api/evaluaciones/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Quiz Activo"));

        verify(evaluacionService, times(1)).listarEvaluacionesActivas();
    }

    @Test
    void marcarComoCorregida_Success() throws Exception {
        evaluacion.setEstado(EstadoEvaluacion.CERRADA);
        when(evaluacionService.marcarComoCorregida(1L)).thenReturn(evaluacion);

        mockMvc.perform(patch("/api/evaluaciones/{id}/corregir", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CORREGIDA"));

        verify(evaluacionService, times(1)).marcarComoCorregida(1L);
    }

    @Test
    void marcarComoCorregida_BadRequest() throws Exception {
        when(evaluacionService.marcarComoCorregida(1L)).thenThrow(new RuntimeException("La evaluación debe estar CERRADA para ser corregida."));

        mockMvc.perform(patch("/api/evaluaciones/{id}/corregir", 1L))
                .andExpect(status().isBadRequest());

        verify(evaluacionService, times(1)).marcarComoCorregida(1L);
    }
}