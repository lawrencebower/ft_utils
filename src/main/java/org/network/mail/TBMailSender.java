package org.network.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            System.out.printf("sending %s, %s, ...", to, toEmail);
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

    public static void main(String[] args) throws IOException {

        TBMailSender mailSender = new TBMailSender();
        String contentPath = args[0];
        String namesFile = args[1];

//        System.out.println("password:");
//        Scanner in = new Scanner(System.in);
//        final String password = in.nextLine();

        String content = Files.readString(Path.of(contentPath), StandardCharsets.US_ASCII);
        String word = Files.readString(Path.of("/home/lb584/software/one_times/lbgmail.txt"), StandardCharsets.US_ASCII);
        Session session = mailSender.getSession(word.trim());

//        String names_file = "/home/lb584/git/ft_utils/src/main/resources/people_coming_emails.csv";
//        String names_file = "/home/lb584/git/ft_utils/src/main/resources/test_tb_emails.csv";
        List<String> results = Files.readAllLines(Paths.get(namesFile));

        for (String line : results) {
            String[] tokens = line.split(",");
            String name = tokens[0].trim();
            String email = tokens[1].trim();
            mailSender.sendMail(name, email, session, content);
        }

        int i = 0;
    }

}