package edutech.inscripcionCursoMS.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idCliente; 

    @Column(nullable = false)
    private Long idCurso;

    @Column(nullable = false)
    private LocalDate fechaInscripcion;

    @Column(nullable = false)
    private Double precioFinal; 

    @Column(nullable = false)
    private Double descuentoAplicado; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado; 

  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descuento_id")
    private Descuento descuentoUtilizado;

    
}