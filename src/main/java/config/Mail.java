
package config;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Mail {
    Dotenv dotenv = Dotenv.load();

    public String sendEmail(String reciever, String subject, String message) {
        
        final String recvr = reciever;
        final String emailsubject = subject;
        final String msg = message;
        //Set the session of email
        final Session newsession = Session.getInstance(this.props(), new Authenticator() {
            @Override
            //password authenication
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(dotenv.get("SMTP_SENDER_NAME"), dotenv.get("SMTP_SENDER_PASSWORD"));
            }
        });
        //MimeMessage to take user input
        try {
            final Message form = new MimeMessage(newsession);
            form.setRecipient(Message.RecipientType.TO, new InternetAddress(recvr));
            form.setFrom(new InternetAddress(dotenv.get("SMTP_SENDER_NAME")));
            form.setSubject(emailsubject); //Takes email subject
            form.setText(msg); // The main message of email
            form.setSentDate(new Date()); //You can set the date of the email here
            Transport.send(form);// Transport the email
            return "Success!";
        } catch (final MessagingException ex) { //exception to catch the errors
            System.out.println("Could not send " + ex); //failed
            if(ex.toString().startsWith("com.sun.mail.util.MailConnectException"))
                return "connection error";
            else
                return "error";
        }
    }

    //The permenant set of prperties containg string keys, the following configuration enables the SMPTs to function
    private Properties props() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", dotenv.get("SMTP_SENDER_HOST"));
        config.put("mail.smtp.port", Integer.parseInt(dotenv.get("SMTP_SENDER_PORT")));
        return config;
    }

    public static void main(final String[] args) {
        new Mail().sendEmail("envanterdogrulama@outlook.com", "Test", "This message was sent for testing.");
    }
}
