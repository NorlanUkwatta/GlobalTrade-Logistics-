package com.globaltrade.logistics.service;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStream;
import java.util.Properties;

@Stateless
public class NotificationServiceBean {

    private static final Logger LOG = LogManager.getLogger(NotificationServiceBean.class);
    private Properties mailProps;
    private String mailUser;
    private String mailPassword;

    @PostConstruct
    public void init() {
        mailProps = new Properties();
        Properties secrets = new Properties();
        
        String secretsPath = System.getProperty("globaltrade.secrets.file");
        boolean loaded = false;

        if (secretsPath != null) {
            try (InputStream is = new java.io.FileInputStream(secretsPath)) {
                secrets.load(is);
                loaded = true;
                LOG.info("Loaded secrets from system property path: {}", secretsPath);
            } catch (Exception e) {
                LOG.error("Failed to load secrets from system property path: " + secretsPath, e);
            }
        }

        if (!loaded) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("secrets.properties")) {
                if (is != null) {
                    secrets.load(is);
                    loaded = true;
                    LOG.info("Loaded secrets from classpath (secrets.properties)");
                }
            } catch (Exception e) {
                LOG.error("Failed to load secrets.properties from classpath", e);
            }
        }

        if (loaded) {
            mailUser = secrets.getProperty("mail.user", "system@test.com");
            mailPassword = secrets.getProperty("mail.apppassword", "");
            
            mailProps.put("mail.smtp.host", secrets.getProperty("mail.smtp.host", "smtp.gmail.com"));
            mailProps.put("mail.smtp.port", secrets.getProperty("mail.smtp.port", "587"));
            mailProps.put("mail.smtp.auth", secrets.getProperty("mail.smtp.auth", "true"));
            mailProps.put("mail.smtp.starttls.enable", secrets.getProperty("mail.smtp.starttls.enable", "true"));
        } else {
            LOG.warn("No secrets.properties found! Email will fail.");
        }
    }

    @Asynchronous
    public void sendEmail(String toAddress, String subject, String body) {
        LOG.info("[NOTIFICATION] Preparing to send email to {} with subject: {}", toAddress, subject);
        
        if (mailPassword == null || mailPassword.isEmpty() || mailPassword.equals("your-app-password-here")) {
            LOG.warn("[NOTIFICATION] Email skipped: apppassword not configured in secrets.properties.");
            return;
        }

        Session session = Session.getInstance(mailProps, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUser, mailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUser));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            LOG.info("[NOTIFICATION] Successfully sent email to {}", toAddress);
        } catch (MessagingException e) {
            LOG.error("[NOTIFICATION] Failed to send email to " + toAddress, e);
        }
    }
}