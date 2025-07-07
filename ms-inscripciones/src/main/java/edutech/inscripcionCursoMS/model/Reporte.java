package edutech.inscripcionCursoMS.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(columnDefinition = "TEXT")
    private String contenidoReporte; 

    @Column(nullable = false)
    private String tipoReporte; 

    
}