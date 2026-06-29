package dev.inal.resume.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private static final String RECIPIENT = "inal4353@mail.ru";

    private final JavaMailSender mailSender;

    public ContactService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String name, String contact, String subject, String message) throws Exception {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
        h.setFrom("inaltuaev@gmail.com", "Inal Tuaev Resume");
        h.setTo(RECIPIENT);
        h.setSubject("[Резюме] " + subject + " — от " + name);
        h.setText(buildHtml(name, contact, subject, message), true);
        mailSender.send(msg);
        log.info("Contact message sent from '{}' (contact: {})", name, contact);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("\n", "<br>");
    }

    private String buildHtml(String name, String contact, String subject, String message) {
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
                            <span style="font-family:'Helvetica Neue',sans-serif;font-size:13px;letter-spacing:.18em;text-transform:uppercase;color:#6ee7b7;">// MESSAGE · НОВОЕ СООБЩЕНИЕ С РЕЗЮМЕ</span>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:32px;">
                            <p style="margin:0 0 24px;font-size:22px;font-weight:700;color:#f5f5f5;">Входящее сообщение</p>
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0d0d0d;border-radius:10px;padding:20px;margin-bottom:24px;">
                              <tr><td style="padding:8px 0;border-bottom:1px solid #2a2a2a;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">ОТ КОГО</span><br>
                                <span style="font-size:16px;font-weight:600;color:#f5f5f5;">%s</span>
                              </td></tr>
                              <tr><td style="padding:8px 0;border-bottom:1px solid #2a2a2a;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">КОНТАКТ ДЛЯ ОТВЕТА</span><br>
                                <span style="font-size:15px;color:#f5f5f5;">%s</span>
                              </td></tr>
                              <tr><td style="padding:8px 0;border-bottom:1px solid #2a2a2a;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">ТЕМА</span><br>
                                <span style="font-size:15px;color:#f5f5f5;">%s</span>
                              </td></tr>
                              <tr><td style="padding:8px 0;">
                                <span style="font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#6ee7b7;">СООБЩЕНИЕ</span><br>
                                <span style="font-size:14px;color:#f5f5f5;line-height:1.7;white-space:pre-wrap;">%s</span>
                              </td></tr>
                            </table>
                            <p style="margin:0;font-size:13px;color:#555;line-height:1.6;">Сообщение отправлено через форму на сайте-резюме inal.tuaev.ru</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:16px 32px;border-top:1px solid #2a2a2a;">
                            <span style="font-size:12px;color:#555;">inal.tuaev.ru · Inal Tuaev · Backend Developer</span>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(escapeHtml(name), escapeHtml(contact), escapeHtml(subject), escapeHtml(message));
    }
}
