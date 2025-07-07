package edutech.proveedorMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.proveedorMS.model.EstadoProveedor;
import edutech.proveedorMS.model.Proveedor;
import edutech.proveedorMS.service.ProveedorService;

import java.util.List;
import java.util.Set; 

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(
            @RequestBody Proveedor proveedor,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long comunaId,
            @RequestParam(required = false) Set<Long> categoriaIds) {
        try {
            Proveedor nuevoProveedor = proveedorService.crearProveedor(proveedor, regionId, comunaId, categoriaIds);
            return new ResponseEntity<>(nuevoProveedor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerProveedorPorId(@PathVariable Long id) {
        return proveedorService.obtenerProveedorPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarTodosLosProveedores() {
        List<Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
        return ResponseEntity.ok(proveedores);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(
            @PathVariable Long id,
            @RequestBody Proveedor proveedor,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long comunaId,
            @RequestParam(required = false) Set<Long> categoriaIds) {
        try {
            Proveedor proveedorActualizado = proveedorService.actualizarProveedor(id, proveedor, regionId, comunaId, categoriaIds);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        try {
            proveedorService.eliminarProveedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Proveedor>> listarProveedoresPorEstado(@PathVariable EstadoProveedor estado) {
        List<Proveedor> proveedores = proveedorService.listarProveedoresPorEstado(estado);
        return ResponseEntity.ok(proveedores);
    }

   
    @GetMapping("/buscar-por-nombre")
    public ResponseEntity<List<Proveedor>> buscarProveedoresPorNombre(@RequestParam String nombre) {
        List<Proveedor> proveedores = proveedorService.buscarProveedoresPorNombre(nombre);
        return ResponseEntity.ok(proveedores);
    }

    
    @GetMapping("/categoria/{nombreCategoria}")
    public ResponseEntity<List<Proveedor>> listarProveedoresPorCategoria(@PathVariable String nombreCategoria) {
        List<Proveedor> proveedores = proveedorService.listarProveedoresPorCategoria(nombreCategoria);
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/region/{nombreRegion}")
    public ResponseEntity<List<Proveedor>> listarProveedoresPorRegion(@PathVariable String nombreRegion) {
        List<Proveedor> proveedores = proveedorService.listarProveedoresPorRegion(nombreRegion);
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/comuna/{nombreComuna}")
    public ResponseEntity<List<Proveedor>> listarProveedoresPorComuna(@PathVariable String nombreComuna) {
        List<Proveedor> proveedores = proveedorService.listarProveedoresPorComuna(nombreComuna);
        return ResponseEntity.ok(proveedores);
    }
}