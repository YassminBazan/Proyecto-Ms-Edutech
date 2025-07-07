package edutech.inscripcionCursoMS.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.inscripcionCursoMS.model.Inscripcion;
import edutech.inscripcionCursoMS.model.InscripcionRequest;
import edutech.inscripcionCursoMS.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import edutech.inscripcionCursoMS.model.EstadoInscripcion;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;


    // Para crear una inscripción se necesitan los IDs del cliente y del curso, y opcionalmente un cupón
    @PostMapping
    public ResponseEntity<Inscripcion> crearInscripcion(

        @RequestBody InscripcionRequest request) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdCliente((request.getIdCliente()));
        inscripcion.setIdCurso(request.getIdCurso());
        //Codifo del cupon en caso de estar incluido en el Json
        Inscripcion nuevaInscripcion = inscripcionService.crearInscripcion(inscripcion, request.getCodigoCupon());
        return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> obtenerInscripcionPorId(@PathVariable Long id) {
        return inscripcionService.obtenerInscripcionPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Inscripcion>> listarTodasLasInscripciones() {
        List<Inscripcion> inscripciones = inscripcionService.listarTodasLasInscripciones();
        return ResponseEntity.ok(inscripciones);
    }

  @PatchMapping("/{id}/estado")
    public ResponseEntity<Inscripcion> actualizarEstadoInscripcion(@PathVariable Long id, @RequestParam String nuevoEstado) {
        EstadoInscripcion estado = EstadoInscripcion.valueOf(nuevoEstado.toUpperCase()); 
           
        Inscripcion inscripcionActualizada = inscripcionService.actualizarInscripcion(id, new Inscripcion(null, null, null, null, null, null, estado, null));
            return ResponseEntity.ok(inscripcionActualizada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Inscripcion> cancelarInscripcion(@PathVariable Long id) {
        Inscripcion inscripcionCancelada = inscripcionService.cancelarInscripcion(id);
        return ResponseEntity.ok(inscripcionCancelada);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Inscripcion>> listarInscripcionesPorCliente(@PathVariable Long idCliente) {
        List<Inscripcion> inscripciones = inscripcionService.listarInscripcionesPorCliente(idCliente);
        return ResponseEntity.ok(inscripciones);
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Inscripcion>> listarInscripcionesPorCurso(@PathVariable Long idCurso) {
        List<Inscripcion> inscripciones = inscripcionService.listarInscripcionesPorCurso(idCurso);
        return ResponseEntity.ok(inscripciones);
    }
}