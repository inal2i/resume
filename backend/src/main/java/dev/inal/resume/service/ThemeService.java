package dev.inal.resume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.inal.resume.api.ThemePalette;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    private static final String SYSTEM_PROMPT = """
            Ты генератор CSS-тем. Пользователь описывает настроение/стиль — ты возвращаешь ТОЛЬКО валидный JSON-объект без markdown, без пояснений, только JSON.
            Формат (все ключи обязательны, значения — CSS-цвета hex или rgba):
            {"bg":"...","bg2":"...","panel":"...","panel2":"...","line":"rgba(...)","ink":"...","dim":"...","accent":"...","accent2":"...","chip":"...","glow":"rgba(...)"}
            Правила:
            - ink — основной цвет текста (контрастный к bg)
            - dim — приглушённый текст
            - accent — главный акцентный цвет (яркий)
            - accent2 — второй акцентный (гармоничный)
            - line — rgba с низкой непрозрачностью для линий
            - glow — rgba с непрозрачностью 0.5 для свечения accent
            """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ThemeService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public ThemePalette generate(String userPrompt) {
        String raw = chatClient.prompt()
                .user(SYSTEM_PROMPT + "\nНастроение: " + userPrompt)
                .call()
                .content();

        int a = raw.indexOf('{');
        int b = raw.lastIndexOf('}');
        if (a < 0 || b <= a) {
            throw new IllegalStateException("AI did not return valid JSON");
        }
        try {
            return objectMapper.readValue(raw.substring(a, b + 1), ThemePalette.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse palette: " + e.getMessage(), e);
        }
    }
}
