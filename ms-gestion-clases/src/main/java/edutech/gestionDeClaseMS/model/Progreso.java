package edutech.gestionDeClaseMS.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idUsuario", "clase_id"})) 
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false)
    private Long idUsuario; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProgreso estado; 

    @Column(nullable = false)
    private Double porcentajeAvance; 

    private Double notaPromedio; 
}
