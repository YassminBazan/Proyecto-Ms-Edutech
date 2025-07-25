package com.proyecto.GestionCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Valoracion;

import jakarta.transaction.Transactional;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    //Devuelve una valoracion por id
    Optional<Valoracion> findById(Long idValoracion);

    //Devuelve una lista de valoraciones de un usuario
    List<Valoracion> findByIdUsuario(Long idUsuario);

    //Devuelve una lista de valoraciones por curso
    List<Valoracion> findByCurso_IdCurso(Long idCurso);

    @Transactional
    @Modifying
    @Query("DELETE FROM Valoracion v WHERE v.curso.idCurso = :idCurso")
    void deleteByIdCurso(Long idCurso);

    

}
