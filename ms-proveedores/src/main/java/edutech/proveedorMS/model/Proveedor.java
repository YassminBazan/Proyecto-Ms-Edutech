package edutech.proveedorMS.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set; 

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String run; 

    @Column(nullable = false)
    private String nombre;

    private String apellido; 

    @Column(unique = true)
    private String razonSocial;

    @Column(nullable = false)
    private String direccion;

    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProveedor estado; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id") 
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_id") 
    private Comuna comuna;

    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "proveedor_categoria",
        joinColumns = @JoinColumn(name = "proveedor_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();
}
