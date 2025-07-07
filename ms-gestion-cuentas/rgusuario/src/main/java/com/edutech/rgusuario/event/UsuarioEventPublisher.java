package com.edutech.rgusuario.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.edutech.rgusuario.model.Usuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioEventPublisher {

    private final RabbitTemplate rabbitTemplate;


    public void publicarUsuarioInstructorCreado(Usuario usuario) {
        InstructorCreadoEvent event = new InstructorCreadoEvent(usuario.getId());
        rabbitTemplate.convertAndSend("exchange.instructores", "usuarios.instructores.creados", event);
        System.out.println("✅ Evento InstructorCreado enviado con ID: " + usuario.getId());
    }
}

