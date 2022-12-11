/*
 *   Copyright (c) 2006 by Thanos Kyritsis
 *
 *   This file is part of Alis Recording Tool
 *
 *   Alis Recording Tool is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 2 of the License.
 *
 *   Alis Recording Tool is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Alis Recording Tool; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.alis.recording;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import net.alis.recording.AlisCommons;
import net.alis.recording.AlisProperties;

/**
 *
 * @author  djart
 */
public class AlisConfigDialog extends javax.swing.JDialog {
    private DefaultListModel modelSoundCards;
    
    public AlisConfigDialog(AlisRecording parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initLists();
        initContent();
    }
    
    /*public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlisConfigDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }*/
    
    private void initLists() {
        modelSoundCards = new DefaultListModel();
        soundCardsList.setModel(modelSoundCards);
    }
    
    private void initContent() {
        int initValue = 0;
        int min = 0;
        int max = 10;
        int step = 1;
        SpinnerModel model = new SpinnerNumberModel(initValue, min, max, step);
        soundCardSpinner.setModel(model);
        
        int cards;
        String cardsprop = AlisProperties.loadProps().getProperty(AlisCommons.cardsNumber);
        if (cardsprop == null) { 
            cards = 0; 
        }
        else { 
            cards = Integer.parseInt(cardsprop); 
        }
        soundCardSpinner.setValue(cards);
        
        cardSetupLabel.setText("Card Options");
        cardIDField.setText("");
        cardLangField.setText("");
    }
    
    private void saveCardID() {
        String selectedCard = soundCardsList.getSelectedValue().toString();
        Properties props = AlisProperties.loadProps();
        props.setProperty(selectedCard+AlisCommons.cardIDProperty, cardIDField.getText());
        AlisProperties.saveProps(props);
    }
    
    private void saveCardLang() {
        String selectedCard = soundCardsList.getSelectedValue().toString();
        Properties props = AlisProperties.loadProps();
        props.setProperty(selectedCard+AlisCommons.cardLangProperty, cardLangField.getText());
        AlisProperties.saveProps(props);
    }
    
