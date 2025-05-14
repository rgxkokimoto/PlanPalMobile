package com.example.planpalmobile;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private final String destinatario;
    private final String asunto;
    private final String mensaje;

    public EmailSender(String destinatario, String asunto, String mensaje) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }

    public void enviar() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            final String remitente = "PlanPalSoport@gmail.com"; // <-- tu correo
            final String clave = "yejpfmbslnlrfzyf";       // <-- contraseña de aplicación

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remitente, clave);
                }
            });

            try {
                Message messageObj = new MimeMessage(session);
                messageObj.setFrom(new InternetAddress(remitente));
                messageObj.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                messageObj.setSubject(asunto);
                messageObj.setText(mensaje);

                Transport.send(messageObj);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }
}