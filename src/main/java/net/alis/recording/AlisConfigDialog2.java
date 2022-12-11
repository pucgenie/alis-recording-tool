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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import net.alis.recording.AlisCommons;
import net.alis.recording.AlisProperties;
import net.alis.recording.AlisSound;
import net.alis.recording.AlisSoundMemoryRecorder;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author  djart
 */
public class AlisConfigDialog2 extends javax.swing.JDialog {
    private DefaultListModel modelSoundCards;
    private DefaultListModel modelTargetDataLines;
    
    public AlisConfigDialog2(AlisRecording parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initLists();
        initContent();
        initTabs();
        initJavaSoundPreferences();
    }
    
    private void initLists() {
        modelSoundCards = new DefaultListModel();
        soundCardsList.setModel(modelSoundCards);
    }
    
    private void initContent() {
        String recordmethod = AlisProperties.loadProps().getProperty(AlisCommons.recordMethod);
        if (recordmethod != null) {
            recordmethodComboBox.setSelectedItem(recordmethod);
        }
        
        String recPath = AlisProperties.loadProps().getProperty(AlisCommons.recPathProperty);
        if (recPath != null) {
            recpathField.setText(recPath);
        }
                
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
        
        cardSetupPanel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Card Options"));
        cardIDField.setText("");
        cardLangField.setText("");
    }
    
    private void initTabs() {
        String recordmethod = recordmethodComboBox.getSelectedItem().toString();
        String recordmethodTabName = null;
        Component recordmethodPanel = null;

        if ( recordmethod == AlisCommons.recordMethodJava) {
            recordmethodTabName = AlisCommons.recordMethodJavaTab;
            recordmethodPanel = new JScrollPane();
            recordmethodPanel = javaPanel;
        }
        else {
            recordmethodTabName = AlisCommons.recordMethodAlsaTab;
            recordmethodPanel = new JPanel();
            recordmethodPanel = alsaPanel;
        }
        
        int count = jTabbedPane1.getTabCount();
    
        // remove all tabs
        for (int i=1; i<count; i++) {
            jTabbedPane1.remove(jTabbedPane1.getTabCount()-1);
        }
        
        jTabbedPane1.addTab(recordmethodTabName, recordmethodPanel);
    }
    
