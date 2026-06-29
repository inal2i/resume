package dev.inal.resume.tools;

import dev.inal.resume.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class EmailTool {

    private static final Logger log = LoggerFactory.getLogger(EmailTool.class);

    private final ContactService contactService;

    public EmailTool(ContactService contactService) {
        this.contactService = contactService;
    }

    @Tool(description = """
            Отправить email Иналу Туаеву от посетителя сайта-резюме.
            Вызывай этот инструмент только когда у тебя есть все четыре параметра: \
            имя посетителя, его контакт для ответа (email, Telegram или телефон), \
            тема письма и текст сообщения. \
            Если какого-то параметра нет — сначала уточни его у посетителя.
            """)
    public String sendEmailToInal(String name, String contact, String subject, String message) {
        try {
            contactService.send(name, contact, subject, message);
            log.info("Email sent via tool call: from='{}' contact='{}'", name, contact);
            return "Email успешно отправлен Иналу. Он получит его на inal4353@mail.ru и свяжется с вами.";
        } catch (Exception e) {
            log.error("Tool failed to send email: {}", e.getMessage(), e);
            return "Не удалось отправить email: " + e.getMessage() + ". Попробуйте написать напрямую: inal4353@mail.ru или Telegram @inal2i";
        }
    }
}
