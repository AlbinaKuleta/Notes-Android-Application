package com.example.aplikacionandroid;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * JavaMailAPI is an asynchronous task for sending emails in Android applications.
 * It uses the JavaMail API to handle email transmission securely over SMTP.
 */
public class JavaMailAPI extends AsyncTask<Void, Void, Void> {
    private final String email;
    private final String subject;
    private final String message;
    private final Context context;

    /**
     * Constructor to initialize the email details and context.
     *
     * @param context The application context.
     * @param email   The recipient's email address.
     * @param subject The subject of the email.
     * @param message The content of the email.
     */
    public JavaMailAPI(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    /**
     * Handles the email sending operation in the background.
     * Configures SMTP properties, authenticates using credentials, and sends the email.
     *
     * @param params Optional parameters (not used).
     * @return null Always returns null.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Properties credentials = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("config.properties");
            credentials.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String email = credentials.getProperty("EMAIL");
        String password = credentials.getProperty("PASSWORD");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
