package edutech.inscripcionCursoMS.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edutech.inscripcionCursoMS.client.CursoClient;
import edutech.inscripcionCursoMS.model.CursoDTO;
import edutech.inscripcionCursoMS.model.Inscripcion;
import edutech.inscripcionCursoMS.model.Reporte;
import edutech.inscripcionCursoMS.repository.InscripcionRepository;
import edutech.inscripcionCursoMS.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final InscripcionRepository inscripcionRepository; 
    //Para consultar informacion al Ms Gestion de Cursos
    private final CursoClient cursoClient;



    @Transactional
    public Reporte generarReporteInscripcionesPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByFechaInscripcionBetween(fechaInicio, fechaFin);

        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Inscripciones entre ").append(fechaInicio).append(" y ").append(fechaFin).append(":\n");
        sb.append("Total de Inscripciones: ").append(inscripciones.size()).append("\n\n");

        for (Inscripcion insc : inscripciones) {
            String nombreCurso;
            try {
                CursoDTO cursoDTO = cursoClient.obtenerCursoPorId(insc.getIdCurso());
                nombreCurso = cursoDTO.getNombreCurso();
            } catch (Exception e) {
                nombreCurso = "Nombre no disponible (error al consultar MS)";
            }
            sb.append("ID Inscripción: ").append(insc.getId())
              .append(", Cliente ID: ").append(insc.getIdCliente())
              .append(", Curso: ").append(nombreCurso) 
              .append(", Fecha: ").append(insc.getFechaInscripcion())
              .append(", Precio Final: ").append(String.format("%.2f", insc.getPrecioFinal()))
              .append(", Descuento Aplicado: ").append(String.format("%.2f", insc.getDescuentoAplicado() * 100)).append("%")
              .append(", Estado: ").append(insc.getEstado())
              .append("\n");
        }

        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setTipoReporte("InscripcionesPorFecha");
        reporte.setContenidoReporte(sb.toString()); 
        return reporteRepository.save(reporte);
    }

    

    public Optional<Reporte> obtenerReportePorId(Long id) {
        return reporteRepository.findById(id);
    }

    public List<Reporte> listarTodosLosReportes() {
        return reporteRepository.findAll();
    }

    public List<Reporte> filtrarReportesPorFechaGeneracion(LocalDateTime start, LocalDateTime end) {
        return reporteRepository.findByFechaGeneracionBetween(start, end);
    }

    public List<Reporte> filtrarReportesPorTipo(String tipo) {
        return reporteRepository.findByTipoReporte(tipo);
    }

    
    public String exportarReporte(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
       
        System.out.println("Exportando reporte ID: " + id);
        System.out.println(reporte.getContenidoReporte()); 
        return reporte.getContenidoReporte(); 
    }

    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        reporteRepository.deleteById(id);
    }
}
