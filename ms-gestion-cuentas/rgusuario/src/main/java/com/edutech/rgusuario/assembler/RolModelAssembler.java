package com.edutech.rgusuario.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.edutech.rgusuario.controller.RolControllerV2;
import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;

@Component
public class RolModelAssembler implements RepresentationModelAssembler<Rol, EntityModel<Rol>> {

    @Override
    @NonNull
    public EntityModel<Rol> toModel(@NonNull Rol rol) {
        return EntityModel.of(rol,
            linkTo(methodOn(RolControllerV2.class).getRolById(rol.getId())).withSelfRel(),
            linkTo(methodOn(RolControllerV2.class).getAllRoles()).withRel("roles"),
            linkTo(methodOn(RolControllerV2.class).addPermiso(rol.getId(), Permiso.CREAR_USUARIO)).withRel("add-permiso"),
            linkTo(methodOn(RolControllerV2.class).removePermiso(rol.getId(), Permiso.ELIMINAR_USUARIO)).withRel("remove-permiso")
        );
    }
}