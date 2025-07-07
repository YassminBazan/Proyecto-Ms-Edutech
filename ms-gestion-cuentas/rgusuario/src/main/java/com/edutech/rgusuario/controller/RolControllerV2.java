package com.edutech.rgusuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.rgusuario.assembler.RolModelAssembler;
import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.service.RolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/roles")
public class RolControllerV2 {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Rol>> getAllRoles() {
        List<EntityModel<Rol>> roles = rolService.findAll().stream()
            .map(assembler::toModel)
            .toList();

        return CollectionModel.of(roles,
            linkTo(methodOn(RolControllerV2.class).getAllRoles()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Rol> getRolById(@PathVariable Long id) {
        Rol rol = rolService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));

        return assembler.toModel(rol);
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Rol>> createRol(@RequestBody @Valid Rol rol) {
        if (rolService.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
        }

        Rol guardado = rolService.save(rol);

        return ResponseEntity
            .created(linkTo(methodOn(RolControllerV2.class).getRolById(guardado.getId())).toUri())
            .body(assembler.toModel(guardado));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Rol>> updateRol(@PathVariable Long id, @RequestBody @Valid Rol rol) {
        Rol existente = rolService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));

        if (!existente.getNombre().equals(rol.getNombre()) &&
            rolService.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Ya existe el rol con el nombre: " + rol.getNombre());
        }

        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        existente.setPermiso(rol.getPermiso());

        Rol actualizado = rolService.save(existente);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteRol(@PathVariable Long id) {
        if (!rolService.existsById(id)) {
            throw new RuntimeException("No se encontró el rol con ID: " + id);
        }

        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/permisos/{permiso}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Rol>> addPermiso(@PathVariable Long id, @PathVariable Permiso permiso) {
        Rol actualizado = rolService.agregarPermiso(id, permiso);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping(value = "/{id}/permisos/{permiso}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Rol>> removePermiso(@PathVariable Long id, @PathVariable Permiso permiso) {
        Rol actualizado = rolService.removerPermiso(id, permiso);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }
}