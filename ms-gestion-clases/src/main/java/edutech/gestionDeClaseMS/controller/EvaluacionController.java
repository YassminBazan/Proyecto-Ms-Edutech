package edutech.gestionDeClaseMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.gestionDeClaseMS.model.Evaluacion;
import edutech.gestionDeClaseMS.service.EvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @Autowired
    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping("/clase/{claseId}")
    public ResponseEntity<Evaluacion> crearEvaluacion(@PathVariable Long claseId, @RequestBody Evaluacion evaluacion) {
        try {
            Evaluacion nuevaEvaluacion = evaluacionService.crearEvaluacion(evaluacion, claseId);
            return new ResponseEntity<>(nuevaEvaluacion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluacion> obtenerEvaluacionPorId(@PathVariable Long id) {
        return evaluacionService.obtenerEvaluacionPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Evaluacion>> listarTodasLasEvaluaciones() {
        List<Evaluacion> evaluaciones = evaluacionService.listarTodasLasEvaluaciones();
        return ResponseEntity.ok(evaluaciones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evaluacion> actualizarEvaluacion(@PathVariable Long id, @RequestBody Evaluacion evaluacion) {
        try {
            Evaluacion evaluacionActualizada = evaluacionService.actualizarEvaluacion(id, evaluacion);
            return ResponseEntity.ok(evaluacionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvaluacion(@PathVariable Long id) {
        try {
            evaluacionService.eliminarEvaluacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/por-clase/{claseId}")
    public ResponseEntity<List<Evaluacion>> listarEvaluacionesPorClase(@PathVariable Long claseId) {
        List<Evaluacion> evaluaciones = evaluacionService.listarEvaluacionesPorClase(claseId);
        return ResponseEntity.ok(evaluaciones);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Evaluacion>> listarEvaluacionesActivas() {
        List<Evaluacion> evaluaciones = evaluacionService.listarEvaluacionesActivas();
        return ResponseEntity.ok(evaluaciones);
    }

    @PatchMapping("/{id}/corregir")
    public ResponseEntity<Evaluacion> marcarComoCorregida(@PathVariable Long id) {
        try {
            Evaluacion evaluacion = evaluacionService.marcarComoCorregida(id);
            return ResponseEntity.ok(evaluacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