    private void setCardEdit(boolean b) {
        cardIDField.setEditable(b);
        cardLangField.setEditable(b);
        cardTestButton.setEnabled(b);
        if ( b == false) {
            cardSetupLabel.setText("Card Options");
            cardIDField.setText(null);
            cardLangField.setText(null);
        }
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel9 = new javax.swing.JPanel();
        detectSoundCardsButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        soundCardSpinner = new javax.swing.JSpinner();
        jScrollPane7 = new javax.swing.JScrollPane();
        soundCardsList = new javax.swing.JList();
        jPanel10 = new javax.swing.JPanel();
        cardSetupLabel = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        cardIDField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cardLangField = new javax.swing.JTextField();
        cardTestButton = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        alsaListingArea = new javax.swing.JTextArea();
        configApplyButton = new javax.swing.JButton();

        setTitle("Configuration Options");
        setModal(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        detectSoundCardsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/misc.png")));
        detectSoundCardsButton.setToolTipText("Detect SoundCards");
        detectSoundCardsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectSoundCardsButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Detected Soundcards");

        jLabel22.setText("SoundCards Number");

        soundCardSpinner.setFont(new java.awt.Font("Dialog", 0, 12));
        soundCardSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                soundCardSpinnerStateChanged(evt);
            }
        });

        soundCardsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        soundCardsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                soundCardsListValueChanged(evt);
            }
        });

        jScrollPane7.setViewportView(soundCardsList);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(jLabel22)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(soundCardSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel13)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, detectSoundCardsButton))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(soundCardSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(34, 34, 34)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detectSoundCardsButton)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        cardSetupLabel.setText("Card Options");

        jLabel23.setText("ALSA Card ID:");

        cardIDField.setEditable(false);
        cardIDField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardIDFieldKeyReleased(evt);
            }
        });

        jLabel24.setText("Input Language:");

        cardLangField.setEditable(false);
        cardLangField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardLangFieldKeyReleased(evt);
            }
        });

        cardTestButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/player_play.png")));
        cardTestButton.setToolTipText("Test SoundCard");
        cardTestButton.setEnabled(false);
        cardTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardTestButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cardSetupLabel)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel24)
                            .add(jLabel23))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cardIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .add(cardLangField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cardTestButton))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(cardSetupLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(cardIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(cardLangField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29)
                .add(cardTestButton)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jLabel25.setText("ALSA Card Listing");

        alsaListingArea.setColumns(20);
        alsaListingArea.setEditable(false);
        alsaListingArea.setRows(5);
        jScrollPane8.setViewportView(alsaListingArea);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                    .add(jLabel25))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel25)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addContainerGap())
        );

        configApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/button_ok.png")));
        configApplyButton.setToolTipText("Apply Changes");
        configApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configApplyButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(configApplyButton)
                    .add(layout.createSequentialGroup()
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(configApplyButton)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void soundCardsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_soundCardsListValueChanged
        // Determine if there are any selected items
        boolean anySelected = !soundCardsList.isSelectionEmpty();
        
        if (anySelected) {
            String selectedCard = soundCardsList.getSelectedValue().toString();
            cardSetupLabel.setText(selectedCard + " Options");
            cardIDField.setText(AlisProperties.loadProps().getProperty(selectedCard + AlisCommons.cardIDProperty));
            cardLangField.setText(AlisProperties.loadProps().getProperty(selectedCard + AlisCommons.cardLangProperty));
            
            setCardEdit(true);
        }
        else {
            setCardEdit(false);
        }
    }//GEN-LAST:event_soundCardsListValueChanged

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        initContent();
    }//GEN-LAST:event_formComponentShown

    private void configApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configApplyButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_configApplyButtonActionPerformed

    private void cardTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardTestButtonActionPerformed
        String selectedCard = soundCardsList.getSelectedValue().toString();
        
        String command[] = new String [] { 
            AlisCommons.mainPath.getAbsolutePath() + File.separator + AlisCommons.scriptsPath + AlisCommons.pathseparator + AlisCommons.cardScriptTest,
            AlisProperties.loadProps().getProperty(selectedCard + AlisCommons.cardIDProperty)//,
            //mainPath + scriptsPath + pathseparatAlisPropertiese.wav"
        };
        
        try {
            Process child = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_cardTestButtonActionPerformed

    private void cardLangFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardLangFieldKeyReleased
        saveCardLang();
    }//GEN-LAST:event_cardLangFieldKeyReleased

    private void cardIDFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardIDFieldKeyReleased
        saveCardID();
    }//GEN-LAST:event_cardIDFieldKeyReleased

    private void soundCardSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_soundCardSpinnerStateChanged
        Properties props = AlisProperties.loadProps();
        int j = Integer.parseInt(soundCardSpinner.getValue().toString());
        
        modelSoundCards.clear();
        for (int i=0; i<=j-1; i++) {
            String currentCard = "Card"+(i+1);
            modelSoundCards.add(i, currentCard);
            
            String id = props.getProperty(currentCard + AlisCommons.cardIDProperty);
            if (id == null) { id = ""; }
            String lang = props.getProperty(currentCard + AlisCommons.cardLangProperty);
            if (lang == null) { lang = ""; }
            
            
            props.setProperty(currentCard + AlisCommons.cardIDProperty, id);
            props.setProperty(currentCard + AlisCommons.cardLangProperty, lang);
        }
        props.setProperty(AlisCommons.cardsNumber,soundCardSpinner.getValue().toString());
        AlisProperties.saveProps(props);
    }//GEN-LAST:event_soundCardSpinnerStateChanged

    private void detectSoundCardsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectSoundCardsButtonActionPerformed
        String command = AlisCommons.mainPath.getAbsolutePath() + File.separator + AlisCommons.scriptsPath + AlisCommons.pathseparator + AlisCommons.cardScriptCount;
        try {
            Process child = Runtime.getRuntime().exec(command);
            InputStream in = child.getInputStream();
            int c = in.read(); 
            in.close();
            String s = ""+(char)c;
            int initValue = 0;
            int min = 0;
            int max = Integer.parseInt(s);
            int step = 1;
            SpinnerModel model = new SpinnerNumberModel(initValue, min, max, step);
            soundCardSpinner.setModel(model);
            soundCardSpinner.setValue(Integer.parseInt(s));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        command = AlisCommons.mainPath.getAbsolutePath() + File.separator + AlisCommons.scriptsPath + AlisCommons.pathseparator + AlisCommons.cardScriptList;
        try {
            Process child = Runtime.getRuntime().exec(command);
            
            String cardslist = "";
            InputStream in = child.getInputStream();
            int c;
            while ((c = in.read()) != -1) {
                cardslist += (char)c;
            }
            in.close();
            alsaListingArea.setText(cardslist);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_detectSoundCardsButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea alsaListingArea;
    private javax.swing.JTextField cardIDField;
    private javax.swing.JTextField cardLangField;
    private javax.swing.JLabel cardSetupLabel;
    private javax.swing.JButton cardTestButton;
    private javax.swing.JButton configApplyButton;
    private javax.swing.JButton detectSoundCardsButton;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSpinner soundCardSpinner;
    private javax.swing.JList soundCardsList;
    // End of variables declaration//GEN-END:variables
    
}
