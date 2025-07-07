package edutech.inscripcionCursoMS.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import edutech.inscripcionCursoMS.model.CursoDTO;

@Service
public class CursoClient {

    @Value("${servicio.cursos.url}")

    private String cursosUrl;
    private final RestTemplate restTemplate;

    public CursoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CursoDTO obtenerCursoPorId(Long idCurso) {
        String url = cursosUrl + "/api/v1/cursos/" + idCurso;

        System.out.println("[CursoClient] Llamando a Ms Gestion de Cursos: " + url);
        try {
            return restTemplate.getForObject(url, CursoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("El curso con ID " + idCurso + " no existe");
        }
    }
}
