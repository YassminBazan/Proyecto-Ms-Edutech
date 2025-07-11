package com.proyecto.GestionCursos.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.InstructorReplicadoRepository;

public class CursoServiceTest {

    //Se crean mocks (doble de prueba o simulacion) de las dependendencias del servicio para los repositorios 
    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private InstructorReplicadoRepository instructorReplicadoRepository;

    //Se crea una instancia real de cursoService para probar y mockito le inyecta los mocks de arriba 
    @InjectMocks
    private CursoService cursoService;

    private Curso cursoPrueba;
    private Curso cursoPrueba2;
    List<Curso> listaCursos;
    
    //Método que se ejecuta antes de cada prueba para preparar el entorno
    @BeforeEach
    void setUp(){
        //Se inicializan los mocks y las inyecciones 
        MockitoAnnotations.openMocks(this);


        //Preparacion de un objeto de prueba para varios test
        cursoPrueba = new Curso();
        cursoPrueba.setIdCurso(1L);
        cursoPrueba.setIdUsuario(11L);
        cursoPrueba.setNombreCurso("Python");
        cursoPrueba.setDescripcion("Curso Python para principiantes");
        cursoPrueba.setValorCurso(5000);

        cursoPrueba2 = new Curso();
        cursoPrueba2.setIdCurso(2L);
        cursoPrueba2.setIdUsuario(11L);
        cursoPrueba2.setNombreCurso("Java");
        cursoPrueba2.setDescripcion("Curso Java para principiantes");
        cursoPrueba2.setValorCurso(5000);

        listaCursos = List.of(cursoPrueba, cursoPrueba2);

    }



