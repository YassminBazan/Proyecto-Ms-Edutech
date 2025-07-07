package edutech.gestionDeClaseMS.controller;

import edutech.gestionDeClaseMS.model.Clase;
import edutech.gestionDeClaseMS.model.EstadoMaterial;
import edutech.gestionDeClaseMS.model.Material;
import edutech.gestionDeClaseMS.model.TipoMaterial;
import edutech.gestionDeClaseMS.service.MaterialService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaterialController.class)
public class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialService materialService;

    private ObjectMapper objectMapper;
    private Clase clase;
    private Material material;

    @BeforeEach
    void setUp() {
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

       
        clase = new Clase(1L, "Programación Java", "Curso completo de Java",
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), 101L,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
     
        material = new Material(1L, "Introducción a Java", "Conceptos básicos de Java",
                TipoMaterial.PDF, LocalDateTime.now(), EstadoMaterial.PENDIENTE, clase);
    }

    @Test
    void crearMaterial_Success() throws Exception {

        when(materialService.crearMaterial(any(Material.class), eq(1L))).thenReturn(material);

        mockMvc.perform(post("/api/materiales/clase/{claseId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.titulo").value("Introducción a Java")) 
                .andExpect(jsonPath("$.tipoMaterial").value("PDF"));

      
        verify(materialService, times(1)).crearMaterial(any(Material.class), eq(1L));
    }

    @Test
    void crearMaterial_ClaseNotFound_ReturnsBadRequest() throws Exception {
   
        when(materialService.crearMaterial(any(Material.class), eq(99L)))
                .thenThrow(new RuntimeException("Clase no encontrada con ID: 99"));

  
        mockMvc.perform(post("/api/materiales/clase/{claseId}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isBadRequest());

     
        verify(materialService, times(1)).crearMaterial(any(Material.class), eq(99L));
    }

    @Test
    void obtenerMaterialPorId_Found() throws Exception {
       
        when(materialService.obtenerMaterialPorId(1L)).thenReturn(Optional.of(material));

       
        mockMvc.perform(get("/api/materiales/{id}", 1L))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Introducción a Java"));

     
        verify(materialService, times(1)).obtenerMaterialPorId(1L);
    }

    @Test
    void obtenerMaterialPorId_NotFound() throws Exception {
    
        when(materialService.obtenerMaterialPorId(99L)).thenReturn(Optional.empty());

       
        mockMvc.perform(get("/api/materiales/{id}", 99L))
                .andExpect(status().isNotFound());

       
        verify(materialService, times(1)).obtenerMaterialPorId(99L);
    }

    @Test
    void listarTodosLosMateriales_Success() throws Exception {
      
        List<Material> materials = Arrays.asList(
                material,
                new Material(2L, "Matrices en Java", "Ejercicios prácticos",
                        TipoMaterial.LECTURA, LocalDateTime.now(), EstadoMaterial.APROBADO, clase)
        );
        when(materialService.listarTodosLosMateriales()).thenReturn(materials);

        mockMvc.perform(get("/api/materiales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) 
                .andExpect(jsonPath("$[0].titulo").value("Introducción a Java"))
                .andExpect(jsonPath("$[1].titulo").value("Matrices en Java"));


        verify(materialService, times(1)).listarTodosLosMateriales();
    }

    @Test
    void actualizarMaterial_Success() throws Exception {
      
        Material updatedMaterial = new Material(1L, "Java Intermedio", "Temas avanzados",
                TipoMaterial.VIDEO, LocalDateTime.now(), EstadoMaterial.APROBADO, clase);
        when(materialService.actualizarMaterial(eq(1L), any(Material.class))).thenReturn(updatedMaterial);

      
        mockMvc.perform(put("/api/materiales/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMaterial)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.titulo").value("Java Intermedio"))
                .andExpect(jsonPath("$.estadoMaterial").value("APROBADO"));

        verify(materialService, times(1)).actualizarMaterial(eq(1L), any(Material.class));
    }

    @Test
    void actualizarMaterial_NotFound() throws Exception {
     
        when(materialService.actualizarMaterial(eq(99L), any(Material.class)))
                .thenThrow(new RuntimeException("Material no encontrado con ID: 99"));

        mockMvc.perform(put("/api/materiales/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isNotFound());

        verify(materialService, times(1)).actualizarMaterial(eq(99L), any(Material.class));
    }

    @Test
    void eliminarMaterial_Success() throws Exception {
   
        doNothing().when(materialService).eliminarMaterial(1L);

       
        mockMvc.perform(delete("/api/materiales/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(materialService, times(1)).eliminarMaterial(1L);
    }

    @Test
    void eliminarMaterial_NotFound() throws Exception {
       
        doThrow(new RuntimeException("Material no encontrado con ID: 99"))
                .when(materialService).eliminarMaterial(99L);

      
        mockMvc.perform(delete("/api/materiales/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(materialService, times(1)).eliminarMaterial(99L);
    }

    @Test
    void listarMaterialesPorClase_Success() throws Exception {
       
        when(materialService.listarMaterialesPorClase(1L)).thenReturn(Arrays.asList(material));

      
        mockMvc.perform(get("/api/materiales/por-clase/{claseId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clase.id").value(1L));

   
        verify(materialService, times(1)).listarMaterialesPorClase(1L);
    }

    @Test
    void listarMaterialesPorTipo_Success() throws Exception {
      
        when(materialService.listarMaterialesPorTipo(TipoMaterial.PDF)).thenReturn(Arrays.asList(material));

       
        mockMvc.perform(get("/api/materiales/por-tipo/{tipoMaterial}", TipoMaterial.PDF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipoMaterial").value("PDF"));

      
        verify(materialService, times(1)).listarMaterialesPorTipo(TipoMaterial.PDF);
    }

    @Test
    void aprobarMaterial_Success() throws Exception {
  
        Material approvedMaterial = new Material(1L, "Introducción a Java", "Conceptos básicos de Java",
                TipoMaterial.PDF, LocalDateTime.now(), EstadoMaterial.APROBADO, clase);
        when(materialService.aprobarMaterial(1L)).thenReturn(approvedMaterial);

        
        mockMvc.perform(patch("/api/materiales/{id}/aprobar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoMaterial").value("APROBADO"));

        verify(materialService, times(1)).aprobarMaterial(1L);
    }

    @Test
    void aprobarMaterial_NotFound() throws Exception {
 
        when(materialService.aprobarMaterial(99L))
                .thenThrow(new RuntimeException("Material no encontrado con ID: 99"));

        
        mockMvc.perform(patch("/api/materiales/{id}/aprobar", 99L))
                .andExpect(status().isNotFound());

    
        verify(materialService, times(1)).aprobarMaterial(99L);
    }

    @Test
    void rechazarMaterial_Success() throws Exception {
     
        Material rejectedMaterial = new Material(1L, "Introducción a Java", "Conceptos básicos de Java",
                TipoMaterial.PDF, LocalDateTime.now(), EstadoMaterial.RECHAZADO, clase);
        when(materialService.rechazarMaterial(1L)).thenReturn(rejectedMaterial);

    
        mockMvc.perform(patch("/api/materiales/{id}/rechazar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoMaterial").value("RECHAZADO"));

      
        verify(materialService, times(1)).rechazarMaterial(1L);
    }

    @Test
    void rechazarMaterial_NotFound() throws Exception {
     
        when(materialService.rechazarMaterial(99L))
                .thenThrow(new RuntimeException("Material no encontrado con ID: 99"));

        
        mockMvc.perform(patch("/api/materiales/{id}/rechazar", 99L))
                .andExpect(status().isNotFound());

        
        verify(materialService, times(1)).rechazarMaterial(99L);
    }

    @Test
    void listarMaterialesPorEstado_Success() throws Exception {
       
        when(materialService.listarMaterialesPorEstado(EstadoMaterial.PENDIENTE)).thenReturn(Arrays.asList(material));


        mockMvc.perform(get("/api/materiales/por-estado/{estadoMaterial}", EstadoMaterial.PENDIENTE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estadoMaterial").value("PENDIENTE"));

      
        verify(materialService, times(1)).listarMaterialesPorEstado(EstadoMaterial.PENDIENTE);
    }
}