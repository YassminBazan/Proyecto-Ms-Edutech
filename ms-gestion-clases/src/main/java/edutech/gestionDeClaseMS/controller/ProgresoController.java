package edutech.gestionDeClaseMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.gestionDeClaseMS.model.Progreso;
import edutech.gestionDeClaseMS.service.ProgresoService;

import java.util.List;

@RestController
@RequestMapping("/api/progresos")
public class ProgresoController {

    private final ProgresoService progresoService;

    @Autowired
    public ProgresoController(ProgresoService progresoService) {
        this.progresoService = progresoService;
    }

    @PostMapping
    public ResponseEntity<Progreso> crearOActualizarProgreso(
            @RequestParam Long idUsuario,
            @RequestParam Long idClase,
            @RequestParam(required = false) Double porcentajeAvance,
            @RequestParam(required = false) Double notaPromedio) {
        try {
            Progreso progreso = progresoService.crearOActualizarProgreso(idUsuario, idClase, porcentajeAvance, notaPromedio);
            return new ResponseEntity<>(progreso, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}") 
    public ResponseEntity<Progreso> actualizarProgreso(
            @PathVariable Long id,
            @RequestParam(required = false) Double porcentajeAvance,
            @RequestParam(required = false) Double notaPromedio) {
        try {
            Progreso progresoActualizado = progresoService.actualizarProgreso(id, porcentajeAvance, notaPromedio);
            return ResponseEntity.ok(progresoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Progreso> obtenerProgresoPorId(@PathVariable Long id) {
        return progresoService.obtenerProgresoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}/clase/{idClase}")
    public ResponseEntity<Progreso> obtenerProgresoPorUsuarioYClase(@PathVariable Long idUsuario, @PathVariable Long idClase) {
        return progresoService.obtenerProgresoPorUsuarioYClase(idUsuario, idClase)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/por-estudiante/{idUsuario}")
    public ResponseEntity<List<Progreso>> listarProgresosPorEstudiante(@PathVariable Long idUsuario) {
        List<Progreso> progresos = progresoService.listarProgresosPorEstudiante(idUsuario);
        return ResponseEntity.ok(progresos);
    }

    @GetMapping("/por-clase/{idClase}")
    public ResponseEntity<List<Progreso>> listarProgresosPorClase(@PathVariable Long idClase) {
        List<Progreso> progresos = progresoService.listarProgresosPorClase(idClase);
        return ResponseEntity.ok(progresos);
    }

    @GetMapping("/{id}/porcentaje-completitud")
    public ResponseEntity<Double> calcularPorcentajeDeCompletitud(@PathVariable Long id) {
        try {
            Double porcentaje = progresoService.calcularPorcentajeDeCompletitud(id);
            return ResponseEntity.ok(porcentaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}