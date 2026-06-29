package dev.inal.resume.api;

import java.util.List;

public record ChatRequest(List<Message> messages) {
    public record Message(String role, String text) {}
}
