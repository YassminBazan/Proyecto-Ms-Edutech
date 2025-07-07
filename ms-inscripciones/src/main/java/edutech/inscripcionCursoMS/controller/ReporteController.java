package edutech.inscripcionCursoMS.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.inscripcionCursoMS.model.Reporte;
import edutech.inscripcionCursoMS.service.ReporteService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/generar/inscripciones-por-fecha")
    public ResponseEntity<Reporte> generarReporteInscripcionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Reporte nuevoReporte = reporteService.generarReporteInscripcionesPorFecha(fechaInicio, fechaFin);
            return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReportePorId(@PathVariable Long id) {
        return reporteService.obtenerReportePorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listarTodosLosReportes() {
        List<Reporte> reportes = reporteService.listarTodosLosReportes();
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/filtrar-por-fecha")
    public ResponseEntity<List<Reporte>> filtrarReportesPorFechaGeneracion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<Reporte> reportes = reporteService.filtrarReportesPorFechaGeneracion(fechaInicio, fechaFin);
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/filtrar-por-tipo")
    public ResponseEntity<List<Reporte>> filtrarReportesPorTipo(@RequestParam String tipo) {
        List<Reporte> reportes = reporteService.filtrarReportesPorTipo(tipo);
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<String> exportarReporte(@PathVariable Long id) {
        try {
            String contenido = reporteService.exportarReporte(id);
            
            return ResponseEntity.ok()
                    .header("Contenido", "adjutno; nombreArchivo=\"reporte_" + id + ".txt\"")
                    .body(contenido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
