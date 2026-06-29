package dev.inal.resume.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CalendarService {

    private static final Logger log = LoggerFactory.getLogger(CalendarService.class);
    private static final String CALENDAR_ID = "inaltuaev@gmail.com";
    private static final String TIME_ZONE = "Europe/Moscow";
    private static final int WORK_HOUR_START = 10;
    private static final int WORK_HOUR_END = 18;

    private final Calendar client;
    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public CalendarService(JavaMailSender mailSender) throws Exception {
        this.mailSender = mailSender;
        InputStream is = saJsonStream();
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(is)
                .createScoped(CalendarScopes.CALENDAR);
        client = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("Resume Calendar").build();
    }

    private static InputStream saJsonStream() {
        String b64 = System.getenv("GOOGLE_SA_JSON");
        if (b64 != null && !b64.isBlank()) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(b64.trim()));
        }
        return CalendarService.class.getResourceAsStream("/google-calendar-sa.json");
    }

    public List<String> getAvailableSlots(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) return Collections.emptyList();

        ZoneId zone = ZoneId.of(TIME_ZONE);
        ZonedDateTime dayStart = date.atTime(WORK_HOUR_START, 0).atZone(zone);
        ZonedDateTime dayEnd = date.atTime(WORK_HOUR_END, 0).atZone(zone);

        List<TimePeriod> busy = Collections.emptyList();
        try {
            FreeBusyRequest req = new FreeBusyRequest()
                    .setTimeMin(new DateTime(dayStart.toInstant().toEpochMilli()))
                    .setTimeMax(new DateTime(dayEnd.toInstant().toEpochMilli()))
                    .setItems(List.of(new FreeBusyRequestItem().setId(CALENDAR_ID)));
            FreeBusyResponse resp = client.freebusy().query(req).execute();
            FreeBusyCalendar cal = resp.getCalendars().get(CALENDAR_ID);
            if (cal != null && cal.getBusy() != null) busy = cal.getBusy();
        } catch (Exception e) {
            log.error("FreeBusy query failed for date={}: {}", date, e.getMessage(), e);
        }

        ZonedDateTime now = ZonedDateTime.now(zone);
        List<String> slots = new ArrayList<>();
        for (int hour = WORK_HOUR_START; hour < WORK_HOUR_END; hour++) {
            ZonedDateTime slotStart = date.atTime(hour, 0).atZone(zone);
            ZonedDateTime slotEnd = slotStart.plusHours(1);
            if (slotStart.isBefore(now.plusMinutes(30))) continue;
            if (!isBusy(busy, slotStart.toInstant(), slotEnd.toInstant())) {
                slots.add(String.format("%02d:00", hour));
            }
        }
        return slots;
    }

    private boolean isBusy(List<TimePeriod> busy, Instant slotStart, Instant slotEnd) {
        return busy.stream().anyMatch(p -> {
            Instant bs = Instant.ofEpochMilli(p.getStart().getValue());
            Instant be = Instant.ofEpochMilli(p.getEnd().getValue());
            return bs.isBefore(slotEnd) && be.isAfter(slotStart);
        });
    }

    /** Books a slot and returns the Google Calendar event ID. */
    public String bookSlot(LocalDate date, String time, String name, String email, String topic, String description) throws Exception {
        ZoneId zone = ZoneId.of(TIME_ZONE);
        int hour = Integer.parseInt(time.split(":")[0]);
        ZonedDateTime start = date.atTime(hour, 0).atZone(zone);
        ZonedDateTime end = start.plusHours(1);

        StringBuilder desc = new StringBuilder("Тема: ").append(topic).append("\nEmail гостя: ").append(email);
        if (description != null && !description.isBlank()) {
            desc.append("\n\nОписание:\n").append(description);
        }

        Event event = new Event()
                .setSummary("Встреча: " + name)
                .setDescription(desc.toString())
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(start.toInstant().toEpochMilli()))
                        .setTimeZone(TIME_ZONE))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(end.toInstant().toEpochMilli()))
                        .setTimeZone(TIME_ZONE));

        Event inserted = client.events().insert(CALENDAR_ID, event).execute();
        String eventId = inserted.getId();
        sendConfirmation(email, name, date, time, topic, description, eventId);
        return eventId;
    }

    /**
     * Deletes the event from Google Calendar and returns the guest's name.
     * Throws exception if event not found or already deleted.
     */
    public String cancelEvent(String eventId) throws Exception {
        Event event = client.events().get(CALENDAR_ID, eventId).execute();
        String guestName = parseGuestName(event.getSummary());
        client.events().delete(CALENDAR_ID, eventId).execute();
        log.info("Event {} cancelled (guest: {})", eventId, guestName);
        return guestName;
    }

    private String parseGuestName(String summary) {
        if (summary != null && summary.startsWith("Встреча: ")) {
            return summary.substring("Встреча: ".length());
        }
        return "Гость";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private void sendConfirmation(String to, String name, LocalDate date, String time,
                                   String topic, String description, String eventId) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom("inaltuaev@gmail.com", "Inal Tuaev");
            h.setTo(to);
            h.setSubject("Встреча с Inal Tuaev · " + formatDate(date) + " " + time);
            h.setText(buildHtml(name, date, time, topic, description, eventId), true);

            // ICS attachment for Apple Calendar / Outlook
            int hour = Integer.parseInt(time.split(":")[0]);
            byte[] ics = buildIcs(date, hour, topic).getBytes(StandardCharsets.UTF_8);
            h.addAttachment("meeting.ics", () -> new java.io.ByteArrayInputStream(ics), "text/calendar");

            mailSender.send(msg);
            log.info("Confirmation sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send confirmation to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildIcs(LocalDate date, int hour, String topic) {
        ZoneId zone = ZoneId.of(TIME_ZONE);
        ZonedDateTime start = date.atTime(hour, 0).atZone(zone);
        ZonedDateTime end = start.plusHours(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        String uid = "resume-meeting-" + date + "T" + hour + "@inaltuaev.ru";
        return "BEGIN:VCALENDAR\r\n" +
               "VERSION:2.0\r\n" +
               "PRODID:-//Inal Tuaev//Resume//RU\r\n" +
               "METHOD:REQUEST\r\n" +
               "BEGIN:VEVENT\r\n" +
               "UID:" + uid + "\r\n" +
               "DTSTART;TZID=" + TIME_ZONE + ":" + start.format(fmt) + "\r\n" +
               "DTEND;TZID=" + TIME_ZONE + ":" + end.format(fmt) + "\r\n" +
               "SUMMARY:Встреча с Inal Tuaev\r\n" +
               "DESCRIPTION:Тема: " + topic.replace("\n", "\\n") + "\r\n" +
               "ORGANIZER:mailto:inaltuaev@gmail.com\r\n" +
               "END:VEVENT\r\n" +
               "END:VCALENDAR\r\n";
    }

    private String buildGoogleCalendarUrl(LocalDate date, int hour, String topic) {
        ZoneId zone = ZoneId.of(TIME_ZONE);
        ZonedDateTime start = date.atTime(hour, 0).atZone(zone);
        ZonedDateTime end = start.plusHours(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String startUtc = start.withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        String endUtc = end.withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        return "https://calendar.google.com/calendar/render?action=TEMPLATE"
                + "&text=" + URLEncoder.encode("Встреча с Inal Tuaev", StandardCharsets.UTF_8)
                + "&dates=" + startUtc + "%2F" + endUtc
                + "&details=" + URLEncoder.encode("Тема: " + topic, StandardCharsets.UTF_8);
    }

    private String buildHtml(String name, LocalDate date, String time, String topic,
                              String description, String eventId) {
        String[] MONTHS = {"янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек"};
        String dateStr = date.getDayOfMonth() + " " + MONTHS[date.getMonthValue() - 1] + " " + date.getYear();
        int hour = Integer.parseInt(time.split(":")[0]);
        String googleCalUrl = buildGoogleCalendarUrl(date, hour, topic);
        String cancelUrl = baseUrl + "/api/calendar/cancel/" + eventId;

        String descBlock = "";
        if (description != null && !description.isBlank()) {
            descBlock = "<tr><td style=\"padding:6px 0;\">"
                + "<span style=\"font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;\">ОПИСАНИЕ</span><br>"
                + "<span style=\"font-size:14px;color:#f5f5f5;white-space:pre-wrap;\">" + escapeHtml(description) + "</span>"
                + "</td></tr>";
        }

        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#0d0d0d;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0d0d0d;padding:40px 0;">
                    <tr><td align="center">
                      <table width="520" cellpadding="0" cellspacing="0" style="background:#161616;border:1px solid #2a2a2a;border-radius:14px;overflow:hidden;">
                        <tr>
                          <td style="background:#1a1a1a;padding:24px 32px;border-bottom:1px solid #2a2a2a;">
                            <span style="font-family:'Helvetica Neue',sans-serif;font-size:13px;letter-spacing:.18em;text-transform:uppercase;color:#6ee7b7;">// CALENDAR · ВСТРЕЧА ЗАБРОНИРОВАНА</span>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:32px;">
                            <p style="margin:0 0 24px;font-size:22px;font-weight:700;color:#f5f5f5;">Привет, %s!</p>
                            <p style="margin:0 0 24px;font-size:15px;color:#a0a0a0;line-height:1.6;">Встреча с <strong style="color:#f5f5f5;">Inal Tuaev</strong> подтверждена.</p>
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0d0d0d;border-radius:10px;padding:20px;margin-bottom:24px;">
                              <tr><td style="padding:6px 0;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">ДАТА</span><br>
                                <span style="font-size:18px;font-weight:600;color:#f5f5f5;">%s</span>
                              </td></tr>
                              <tr><td style="padding:6px 0;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">ВРЕМЯ</span><br>
                                <span style="font-size:18px;font-weight:600;color:#f5f5f5;">%s (МСК)</span>
                              </td></tr>
                              <tr><td style="padding:6px 0;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">ТЕМА</span><br>
                                <span style="font-size:15px;color:#f5f5f5;">%s</span>
                              </td></tr>
                              %s
                            </table>
                            <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                              <tr>
                                <td style="padding-right:8px;">
                                  <a href="%s" target="_blank"
                                     style="display:block;text-align:center;padding:12px 20px;background:#6ee7b7;color:#0d0d0d;font-size:14px;font-weight:700;text-decoration:none;border-radius:8px;letter-spacing:.06em;">
                                    Добавить в календарь
                                  </a>
                                </td>
                                <td style="padding-left:8px;">
                                  <a href="%s"
                                     style="display:block;text-align:center;padding:12px 20px;background:#1a1a1a;color:#a0a0a0;font-size:14px;font-weight:700;text-decoration:none;border-radius:8px;border:1px solid #3a3a3a;letter-spacing:.06em;">
                                    Отменить встречу
                                  </a>
                                </td>
                              </tr>
                            </table>
                            <p style="margin:0 0 4px;font-size:13px;color:#555;line-height:1.6;">Файл <em>meeting.ics</em> во вложении — откройте его, чтобы добавить встречу в Apple Calendar или Outlook.</p>
                            <p style="margin:0;font-size:13px;color:#a0a0a0;line-height:1.6;">Inal свяжется с вами перед встречей.<br>Вопросы — <a href="mailto:inal4353@mail.ru" style="color:#6ee7b7;text-decoration:none;">inal4353@mail.ru</a></p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:16px 32px;border-top:1px solid #2a2a2a;">
                            <span style="font-size:12px;color:#555;">inal.tuaev.ru · Inal Tuaev · Backend Architect</span>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(escapeHtml(name), dateStr, time, escapeHtml(topic), descBlock,
                              googleCalUrl, escapeHtml(cancelUrl));
    }

    private String formatDate(LocalDate date) {
        String[] MONTHS = {"янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек"};
        return date.getDayOfMonth() + " " + MONTHS[date.getMonthValue() - 1] + " " + date.getYear();
    }
}
