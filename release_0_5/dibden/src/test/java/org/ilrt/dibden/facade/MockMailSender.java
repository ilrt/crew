package org.ilrt.dibden.facade;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class MockMailSender implements MailSender {

    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        // do nothing
    }

    public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {
        // do nothing
    }
}
