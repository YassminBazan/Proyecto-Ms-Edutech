package edutech.proveedorMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.proveedorMS.model.Region;
import edutech.proveedorMS.service.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping
    public ResponseEntity<Region> crearRegion(@RequestBody Region region) {
        try {
            Region nuevaRegion = regionService.crearRegion(region);
            return new ResponseEntity<>(nuevaRegion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> obtenerRegionPorId(@PathVariable Long id) {
        return regionService.obtenerRegionPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Region>> listarTodasLasRegiones() {
        List<Region> regiones = regionService.listarTodasLasRegiones();
        return ResponseEntity.ok(regiones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Region> actualizarRegion(@PathVariable Long id, @RequestBody Region region) {
        try {
            Region regionActualizada = regionService.actualizarRegion(id, region);
            return ResponseEntity.ok(regionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegion(@PathVariable Long id) {
        try {
            regionService.eliminarRegion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Region> obtenerRegionPorNombre(@PathVariable String nombre) {
        return regionService.obtenerRegionPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}