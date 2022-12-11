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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author djart
 */
public class AlisRecording extends javax.swing.JFrame {
    
    private static final long serialVersionUID = -6318392042915812236L;
	private File recTempDir;
    private String recTempTimeStamp;
    
    private DefaultListModel<String> modelRecordings;
    
    private AlisAboutDialog aboutDialog;
    private AlisConfigDialog2 configDialog;
    private AlisXMLHandler selectedXML;
    
    private AlisSoundFileRecorder[] reclinesArr;
    
    private Timer timer;
    
    public AlisRecording() {
        /*this.setIconImage(
                new javax.swing.ImageIcon(
                getClass().getResource("/icons/player_play.png")).getImage());*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException ex) {
            //ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            //ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            //ex.printStackTrace();
        } catch (InstantiationException ex) {
            //ex.printStackTrace();
        }
        initComponents();
        initLists();
        initDialogs();
    }
    
    private void initLists() {
        modelRecordings = new DefaultListModel<>();
        recsList.setModel(modelRecordings);
        
        refreshRecsList();
    }
    
    private void initDialogs() {
        aboutDialog = new AlisAboutDialog(this, false);
        configDialog = new AlisConfigDialog2(this, true);
    }
    
    private void closeWindow() {
        if (stopButton.isEnabled()) {
            String warning = "You cannot quit while recording.\n" +
                    "Stop recording and try again.";
            JOptionPane.showMessageDialog(this, 
                    warning,
                    "Recording in progress",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
            return;
        }
        
        String message = "Do you really want to exit ?";    
        int answer = JOptionPane.showConfirmDialog(this, message, 
                "Quit Confirmation", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.ERROR_MESSAGE, 
                (new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png")))
                );
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        
        aboutDialog.dispose();
        configDialog.dispose();
        dispose();
        System.exit(0);
    }
    
    private void refreshRecsList() {
        AlisCommons.getRecordPath().filter(File::exists).ifPresent(dir -> {
            var filter = new AlisFilenameFilter(AlisCommons.recDirPrefix);

            var children = dir.list(filter);
            // pucgenie: Is it necessary?
            Arrays.sort(children);

            modelRecordings.clear();
            // pucgenie: I want to access java.util.ImmutableCollections.ListN<E> (zero-copy references) :'(
            modelRecordings.addAll(java.util.List.of(children));
            
            // pucgenie: Fires intervalAdded for every element -.-
            //for (var child : children) {
            //    int pos = recsList.getModel().getSize();
            //    modelRecordings.add(pos, child);
            //}
        });
    }
    
    private void refreshRecsListUntil(String found) {
        boolean foundflag = false;
        
        while(foundflag == false) {
            refreshRecsList();
            int size = recsList.getModel().getSize();
        
            for (int i=0; i<size; i++) {
                Object item = recsList.getModel().getElementAt(i);
                if (item.toString().equals(found)) {
                    foundflag = true;
                }
            }
        }
    }
    
    private void setEditRec(boolean b) {
        currentSpeakerField.setEditable(b);
        currentLanguageField.setEditable(b);
        currentSeminarField.setEditable(b);
        currentAuditoriumField.setEditable(b);
        currentKeywordsArea.setEditable(b);
        currentCommentsArea.setEditable(b);
        if (b == false) {
            currentSpeakerField.setText(null);
            currentLanguageField.setText(null);
            currentSeminarField.setText(null);
            currentAuditoriumField.setText(null);
            currentKeywordsArea.setText(null);
            currentCommentsArea.setText(null);
        }
    }

    private void setEditRec(AlisXMLHandler alisxml) {
        currentSpeakerField.setText(alisxml.speaker);
        currentLanguageField.setText(alisxml.language);
        currentSeminarField.setText(alisxml.seminar);
        currentAuditoriumField.setText(alisxml.audit);
        currentKeywordsArea.setText(alisxml.keywds);
        currentCommentsArea.setText(alisxml.comments);
    }
    
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AlisRecording().setVisible(true));
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recsList = new javax.swing.JList<>();
        recsReloadButton = new javax.swing.JButton();
        recsDelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        currentSpeakerField = new javax.swing.JTextField();
        currentLanguageField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        currentKeywordsArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        currentCommentsArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        currentSeminarField = new javax.swing.JTextField();
        currentAuditoriumField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        aboutButton = new javax.swing.JButton();
        configButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        recordingLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        quitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Alis Recording Tool");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Saved Recordings"));
        recsList.addListSelectionListener(this::recsListValueChanged);

        jScrollPane1.setViewportView(recsList);

        recsReloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/reload.png")));
        recsReloadButton.setToolTipText("Refresh");
        recsReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recsReloadButtonActionPerformed(evt);
            }
        });

        recsDelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stop.png")));
        recsDelButton.setToolTipText("Delete");
        recsDelButton.setEnabled(false);
        recsDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recsDelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(recsReloadButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(recsDelButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(13, 13, 13)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(recsDelButton)
                    .add(recsReloadButton))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected Recording"));
        currentSpeakerField.setEditable(false);
        currentSpeakerField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentSpeakerFieldKeyReleased(evt);
            }
        });

        currentLanguageField.setEditable(false);
        currentLanguageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentLanguageFieldKeyReleased(evt);
            }
        });

        jLabel3.setText("Speaker:");

        jLabel4.setText("Language:");

        jLabel5.setText("Keywords:");

        jLabel6.setText("Comments:");

        currentKeywordsArea.setColumns(20);
        currentKeywordsArea.setEditable(false);
        currentKeywordsArea.setRows(5);
        currentKeywordsArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentKeywordsAreaKeyReleased(evt);
            }
        });

        jScrollPane2.setViewportView(currentKeywordsArea);

        currentCommentsArea.setColumns(20);
        currentCommentsArea.setEditable(false);
        currentCommentsArea.setRows(5);
        currentCommentsArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentCommentsAreaKeyReleased(evt);
            }
        });

        jScrollPane3.setViewportView(currentCommentsArea);

        jLabel9.setText("Seminar:");

        currentSeminarField.setEditable(false);
        currentSeminarField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentSeminarFieldKeyReleased(evt);
            }
        });

        currentAuditoriumField.setEditable(false);
        currentAuditoriumField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentAuditoriumFieldKeyReleased(evt);
            }
        });

        jLabel10.setText("Auditorium:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jLabel3)
                    .add(jLabel6)
                    .add(jLabel5)
                    .add(jLabel9)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, currentSpeakerField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, currentLanguageField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(currentSeminarField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, currentAuditoriumField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(currentSpeakerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(currentLanguageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(currentSeminarField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(currentAuditoriumField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addContainerGap())
        );

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_info.png")));
        aboutButton.setToolTipText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        configButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/configure.png")));
        configButton.setToolTipText("Configure");
        configButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configButtonActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Command Panel"));
        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ledgreen.png")));
        startButton.setToolTipText("Start Recording");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/player_stop.png")));
        stopButton.setToolTipText("Stop Recording");
        stopButton.setEnabled(false);
        stopButton.addActionListener(this::stopButtonActionPerformed);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(recordingLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(stopButton)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startButton)
                    .add(stopButton)
                    .add(recordingLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/alis_logo-head.png")));

        quitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png")));
        quitButton.setToolTipText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jLabel18)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 527, Short.MAX_VALUE)
                        .add(configButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(quitButton)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(quitButton)
                        .add(aboutButton)
                        .add(configButton))
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void currentCommentsAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentCommentsAreaKeyReleased
        selectedXML.comments = AlisXMLHandler.escapeXML(currentCommentsArea.getText());
        currentCommentsArea.setText(selectedXML.comments);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentCommentsAreaKeyReleased

    private void currentKeywordsAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentKeywordsAreaKeyReleased
        selectedXML.keywds = AlisXMLHandler.escapeXML(currentKeywordsArea.getText());
        currentKeywordsArea.setText(selectedXML.keywds);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentKeywordsAreaKeyReleased

    private void currentAuditoriumFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentAuditoriumFieldKeyReleased
        selectedXML.audit = AlisXMLHandler.escapeXML(currentAuditoriumField.getText());
        currentAuditoriumField.setText(selectedXML.audit);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentAuditoriumFieldKeyReleased

    private void currentSeminarFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentSeminarFieldKeyReleased
        selectedXML.seminar = AlisXMLHandler.escapeXML(currentSeminarField.getText());
        currentSeminarField.setText(selectedXML.seminar);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentSeminarFieldKeyReleased

    private void currentLanguageFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentLanguageFieldKeyReleased
        selectedXML.language = AlisXMLHandler.escapeXML(currentLanguageField.getText());
        currentLanguageField.setText(selectedXML.language);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentLanguageFieldKeyReleased

    private void currentSpeakerFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentSpeakerFieldKeyReleased
        selectedXML.speaker = AlisXMLHandler.escapeXML(currentSpeakerField.getText());
        currentSpeakerField.setText(selectedXML.speaker);
        selectedXML.xmlWriter();
    }//GEN-LAST:event_currentSpeakerFieldKeyReleased

    private void recsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_recsListValueChanged
        // Determine if there are any selected items
        boolean anySelected = !recsList.isSelectionEmpty();
        
        if (anySelected) {
            boolean delflag=true;
            int[] selectedIx = recsList.getSelectedIndices();
            
            // go through the table and search for temp rec dir prefix
            // if found don't enable the delete button
            for (int i=0; i<selectedIx.length; i++) {
                Object sel = recsList.getModel().getElementAt(selectedIx[i]);
                if (sel.toString().startsWith(AlisCommons.recTempPrefix)) {
                    delflag = false;
                    break;
                }
            }
            
            recsDelButton.setEnabled(delflag);
        
            if (selectedIx.length == 1) {
                setEditRec(true);
                try {
					selectedXML = new AlisXMLHandler(recsList.getSelectedValue());
					setEditRec(selectedXML);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else {
                setEditRec(false);
            }
        }
        else {
            recsDelButton.setEnabled(false);
            setEditRec(false);
        }
    }//GEN-LAST:event_recsListValueChanged

    private void recsReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recsReloadButtonActionPerformed
        refreshRecsList();
        recsDelButton.setEnabled(false);
    }//GEN-LAST:event_recsReloadButtonActionPerformed

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        closeWindow();
    }//GEN-LAST:event_quitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeWindow();
    }//GEN-LAST:event_formWindowClosing
        
    private void configButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configButtonActionPerformed
        configDialog.setVisible(true);
    }//GEN-LAST:event_configButtonActionPerformed

    private void recsDelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recsDelButtonActionPerformed
        var selectedIx = recsList.getSelectedIndices();
        
        if (selectedIx.length == 0) {
            return;
        }
        
        String message = "Are you sure you want to delete the selected recordings ?";    
        int answer = JOptionPane.showConfirmDialog(this, message, 
                "Delete Confirmation", JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE, 
                new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png"))
                );
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        
        int removed = 0;
	    try {
	        var recordpath = AlisCommons.getRecordPath().orElseThrow();
	        for (int i=0; i<selectedIx.length; i++) {
	            String item = modelRecordings.get(selectedIx[i]-removed);
	            // Delete Directory with recording
	            boolean success = deleteDir(new File(recordpath, item.toString()));
	            if(success) {
	                System.out.println("Deleted Directory: " + recordpath + item.toString());
	                modelRecordings.remove(selectedIx[i]-removed);
	                removed++;
	            }
	        }
        } catch (java.util.NoSuchElementException nseex) {
        	System.err.println("RecordPath vanished. wtf");
        }
        
        recsDelButton.setEnabled(false);
    }//GEN-LAST:event_recsDelButtonActionPerformed
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        var recordmethod = AlisProperties.loadProps().getProperty(AlisCommons.recordMethod, 
                AlisCommons.recordMethodJava);
        var message = "Do you really want to stop recording ?";    
        int answer = JOptionPane.showConfirmDialog(this, message, 
                "Stop Recording Confirmation", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.ERROR_MESSAGE, 
                (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                );
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }        
        
        AlisXMLHandler alisxml;
		try {
			alisxml = new AlisXMLHandler(AlisCommons.recTempPrefix + recTempTimeStamp);var date = new Date();
	        var formatter = new SimpleDateFormat(AlisCommons.dateFormat);
	        alisxml.stopTime = formatter.format(date);
	        alisxml.xmlWriter();
	        
	        if (recordmethod.equals(AlisCommons.recordMethodJava)) {
	            var recordpath = AlisCommons.getRecordPath().orElseThrow();
	            var recDir = new File(recordpath, AlisCommons.recDirPrefix + recTempTimeStamp);
	            
	            for (int i=0; i<reclinesArr.length; i++) {
	                if (reclinesArr[i] != null) {
	                    reclinesArr[i].stopRecording();
	                    long delayMillis = 5000;
	                    try {
	                        reclinesArr[i].join(delayMillis); // wait 5 seconds for the thread to end
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    }
	                }
	            }
	            
	            String audit = AlisXMLHandler.escapeXMLforFilename(alisxml.audit);
	            String seminar = AlisXMLHandler.escapeXMLforFilename(alisxml.seminar);
	            
	            if (audit.length() > 0) {
	            	recDir = new File(recDir.getParent(), recDir.getName() + "_" + audit);
	            }
	            
	            if (seminar.length() > 0) {
	            	recDir = new File(recDir.getParent(), recDir.getName() + "_" + seminar);
	            }
	            
	            recTempDir.renameTo(recDir);
	        }
	        else if (recordmethod.equals(AlisCommons.recordMethodAlsa)) {
	            var commands = new String[]{
	            		AlisCommons.mainPath.getAbsolutePath() + File.separator + AlisCommons.scriptsPath + AlisCommons.pathseparator + AlisCommons.recScriptStop, 
	                "-d",
	                recTempDir.getAbsolutePath(),
	                "-t",
	                recTempTimeStamp,
	                "-s",
	                AlisXMLHandler.escapeXMLforFilename(alisxml.seminar),
	                "-a",
	                AlisXMLHandler.escapeXMLforFilename(alisxml.audit),
	                "-sp",
	                AlisXMLHandler.escapeXMLforFilename(alisxml.speaker)
	            };
	
	            try {
	                Process child = Runtime.getRuntime().exec(commands);
	                InputStream in = child.getInputStream();
	                var buf = new byte[256];
	                for (int n; (n = in.read(buf)) != 0;) {
	                    System.out.write(buf, 0, n);
	                }
	                in.close();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        
	        startButton.setEnabled(true);
	        stopButton.setEnabled(false);
	        recsDelButton.setEnabled(false);
	        configDialog.setPathEdit(true);
	        
	        timer.cancel();
	        recordingLabel.setText("");
	        
	        refreshRecsList();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }//GEN-LAST:event_stopButtonActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        var recordmethod = AlisProperties.loadProps().getProperty(AlisCommons.recordMethod, 
                AlisCommons.recordMethodJava);
        int err = AlisCommons.checkRecordPath();
        boolean ret = false;
        
        switch (err) {
            case 1:
                JOptionPane.showMessageDialog(this, 
                    "You have not configured any path for storing recordings.\nRecording will not start!",
                    "Recording Path Error",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
                ret = true;
                break;
            case 2:
                JOptionPane.showMessageDialog(this, 
                    "The configured recording path does not exist.\nRecording will not start!",
                    "Recording Path Error",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
                ret = true;
                break;
            case 3:
                JOptionPane.showMessageDialog(this, 
                    "The configured recording path cannot be written.\nRecording will not start!",
                    "Recording Path Error",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
                ret = true;
                break;
            case 0:
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Unknown error!\nRecording will not start!",
                    "Recording Path Error",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
                ret = true;
                break;
        }
        
        if (ret != false) {
            return;
        }
        
        // Get today's date
        Date date = new Date();
        Format formatter = new SimpleDateFormat(AlisCommons.dateFormat);
        recTempTimeStamp = formatter.format(date);

        recTempDir = AlisCommons.getRecordPath().map(recordpath -> new File(recordpath, AlisCommons.recTempPrefix + recTempTimeStamp)).orElseThrow();

        if (recordmethod.equals(AlisCommons.recordMethodJava)) {
            int linesnum = 0, reallinesnum = 0;
            
            String linesprop = AlisProperties.loadProps().getProperty(AlisCommons.linesNumber);
            if (linesprop == null) {
                linesnum = 0;
            }
            else {
                linesnum = Integer.parseInt(linesprop);
            }
            
            AlisSoundFileRecorder[] linesArr = new AlisSoundFileRecorder[linesnum];
            String recordinwave = AlisProperties.loadProps().getProperty(AlisCommons.recordWaveProperty, "false");
            String recordin = null;
            if (recordinwave.equals("true")) {
                recordin = "wav";
            }
            else {
                recordin = "mp3";
            }
            
            if (linesnum != 0) {
                recTempDir.mkdir();
                // count the reallinesnum, only those which are properly configured
                for (int i=1; i<=linesnum; i++) {
                    String lineID = AlisProperties.loadProps().getProperty("Line" + i + 
                            AlisCommons.lineIDProperty);
                    int lineIDnum = Integer.parseInt(lineID);
                    String lineLang = AlisProperties.loadProps().getProperty("Line" + i +
                            AlisCommons.lineLangProperty, "line"+lineIDnum);
                    String lineEnabled = AlisProperties.loadProps().getProperty("Line" + i + 
                            AlisCommons.lineEnabled);
                    
                    
                    if (lineEnabled.equals("true") && AlisSound.mixerExists(lineIDnum, false)) {
                        try {
							linesArr[reallinesnum] = new AlisSoundFileRecorder(
							        recTempDir,
							        lineIDnum,
							        lineLang,
							        recordin
							        );
	                        linesArr[reallinesnum].start();
	                        reallinesnum++;
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                }
            }
            
            if (linesnum == 0 || reallinesnum == 0) {
                JOptionPane.showMessageDialog(this, 
                    "You have not configured any input lines.\nRecording will not start!",
                    "No Input Lines Found",
                    JOptionPane.WARNING_MESSAGE,
                    (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                    );
                deleteDir(recTempDir);
                return;
            }
            
            reclinesArr = linesArr;
        }
        else if (recordmethod.equals(AlisCommons.recordMethodAlsa)) {
            int cardsnum = 0, realcardsnum = 0;
            
            String cardsprop = AlisProperties.loadProps().getProperty(AlisCommons.cardsNumber);
            if (cardsprop == null) { 
                cardsnum = 0; 
            }
            else { 
                cardsnum = Integer.parseInt(cardsprop); 
            }

            if (cardsnum != 0) {
                // count the realcardsnum, only those which are properly configured
                for (int i=1; i<=cardsnum; i++) {
                    String cardID = AlisProperties.loadProps().getProperty("Card" + i + 
                            AlisCommons.cardIDProperty);
                    if (cardID.length() > 0 ) { realcardsnum++; }
                }
            }

            if ( cardsnum == 0 || realcardsnum == 0) {
                JOptionPane.showMessageDialog(this, 
                        "You have not configured any soundcards.\nRecording will not start!",
                        "No Cards Found",
                        JOptionPane.WARNING_MESSAGE,
                        (new javax.swing.ImageIcon(getClass().getResource("/icons/messagebox_warning.png")))
                        );
                return;
            }

            var commands = new String[(2*realcardsnum)+3];
            commands[0] = AlisCommons.mainPath.getAbsolutePath() + File.separator + AlisCommons.scriptsPath + AlisCommons.pathseparator + AlisCommons.recScriptStart;
            commands[1] = "-d";
            commands[2] = recTempDir.getAbsolutePath();
            int realcardsptr = 0;

            for (int i=1; i<=cardsnum; i++) {
                String cardID   = AlisProperties.loadProps().getProperty("Card" + i + AlisCommons.cardIDProperty);
                String cardLang = AlisProperties.loadProps().getProperty("Card" + i + AlisCommons.cardLangProperty);

                if (cardID.length() > 0) {
                    realcardsptr++;
                }
                else {
                    continue;
                }

                if ( cardLang.length() <= 0 ) {
                    cardLang = "card" + cardID;
                }

                commands[2*realcardsptr+1] = cardID;
                commands[2*realcardsptr+2] = cardLang;
            }

            try {
                Process child = Runtime.getRuntime().exec(commands);

                // This is ***ONLY*** for debugging purposes
                // If we do this on the ALIS Record scripts, the gui is waiting
                // forever to get the output(inputstream) from the command
                // Another way would be to use threads
                /*InputStream in = child.getInputStream();
                int c;
                while ((c = in.read()) != -1) {
                    System.out.print((char)c);
                }
                in.close();*/
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
            

        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        configDialog.setPathEdit(false);
        
        int delay = 0;
        int period = 1000; // every second
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int i=0;
            public void run () {
                recordingLabel.setText("Recording ... " + AlisCommons.formatSeconds(i));
                i++;
            }
        }, delay, period);
                

        refreshRecsListUntil(AlisCommons.recTempPrefix + recTempTimeStamp);

        AlisXMLHandler.dtdWriter(recTempDir);
        AlisXMLHandler alisxml;
		try {
			alisxml = new AlisXMLHandler(AlisCommons.recTempPrefix + recTempTimeStamp);
			alisxml.startTime = recTempTimeStamp;
	        alisxml.xmlWriter();
	
	        this.recsList.setSelectedValue(AlisCommons.recTempPrefix + recTempTimeStamp, true);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }//GEN-LAST:event_startButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JButton configButton;
    private javax.swing.JTextField currentAuditoriumField;
    private javax.swing.JTextArea currentCommentsArea;
    private javax.swing.JTextArea currentKeywordsArea;
    private javax.swing.JTextField currentLanguageField;
    private javax.swing.JTextField currentSeminarField;
    private javax.swing.JTextField currentSpeakerField;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton quitButton;
    private javax.swing.JLabel recordingLabel;
    private javax.swing.JButton recsDelButton;
    private javax.swing.JList<String> recsList;
    private javax.swing.JButton recsReloadButton;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    
}
