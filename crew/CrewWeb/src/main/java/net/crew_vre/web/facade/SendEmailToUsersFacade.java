/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.crew_vre.web.facade;

import java.util.List;
import org.ilrt.dibden.domain.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSender;

/**
 * @author: Phil Cross (phil.cross@bristol.ac.uk)
 */
public interface SendEmailToUsersFacade {

    void setMailSender(MailSender mailSender);

    void setMailMessage(SimpleMailMessage mailMessage);

    void setLocalUser(User user);

    void setUserMessage(String message);

    void setUsers(List<User> users);
    
    boolean sendMessages();

}
