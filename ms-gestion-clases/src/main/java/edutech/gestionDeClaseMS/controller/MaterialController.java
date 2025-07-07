package edutech.gestionDeClaseMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.gestionDeClaseMS.model.Material;
import edutech.gestionDeClaseMS.model.TipoMaterial;
import edutech.gestionDeClaseMS.model.EstadoMaterial; // Importar el nuevo Enum
import edutech.gestionDeClaseMS.service.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/materiales")
public class MaterialController {

    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping("/clase/{claseId}")
    public ResponseEntity<Material> crearMaterial(@PathVariable Long claseId, @RequestBody Material material) {
        try {
            Material nuevoMaterial = materialService.crearMaterial(material, claseId);
            return new ResponseEntity<>(nuevoMaterial, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> obtenerMaterialPorId(@PathVariable Long id) {
        return materialService.obtenerMaterialPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Material>> listarTodosLosMateriales() {
        List<Material> materiales = materialService.listarTodosLosMateriales();
        return ResponseEntity.ok(materiales);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Material> actualizarMaterial(@PathVariable Long id, @RequestBody Material material) {
        try {
            Material materialActualizado = materialService.actualizarMaterial(id, material);
            return ResponseEntity.ok(materialActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMaterial(@PathVariable Long id) {
        try {
            materialService.eliminarMaterial(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/por-clase/{claseId}")
    public ResponseEntity<List<Material>> listarMaterialesPorClase(@PathVariable Long claseId) {
        List<Material> materiales = materialService.listarMaterialesPorClase(claseId);
        return ResponseEntity.ok(materiales);
    }

    @GetMapping("/por-tipo/{tipoMaterial}")
    public ResponseEntity<List<Material>> listarMaterialesPorTipo(@PathVariable TipoMaterial tipoMaterial) {
        List<Material> materiales = materialService.listarMaterialesPorTipo(tipoMaterial);
        return ResponseEntity.ok(materiales);
    }

    @PatchMapping("/{id}/aprobar") 
    public ResponseEntity<Material> aprobarMaterial(@PathVariable Long id) {
        try {
            Material materialAprobado = materialService.aprobarMaterial(id);
            return ResponseEntity.ok(materialAprobado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}/rechazar") 
    public ResponseEntity<Material> rechazarMaterial(@PathVariable Long id) {
        try {
            Material materialRechazado = materialService.rechazarMaterial(id);
            return ResponseEntity.ok(materialRechazado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/por-estado/{estadoMaterial}") 
    public ResponseEntity<List<Material>> listarMaterialesPorEstado(@PathVariable EstadoMaterial estadoMaterial) {
        List<Material> materiales = materialService.listarMaterialesPorEstado(estadoMaterial);
        return ResponseEntity.ok(materiales);
    }
}