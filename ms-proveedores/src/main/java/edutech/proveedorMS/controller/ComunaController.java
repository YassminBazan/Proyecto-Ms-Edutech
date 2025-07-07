package edutech.proveedorMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.proveedorMS.model.Comuna;
import edutech.proveedorMS.service.ComunaService;

import java.util.List;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    private final ComunaService comunaService;

    @Autowired
    public ComunaController(ComunaService comunaService) {
        this.comunaService = comunaService;
    }

    @PostMapping("/region/{regionId}")
    public ResponseEntity<Comuna> crearComuna(@PathVariable Long regionId, @RequestBody Comuna comuna) {
        try {
            Comuna nuevaComuna = comunaService.crearComuna(comuna, regionId);
            return new ResponseEntity<>(nuevaComuna, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comuna> obtenerComunaPorId(@PathVariable Long id) {
        return comunaService.obtenerComunaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Comuna>> listarTodasLasComunas() {
        List<Comuna> comunas = comunaService.listarTodasLasComunas();
        return ResponseEntity.ok(comunas);
    }

    @PutMapping("/{id}/region/{newRegionId}")
    public ResponseEntity<Comuna> actualizarComuna(
            @PathVariable Long id,
            @RequestBody Comuna comuna,
            @PathVariable(required = false) Long newRegionId) {
        try {
            Comuna comunaActualizada = comunaService.actualizarComuna(id, comuna, newRegionId);
            return ResponseEntity.ok(comunaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComuna(@PathVariable Long id) {
        try {
            comunaService.eliminarComuna(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/por-region/{regionId}")
    public ResponseEntity<List<Comuna>> listarComunasPorRegion(@PathVariable Long regionId) {
        List<Comuna> comunas = comunaService.listarComunasPorRegion(regionId);
        return ResponseEntity.ok(comunas);
    }
}