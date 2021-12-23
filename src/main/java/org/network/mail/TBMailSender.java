package org.network.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class TBMailSender {

    public void sendMail(String to, String toEmail, Session session, String content) {
        try {

            String from = "lawrencebower@gmail.com";

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            message.setSubject("");

            message.setText(content);

            System.out.printf("sending %s, %s, %s...", to, toEmail, content);
            // Send message
            Transport.send(message);
            System.out.println(" sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    private Session getSession(String password) {
        // Get system properties
        Properties properties = System.getProperties();

        String host = "smtp.gmail.com";

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lawrencebower@gmail.com", password);
            }

        });

        // Used to debug SMTP issues
        session.setDebug(false);
        return session;
    }

    private static void sendMailsToList(TBMailSender mailSender,
                                        String content,
                                        Session session,
                                        List<String> successEmails) {

        for (String line : successEmails) {
            String[] tokens = line.split(",");
            String name = tokens[0].trim();
            String email = tokens[1].trim();
            mailSender.sendMail(name, email, session, content);
        }
    }

    public static void main(String[] args) throws IOException {

        TBMailSender mailSender = new TBMailSender();
        String contentPath = args[0];
        String successEmailsFile = args[1];
        String errorEmailFile = args[2];
        String dateString = args[3];

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        LocalDate resultDate;
        resultDate = LocalDate.parse(dateString, format);
        int resultDayOfYear = resultDate.getDayOfYear();

        Calendar now = Calendar.getInstance();
        int dayOfYear = now.get(Calendar.DAY_OF_YEAR);

        String content = Files.readString(Path.of(contentPath), StandardCharsets.US_ASCII);
        String word = Files.readString(Path.of("/home/lb584/software/one_times/lbgmail.txt"), StandardCharsets.US_ASCII);
        Session session = mailSender.getSession(word.trim());

        if (dayOfYear == resultDayOfYear) {
            List<String> successEmails = Files.readAllLines(Paths.get(successEmailsFile));
            sendMailsToList(mailSender, content, session, successEmails);
        } else {
            List<String> errorEmails = Files.readAllLines(Paths.get(errorEmailFile));
            sendMailsToList(mailSender, "ERROR!!!" + content, session, errorEmails);
        }

        int i = 0;
    }
}