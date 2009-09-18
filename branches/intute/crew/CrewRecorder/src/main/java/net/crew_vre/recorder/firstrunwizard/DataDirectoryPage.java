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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.crew_vre.recorder.Recorder;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 * The data directory selection page
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DataDirectoryPage extends WizardPage implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The key for the directory
     */
    public static final String DIRECTORY_KEY = "directory";

    // The vertical spacing
    private static final int VERTICAL_SPACE = 5;

    // The number of columns in the directory selection box
    private static final int DIRECTORY_WIDTH = 40;

    // The height in pixels of the directory selection box
    private static final int DIRECTORY_HEIGHT = 26;

    // The text on the browse button
    private static final String BROWSE_BUTTON_TEXT = "Browse...";

    // The location to store the data
    private JTextField directory = null;

    /**
     * Creates a new DataDirectoryPage
     */
    public DataDirectoryPage() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel prompt = new JLabel(
                "Select the directory where data will be stored:");
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(prompt);
        add(Box.createVerticalStrut(VERTICAL_SPACE));

        JPanel directorySelection = new JPanel();
        directorySelection.setLayout(new BoxLayout(directorySelection,
                BoxLayout.X_AXIS));
        directorySelection.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                DIRECTORY_HEIGHT));
        directorySelection.setAlignmentX(Component.LEFT_ALIGNMENT);

        directory = new JTextField(DIRECTORY_WIDTH);
        directory.setName(DIRECTORY_KEY);
        directorySelection.add(directory);

        JButton browse = new JButton(BROWSE_BUTTON_TEXT);
        browse.addActionListener(this);
        directorySelection.add(Box.createHorizontalGlue());
        directorySelection.add(browse);

        add(directorySelection);
    }

    /**
     *
     * @see org.netbeans.spi.wizard.WizardPage#renderingPage()
     */
    public void renderingPage() {
        if (directory.getText().equals("")) {
            directory.setText((String) getWizardData(
                Recorder.CONFIG_DATA_DIRECTORY));
        }
    }

    /**
     * Gets the description of the page
     * @return The description
     */
    public static final String getDescription() {
        return "Data Directory";
    }

    /**
     *
     * @see org.netbeans.spi.wizard.WizardPage#allowNext(java.lang.String,
     *     java.util.Map, org.netbeans.spi.wizard.Wizard)
     */
    public WizardPanelNavResult allowNext(String stepName, Map settings,
            Wizard wizard) {
        File dir = new File(directory.getText());
        if (!dir.exists()) {
            int result = JOptionPane.showConfirmDialog(this,
              "The specified directory does not exist.  Should it be created?",
              "Create Directory?", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                if (dir.mkdirs()) {
                    return WizardPanelNavResult.PROCEED;
                }
                JOptionPane.showMessageDialog(this,
                    "There was an error creating the directory.  "
                    + "Please select a different directory.",
                    "Error creating Data Directory",
                    JOptionPane.ERROR_MESSAGE);
                return WizardPanelNavResult.REMAIN_ON_PAGE;
            }
            return WizardPanelNavResult.REMAIN_ON_PAGE;
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

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BROWSE_BUTTON_TEXT)) {

            // Ask the user where they would like to install
            JFileChooser chooser = new JFileChooser();
            File startLoc = new File(directory.getText());
            if (!startLoc.exists()) {
                startLoc = startLoc.getParentFile();
            }
            chooser.setCurrentDirectory(startLoc);
            chooser.setDialogTitle("Select Installation Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                directory.setText(
                        chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
