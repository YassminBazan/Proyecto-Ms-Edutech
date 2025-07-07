package edutech.incidenciaMS.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  genera la id automaticamente
    private Long id; 

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT") 
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private Long tiempoEstimadoResolucionHoras; 

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private EstadoIncidencia estado;

    @ManyToOne(fetch = FetchType.LAZY) // muchos incidentes pueden estar asociado a un tecnicoo
    @JoinColumn(name = "tecnico_id") // columna de clave foranea
    private Tecnico tecnicoAsignado; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadIncidencia prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaIncidencia categoria;

}