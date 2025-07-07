package edutech.gestionDeClaseMS.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClaseDTO {
    private Long id;
    private Long idCurso;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaTermino;
}
