package edutech.gestionDeClaseMS.controller;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.service.ClaseService;
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

@WebMvcTest(ClaseController.class)
public class ClaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaseService claseService;

    private ObjectMapper objectMapper;
    private Clase clase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 

        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
    }

    @Test
    void crearClase_Success() throws Exception {
        when(claseService.crearClase(any(Clase.class))).thenReturn(clase);

        mockMvc.perform(post("/api/clases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clase)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Programación Java"));

        verify(claseService, times(1)).crearClase(any(Clase.class));
    }

    @Test
    void crearClase_InvalidInput_ReturnsBadRequest() throws Exception {
        Clase invalidClase = new Clase(null, "", "Descripción", LocalDateTime.now(), LocalDateTime.now(), 101L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(claseService.crearClase(any(Clase.class))).thenThrow(new IllegalArgumentException("El nombre de la clase no puede estar vacío."));

        mockMvc.perform(post("/api/clases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidClase)))
                .andExpect(status().isBadRequest());

        verify(claseService, times(1)).crearClase(any(Clase.class));
    }

    @Test
    void obtenerClasePorId_Found() throws Exception {
        when(claseService.obtenerClasePorId(1L)).thenReturn(Optional.of(clase));

        mockMvc.perform(get("/api/clases/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Programación Java"));

        verify(claseService, times(1)).obtenerClasePorId(1L);
    }

    @Test
    void obtenerClasePorId_NotFound() throws Exception {
        when(claseService.obtenerClasePorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clases/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(claseService, times(1)).obtenerClasePorId(2L);
    }

    @Test
    void listarTodasLasClases_Success() throws Exception {
        when(claseService.listarTodasLasClases()).thenReturn(Arrays.asList(clase, new Clase(2L, "Python Básico", "Intro", LocalDateTime.now(),
         LocalDateTime.now(), 102L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>())));

        mockMvc.perform(get("/api/clases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Programación Java"));

        verify(claseService, times(1)).listarTodasLasClases();
    }

    @Test
    void actualizarClase_Success() throws Exception {
        Clase claseActualizada = new Clase(1L, "Java Avanzado", "Curso de Java para expertos", LocalDateTime.now(), LocalDateTime.now().plusDays(60), 101L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(claseService.actualizarClase(eq(1L), any(Clase.class))).thenReturn(claseActualizada);

        mockMvc.perform(put("/api/clases/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claseActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Java Avanzado"));

        verify(claseService, times(1)).actualizarClase(eq(1L), any(Clase.class));
    }

    @Test
    void actualizarClase_NotFound() throws Exception {
        Clase claseActualizada = new Clase(2L, "Java Avanzado", "Curso de Java para expertos", LocalDateTime.now(), LocalDateTime.now().plusDays(60), 101L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(claseService.actualizarClase(eq(2L), any(Clase.class))).thenThrow(new RuntimeException("Clase no encontrada con ID: 2"));

        mockMvc.perform(put("/api/clases/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claseActualizada)))
                .andExpect(status().isNotFound());

        verify(claseService, times(1)).actualizarClase(eq(2L), any(Clase.class));
    }

    @Test
    void eliminarClase_Success() throws Exception {
        doNothing().when(claseService).eliminarClase(1L);

        mockMvc.perform(delete("/api/clases/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(claseService, times(1)).eliminarClase(1L);
    }

    @Test
    void eliminarClase_NotFound() throws Exception {
        doThrow(new RuntimeException("Clase no encontrada con ID: 2")).when(claseService).eliminarClase(2L);

        mockMvc.perform(delete("/api/clases/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(claseService, times(1)).eliminarClase(2L);
    }

    @Test
    void listarClasesPorCurso_Success() throws Exception {
        when(claseService.listarClasesPorCurso(101L)).thenReturn(Arrays.asList(clase));

        mockMvc.perform(get("/api/clases/por-curso/{idCurso}", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idCurso").value(101L));

        verify(claseService, times(1)).listarClasesPorCurso(101L);
    }
}