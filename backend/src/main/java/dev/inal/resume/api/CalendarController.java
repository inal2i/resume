package dev.inal.resume.api;

import dev.inal.resume.service.CalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private static final Logger log = LoggerFactory.getLogger(CalendarController.class);
    private final CalendarService calendarService;

    @Value("${app.base-url:https://inal.tuaev.ru}")
    private String baseUrl;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/slots")
    public ResponseEntity<?> slots(@RequestParam String date) {
        try {
            List<String> slots = calendarService.getAvailableSlots(LocalDate.parse(date));
            return ResponseEntity.ok(Map.of("slots", slots));
        } catch (Exception e) {
            log.error("Failed to load slots for date={}: {}", date, e.getMessage(), e);
            return ResponseEntity.ok(Map.of("slots", List.of()));
        }
    }

    @PostMapping("/book")
    public ResponseEntity<?> book(@RequestBody BookRequest req) {
        try {
            String eventId = calendarService.bookSlot(
                    LocalDate.parse(req.date()), req.time(), req.name(),
                    req.email(), req.topic(), req.description());
            return ResponseEntity.ok(Map.of("success", true, "eventId", eventId));
        } catch (Exception e) {
            log.error("Booking failed date={} time={} name={}: {}", req.date(), req.time(), req.name(), e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping(value = "/cancel/{eventId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> cancel(@PathVariable String eventId) {
        try {
            String guestName = calendarService.cancelEvent(eventId);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(cancelPage(
                            "Встреча отменена",
                            "Привет, " + escHtml(guestName) + "!",
                            "Ваша встреча с <strong style=\"color:#f5f5f5;\">Inal Tuaev</strong> успешно отменена. Событие удалено из календаря.",
                            false));
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            int code = e.getStatusCode();
            log.warn("Cancel event {}: HTTP {}", eventId, code);
            if (code == 404 || code == 410) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(cancelPage(
                                "Встреча не найдена",
                                "Встреча не найдена",
                                "Эта встреча уже была отменена ранее или не существует.",
                                true));
            }
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_HTML)
                    .body(cancelPage("Ошибка", "Что-то пошло не так", "Не удалось отменить встречу. Напишите нам: <a href=\"mailto:inal4353@mail.ru\" style=\"color:#6ee7b7;\">inal4353@mail.ru</a>", true));
        } catch (Exception e) {
            log.error("Cancel event {} failed: {}", eventId, e.getMessage(), e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_HTML)
                    .body(cancelPage("Ошибка", "Что-то пошло не так", "Не удалось отменить встречу. Напишите нам: <a href=\"mailto:inal4353@mail.ru\" style=\"color:#6ee7b7;\">inal4353@mail.ru</a>", true));
        }
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String cancelPage(String title, String heading, String body, boolean isError) {
        String accentColor = isError ? "#f87171" : "#6ee7b7";
        String siteUrl = baseUrl;
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width,initial-scale=1">
                  <title>%s</title>
                  <style>
                    body { margin:0; padding:0; background:#0d0d0d; font-family:'Helvetica Neue',Helvetica,Arial,sans-serif; min-height:100vh; display:flex; align-items:center; justify-content:center; }
                    .card { width:100%%; max-width:480px; background:#161616; border:1px solid #2a2a2a; border-radius:14px; overflow:hidden; }
                    .card-header { background:#1a1a1a; padding:24px 32px; border-bottom:1px solid #2a2a2a; }
                    .card-body { padding:32px; }
                    .card-footer { padding:16px 32px; border-top:1px solid #2a2a2a; }
                    .card-label { font-size:13px; letter-spacing:.18em; text-transform:uppercase; color:%s; }
                    .card-heading { margin:0 0 16px; font-size:22px; font-weight:700; color:#f5f5f5; }
                    .card-text { margin:0 0 28px; font-size:15px; color:#a0a0a0; line-height:1.7; }
                    .card-btn { display:inline-block; padding:12px 24px; background:%s; color:#0d0d0d; font-size:14px; font-weight:700; text-decoration:none; border-radius:8px; letter-spacing:.06em; }
                    .card-meta { font-size:12px; color:#555; }
                    @media (max-width:540px) {
                      .card-header { padding:18px 20px; }
                      .card-body { padding:24px 20px; }
                      .card-footer { padding:14px 20px; }
                      .card-heading { font-size:19px; }
                      .card-btn { display:block; text-align:center; }
                    }
                  </style>
                </head>
                <body>
                  <div style="width:100%%;padding:32px 16px;box-sizing:border-box;display:flex;justify-content:center;">
                    <div class="card">
                      <div class="card-header">
                        <span class="card-label">// CALENDAR · %s</span>
                      </div>
                      <div class="card-body">
                        <p class="card-heading">%s</p>
                        <p class="card-text">%s</p>
                        <a href="%s" class="card-btn">← Вернуться на сайт</a>
                      </div>
                      <div class="card-footer">
                        <span class="card-meta">inal.tuaev.ru · Inal Tuaev · Backend Architect</span>
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(title, accentColor, accentColor, title.toUpperCase(), heading, body, siteUrl);
    }
}
