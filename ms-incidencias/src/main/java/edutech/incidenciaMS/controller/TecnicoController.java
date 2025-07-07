package edutech.incidenciaMS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edutech.incidenciaMS.model.Tecnico;
import edutech.incidenciaMS.service.TecnicoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    @Autowired
    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<Tecnico> crearTecnico(@RequestBody Tecnico tecnico) {
        try {
            Tecnico nuevoTecnico = tecnicoService.crearTecnico(tecnico);
            return new ResponseEntity<>(nuevoTecnico, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Retorna error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tecnico> obtenerTecnicoPorId(@PathVariable Long id) {
        Optional<Tecnico> tecnico = tecnicoService.obtenerTecnicoPorId(id);
        return tecnico.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Tecnico>> listarTodosTecnicos() {
        List<Tecnico> tecnicos = tecnicoService.listarTodosTecnicos();
        return ResponseEntity.ok(tecnicos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tecnico> actualizarTecnico(@PathVariable Long id, @RequestBody Tecnico tecnico) {
        try {
            Tecnico tecnicoActualizado = tecnicoService.actualizarTecnico(id, tecnico);
            return ResponseEntity.ok(tecnicoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTecnico(@PathVariable Long id) {
        try {
            tecnicoService.eliminarTecnico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
}