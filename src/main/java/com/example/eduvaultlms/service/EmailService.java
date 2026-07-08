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
        String subject = "🎉  Welcome to " + APP_NAME;
        String body = """
        <div style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, sans-serif;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8; padding:30px 0;">
                <tr>
                    <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.06);">
                            
                            <!-- Header -->
                            <tr>
                                <td style="background-color:#2c3e50; padding:28px 40px; text-align:center;">
                                    <h1 style="margin:0; color:#ffffff; font-size:22px; letter-spacing:0.5px;">%s</h1>
                                </td>
                            </tr>

                            <!-- Body -->
                            <tr>
                                <td style="padding:36px 40px; color:#333333; line-height:1.7; text-align:left; direction:ltr;">
                                    <h2 style="color:#2c3e50; margin-top:0; font-size:20px;">Welcome, %s 👋</h2>

                                    <p style="font-size:15px; margin:16px 0;">
                                        We're excited to have you on board at <b>%s</b>.
                                    </p>

                                    <p style="font-size:15px; margin:16px 0;">
                                        Your account has been created successfully, and you're now ready to explore our learning platform.
                                    </p>

                                    <p style="font-size:15px; margin:16px 0;">
                                        Browse available courses, track your progress, and start your learning journey today.
                                    </p>

                                    <!-- CTA Button -->
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="margin:28px auto;">
                                        <tr>
                                            <td align="center" style="border-radius:6px; background-color:#2c3e50;">
                                                <a href="#" style="display:inline-block; padding:12px 28px; color:#ffffff; text-decoration:none; font-size:15px; font-weight:bold; border-radius:6px;">
                                                    Get Started
                                                </a>
                                            </td>
                                        </tr>
                                    </table>

                                    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;">

                                    <p style="font-size:13px; color:#888888; margin:0;">
                                        Thank you for joining us.<br>
                                        The <b>%s</b> Team
                                    </p>
                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="background-color:#f0f2f5; padding:18px 40px; text-align:center;">
                                    <p style="font-size:12px; color:#999999; margin:0;">
                                        © %s %s. All rights reserved.
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </div>
        """.formatted(APP_NAME, name, APP_NAME, APP_NAME, java.time.Year.now().getValue(), APP_NAME);
        send(toEmail, subject, body);
    }

    @Async
    public void sendCertificateEmail(String toEmail, String studentName,
                                     String courseTitle, String certificateUrl) {
        String subject = "🎉 Congratulations! Your Certificate for " + courseTitle + " is Ready";
        String body = """
        <div style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, sans-serif;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8; padding:30px 0;">
                <tr>
                    <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.06);">

                            <!-- Header -->
                            <tr>
                                <td style="background-color:#2563eb; padding:32px 40px; text-align:center;">
                                    <div style="font-size:40px; margin-bottom:8px;">🎓</div>
                                    <h1 style="margin:0; color:#ffffff; font-size:22px;">Certificate Ready!</h1>
                                </td>
                            </tr>

                            <!-- Body -->
                            <tr>
                                <td style="padding:36px 40px; color:#333333; line-height:1.7; text-align:left; direction:ltr;">
                                    <h2 style="color:#2c3e50; margin-top:0; font-size:20px;">Congratulations, %s! 🎉</h2>

                                    <p style="font-size:15px; margin:16px 0;">
                                        You have successfully completed the course <b>%s</b> with <b>100%% progress</b>.
                                    </p>

                                    <p style="font-size:15px; margin:16px 0;">
                                        This is a significant achievement, and we're proud to be part of your learning journey.
                                    </p>

                                    <p style="font-size:15px; margin:16px 0;">
                                        Your certificate is now ready and can be downloaded using the button below:
                                    </p>

                                    <!-- CTA Button -->
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="margin:28px auto;">
                                        <tr>
                                            <td align="center" style="border-radius:8px; background-color:#2563eb;">
                                                <a href="%s" style="display:inline-block; padding:14px 30px; color:#ffffff; text-decoration:none; font-size:15px; font-weight:bold; border-radius:8px;">
                                                    📄 Download Certificate (PDF)
                                                </a>
                                            </td>
                                        </tr>
                                    </table>

                                    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;">

                                    <p style="font-size:13px; color:#888888; margin:0;">
                                        Keep learning and keep growing.<br>
                                        The <b>%s</b> Team
                                    </p>
                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="background-color:#f0f2f5; padding:18px 40px; text-align:center;">
                                    <p style="font-size:12px; color:#999999; margin:0;">
                                        © %s %s. All rights reserved.
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </div>
        """.formatted(studentName, courseTitle, certificateUrl, APP_NAME,
                java.time.Year.now().getValue(), APP_NAME);
        send(toEmail, subject, body);
    }

    @Async
    public void sendQuizResultEmail(String toEmail, String studentName,
                                    String quizTitle, int score, boolean isPassed) {
        String subject = isPassed
                ? "✅ Congratulations! You Passed " + quizTitle
                : "Quiz Result for " + quizTitle;

        String headerColor = isPassed ? "#16a34a" : "#f59e0b";
        String headerIcon = isPassed ? "🎉" : "📊";
        String headerTitle = isPassed ? "Quiz Passed!" : "Quiz Result";

        String resultTitle = isPassed
                ? "Congratulations, " + studentName + "! 🎉"
                : "Hello, " + studentName;
        String resultMessage = isPassed
                ? "You successfully passed the quiz. Great job!"
                : "Unfortunately, you did not reach the passing score this time.";
        String extraMessage = isPassed
                ? "Keep up the excellent work and continue your learning journey."
                : "Review the course material and try again. Improvement comes with practice.";
        String scoreBadgeColor = isPassed ? "#16a34a" : "#f59e0b";

        String body = """
        <div style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, sans-serif;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8; padding:30px 0;">
                <tr>
                    <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.06);">

                            <!-- Header -->
                            <tr>
                                <td style="background-color:%s; padding:32px 40px; text-align:center;">
                                    <div style="font-size:40px; margin-bottom:8px;">%s</div>
                                    <h1 style="margin:0; color:#ffffff; font-size:22px;">%s</h1>
                                </td>
                            </tr>

                            <!-- Body -->
                            <tr>
                                <td style="padding:36px 40px; color:#333333; line-height:1.7; text-align:left; direction:ltr;">
                                    <h2 style="color:#2c3e50; margin-top:0; font-size:20px;">%s</h2>

                                    <p style="font-size:15px; margin:16px 0;">%s</p>

                                    <!-- Score Badge -->
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="margin:20px 0;">
                                        <tr>
                                            <td style="background-color:%s1a; border-left:4px solid %s; padding:14px 20px; border-radius:6px;">
                                                <span style="font-size:14px; color:#555;">Quiz:</span> <b>%s</b><br>
                                                <span style="font-size:14px; color:#555;">Score:</span> <b style="font-size:18px; color:%s;">%d / 100</b>
                                            </td>
                                        </tr>
                                    </table>

                                    <p style="font-size:15px; margin:16px 0;">%s</p>

                                    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;">

                                    <p style="font-size:13px; color:#888888; margin:0;">
                                        The <b>%s</b> Team
                                    </p>
                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="background-color:#f0f2f5; padding:18px 40px; text-align:center;">
                                    <p style="font-size:12px; color:#999999; margin:0;">
                                        © %s %s. All rights reserved.
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </div>
        """.formatted(
                headerColor,
                headerIcon,
                headerTitle,
                resultTitle,
                resultMessage,
                scoreBadgeColor, scoreBadgeColor,
                quizTitle,
                scoreBadgeColor, score,
                extraMessage,
                APP_NAME,
                java.time.Year.now().getValue(), APP_NAME
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