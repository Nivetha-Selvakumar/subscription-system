package com.subscription.subscription_system.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String message) {

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(message);

            mailSender.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendExpiryWarningEmail(String email, String name, LocalDate expiryDate, long daysLeft) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String subject = "Your Subscription Expires in " + daysLeft + " Day" + (daysLeft > 1 ? "s!" : "!");
        helper.setTo(email);
        helper.setSubject(subject);

        // Plain-text fallback
        String plainText = String.format(
                "Hi %s,\n\nThis is a reminder that your subscription will expire in %d day%s on %s.\nPlease renew your plan to continue enjoying uninterrupted service.\n\nRegards,\nSubscription Management System",
                name,
                daysLeft,
                (daysLeft > 1 ? "s" : ""),
                expiryDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        );


        String expiryPretty = expiryDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String daysText = daysLeft + (daysLeft > 1 ? " days" : " day");

        String html = "<!doctype html>"
                + "<html lang='en'>"
                + "<head>"
                + "  <meta charset='utf-8' />"
                + "  <meta name='viewport' content='width=device-width, initial-scale=1'/>"
                + "  <style>"
                + "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial; margin:0; padding:0; background:#f3f6f8; }"
                + "    .container { max-width:600px; margin:24px auto; background:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 8px 30px rgba(14,30,37,0.08); }"
                + "    .header { background:#034078; color:#fff; padding:20px 24px; }"
                + "    .header h1 { margin:0; font-size:18px; }"
                + "    .content { padding:24px; color:#0f172a; }"
                + "    .lead { font-size:16px; margin-bottom:12px; }"
                + "    .muted { color:#6b7280; font-size:14px; margin-bottom:20px; }"
                + "    .panel { background:#f8fafc; border-radius:8px; padding:14px; margin-bottom:20px; }"
                + "    .panel .days { font-weight:700; color:#0b3d52; }"
                + "    .cta { display:block; width:100%; text-align:center; text-decoration:none; background:#034078; color:#fff; padding:12px 18px; border-radius:8px; font-weight:700; }"
                + "    .small { font-size:12px; color:#94a3b8; margin-top:18px; }"
                + "    .footer { padding:18px 24px; text-align:center; font-size:12px; color:#9aa4ad; }"
                + "    @media (max-width:500px){ .container{ margin:12px; } .header h1{ font-size:16px } }"
                + "  </style>"
                + "</head>"
                + "<body>"
                + "  <div class='container'>"
                + "    <div class='header'>"
                + "      <h1>Subscription Management System</h1>"
                + "    </div>"
                + "    <div class='content'>"
                + "      <p class='lead'>Hi " + escapeHtml(name) + ",</p>"
                + "      <p class='muted'>Just a friendly reminder — your subscription is about to expire.</p>"
                + "      <div class='panel'>"
                + "        <p style='margin:0;'>Expires in <span class='days'>" + daysText + "</span></p>"
                + "        <p style='margin:6px 0 0 0; color:#475569;'>Expiry date: <strong>" + expiryPretty + "</strong></p>"
                + "      </div>"
                + "      <p style='margin:0 0 16px 0;'>Please renew your plan to continue enjoying uninterrupted access to all features.</p>"
                + "      <a href='https://localhost:3000' class='cta' target='_blank' rel='noopener'>Renew Now</a>"
                + "      <p class='small'>If you've already renewed, please ignore this message.</p>"
                + "    </div>"
                + "    <div class='footer'>"
                + "      © " + java.time.Year.now() + " Subscription Management System — Need help? contact <a href='mailto:support@subsystem.com'>support@subsystem.com</a>"
                + "    </div>"
                + "  </div>"
                + "</body>"
                + "</html>";

        // Set both plain and html (plain as first param, html as second)
        helper.setText(plainText, html);

        // OPTIONAL: Attach a file or logo (uncomment & set path if needed)
        // FileSystemResource logo = new FileSystemResource(new File("/mnt/data/your-logo.png"));
        // helper.addInline("logo", logo);  // then reference with <img src='cid:logo'/> in html
        // helper.addAttachment("terms.pdf", new File("/path/to/terms.pdf"));

        mailSender.send(mimeMessage);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }

}
