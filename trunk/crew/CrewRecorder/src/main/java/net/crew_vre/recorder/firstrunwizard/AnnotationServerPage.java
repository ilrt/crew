/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.crew_vre.recorder.firstrunwizard;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AnnotationServerPage extends WizardPage {

    private static final long serialVersionUID = 1L;

    /**
     * The key for the port number
     */
    public static final String PORT_KEY = "port";

    private static final int PORT_WIDTH = 10;

    private static final int PORT_HEIGHT = 26;

    // The vertical spacing
    private static final int VERTICAL_SPACE = 5;

    private JTextField port = new JTextField("80", PORT_WIDTH);

    /**
     * Creates a new AnnotationServerPage
     */
    public AnnotationServerPage() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel prompt = new JLabel(
                "Select the port to run the Annotation Server on:");
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        port.setName(PORT_KEY);
        port.setMaximumSize(new Dimension(Integer.MAX_VALUE, PORT_HEIGHT));
        add(prompt);
        add(Box.createVerticalStrut(VERTICAL_SPACE));
        add(port);
    }

    /**
     * Gets the description of the page
     * @return The description
     */
    public static final String getDescription() {
        return "Annotation Server";
    }


    /**
     *
     * @see org.netbeans.spi.wizard.WizardPage#allowNext(java.lang.String,
     *     java.util.Map, org.netbeans.spi.wizard.Wizard)
     */
    public WizardPanelNavResult allowNext(String stepName, Map settings,
            Wizard wizard) {
        String portString = port.getText();
        if (!portString.matches("\\d{1,5}")) {
            JOptionPane.showMessageDialog(this,
                    "The port must be a number between 1 and 65535",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }

        int portVal = Integer.parseInt(portString);
        if ((portVal < 1) || (portVal > 65535)) {
            JOptionPane.showMessageDialog(this,
                    "The port must be a number between 1 and 65535",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }

        if (portVal < 1025) {
            if (!System.getProperty("os.name").toLowerCase().startsWith(
                    "windows")) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Warning: Ports below 1025 are not likely to work on\n"
                        + " non-windows operating systems unless you have root"
                        + " privileges.  Do you want to continue?",
                        "Use Admin Port?", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return WizardPanelNavResult.REMAIN_ON_PAGE;
                }
            }
        }

        return WizardPanelNavResult.PROCEED;
    }

    /**
     *
     * @see org.netbeans.spi.wizard.WizardPage#allowFinish(java.lang.String,
     *     java.util.Map, org.netbeans.spi.wizard.Wizard)
     */
    public WizardPanelNavResult allowFinish(String stepName, Map settings,
            Wizard wizard) {
        return allowNext(stepName, settings, wizard);
    }

}