    //Guardar curso
    @Test
    @DisplayName("Crear curso exitosamente con datos válidos")
    void testCrearCursoOk() {
        Long idCreador = 11L;
        String nombreCurso = "Java Básico";
        String descripcion = "Curso para aprender Java desde cero";
        double valorCurso = 1500;
        Set<Long> idsCategorias = Set.of(1L, 2L);

        // Mock categorías válidas
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(1L);
        cat1.setNombreCategoria("Programación");

        Categoria cat2 = new Categoria();
        cat2.setIdCategoria(2L);
        cat2.setNombreCategoria("Desarrollo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(cat2));

        // Mock save del curso
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Curso resultado = cursoService.crearCurso(nombreCurso, descripcion, valorCurso, idCreador, idsCategorias);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreCurso()).isEqualTo(nombreCurso);
        assertThat(resultado.getDescripcion()).isEqualTo(descripcion);
        assertThat(resultado.getValorCurso()).isEqualTo(valorCurso);
        assertThat(resultado.getIdUsuario()).isEqualTo(idCreador);
        assertThat(resultado.getCategorias()).hasSize(2);
        assertThat(resultado.getFechaCreacion()).isEqualTo(LocalDate.now());

        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre del curso es null")
    void testCrearCursoConNombreNull() {
        Long idCreador = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crearCurso(null, "Descripción válida", 2000, idCreador, Set.of(1L));
        });

        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre del curso es blanco")
    void testCrearCursoConNombreBlanco() {
        Long idCreador = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crearCurso("   ", "Descripción válida", 2000, idCreador, Set.of(1L));
        });

        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");
    }
    @Test
    @DisplayName("Debe permitir descripción null")
    void testCrearCursoDescripcionNull() {
        Long idCreador = 1L;
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Curso curso = cursoService.crearCurso("Curso válido", null, 2000, idCreador, Set.of());

        assertThat(curso.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Debe lanzar excepción si la descripción excede los 1000 caracteres")
    void testCrearCursoDescripcionMuyLarga() {
        Long idCreador = 1L;

        String descripcionLarga = "a".repeat(1001); // 1001 caracteres
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crearCurso("Curso válido", descripcionLarga, 2000, idCreador, Set.of(1L));
        });

        assertThat(exception.getMessage()).isEqualTo("La descripcion no puede exceder los 1000 caracteres");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el valor del curso es menor a 1000")
    void testCrearCursoValorMenorA1000() {
        Long idCreador = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crearCurso("Curso válido", "Descripción válida", 999, idCreador, Set.of(1L));
        });

        assertThat(exception.getMessage()).isEqualTo("El valor del curso debe ser mayor o igual $1000");
    }

    //TEST PARA VALIDACION DE CATEGORIA

    @Test
    @DisplayName("Debe asignar 'sinCategoria' si idsCategorias es null")
    void testCrearCursoConIdsCategoriasNull() {
        Long idCreador = 1L;

        Categoria sinCategoria = new Categoria();
        sinCategoria.setIdCategoria(99L);
        sinCategoria.setNombreCategoria("sinCategoria");

        when(categoriaRepository.findByNombreCategoriaIgnoreCase("sinCategoria"))
            .thenReturn(Optional.of(sinCategoria));
        when(cursoRepository.save(any(Curso.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Curso curso = cursoService.crearCurso("Curso válido", "Descripción", 1500, idCreador, null);

        assertThat(curso.getCategorias()).hasSize(1);
        assertThat(curso.getCategorias().iterator().next().getNombreCategoria()).isEqualTo("sinCategoria");
    }

    @Test
    @DisplayName("Debe asignar 'sinCategoria' si idsCategorias está vacío")
    void testCrearCursoConIdsCategoriasVacio() {
        Long idCreador = 1L;

        Categoria sinCategoria = new Categoria();
        sinCategoria.setIdCategoria(99L);
        sinCategoria.setNombreCategoria("sinCategoria");

        when(categoriaRepository.findByNombreCategoriaIgnoreCase("sinCategoria"))
            .thenReturn(Optional.of(sinCategoria));
        when(cursoRepository.save(any(Curso.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Curso curso = cursoService.crearCurso("Curso válido", "Descripción", 1500, idCreador, Collections.emptySet());

        assertThat(curso.getCategorias()).hasSize(1);
        assertThat(curso.getCategorias().iterator().next().getNombreCategoria()).isEqualTo("sinCategoria");
    }

    //OBTENER CURSOS 
    @DisplayName("Test para obtener curso por id")
    @Test
    void testObtenerCursoPorId(){
        //Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoPrueba));
        //Act: se llama el metodo a probar
        Optional<Curso> resultado = cursoService.obtenerCursoPorId(1L);

        //Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get().getIdCurso()).isEqualTo(1L);
        
    }
    @DisplayName("Debe retornar vacío si el curso no existe por ID")
    @Test
    void testObtenerCursoPorIdNoExiste() {
        Long idCurso = 999L;

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.obtenerCursoPorId(idCurso);

        assertThat(resultado).isEmpty();
    }

    @DisplayName("Test para obtener todos los cursos")
    @Test
    void testObtenerTodosLosCursos(){
        //Arrange
        when(cursoRepository.findAll()).thenReturn(listaCursos);

        //Act: se llama el metodo a probar
        List<Curso> resultado = cursoService.obtenerTodosLosCursos();

        //Assert
        assertThat(resultado).isNotNull();
        assertEquals(2, resultado.size());
        assertEquals("Python", resultado.get(0).getNombreCurso());
        assertEquals("Java", resultado.get(1).getNombreCurso());

        verify(cursoRepository, times(1)).findAll();
    }

    //TEST ACTUALIZAR CURSO
    @Test
    @DisplayName("Debe actualizar el curso si los datos son válidos y el curso existe")
    void testActualizarCurso() {
        Long idCurso = 1L;

        Curso cursoExistente = new Curso();
        cursoExistente.setIdCurso(idCurso);
        cursoExistente.setNombreCurso("Antiguo");
        cursoExistente.setDescripcion("Vieja desc");
        cursoExistente.setValorCurso(2000);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Curso> actualizado = cursoService.actualizarCurso(idCurso, "Nuevo nombre", "Nueva descripción", 2500);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombreCurso()).isEqualTo("Nuevo nombre");
        assertThat(actualizado.get().getDescripcion()).isEqualTo("Nueva descripción");
        assertThat(actualizado.get().getValorCurso()).isEqualTo(2500);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre es nulo o vacío")
    void testActualizarCursoNombreInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.actualizarCurso(1L, null, "Desc", 1500);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.actualizarCurso(1L, "   ", "Desc", 1500);
        });
    }
    @DisplayName("Debe lanzar excepción si la descripción tiene más de 1000 caracteres al actualizar")
    @Test
    void testActualizarCursoDescripcionMuyLarga() {
        String descripcionLarga = "a".repeat(1001);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.actualizarCurso(1L, "Curso válido", descripcionLarga, 1500);
        });

        assertThat(exception.getMessage()).isEqualTo("La descripcion no puede exceder los 1000 caracteres");
    }


    @Test
    @DisplayName("Debe lanzar excepción si el valor del curso es menor a 1000")
    void testActualizarCursoValorMenorA1000() {
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.actualizarCurso(1L, "Nombre", "Descripción", 999);
        });
    }

    @DisplayName("Test para probar el método asignarInstructor correctamente para agregarlo a la lista de instructores del curso")
    @Test
    void testAsignarInstructorOk(){
        //ARRANGE: Preparacion de los datos, asignacion de valores
        Long idCurso = 1L;
        Long idInstructor = 20L;
        Curso cursoExistente = new Curso();
        cursoExistente.setIdsInstructores(new HashSet<>()); //Inicializa la colección 

        //Simulamos que el instructor si existe en la tabla replicada
        when(instructorReplicadoRepository.existsById(idInstructor)).thenReturn(true);
        //Lo mismo para el curso
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(cursoExistente));
        //Simulacion de guardar
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);

        //ACT
        Curso resultado = cursoService.asignarInstructor(idCurso, idInstructor);

        //ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdsInstructores()).contains(idInstructor);
        verify(cursoRepository).save(cursoExistente);

    }

    @DisplayName("Test para probar el método asignarInstructor de manera incorrecta, por no existencia de de instructor")
    @Test
    void testAsignarInstructorFail(){
        Long idCurso = 1L;
        Long idInstructorInvalido = 99L;

        //Simulamos que el instructor no existe
        when(instructorReplicadoRepository.existsById(idInstructorInvalido)).thenReturn(false);

        //ACT: Para llamr al método o funcion a probar y ASSERT: Verifica que el resultado obtenido sea el esperado
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.asignarInstructor(idCurso, idInstructorInvalido);

        });

    }
    @DisplayName("Test para desvincular a un instructor de manera exitosa")
    @Test
    void testDesvincularInstructorOk(){
        Long idCurso = 1L;
        Long idInstructor = 10L;

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        Set<Long> instructores = new HashSet<>();
        instructores.add(idInstructor);
        curso.setIdsInstructores(instructores);



        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation ->invocation.getArgument(0));

        //ACT
        Curso resultado = cursoService.desvincularInstructor(idCurso, idInstructor);

        //ASSERT
        assertThat(resultado).isNotNull();

        //Se verifica que la lista de instructores no contenga el id del instructor eliminado
        assertThat(resultado.getIdsInstructores()).doesNotContain(idInstructor);
        
    }


    @DisplayName("Test para desvincular a un instructor de manera fallida ")
    @Test
    void testDesvincularIntructorFail(){
        Long idCursoInvalido = 1L;
        Long idInstructor = 10L; 
        
        //Simulacion de que el curso no existe 
        when(cursoRepository.findById(idCursoInvalido)).thenReturn(Optional.empty());

        //ACT
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.desvincularInstructor(idCursoInvalido, idInstructor);
        });
    }

    @DisplayName("Test para actualizar curso de manera correcta")
    @Test
    void testActualizarCursoOk(){
        //Preparacion de datos existentes
        Long idCursoExistente = 1L;
        String nuevoNombre = "Curso Python 2.0";
        String nuevaDescripcion = "Curso python version actualizada";
        double nuevoValor = 6000;

        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCursoExistente)).thenReturn(Optional.of(cursoPrueba));

        //Simulacion del metodo 
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act 
        Optional<Curso> resultadoOpt = cursoService.actualizarCurso(idCursoExistente,nuevoNombre, nuevaDescripcion, nuevoValor);

        //Verificacion de que el optional no esta vacio
        assertThat(resultadoOpt).isPresent();

        Curso resultado = resultadoOpt.get();

        //Verificamos que se actualizaron correctamente los campos
        assertThat(resultado.getNombreCurso()).isEqualTo(nuevoNombre);
        assertThat(resultado.getDescripcion()).isEqualTo(nuevaDescripcion);
        assertThat(resultado.getValorCurso()).isEqualTo(nuevoValor);
    }

    @DisplayName("Test para actualizar un curso de manera incorrecta")
    @Test
    void testactualizarCursoFail(){

        //Preparacion de datos existentes
        Long idCurso = 100L;

        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.empty());

        //Act
        Optional<Curso> resultadoOpt = cursoService.actualizarCurso(idCurso, "BD", "Quien sabe", 10000);

        //Assert
        //Verificacion del optional 
        assertThat(resultadoOpt).isEmpty();
       

        //verificacion de que no se uso el metodo
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @DisplayName("Test para eliminar un curso de manera correcta")
    @Test
    void testEliminarCursoOk(){
        //Arrange
        Long idCursoExistente = 1L;

        //Simulacion de que el curso si existe
        when(cursoRepository.existsById(idCursoExistente)).thenReturn(true);

        //Act 
        cursoService.eliminarCurso(idCursoExistente);

        //Verificamos que el metodo delete se llamo correctamente 
        verify(cursoRepository, times(1)).deleteById(idCursoExistente);
    }

    @DisplayName("Test para eliminar un curso que no existe")
    @Test
    void testEliminarCursoFail(){
        //Arrange 
        Long idCursofake = 100L;

        //Simulamos que el curso no existe
        when(cursoRepository.existsById(idCursofake)).thenReturn(false);

        //Verificamos que se lanza una excepcion
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.eliminarCurso(idCursofake);
        });
        //Verificacion del mensaje
        assertThat(exception.getMessage()).isEqualTo("El curso ingresado no fue encontrado");
    }

    

}
