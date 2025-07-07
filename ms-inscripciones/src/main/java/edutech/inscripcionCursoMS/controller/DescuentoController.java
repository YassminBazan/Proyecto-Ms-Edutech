package edutech.inscripcionCursoMS.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.inscripcionCursoMS.model.Descuento;
import edutech.inscripcionCursoMS.service.DescuentoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    private final DescuentoService descuentoService;


    @PostMapping
    public ResponseEntity<Descuento> crearDescuento(@RequestBody Descuento descuento) {
        try {
            Descuento nuevoDescuento = descuentoService.crearDescuento(descuento);
            return new ResponseEntity<>(nuevoDescuento, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Descuento> obtenerDescuentoPorId(@PathVariable Long id) {
        return descuentoService.obtenerDescuentoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoCupon}")
    public ResponseEntity<Descuento> obtenerDescuentoPorCodigoCupon(@PathVariable String codigoCupon) {
        return descuentoService.obtenerDescuentoPorCodigoCupon(codigoCupon)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Descuento>> listarTodosLosDescuentos() {
        List<Descuento> descuentos = descuentoService.listarTodosLosDescuentos();
        return ResponseEntity.ok(descuentos);
    }

    @GetMapping("/activos-hoy")
    public ResponseEntity<List<Descuento>> listarDescuentosActivosYValidosHoy() {
        List<Descuento> descuentos = descuentoService.listarDescuentosActivosYValidosHoy();
        return ResponseEntity.ok(descuentos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Descuento> actualizarDescuento(@PathVariable Long id, @RequestBody Descuento descuento) {
        try {
            Descuento descuentoActualizado = descuentoService.actualizarDescuento(id, descuento);
            return ResponseEntity.ok(descuentoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarDescuento(@PathVariable Long id) {
        try {
            descuentoService.borrarDescuento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}