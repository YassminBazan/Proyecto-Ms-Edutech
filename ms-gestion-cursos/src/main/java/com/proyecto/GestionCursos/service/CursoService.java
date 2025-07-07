package com.proyecto.GestionCursos.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.client.ClaseClient;
import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.ClaseDTO;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.model.CursoConClasesDTO;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.InstructorReplicadoRepository;
import com.proyecto.GestionCursos.repository.ValoracionRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CursoService {

    //Dependencias      
    private final CursoRepository cursoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InstructorReplicadoRepository instructorReplicadoRepository;
    private final ValoracionRepository valoracionRepository;
    //Añadido para hacer la conexion con MS GESTION DE CLASES
    private final ClaseClient claseClient;


    //Para crear un curso
    @Transactional
    public Curso crearCurso(String nombreCurso, String descripcion, double valorCurso, Long idCreador, Set<Long> idsCategorias){
        if (nombreCurso == null || nombreCurso.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (descripcion != null && descripcion.length() > 1000) {
            throw new IllegalArgumentException("La descripcion no puede exceder los 1000 caracteres");
        }

        if (valorCurso < 1000) {
            throw new IllegalArgumentException("El valor del curso debe ser mayor o igual $1000");
        }

        //Validacion de las categorias 
        Set<Categoria> categorias;

        if (idsCategorias == null || idsCategorias.isEmpty()) {
            // Buscar la categoria "sinCategoria"
            categorias = new HashSet<>();
            Optional<Categoria> sinCatOpt = categoriaRepository.findByNombreCategoriaIgnoreCase("sinCategoria");
            
            Categoria sinCategoria;
            if (sinCatOpt.isPresent()) {
                sinCategoria = sinCatOpt.get();
            } else {
                // Si no existe, crearla
                sinCategoria = new Categoria();
                sinCategoria.setNombreCategoria("sinCategoria");
                sinCategoria = categoriaRepository.save(sinCategoria);
            }

            categorias.add(sinCategoria);

        } else {
            // Validar categorias que llegaron
            categorias = idsCategorias.stream()
                    .map(idCat -> categoriaRepository.findById(idCat)
                        .orElseThrow(() -> new IllegalArgumentException("La categoria no existe")))
                    .collect(Collectors.toSet());
        }

        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombreCurso(nombreCurso);
        nuevoCurso.setDescripcion(descripcion);
        nuevoCurso.setValorCurso(valorCurso);
        nuevoCurso.setIdUsuario(idCreador); // Asigna el creador
        nuevoCurso.setFechaCreacion(LocalDate.now());
        nuevoCurso.setCategorias(categorias); // Asigna el conjunto de entidades Categoria

        return cursoRepository.save(nuevoCurso);
    }

    //Para obtener curso por id
    public Optional<Curso> obtenerCursoPorId(Long idCurso){
        return cursoRepository.findById(idCurso);
    }

    //Para obtener todos los cursos
    public List<Curso> obtenerTodosLosCursos(){
        return cursoRepository.findAll();
    }

    //Para actualizar un curso
    @Transactional
    public Optional<Curso> actualizarCurso(Long idCurso, String nombreCurso, String descripcion, double valorCurso){

        if (nombreCurso == null || nombreCurso.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (descripcion != null && descripcion.length() > 1000) {
            throw new IllegalArgumentException("La descripcion no puede exceder los 1000 caracteres");
        }

        if (valorCurso < 1000) {
            throw new IllegalArgumentException("El valor del curso debe ser mayor o igual $1000");
        }

        Optional<Curso> curso = cursoRepository.findById(idCurso);

        if (curso.isEmpty()) {
            return Optional.empty();
        }

        Curso cursoExistente = curso.get();

        cursoExistente.setNombreCurso(nombreCurso);
        cursoExistente.setDescripcion(descripcion);
        cursoExistente.setValorCurso(valorCurso);

        Curso cursoActualizado = cursoRepository.save(cursoExistente);
        return Optional.of(cursoActualizado);

    }


    //Asignacion de instructores 
    @Transactional
    public Curso asignarInstructor(Long idCurso, Long idInstructor){
        //Valida que el instru exista en la tabla replicada
        if(!instructorReplicadoRepository.existsById(idInstructor)){
            throw new IllegalArgumentException("Ingrese un instructor valido");
        }

        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new IllegalArgumentException("Ingrese un curso valido"));

        curso.getIdsInstructores().add(idInstructor);
        return cursoRepository.save(curso);
    }

    //Desvincular instructores
    @Transactional
    public Curso desvincularInstructor(Long idCurso, Long idInstructor){
        
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new IllegalArgumentException("Ingrese un curso valido"));

        curso.getIdsInstructores().remove(idInstructor);
        return cursoRepository.save(curso);
    }


    
    //INCLUIDO PARA LLAMAR AL MS GESTION DE CLASES
    public CursoConClasesDTO obtenerCursoConClases(Long idCurso) {
    Curso curso = cursoRepository.findById(idCurso)
        .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        List<ClaseDTO> clases = claseClient.obtenerClasesPorCurso(idCurso);

        return new CursoConClasesDTO(
            curso.getIdCurso(),
            curso.getNombreCurso(),
            curso.getDescripcion(),
            curso.getFechaCreacion(),
            clases
        );
    }

    //Para eliminar curso
    @Transactional
    public void eliminarCurso(Long idCurso){
        if (!cursoRepository.existsById(idCurso)) {
            throw new IllegalArgumentException("El curso ingresado no fue encontrado" );
        }
        
        //Hace la llamada al MS Gestion de clases 
        claseClient.eliminarClasesPorCurso(idCurso);
        //Elimina las valoraciones asociadas
        valoracionRepository.deleteByIdCurso(idCurso);
        //Elimina el curso
        cursoRepository.deleteById(idCurso);
    }


    

}
