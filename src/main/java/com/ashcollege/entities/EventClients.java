package com.ashcollege.entities;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class EventClients {
    private String secret;
    private SseEmitter emitter;

    public EventClients(String secret, SseEmitter emitter) {
        this.secret = secret;
        this.emitter = emitter;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }
}
