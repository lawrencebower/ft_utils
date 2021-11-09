package org.network.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    public void sendMail(String to, String toEmail, Session session, String content) {
        try {

            // Sender's email ID needs to be mentioned
//            String from = "nfscct@gmail.com";
            String from = "lawrencebower@gmail.com";

            // Assuming you are sending email from through gmails smtp

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            // Set Subject: header field
//            message.setSubject("FT pledging link");
            message.setSubject("results");

            // Now set the actual message
//            String template = "http://cfbb000385.r.cam.camfibre.uk:9877/ft_pledging_main_war_exploded/phone_client?name=%s";
            String externalLinkTemplate = "http://cfbb000385.r.cam.camfibre.uk:9877/ft_pledging-1.0/phone_client?name=%s";
            String externalLink = String.format(externalLinkTemplate, to);

//            String internalLinkTemplate = "http://192.168.68.104:8080/ft_pledging_main_war_exploded/phone_client?name=%s";
            String internalLinkTemplate = "http://192.168.68.104:8080/ft_pledging_main_war_exploded/phone_client?name=%s";
            String internalLink = String.format(internalLinkTemplate, to);

            String pledge = String.format("""
                    (If youre not in the FT demo session, ignore this mail)

                    Hi %s, this is your FT pledging link. Lawrence will explain what to do

                    %s

                     Also, here is another link, which Lawrence will explain
                     
                     %s
                     """, to, externalLink, internalLink);

            String website = String.format("""
                    
                    Hi Friends house attendee. I said Id mail a link to the new website but forgot, so here it is.
                    
                    Remember this is a mock-up, ignore the colors and pics etc. Do consider how it feels to navigate and consider the impression on someone new to the site. Comments appreciated.
                    
                    https://sites.google.com/view/network-mock-site/
                     """, to, externalLink, internalLink);

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

        MailSender mailSender = new MailSender();
        String contentPath = args[0];

//        System.out.println("password:");
//        Scanner in = new Scanner(System.in);
//        final String password = in.nextLine();

        String content = Files.readString(Path.of(contentPath), StandardCharsets.US_ASCII);
        String word = Files.readString(Path.of("/home/lb584/software/one_times/lbgmail.txt"), StandardCharsets.US_ASCII);
        Session session = mailSender.getSession(word.trim());

//        String names_file = "/home/lb584/git/ft_utils/src/main/resources/people_coming_emails.csv";
        String names_file = "/home/lb584/git/ft_utils/src/main/resources/test_emails.csv";
        List<String> results = Files.readAllLines(Paths.get(names_file));

        for (String line : results) {
            String[] tokens = line.split(",");
            String name = tokens[0].trim();
            String email = tokens[1].trim();
            mailSender.sendMail(name, email, session, content);
        }

        int i = 0;
    }

}