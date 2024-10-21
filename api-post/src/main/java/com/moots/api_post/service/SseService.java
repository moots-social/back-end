package com.moots.api_post.service;

import com.moots.api_post.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    public void sendPostEvent(Post postData) {
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("post-data").data(postData));
                return false;
            } catch (IOException e) {
                emitter.complete();
                return true;
            }
        });
    }

}

