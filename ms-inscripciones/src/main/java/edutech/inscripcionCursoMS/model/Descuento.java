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
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoCupon;

    @Column(nullable = false)
    private Double porcentajeDescuento; 

    @Column(nullable = false)
    private LocalDate fechaInicioValidez;

    @Column(nullable = false)
    private LocalDate fechaFinValidez;

    @Column(nullable = false)
    private boolean activo; 
}