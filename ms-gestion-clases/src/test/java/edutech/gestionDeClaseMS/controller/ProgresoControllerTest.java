package edutech.gestionDeClaseMS.controller;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoProgreso;
import edutech.gestionDeClaseMS.model.Progreso;
import edutech.gestionDeClaseMS.service.ProgresoService;
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

@WebMvcTest(ProgresoController.class)
public class ProgresoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgresoService progresoService;

    private ObjectMapper objectMapper;
    private Clase clase;
    private Progreso progreso;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        progreso = new Progreso(1L, 10L, clase, EstadoProgreso.INICIADO, 0.0, null);
    }

    @Test
    void crearOActualizarProgreso_NewProgreso_Success() throws Exception {
        Progreso newProgreso = new Progreso(1L, 10L, clase, EstadoProgreso.EN_PROGRESO, 25.0, null);
        when(progresoService.crearOActualizarProgreso(eq(10L), eq(1L), eq(25.0), isNull())).thenReturn(newProgreso);

        mockMvc.perform(post("/api/progresos")
                .param("idUsuario", "10")
                .param("idClase", "1")
                .param("porcentajeAvance", "25.0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(10L))
                .andExpect(jsonPath("$.porcentajeAvance").value(25.0));

        verify(progresoService, times(1)).crearOActualizarProgreso(eq(10L), eq(1L), eq(25.0), isNull());
    }

    @Test
    void crearOActualizarProgreso_InvalidInput_ReturnsBadRequest() throws Exception {
        when(progresoService.crearOActualizarProgreso(eq(10L), eq(1L), eq(101.0), isNull())).thenThrow(new IllegalArgumentException("El porcentaje de avance debe estar entre 0 y 100."));

        mockMvc.perform(post("/api/progresos")
                .param("idUsuario", "10")
                .param("idClase", "1")
                .param("porcentajeAvance", "101.0"))
                .andExpect(status().isBadRequest());

        verify(progresoService, times(1)).crearOActualizarProgreso(eq(10L), eq(1L), eq(101.0), isNull());
    }

    @Test
    void actualizarProgreso_Success() throws Exception {
        Progreso updatedProgreso = new Progreso(1L, 10L, clase, EstadoProgreso.EN_PROGRESO, 75.0, 8.5);
        when(progresoService.actualizarProgreso(eq(1L), eq(75.0), eq(8.5))).thenReturn(updatedProgreso);

        mockMvc.perform(put("/api/progresos/{id}", 1L)
                .param("porcentajeAvance", "75.0")
                .param("notaPromedio", "8.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeAvance").value(75.0))
                .andExpect(jsonPath("$.notaPromedio").value(8.5));

        verify(progresoService, times(1)).actualizarProgreso(eq(1L), eq(75.0), eq(8.5));
    }

    @Test
    void actualizarProgreso_NotFound() throws Exception {
        when(progresoService.actualizarProgreso(eq(2L), eq(50.0), isNull())).thenThrow(new RuntimeException("Progreso no encontrado con ID: 2"));

        mockMvc.perform(put("/api/progresos/{id}", 2L)
                .param("porcentajeAvance", "50.0"))
                .andExpect(status().isNotFound());

        verify(progresoService, times(1)).actualizarProgreso(eq(2L), eq(50.0), isNull());
    }

    @Test
    void obtenerProgresoPorId_Found() throws Exception {
        when(progresoService.obtenerProgresoPorId(1L)).thenReturn(Optional.of(progreso));

        mockMvc.perform(get("/api/progresos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(10L));

        verify(progresoService, times(1)).obtenerProgresoPorId(1L);
    }

    @Test
    void obtenerProgresoPorId_NotFound() throws Exception {
        when(progresoService.obtenerProgresoPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/progresos/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(progresoService, times(1)).obtenerProgresoPorId(2L);
    }

    @Test
    void obtenerProgresoPorUsuarioYClase_Found() throws Exception {
        when(progresoService.obtenerProgresoPorUsuarioYClase(10L, 1L)).thenReturn(Optional.of(progreso));

        mockMvc.perform(get("/api/progresos/usuario/{idUsuario}/clase/{idClase}", 10L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(10L))
                .andExpect(jsonPath("$.clase.id").value(1L));

        verify(progresoService, times(1)).obtenerProgresoPorUsuarioYClase(10L, 1L);
    }

    @Test
    void obtenerProgresoPorUsuarioYClase_NotFound() throws Exception {
        when(progresoService.obtenerProgresoPorUsuarioYClase(10L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/progresos/usuario/{idUsuario}/clase/{idClase}", 10L, 2L))
                .andExpect(status().isNotFound());

        verify(progresoService, times(1)).obtenerProgresoPorUsuarioYClase(10L, 2L);
    }

    @Test
    void listarProgresosPorEstudiante_Success() throws Exception {
        Progreso progreso2 = new Progreso(2L, 10L, clase, EstadoProgreso.COMPLETADO, 100.0, 9.5);
        when(progresoService.listarProgresosPorEstudiante(10L)).thenReturn(Arrays.asList(progreso, progreso2));

        mockMvc.perform(get("/api/progresos/por-estudiante/{idUsuario}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idUsuario").value(10L));

        verify(progresoService, times(1)).listarProgresosPorEstudiante(10L);
    }

    @Test
    void listarProgresosPorClase_Success() throws Exception {
        Progreso progreso2 = new Progreso(2L, 11L, clase, EstadoProgreso.EN_PROGRESO, 60.0, null);
        when(progresoService.listarProgresosPorClase(1L)).thenReturn(Arrays.asList(progreso, progreso2));

        mockMvc.perform(get("/api/progresos/por-clase/{idClase}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].clase.id").value(1L));

        verify(progresoService, times(1)).listarProgresosPorClase(1L);
    }

    @Test
    void calcularPorcentajeDeCompletitud_Success() throws Exception {
        when(progresoService.calcularPorcentajeDeCompletitud(1L)).thenReturn(75.0);

        mockMvc.perform(get("/api/progresos/{id}/porcentaje-completitud", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(75.0));

        verify(progresoService, times(1)).calcularPorcentajeDeCompletitud(1L);
    }

    @Test
    void calcularPorcentajeDeCompletitud_NotFound() throws Exception {
        when(progresoService.calcularPorcentajeDeCompletitud(2L)).thenThrow(new RuntimeException("Progreso no encontrado con ID: 2"));

        mockMvc.perform(get("/api/progresos/{id}/porcentaje-completitud", 2L))
                .andExpect(status().isNotFound());

        verify(progresoService, times(1)).calcularPorcentajeDeCompletitud(2L);
    }
}