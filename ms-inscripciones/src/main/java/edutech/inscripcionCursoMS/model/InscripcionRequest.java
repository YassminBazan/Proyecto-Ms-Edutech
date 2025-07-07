package edutech.inscripcionCursoMS.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequest {
    private Long idCliente;
    private Long idCurso;
    private String codigoCupon; // Puede ser null o vacío

}
