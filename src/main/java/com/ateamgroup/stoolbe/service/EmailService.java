package com.ateamgroup.stoolbe.service;


import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


import static com.ateamgroup.stoolbe.constant.EmailConstant.*;

@Service
public class EmailService {
    private Session getMailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST , GMAIL_SMTP_SERVER) ;
        properties.put(SMTP_AUTH , true) ;
        properties.put(SMTP_PORT , DEFAULT_PORT) ;
        properties.put(SMTP_STARTTLS_ENABLE , true) ;
        properties.put(SMTP_STARTTLS_REQUIRED , true) ;
        return Session.getInstance(properties,null);
    }

    private Message creatEmail(String firstname , String password , String Email)  {
        try {
            Message message = new MimeMessage(getMailSession());
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO , InternetAddress.parse(Email , false));
            message.setRecipients(Message.RecipientType.CC , InternetAddress.parse(CC_EMAIL , false));
            message.setSubject(EMAIL_SUBJECT);
            message.setText(" Hello " + firstname + ", \n \n Your new account password is: " + password + "\n \n  The Support Team");
            message.setSentDate(new Date());
            message.saveChanges();
            return  message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null ;
    }

    public void  sendNewPasswordEmail(String firstname , String password , String Email)   {
        Message message = creatEmail(firstname,password,Email);
        try {
            SMTPTransport smtpTransport = (SMTPTransport) getMailSession().getTransport(SIMPLE_EMAIL_TRANSFER_PROTOCOL);
            smtpTransport.connect(GMAIL_SMTP_SERVER,USERNAME,PASSWORD);
            smtpTransport.sendMessage(message,message.getAllRecipients());
            smtpTransport.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