    private void initJavaSoundPreferences() {
        modelTargetDataLines = new DefaultListModel();
        targetDataLinesList.setModel(modelTargetDataLines);

        // Request number of targetdatalines
        int totaltargetdatalines = AlisSound.getMixersNum(false);
                
        // initialize the card list
        modelTargetDataLines.clear();
        
        for (int i=0; i<totaltargetdatalines; i++) {
            modelTargetDataLines.add(i, AlisSound.getMixerName(i, false));
        }
        
        String recordinwave = AlisProperties.loadProps().getProperty(AlisCommons.recordWaveProperty, "false");
        if (recordinwave.equals("true")) {
            chooseWaveFormat.setSelected(true);
        }
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
            cardSetupPanel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Card Options"));
            cardIDField.setText(null);
            cardLangField.setText(null);
        }
    }
    
    private void setLineEdit(boolean b) {
        lineLangField.setEditable(b);
        lineTestButton.setEnabled(b);
        lineCheckBox.setEnabled(b);
        if ( b == false ) {
            lineIDField.setText(null);
            lineDescField.setText(null);
            lineLangField.setText(null);
        }
    }
    
    public void setPathEdit(boolean b) {
        recpathField.setEditable(b);
        recpathChooseButton.setEnabled(b);
    }
    
    private int getLineConfNum(int currentlineid) {
        Properties props = AlisProperties.loadProps();
        int totallines = 0;
        boolean alreadyconfigured = false;
        int currentlinenum = -1;
        
        // determine if the checked line is already configured
        String linesnumstring = props.getProperty(AlisCommons.linesNumber);
        if (linesnumstring != null) {
            totallines = Integer.parseInt(linesnumstring);
        }

        if (totallines != 0) {
            for (int i = 1; i<=totallines; i++) {
                int lineid = Integer.parseInt(props.getProperty("Line" + i + AlisCommons.lineIDProperty));
                if ( lineid == currentlineid) {
                    alreadyconfigured = true;
                    currentlinenum = i;
                    break;
                }
            }
        }
        
        // if not already configured, increase the lines.number counter
        if (alreadyconfigured == false) {
            totallines++;
            currentlinenum = totallines;
            
            // write properties
            String currentline = "Line"+currentlinenum;
            props.setProperty(AlisCommons.linesNumber, Integer.toString(totallines));
            props.setProperty(currentline + AlisCommons.lineIDProperty, Integer.toString(currentlineid));
            props.setProperty(currentline + AlisCommons.lineLangProperty, "");
            props.setProperty(currentline + AlisCommons.lineEnabled, "false");
            AlisProperties.saveProps(props);
        }
        
        return currentlinenum;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        configApplyButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        recordmethodComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        recpathField = new javax.swing.JTextField();
        recpathChooseButton = new javax.swing.JButton();
        alsaPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        detectSoundCardsButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        soundCardSpinner = new javax.swing.JSpinner();
        jScrollPane7 = new javax.swing.JScrollPane();
        soundCardsList = new javax.swing.JList();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        alsaListingArea = new javax.swing.JTextArea();
        cardSetupPanel = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        cardIDField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cardLangField = new javax.swing.JTextField();
        cardTestButton = new javax.swing.JButton();
        javaPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        targetDataLinesList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lineTestButton = new javax.swing.JButton();
        lineIDField = new javax.swing.JTextField();
        lineLangField = new javax.swing.JTextField();
        lineCheckBox = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        lineDescField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        chooseWaveFormat = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();

        setTitle("Configuration Options");
        setModal(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        configApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/button_ok.png")));
        configApplyButton.setToolTipText("Apply Changes");
        configApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configApplyButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Recording Method:");

        recordmethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Java Sound API (OS Portable)", "ALSA System (Linux only)" }));
        recordmethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordmethodComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Saved Recordings Path:");

        recpathField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                recpathFieldKeyReleased(evt);
            }
        });

        recpathChooseButton.setText("Choose...");
        recpathChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recpathChooseButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(recpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(recpathChooseButton))
                    .add(recordmethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(recordmethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(recpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(recpathChooseButton))
                .addContainerGap(442, Short.MAX_VALUE))
        );
        jTabbedPane1.addTab("General Properties", jPanel1);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Configured Soundcards"));
        detectSoundCardsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/misc.png")));
        detectSoundCardsButton.setToolTipText("Detect SoundCards");
        detectSoundCardsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectSoundCardsButtonActionPerformed(evt);
            }
        });

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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detectSoundCardsButton)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "ALSA Card Listing"));
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
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        cardSetupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Card Options"));
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

        org.jdesktop.layout.GroupLayout cardSetupPanelLayout = new org.jdesktop.layout.GroupLayout(cardSetupPanel);
        cardSetupPanel.setLayout(cardSetupPanelLayout);
        cardSetupPanelLayout.setHorizontalGroup(
            cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardSetupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cardSetupPanelLayout.createSequentialGroup()
                        .add(cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel23)
                            .add(jLabel24))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cardLangField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .add(cardIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cardSetupPanelLayout.createSequentialGroup()
                        .add(cardTestButton)
                        .add(12, 12, 12))))
        );
        cardSetupPanelLayout.setVerticalGroup(
            cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardSetupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(cardIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cardSetupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(cardLangField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 31, Short.MAX_VALUE)
                .add(cardTestButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout alsaPanelLayout = new org.jdesktop.layout.GroupLayout(alsaPanel);
        alsaPanel.setLayout(alsaPanelLayout);
        alsaPanelLayout.setHorizontalGroup(
            alsaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(alsaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(alsaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cardSetupPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        alsaPanelLayout.setVerticalGroup(
            alsaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(alsaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(alsaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, alsaPanelLayout.createSequentialGroup()
                        .add(cardSetupPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jTabbedPane1.addTab("ALSA Properties", alsaPanel);

        javaPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                javaPanelComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Detected Input Lines"));
        targetDataLinesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                targetDataLinesListValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(targetDataLinesList);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Line Input Options"));
        jLabel3.setText("Line Input ID:");

        jLabel4.setText("Input Language:");

        jLabel5.setText("Enabled:");

        lineTestButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/player_play.png")));
        lineTestButton.setToolTipText("Test LineInput");
        lineTestButton.setEnabled(false);
        lineTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineTestButtonActionPerformed(evt);
            }
        });

        lineIDField.setEditable(false);

        lineLangField.setEditable(false);
        lineLangField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lineLangFieldKeyReleased(evt);
            }
        });

        lineCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lineCheckBox.setEnabled(false);
        lineCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineCheckBoxActionPerformed(evt);
            }
        });

        jLabel6.setText("Line Description:");

        lineDescField.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel6)
                            .add(jLabel4)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lineCheckBox)
                            .add(lineLangField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .add(lineDescField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .add(lineIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lineTestButton))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(lineIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(lineDescField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(lineLangField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lineCheckBox)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                .add(lineTestButton)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Other Options"));
        chooseWaveFormat.setText("Record in WAVE format");
        chooseWaveFormat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chooseWaveFormat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chooseWaveFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseWaveFormatActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(chooseWaveFormat)
                .addContainerGap(212, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(chooseWaveFormat)
                .addContainerGap(183, Short.MAX_VALUE))
        );

        jLabel7.setText("Check the corresponding checkbox for each input line you wish to enable for recording.");

        org.jdesktop.layout.GroupLayout javaPanelLayout = new org.jdesktop.layout.GroupLayout(javaPanel);
        javaPanel.setLayout(javaPanelLayout);
        javaPanelLayout.setHorizontalGroup(
            javaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(javaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(javaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(javaPanelLayout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(javaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(jLabel7))
                .addContainerGap())
        );
        javaPanelLayout.setVerticalGroup(
            javaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, javaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(javaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(javaPanelLayout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jTabbedPane1.addTab("Java Sound Properties", javaPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, configApplyButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(configApplyButton)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chooseWaveFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseWaveFormatActionPerformed
        boolean b = chooseWaveFormat.isSelected();
        
        Properties props = AlisProperties.loadProps();
        props.setProperty(AlisCommons.recordWaveProperty, Boolean.toString(b));
        AlisProperties.saveProps(props);
    }//GEN-LAST:event_chooseWaveFormatActionPerformed

    private void javaPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_javaPanelComponentShown
        initJavaSoundPreferences();
    }//GEN-LAST:event_javaPanelComponentShown

    private void recpathFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recpathFieldKeyReleased
        Properties props = AlisProperties.loadProps();
        props.setProperty(AlisCommons.recPathProperty, recpathField.getText());
        AlisProperties.saveProps(props);
    }//GEN-LAST:event_recpathFieldKeyReleased

    private void recpathChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recpathChooseButtonActionPerformed
        String recpath = recpathField.getText();
        JFileChooser chooser = new JFileChooser(recpath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showDialog(this, "Choose");
        
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                File selFile = chooser.getSelectedFile();
                recpathField.setText(selFile.getAbsolutePath());
                Properties props = AlisProperties.loadProps();
                props.setProperty(AlisCommons.recPathProperty, selFile.getAbsolutePath());
                AlisProperties.saveProps(props);
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }
    }//GEN-LAST:event_recpathChooseButtonActionPerformed

    private void lineTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineTestButtonActionPerformed
        int mixnum = Integer.parseInt(lineIDField.getText());
        final AlisSoundMemoryRecorder recorder = new AlisSoundMemoryRecorder(mixnum);
        recorder.start();
        lineTestButton.setEnabled(false);
        
        Date timeToRun = new Date(System.currentTimeMillis() + 10000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run () {
                recorder.stop();
                lineTestButton.setEnabled(true);
            }
        }, timeToRun);
    }//GEN-LAST:event_lineTestButtonActionPerformed

    private void lineLangFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lineLangFieldKeyReleased
        int currentlineconfnum = getLineConfNum(Integer.parseInt(lineIDField.getText()));
        String currentline = "Line" + currentlineconfnum;
        
        Properties props = AlisProperties.loadProps();
        props.setProperty(currentline + AlisCommons.lineLangProperty, lineLangField.getText());
        AlisProperties.saveProps(props);
    }//GEN-LAST:event_lineLangFieldKeyReleased

    private void lineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineCheckBoxActionPerformed
        int currentlineconfnum = getLineConfNum(Integer.parseInt(lineIDField.getText()));
        String currentline = "Line" + currentlineconfnum;
        String isenabled = "false";
        
        Properties props = AlisProperties.loadProps();
        // get the status of the checkbox change
        boolean b = lineCheckBox.isSelected();
        if ( b == true ) { isenabled = "true"; }
        props.setProperty(currentline + AlisCommons.lineEnabled, isenabled);
        AlisProperties.saveProps(props);
    }//GEN-LAST:event_lineCheckBoxActionPerformed

    private void targetDataLinesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_targetDataLinesListValueChanged
        // Determine if there are any selected items
        boolean anySelected = !targetDataLinesList.isSelectionEmpty();
        
        if (anySelected) {
            int selectedLineOrder = targetDataLinesList.getSelectedIndex();
            
            int selectedLine = AlisSound.getMixerIDByOrder(selectedLineOrder, false);
            lineIDField.setText(Integer.toString(selectedLine));
            
            Properties props = AlisProperties.loadProps();
            int linesnum = 0;
            int currentline = Integer.parseInt(lineIDField.getText());
            
            lineDescField.setText(AlisSound.getMixerDescriptionByID(selectedLine));
            lineDescField.setToolTipText(AlisSound.getMixerDescriptionByID(selectedLine));
            lineLangField.setText("");
            lineCheckBox.setSelected(false);
            
            String linesnumstring = props.getProperty(AlisCommons.linesNumber);
            if (linesnumstring != null) {
                linesnum = Integer.parseInt(linesnumstring);
            }
            
            if (linesnum != 0) {
                for(int i=1; i<=linesnum; i++) {
                    int lineid = Integer.parseInt(props.getProperty("Line" + i + AlisCommons.lineIDProperty));
                    String linelang = props.getProperty("Line" + i + AlisCommons.lineLangProperty);
                    String lineenabled = props.getProperty("Line" + i + AlisCommons.lineEnabled);
                    if (lineid == currentline) {
                        if (lineenabled.equals("true")) {
                            lineCheckBox.setSelected(true);
                        }
                        if ( linelang != null) {
                            lineLangField.setText(linelang);
                        }
                        break;
                    }
                }
            }

            setLineEdit(true);
        } else {
            setLineEdit(false);
        }
    }//GEN-LAST:event_targetDataLinesListValueChanged

    private void recordmethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordmethodComboBoxActionPerformed
        Properties props = AlisProperties.loadProps();
        if (! props.getProperty(AlisCommons.recordMethod, AlisCommons.recordMethodJava).equals(recordmethodComboBox.getSelectedItem().toString()) ) {
            props.setProperty(AlisCommons.recordMethod, recordmethodComboBox.getSelectedItem().toString());
            AlisProperties.saveProps(props);
            initTabs();
        }
    }//GEN-LAST:event_recordmethodComboBoxActionPerformed

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

    private void soundCardsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_soundCardsListValueChanged
        // Determine if there are any selected items
        boolean anySelected = !soundCardsList.isSelectionEmpty();
        
        if (anySelected) {
            String selectedCard = soundCardsList.getSelectedValue().toString();
            cardSetupPanel.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    selectedCard + " Options"));
            cardIDField.setText(AlisProperties.loadProps().getProperty(selectedCard + AlisCommons.cardIDProperty));
            cardLangField.setText(AlisProperties.loadProps().getProperty(selectedCard + AlisCommons.cardLangProperty));
            
            setCardEdit(true);
        } else {
            setCardEdit(false);
        }
    }//GEN-LAST:event_soundCardsListValueChanged

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

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        initContent();
        initJavaSoundPreferences();
    }//GEN-LAST:event_formComponentShown

    private void configApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configApplyButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_configApplyButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea alsaListingArea;
    private javax.swing.JPanel alsaPanel;
    private javax.swing.JTextField cardIDField;
    private javax.swing.JTextField cardLangField;
    private javax.swing.JPanel cardSetupPanel;
    private javax.swing.JButton cardTestButton;
    private javax.swing.JCheckBox chooseWaveFormat;
    private javax.swing.JButton configApplyButton;
    private javax.swing.JButton detectSoundCardsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel javaPanel;
    private javax.swing.JCheckBox lineCheckBox;
    private javax.swing.JTextField lineDescField;
    private javax.swing.JTextField lineIDField;
    private javax.swing.JTextField lineLangField;
    private javax.swing.JButton lineTestButton;
    private javax.swing.JComboBox recordmethodComboBox;
    private javax.swing.JButton recpathChooseButton;
    private javax.swing.JTextField recpathField;
    private javax.swing.JSpinner soundCardSpinner;
    private javax.swing.JList soundCardsList;
    private javax.swing.JList targetDataLinesList;
    // End of variables declaration//GEN-END:variables
    
}
