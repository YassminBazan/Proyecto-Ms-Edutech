package edutech.incidenciaMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.incidenciaMS.model.EstadoIncidencia;
import edutech.incidenciaMS.model.Incidencia;
import edutech.incidenciaMS.service.IncidenciaService;

import java.util.List;
import java.util.Optional;

@RestController 
@RequestMapping("/api/incidencias") 
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @Autowired
    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // Crear una nueva incidencia
    @PostMapping
    public ResponseEntity<Incidencia> crearIncidencia(@RequestBody Incidencia incidencia) {
        Incidencia nuevaIncidencia = incidenciaService.crearIncidencia(incidencia);
        return new ResponseEntity<>(nuevaIncidencia, HttpStatus.CREATED);
    }

    // Obtener una incidencia por ID
    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> obtenerIncidenciaPorId(@PathVariable Long id) {
        Optional<Incidencia> incidencia = incidenciaService.obtenerIncidenciaPorId(id);
        return incidencia.map(ResponseEntity::ok) // si hay, retornaraa 200 ok
                .orElseGet(() -> ResponseEntity.notFound().build()); // o retornara 404
    }

    // Listar todas las incidencias
    @GetMapping
    public ResponseEntity<List<Incidencia>> listarTodasIncidencias() {
        List<Incidencia> incidencias = incidenciaService.listarTodasIncidencias();
        return ResponseEntity.ok(incidencias);
    }

    // Actualizar una incidencia
    @PutMapping("/{id}")
    public ResponseEntity<Incidencia> actualizarIncidencia(@PathVariable Long id, @RequestBody Incidencia incidencia) {
        try {
            Incidencia incidenciaActualizada = incidenciaService.actualizarIncidencia(id, incidencia);
            return ResponseEntity.ok(incidenciaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }
    }

    // Eliminar una incidencia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncidencia(@PathVariable Long id) {
        try {
            incidenciaService.eliminarIncidencia(id);
            return ResponseEntity.noContent().build(); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    

    @PatchMapping("/{incidenciaId}/asignar-tecnico/{tecnicoId}")
    public ResponseEntity<Incidencia> asignarTecnico(@PathVariable Long incidenciaId, @PathVariable Long tecnicoId) {
        try {
            Incidencia incidencia = incidenciaService.asignarTecnico(incidenciaId, tecnicoId);
            return ResponseEntity.ok(incidencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Incidencia> resolverIncidencia(@PathVariable Long id) {
        try {
            Incidencia incidencia = incidenciaService.resolverIncidencia(id);
            return ResponseEntity.ok(incidencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Incidencia> cerrarIncidencia(@PathVariable Long id) {
        try {
            Incidencia incidencia = incidenciaService.cerrarIncidencia(id);
            return ResponseEntity.ok(incidencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<Incidencia> reabrirIncidencia(@PathVariable Long id) {
        try {
            Incidencia incidencia = incidenciaService.reabrirIncidencia(id);
            return ResponseEntity.ok(incidencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/tiempo-transcurrido")
    public ResponseEntity<Long> calcularTiempoTranscurrido(@PathVariable Long id) {
        try {
            long horas = incidenciaService.calcularTiempoDesdeCreacionHoras(id);
            return ResponseEntity.ok(horas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // filtrar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Incidencia>> listarIncidenciasPorEstado(@PathVariable EstadoIncidencia estado) {
        List<Incidencia> incidencias = incidenciaService.listarIncidenciasPorEstado(estado);
        return ResponseEntity.ok(incidencias);
    }
}