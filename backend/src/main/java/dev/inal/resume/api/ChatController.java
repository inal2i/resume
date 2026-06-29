package dev.inal.resume.api;

import dev.inal.resume.agents.ResumeAgent;
import dev.inal.resume.service.ThemeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ResumeAgent resumeAgent;
    private final ThemeService themeService;

    @Value("${spring.ai.gigachat.chat.options.model}")
    private String modelName;

    public ChatController(ResumeAgent resumeAgent, ThemeService themeService) {
        this.resumeAgent = resumeAgent;
        this.themeService = themeService;
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(Map.of("model", modelName));
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (request.messages() == null || request.messages().isEmpty()) {
            return ResponseEntity.badRequest().body("No messages provided");
        }
        return ResponseEntity.ok(resumeAgent.respond(request.messages()));
    }

    @PostMapping(value = "/theme", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateTheme(@RequestBody ThemeRequest request) {
        if (request.prompt() == null || request.prompt().isBlank()) {
            return ResponseEntity.badRequest().body("No prompt provided");
        }
        try {
            return ResponseEntity.ok(themeService.generate(request.prompt()));
        } catch (IllegalStateException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }
}
