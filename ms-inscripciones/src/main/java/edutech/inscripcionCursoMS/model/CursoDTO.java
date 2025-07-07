package edutech.inscripcionCursoMS.model;

import lombok.Data;

@Data
public class CursoDTO {
    private Long idCurso;
    private String nombreCurso;
    private Double valorCurso;
    private String descripcion;

}
