package com.proyecto.GestionCursos.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.proyecto.GestionCursos.model.InstructorReplicado;
import com.proyecto.GestionCursos.repository.InstructorReplicadoRepository;

import com.proyecto.GestionCursos.event.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Component
@RequiredArgsConstructor
public class InstructorCreadoListener {

    private final InstructorReplicadoRepository instructorRepo; 

    @RabbitListener(queues = "usuarios.instructores.creados")
    public void recibirInstructor(InstructorCreadoEvent event){
        System.out.println("Evento recibido: " + event.getId());
        
        if (!instructorRepo.existsById(event.getId())) {
            InstructorReplicado nuevoInstructor = new InstructorReplicado();
            nuevoInstructor.setIdInstructor(event.getId());
            instructorRepo.save(nuevoInstructor);
            System.out.println("Instructor replicado: " + event.getId());
        } else {
            System.out.println("Instructor ya existe: " + event.getId());
        }
    }
}
