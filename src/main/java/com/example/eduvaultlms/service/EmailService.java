package com.example.eduvaultlms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String APP_NAME = "EduVault LMS";

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to " + APP_NAME;

        String body = """
            <div style="font-family: Arial, sans-serif; direction:ltr; text-align:left; color:#333; line-height:1.6;">
                <h2 style="color:#2c3e50;">Welcome, %s 👋</h2>
            
                <p>We’re excited to have you on board at <b>%s</b>.</p>
            
                <p>Your account has been created successfully, and you’re now ready to explore our learning platform.</p>
            
                <p>Browse available courses, track your progress, and start your learning journey today.</p>
            
                <hr style="border:none; border-top:1px solid #ddd; margin:20px 0;">
            
                <p style="font-size:14px; color:#777;">
                    Thank you for joining us.<br>
                    The <b>%s</b> Team
                </p>
            </div>
            """.formatted(name, APP_NAME, APP_NAME);

        send(toEmail, subject, body);
    }

    @Async
    public void sendCertificateEmail(String toEmail, String studentName,
                                     String courseTitle, String certificateUrl) {
        String subject = "🎉 Congratulations! Your Certificate for " + courseTitle + " is Ready";

        String body = """
            <div style="font-family: Arial, sans-serif; direction:ltr; text-align:left; color:#333; line-height:1.6;">
                <h2 style="color:#2c3e50;">Congratulations, %s! 🎓</h2>

                <p>You have successfully completed the course <b>%s</b> with <b>100%% progress</b>.</p>

                <p>This is a significant achievement, and we’re proud to be part of your learning journey.</p>

                <p>Your certificate is now ready and can be downloaded using the button below:</p>

                <div style="margin: 25px 0;">
                    <a href="%s"
                       style="
                           background-color:#2563eb;
                           color:white;
                           padding:12px 24px;
                           text-decoration:none;
                           border-radius:8px;
                           font-weight:bold;
                           display:inline-block;">
                        Download Certificate (PDF)
                    </a>
                </div>

                <hr style="border:none; border-top:1px solid #ddd; margin:20px 0;">

                <p style="font-size:14px; color:#777;">
                    Keep learning and keep growing.<br>
                    The <b>%s</b> Team
                </p>
            </div>
            """.formatted(studentName, courseTitle, certificateUrl, APP_NAME);

        send(toEmail, subject, body);
    }

    @Async
    public void sendQuizResultEmail(String toEmail, String studentName,
                                    String quizTitle, int score, boolean isPassed) {

        String subject = isPassed
                ? "✅ Congratulations! You Passed " + quizTitle
                : "Quiz Result for " + quizTitle;

        String resultTitle = isPassed
                ? "Congratulations, " + studentName + "! 🎉"
                : "Hello, " + studentName;

        String resultMessage = isPassed
                ? "You successfully passed the quiz. Great job!"
                : "Unfortunately, you did not reach the passing score this time.";

        String extraMessage = isPassed
                ? "Keep up the excellent work and continue your learning journey."
                : "Review the course material and try again. Improvement comes with practice.";

        String body = """
            <div style="font-family: Arial, sans-serif; direction:ltr; text-align:left; color:#333; line-height:1.6;">
                <h2 style="color:#2c3e50;">%s</h2>

                <p>%s</p>

                <p>Your score in <b>%s</b>: <b>%d</b></p>

                <p>%s</p>

                <hr style="border:none; border-top:1px solid #ddd; margin:20px 0;">

                <p style="font-size:14px; color:#777;">
                    The <b>%s</b> Team
                </p>
            </div>
            """.formatted(
                resultTitle,
                resultMessage,
                quizTitle,
                score,
                extraMessage,
                APP_NAME
        );

        send(toEmail, subject, body);
    }


    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} | subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}