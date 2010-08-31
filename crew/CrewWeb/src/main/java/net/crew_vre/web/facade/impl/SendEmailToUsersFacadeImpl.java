/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.crew_vre.web.facade.impl;

import java.util.ArrayList;
import java.util.List;
import net.crew_vre.web.facade.SendEmailToUsersFacade;
import org.ilrt.dibden.domain.User;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.StringUtils;

import org.apache.log4j.Logger;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class SendEmailToUsersFacadeImpl implements SendEmailToUsersFacade {

    private MailSender mailSender;
    private SimpleMailMessage mailMessage;
    private String userMessage;
    private User localUser;
    private ArrayList<User> users;

    private Logger logger = Logger.getLogger("net.crew_vre.web.facade.impl.SendEmailToUsersFacadeImpl");

    public SendEmailToUsersFacadeImpl() {}

    // Injected properties
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMailMessage(SimpleMailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }


    // Locally set properties
    public void setLocalUser(User localUser) {
        this.localUser = localUser;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setUsers(List<User> users) {
        this.users = (ArrayList)users;
    }

    public boolean sendMessages() {

        if (mailSender == null || mailMessage == null || localUser == null
                || localUser.getEmail() == null || users == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error sending message due to insufficient data:");
                if (mailSender == null) logger.debug("mailSender object is null");
                if (mailMessage == null) logger.debug("mailMessage object is null");
                if (localUser == null) logger.debug("localUser object is null");
                if (localUser.getEmail() == null) logger.debug("localuser has not email address");
                if (users == null) logger.debug("There are no users specified");
            }
            return false;
        }


        SimpleMailMessage smm = new SimpleMailMessage(mailMessage);
        
        // Set 'reply to' to local user
        smm.setReplyTo(localUser.getEmail());

        String commonText = smm.getText();

        // Fill in blanks
        commonText = StringUtils.replace(commonText, "%LOCALUSERNAME%", localUser.getName());
        commonText = StringUtils.replace(commonText, "%LOCALUSEREMAIL%", localUser.getEmail());
        if (userMessage == null) userMessage = "";
        commonText = StringUtils.replace(commonText, "%USERMESSAGE%", userMessage);

        // Send to each recipient
        boolean sentMail = false;

        String messageText = null;
        for (User user : users) {
            if ( user.getEmail() == null ) {
                continue;
            }
            smm.setTo(user.getEmail()); // ASSUMING THIS DELETE PREVIOUS?
            messageText = StringUtils.replace(commonText, "%RECIPIENTUSERNAME%", user.getName());
            smm.setText(messageText);
            try {
                mailSender.send(smm);
                sentMail = true;
            } catch (MailException me) {
                logger.warn("Failed to send email to: " + user.getEmail()
                        + " from: " + localUser.getEmail() + ". Error: " + me.getMessage());
            }
        }
        if (logger.isDebugEnabled()) {
            if (sentMail == false) {
                logger.debug("No emails were successfully sent");
            }
        }

        return sentMail;
    }

}
