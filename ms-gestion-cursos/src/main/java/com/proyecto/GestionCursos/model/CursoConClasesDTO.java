package com.proyecto.GestionCursos.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CursoConClasesDTO {
    private Long idCurso;
    private String nombreCurso;
    private String descripcion;
    private LocalDate fechaCreacion;
    private List<ClaseDTO> clases;

}
