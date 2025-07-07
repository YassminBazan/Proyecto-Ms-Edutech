package com.proyecto.GestionCursos.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CursoConClasesDTO {
    private Long idCurso;
    private String nombreCurso;
    private String descripcion;
    private LocalDate fechaCreacion;
    private List<ClaseDTO> clases;

    public CursoConClasesDTO(Long idCurso, String nombreCurso, String descripcion, LocalDate fechaCreacion, List<ClaseDTO> clases) {
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.clases = clases;
    }
}
