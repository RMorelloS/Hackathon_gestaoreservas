package com.fiap.postech.gestaoreservas.service;


import com.fiap.postech.gestaoreservas.model.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.time.LocalDate;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    public Environment env;

    public void sendMail(String destinatario, LocalDate dataInicio, LocalDate dataFim) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(env.getProperty("email.usuario").toString(), env.getProperty("email.senha").toString());
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(env.getProperty("email.usuario")));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            email.setSubject("Reserva confirmada com sucesso");
            email.setText("Reserva confirmada com sucesso entre as datas " + dataInicio + " " + dataFim);

            Transport.send(email);

        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }
}
