package com.nfssoundtrack.racingsoundtracks.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("/streaming")
public class StreamingController {

    private record NowPlayingInfo(String artist, String title, String game, String subgroupType) {}

    private final ConcurrentHashMap<String, NowPlayingInfo> nowPlayingByToken = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<SseEmitter>> emittersByToken = new ConcurrentHashMap<>();

    @GetMapping("/overlay/{token}")
    public String overlay() {
        return "streaming-overlay";
    }

    @PostMapping("/now-playing/{token}")
    @ResponseBody
    public ResponseEntity<Void> setNowPlaying(@PathVariable String token,
                                               @RequestBody Map<String, String> body) {
        NowPlayingInfo info = new NowPlayingInfo(
                body.getOrDefault("artist", ""),
                body.getOrDefault("title", ""),
                body.getOrDefault("game", ""),
                body.getOrDefault("subgroupType", "")
        );
        nowPlayingByToken.put(token, info);
        pushToEmitters(token, info);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/events/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter subscribe(@PathVariable String token) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emittersByToken.computeIfAbsent(token, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(token, emitter));
        emitter.onTimeout(() -> removeEmitter(token, emitter));
        emitter.onError(e -> removeEmitter(token, emitter));

        // Send current state immediately so overlay catches up on (re)connect
        NowPlayingInfo current = nowPlayingByToken.get(token);
        if (current != null) {
            try {
                emitter.send(SseEmitter.event().data(current));
            } catch (IOException e) {
                removeEmitter(token, emitter);
            }
        }
        return emitter;
    }

    private void pushToEmitters(String token, NowPlayingInfo info) {
        List<SseEmitter> emitters = emittersByToken.get(token);
        if (emitters == null) return;
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(info));
            } catch (IOException e) {
                removeEmitter(token, emitter);
            }
        }
    }

    private void removeEmitter(String token, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByToken.get(token);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
