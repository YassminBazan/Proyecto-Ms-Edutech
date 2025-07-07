package com.proyecto.GestionCursos.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proyecto.GestionCursos.model.ClaseDTO;

@Service
public class ClaseClient {
    
    @Value("${servicio.clases.url}")
    private String clasesUrl;

    private final RestTemplate restTemplate;

    public ClaseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ClaseDTO> obtenerClasesPorCurso(Long idCurso) {
        String url = clasesUrl + "/api/clases/por-curso/"+ idCurso;

        System.out.println("[ClaseClient] Llamando a Ms Gestion de Clases: " + url);

        ResponseEntity<ClaseDTO[]> response = restTemplate.getForEntity(url, ClaseDTO[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public void eliminarClasesPorCurso(Long idCurso) {
        String url = clasesUrl + "/api/clases/por-curso/" + idCurso;

        System.out.println("[ClaseClient] Llamando a Ms Gestion de Clases: " + url);
        restTemplate.delete(url);
    }


}
